/**
 *  Xiaomi Power Strip (v.0.0.2)
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

metadata {
	definition (name: "Xiaomi Power Strip", namespace: "fison67", author: "fison67") {
    	capability "Actuator"
        capability "Switch"				
        capability "Power Meter"
        capability "Energy Meter"			//"on", "off"
        capability "Temperature Measurement"	
        capability "Configuration"
        capability "Sensor"
        capability "Outlet"
        capability "Refresh"
         
        attribute "mode", "string"
        attribute "current", "string"
        
        attribute "lastCheckin", "Date"
        
        command "wifiLedOn"
        command "wifiLedOff"
        command "realTimePowerOn"
        command "realTimePowerOff"
        
        command "setModeGreen"
        command "setModeNormal"
        
        command "chartTemperature"
        command "chartPowerMeter"
        command "chartEnergyMeter"
        command "chartTotalTemperature"
        command "chartTotalPowerMeter"
        command "chartTotalEnergyMeter"
	}

	simulator { }

	preferences {
		input name: "historyDayCount", type:"number", title: "Day for History Graph", description: "", defaultValue:1, displayDuringSetup: true
		input name: "historyTotalDayCount", type:"number", title: "Total Day for History Graph", description: "0 is max", defaultValue:7, range: "2..7", displayDuringSetup: true
        
		input name: "temperatureHistoryDataMaxCount", type:"number", title: "Temperature Graph Data Max Count", description: "0 is max", defaultValue:0, displayDuringSetup: true
		input name: "powerMeterHistoryDataMaxCount", type:"number", title: "PowerMeter Graph Data Max Count", description: "0 is max", defaultValue:0, displayDuringSetup: true
		input name: "energyMeterHistoryDataMaxCount", type:"number", title: "EnergyMeter Graph Data Max Count", description: "0 is max", defaultValue:0, displayDuringSetup: true
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

def setExternalAddress(address){
	log.debug "External Address >> ${address}"
	state.externalAddress = address
}

def setStatus(params){
    log.debug "${params.key} >> ${params.data}"
 
 	switch(params.key){
    case "mode":
    	sendEvent(name:"mode", value: params.data )
    	break;
    case "power":
        sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "powerConsumeRate":
    	sendEvent(name:"power", value: params.data )
    	break;
    case "temperature":
    	sendEvent(name:"temperature", value: params.data.replace(" C","").toFloat()/2)
    	break;
    case "current":
    	sendEvent(name:"current", value: params.data )
    	break;
    case "wifiLed":
    	sendEvent(name:"wifiLed", value: params.data )
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def realTimePowerOn(){
	log.debug "setRealTimePowerOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "realTimePower",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def realTimePowerOff(){
	log.debug "setRealTimePowerOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "realTimePower",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def wifiLedOn(){
	log.debug "setWifiLedOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "wifiLed",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def wifiLedOff(){
	log.debug "setWifiLedOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "wifiLed",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeGreen(){
	log.debug "setModeGreen >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "green"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeNormal(){
	log.debug "setModeNormal >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "normal"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	log.debug "Off >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "power",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
     
     	sendEvent(name:"temperature", value: (jsonObj.state.temperature).toFloat()/2)
     	sendEvent(name:"powerMeter", value: jsonObj.state.powerConsumeRate)
     	sendEvent(name:"switch", value: jsonObj.state.power ? "on" : "off")
     	sendEvent(name:"wifiLed", value: jsonObj.state.wifiLed)
     	sendEvent(name:"mode", value: jsonObj.state.mode)
        
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def refresh(){
	log.debug "Refresh"
    def options = [
     	"method": "GET",
        "path": "/devices/get/${state.id}",
        "headers": [
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ]
    ]
    sendCommand(options, callback)
}

def updated() {
}

def sendCommand(options, _callback){
	def myhubAction = new hubitat.device.HubAction(options, null, [callback: _callback])
    sendHubCommand(myhubAction)
}

def makeCommand(body){
	def options = [
     	"method": "POST",
        "path": "/control",
        "headers": [
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
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
        	"name": name
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

private getPictureName(type) {
  def pictureUuid = java.util.UUID.randomUUID().toString().replaceAll('-', '')
  return "image" + "_$pictureUuid" + "_" + type + ".png"
}

def chartTemperature() {
	def url = makeURL("temperature", "Temperature")
    if(settings.temperatureHistoryDataMaxCount > 0){
    	url.query.limit = settings.temperatureHistoryDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "temperature")
    }
}

def chartPowerMeter() {
	def url = makeURL("powerConsumeRate", "Power Meter")
    if(settings.powerMeterHistoryDataMaxCount > 0){
    	url.query.limit = settings.powerMeterHistoryDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "powerMeter")
    }
}

def chartEnergyMeter(){
	def url = makeURL("current", "Energy Meter")
    if(settings.energyMeterHistoryDataMaxCount > 0){
    	url.query.limit = settings.energyMeterHistoryDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "energyMeter")
    }
}

def chartTotalTemperature() {
	def url = makeTotalURL("temperature", "Temperature")
    httpGet(url) { response ->
    	processImage(response, "temperature")
    }
}

def chartTotalPowerMeter() {
	def url = makeTotalURL("powerConsumeRate", "Power Meter")
    httpGet(url) { response ->
    	processImage(response, "powerMeter")
    }
}

def chartTotalEnergyMeter() {
	def url = makeTotalURL("current", "Energy Meter")
    httpGet(url) { response ->
    	processImage(response, "energyMeter")
    }
}
