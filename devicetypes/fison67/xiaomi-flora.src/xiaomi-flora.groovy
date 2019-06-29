/**
 *  Xiaomi Mi Flora (v.0.0.2)
 *
 * MIT License
 *
 * Copyright (c) 2018 fison67@nate.com
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
 
import groovy.json.JsonSlurper
import groovy.transform.Field

metadata {
	definition (name: "Xiaomi Flora", namespace: "fison67", author: "fison67") {
        capability "Sensor"
        capability "Battery"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Illuminance Measurement"
		capability "Refresh"
        
        attribute "fertility", "number"
        attribute "lastCheckin", "Date"
	}

	simulator {
	}
    
    preferences {
    	input name: "selectedLang", title:"Select a language" , type: "enum", required: true, options: ["English", "Korean"], defaultValue: "English", description:"Language for DTH"
		input name: "temperatureType", title:"Select a type" , type: "enum", required: true, options: ["C", "F"], defaultValue: "C"
        
        input name: "totalChartType", title:"Total-Chart Type" , type: "enum", required: true, options: ["line", "bar"], defaultValue: "line", description:"Total Chart Type [ line, bar ]" 
        input name: "historyDayCount", type:"number", title: "Maximum days for single graph", required: true, description: "", defaultValue:1, displayDuringSetup: true
        input name: "historyTotalDayCount", type:"number", title: "Maximum days for total graph", required: true, description: "", defaultValue:7, range: "2..31", displayDuringSetup: true

	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setInfo(String app_url, String id) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
}

def setLanguage(language){
    log.debug "Languge >> ${language}"
}

def setExternalAddress(address){
	log.debug "External Address >> ${address}"
	state.externalAddress = address
}

def setStatus(params){
    log.debug "${params.key} : ${params.data}"
    
    def data = new JsonSlurper().parseText(params.data)
    
    if(data.firmware != null){
        sendEvent(name:"battery", value: data.firmware.battery)
        sendEvent(name:"versions", value: 'version: ' + data.firmware.firmware)
    }
    
    if(data.sensor != null){
        sendEvent(name:"temperature", value: makeTemperature(data.sensor.temperature))
        sendEvent(name:"illuminance", value: data.sensor.lux)
        sendEvent(name:"humidity", value: data.sensor.moisture)
        sendEvent(name:"fertility", value: data.sensor.fertility)
    }
    
    if(data.temperature != null){
        sendEvent(name:"temperature", value: makeTemperature(data.temperature))
    }
    if(data.moisture != null){
        sendEvent(name:"humidity", value: data.moisture)
    }
    if(data.lux != null){
        sendEvent(name:"illuminance", value: data.lux)
    }
    if(data.fertility != null){
        sendEvent(name:"fertility", value: data.fertility)
    }
    
    updateLastTime()
}

def makeTemperature(temperature){
	if(temperatureType == "F"){
    	return ((temperature * 9 / 5) + 32)
    }else{
    	return temperature
    }
}

def updated() {
    setLanguage(settings.selectedLang)
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def getMac(){
	def mac = state.id
    return mac.substring(0,2) + ":" + mac.substring(2,4) + ":" + mac.substring(4,6) + ":" + mac.substring(6,8) + ":" + mac.substring(8,10) + ":" + mac.substring(10,12)
}


def makeURL(type, name){
	def sDate
    def eDate
	use (groovy.time.TimeCategory) {
      def now = new Date()
      def day = settings.historyDayCount == null ? 1 : settings.historyDayCount
      sDate = (now - day.days).format( 'yyyy-MM-dd HH:mm:ss', location.timeZone )
      eDate = now.format( 'yyyy-MM-dd HH:mm:ss', location.timeZone )
    }
	return [
        uri: "http://${state.externalAddress}",
        path: "/devices/history/${state.id}/${type}/${sDate}/${eDate}/image",
        query: [
        	"name": name
        ]
    ]
}

def makeTotalURL(type, name){
	def sDate
    def eDate
	use (groovy.time.TimeCategory) {
      def now = new Date()
      def day = (settings.historyTotalDayCount == null ? 7 : settings.historyTotalDayCount) - 1
      sDate = (now - day.days).format( 'yyyy-MM-dd', location.timeZone )
      eDate = (now + 1.days).format( 'yyyy-MM-dd', location.timeZone )
    }
	return [
        uri: "http://${state.externalAddress}",
        path: "/devices/history/${state.id}/${type}/${sDate}/${eDate}/total/image",
        query: [
        	"name": name,
            "chartType": (settings.totalChartType == null ? 'line' : settings.totalChartType) 
        ]
    ]
}

def processImage(response, type){
	if (response.status == 200 && response.headers.'Content-Type'.contains("image/png")) {
        def imageBytes = response.data
        if (imageBytes) {
            try {
                storeImage(getPictureName(type), imageBytes)
            } catch (e) {
                log.error "Error storing image ${name}: ${e}"
            }
        }
    } else {
        log.error "Image response not successful or not a jpeg response"
    }
}

def chartMoisture() {
    httpGet(makeURL("moisture", "Moisture")) { response ->
    	processImage(response, "moisture")
    }
}

def chartTemperature(){
    httpGet(makeURL("temperature", "Temperature")) { response ->
    	processImage(response, "temperature")
    }
}

def chartLux(){
    httpGet(makeURL("lux", "Lux")) { response ->
    	processImage(response, "lux")
    }
}

def chartFertility(){
    httpGet(makeURL("fertility", "Fertility")) { response ->
    	processImage(response, "fertility")
    }
}

def chartTotalTemperature(){
    httpGet(makeTotalURL("temperature", "T-Temperature")) { response ->
    	processImage(response, "temperature")
    }
}

def chartTotalLux(){
    httpGet(makeTotalURL("lux", "T-Lux")) { response ->
    	processImage(response, "lux")
    }
}

def chartTotalFertility(){
    httpGet(makeTotalURL("fertility", "T-Fertility")) { response ->
    	processImage(response, "fertility")
    }
}

def chartTotalMoisture(){
    httpGet(makeTotalURL("moisture", "T-Moisture")) { response ->
    	processImage(response, "moisture")
    }
}

private getPictureName(type) {
  def pictureUuid = java.util.UUID.randomUUID().toString().replaceAll('-', '')
  return "image" + "_$pictureUuid" + "_" + type + ".png"
}
