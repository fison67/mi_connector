/**
 *  Xiaomi Sensor Temperature & Humidity (v.0.0.2)
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
import java.text.DateFormat

@Field 
LANGUAGE_MAP = [
	"atmospheric_pressure":[
    	"Korean": "기압",
        "English": "Atmospheric Pressure"
    ],
    "temperature": [
        "Korean": "온도",
        "English": "Temperature"
    ],
    "humidity": [
        "Korean": "습도",
        "English": "Humidity"
    ],
    "battery": [
    	"Korean": "배터리",
        "English": "Battery"
    ],
    "todays": [
    	"Korean": "오늘",
        "English": "Today's"
    ],
    "high": [
    	"Korean": "최고",
        "English": "High"
    ],
    "low": [
    	"Korean": "최저",
        "English": "Low"
    ]
]

metadata {
	definition (name: "xiaomi weather", namespace: "fison67", author: "fison67") {
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Sensor"
        capability "Battery"
        capability "Refresh"
         
        attribute "pressure", "string"
		attribute "maxTemp", "number"
		attribute "minTemp", "number"
		attribute "maxHumidity", "number"
		attribute "minHumidity", "number"
		attribute "multiAttributesReport", "String"
		attribute "currentDay", "String"

        attribute "lastCheckin", "Date"
		attribute "lastCheckinDate", "String"

        attribute "chartMode", "string"
        
        command "setLanguage" 
        command "chartTemperature"
        command "chartHumidity"
        command "chartPressure"
        command "chartTotalTemperature"
        command "chartTotalHumidity"
        command "chartTotalPressure"
	}

	simulator {}
    
	preferences {
		input name: "selectedLang", title:"Select a language" , type: "enum", required: true, options: ["English", "Korean"], defaultValue: "English", description:"Language for DTH"
		input name: "temperatureType", title:"Select a type" , type: "enum", required: true, options: ["C", "F"], defaultValue: "C"
        
		input name: "displayTempHighLow", type: "bool", title: "Display high/low temperature?"
		input name: "displayHumidHighLow", type: "bool", title: "Display high/low humidity?"
        
        input name: "totalChartType", title:"Total-Chart Type" , type: "enum", required: true, options: ["line", "bar"], defaultValue: "line", description:"Total Chart Type [ line, bar ]" 
        input name: "historyDayCount", type:"number", title: "Maximum days for single graph", required: true, description: "", defaultValue:1, displayDuringSetup: true
        input name: "historyTotalDayCount", type:"number", title: "Maximum days for total graph", required: true, description: "", defaultValue:7, range: "2..31", displayDuringSetup: true

	}


	tiles(scale: 2) {
        multiAttributeTile(name:"temperature", type:"generic", width:6, height:4) {
            tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState("temperature", label:'${currentValue}°',
                    backgroundColors:[
                        // Fahrenheit color set
                        [value: 0, color: "#153591"],
                        [value: 5, color: "#1e9cbb"],
                        [value: 10, color: "#90d2a7"],
                        [value: 15, color: "#44b621"],
                        [value: 20, color: "#f1d801"],
                        [value: 25, color: "#d04e00"],
                        [value: 30, color: "#bc2323"],
                        [value: 44, color: "#1e9cbb"],
                        [value: 59, color: "#90d2a7"],
                        [value: 74, color: "#44b621"],
                        [value: 84, color: "#f1d801"],
                        [value: 95, color: "#d04e00"],
                        [value: 96, color: "#bc2323"]
						// Celsius color set (to switch, delete the 13 lines above anmd remove the two slashes at the beginning of the line below)
                        //[value: 0, color: "#153591"], [value: 7, color: "#1e9cbb"], [value: 15, color: "#90d2a7"], [value: 23, color: "#44b621"], [value: 28, color: "#f1d801"], [value: 35, color: "#d04e00"], [value: 37, color: "#bc2323"]
                    ]
                )
            }
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}\n')
            }
            tileAttribute("device.multiAttributesReport", key: "SECONDARY_CONTROL") {
                attributeState("multiAttributesReport", label:'\n${currentValue}' //icon:"st.Weather.weather12",
                ) }
        }        
        valueTile("temperature2", "device.temperature", inactiveLabel: false) {
            state "temperature", label:'${currentValue}°', icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNzkg/MDAxNTIyNjcwOTc4NTIy.9VGDZZ4ieBY5jCJ0tvO8L5HFKbkvnms3ymk62HL4rzMg.HYTGtieTVMLE421M8lF8WE1THRgdyFfb1GG39OhtrU4g.PNG.shin4299/temp.png?type=w3",
            backgroundColors:[
                // Fahrenheit color set
                [value: 0, color: "#153591"],
                [value: 5, color: "#1e9cbb"],
                [value: 10, color: "#90d2a7"],
                [value: 15, color: "#44b621"],
                [value: 20, color: "#f1d801"],
                [value: 25, color: "#d04e00"],
                [value: 30, color: "#bc2323"],
                [value: 44, color: "#1e9cbb"],
                [value: 59, color: "#90d2a7"],
                [value: 74, color: "#44b621"],
                [value: 84, color: "#f1d801"],
                [value: 95, color: "#d04e00"],
                [value: 96, color: "#bc2323"]
                // Celsius color set (to switch, delete the 13 lines above anmd remove the two slashes at the beginning of the line below)
                //[value: 0, color: "#153591"], [value: 7, color: "#1e9cbb"], [value: 15, color: "#90d2a7"], [value: 23, color: "#44b621"], [value: 28, color: "#f1d801"], [value: 35, color: "#d04e00"], [value: 37, color: "#bc2323"]
            ]
        }
        
        valueTile("humidity", "device.humidity", width: 2, height: 2, unit: "%") {
            state("val", label:'${currentValue}%', defaultState: true, 
            	backgroundColors:[
                    [value: 10, color: "#153591"],
                    [value: 30, color: "#1e9cbb"],
                    [value: 40, color: "#90d2a7"],
                    [value: 50, color: "#44b621"],
                    [value: 60, color: "#f1d801"],
                    [value: 80, color: "#d04e00"],
                    [value: 90, color: "#bc2323"]
                ]
            )
        }
        
        
        valueTile("pressure", "device.pressure", width: 2, height: 2, unit: "") {
            state("val", label:'${currentValue} kpa', defaultState: true, 
            	backgroundColors:[
                    [value: 10, color: "#153591"],
                    [value: 30, color: "#1e9cbb"],
                    [value: 40, color: "#90d2a7"],
                    [value: 50, color: "#44b621"],
                    [value: 60, color: "#f1d801"],
                    [value: 80, color: "#d04e00"],
                    [value: 90, color: "#bc2323"]
                ]
            )
        }
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state "val", label:'${currentValue}%', defaultState: true
        }		
        valueTile("pre", "device.pre", decoration: "flat", inactiveLabel: false, width: 2, height: 1) {
            state("val", label:'${currentValue}', defaultState: true)
        }
        valueTile("humi", "device.humi", decoration: "flat", inactiveLabel: false, width: 2, height: 1) {
            state("val", label:'${currentValue}', defaultState: true)
        }
        
		valueTile("bat", "device.bat", decoration: "flat", inactiveLabel: false, width: 2, height: 1) {
            state("val", label:'${currentValue}', defaultState: true)
        }
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
        
        carouselTile("history", "device.image", width: 6, height: 4) { }
	
    	standardTile("chartMode", "device.chartMode", width: 2, height: 2, decoration: "flat") {
			state "temperature", label:'Temperature', nextState: "humidity", action: 'chartTemperature'
			state "humidity", label:'Humidity', nextState: "pressure", action: 'chartHumidity'
			state "pressure", label:'Pressure', nextState: "totalTemperature", action: 'chartPressure'
			state "totalTemperature", label:'T-Temperature', nextState: "totalHumidity", action: 'chartTotalTemperature'
			state "totalHumidity", label:'T-Humidity', nextState: "totalPressure", action: 'chartTotalHumidity'
			state "totalPressure", label:'T-Pressure', nextState: "temperature", action: 'chartTotalPressure'
		}

        main("temperature2")
        details(["temperature", "humi", "pre", "bat", "humidity", "pressure", "battery",
            "refresh", "chartMode", "history"
		])
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
    log.debug "${params.key} : ${params.data}"
 //    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
//    sendEvent(name: "lastCheckin", value: now)
//    def now = formatDate()
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)

	// Any report - temp, humidity, pressure, & battery - results in a lastCheckin event and update to Last Checkin tile
	// However, only a non-parseable report results in lastCheckin being displayed in events log
    sendEvent(name: "lastCheckin", value: now, displayed: false)

	// Check if the min/max temp and min/max humidity should be reset
    
 	switch(params.key){
    case "relativeHumidity":
		def para = "${params.data}"
		String data = para
		def stf = Float.parseFloat(data)
		def humidity = Math.round(stf)
    	sendEvent(name:"humidity", value: humidity )
        updateMinMaxHumidity(humidity)
    	break;
    case "temperature":
		def para = "${params.data}"
		String data = para
		def st = data.replace("C","");
		def stf = Float.parseFloat(st)
		def tem = Math.round(stf*10)/10
        sendEvent(name:"temperature", value: makeTemperature(tem) )
        updateMinMaxTemps(tem)
    	break;
    case "atmosphericPressure":
    	sendEvent(name:"pressure", value: params.data.replace(" Pa","").replace(",","").toInteger()/1000 )
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data )
    	break;	
    }
    checkNewDay()
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

def setLanguage(language){
    log.debug "Languge >> ${language}"
	state.language = language
    
    sendEvent(name:"pre", value: LANGUAGE_MAP["atmospheric_pressure"][language] )
    sendEvent(name:"humi", value: LANGUAGE_MAP["humidity"][language] )
    sendEvent(name:"bat", value: LANGUAGE_MAP["battery"][language] )
    
    refreshMultiAttributes()
}

def getNameLang(name){
	return LANGUAGE_MAP[name][state.language]
}

def checkNewDay() {
	def now = new Date().format("yyyy-MM-dd", location.timeZone)
	if(state.prvDate == null){
		state.prvDate = now
	}else{
		if(state.prvDate != now){
			state.prvDate = now
            log.debug "checkNewDay _ ${state.prvDate}"
			resetMinMax()            
		}
	}
}

def resetMinMax() {
	def day = new Date().format("EEE", location.timeZone)
	log.debug "resetMinMax day_ ${day}"
	def currentMaxTemp = device.currentValue('maxTemp')
	def currentMinTemp = device.currentValue('minTemp')
	def currentMaxHumi = device.currentValue('maxHumidity')
	def currentMinHumi = device.currentValue('minHumidity')	
	log.debug "resetMinMax day_ ${day}_xT${currentMaxTemp}_nT${currentMinTemp}_xH${currentMaxHumi}_nH${currentMinHumi}"
	
	def currentTemp = device.currentValue('temperature')
	def currentHumidity = device.currentValue('humidity')
    currentTemp = currentTemp ? (int) currentTemp : currentTemp
	log.debug "${device.displayName}: Resetting daily min/max values to current temperature of ${currentTemp}° and humidity of ${currentHumidity}%"
    sendEvent(name: "maxTemp", value: currentTemp, displayed: false)
    sendEvent(name: "minTemp", value: currentTemp, displayed: false)
    sendEvent(name: "maxHumidity", value: currentHumidity, displayed: false)
    sendEvent(name: "minHumidity", value: currentHumidity, displayed: false)
    refreshMultiAttributes()
}

// Check new min or max temp for the day
def updateMinMaxTemps(temp) {
	temp = temp ? (int) temp : temp
	if ((temp > device.currentValue('maxTemp')) || (device.currentValue('maxTemp') == null))
		sendEvent(name: "maxTemp", value: temp, displayed: false)	
	if ((temp < device.currentValue('minTemp')) || (device.currentValue('minTemp') == null))
		sendEvent(name: "minTemp", value: temp, displayed: false)
	refreshMultiAttributes()
}

// Check new min or max humidity for the day
def updateMinMaxHumidity(humidity) {
	if ((humidity > device.currentValue('maxHumidity')) || (device.currentValue('maxHumidity') == null))
		sendEvent(name: "maxHumidity", value: humidity, displayed: false)
	if ((humidity < device.currentValue('minHumidity')) || (device.currentValue('minHumidity') == null))
		sendEvent(name: "minHumidity", value: humidity, displayed: false)
	refreshMultiAttributes()
}

// Update display of multiattributes in main tile
def refreshMultiAttributes() {
	def day = new Date().format("EEE", location.timeZone)		
    
	def temphiloAttributes = displayTempHighLow ? (displayHumidHighLow ? getWordByLang("todays") + " " + getWordByLang("high") + "/" + getWordByLang("low") + ":  ${device.currentState('maxTemp')?.value}° / ${device.currentState('minTemp')?.value}°" : getWordByLang("todays") + " " + getWordByLang("high") + ": ${device.currentState('maxTemp')?.value}°  /  " + getWordByLang("low") + ": ${device.currentState('minTemp')?.value}°") : ""
	def humidhiloAttributes = displayHumidHighLow ? (displayTempHighLow ? "    ${device.currentState('maxHumidity')?.value}% / ${device.currentState('minHumidity')?.value}%" : getWordByLang("todays") + " " + getWordByLang("high") + ": ${device.currentState('maxHumidity')?.value}%  /  " + getWordByLang("low") + ": ${device.currentState('minHumidity')?.value}%") : ""
	sendEvent(name: "multiAttributesReport", value: "${temphiloAttributes}${humidhiloAttributes}", displayed: false)
   	
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

def take(){
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

def sendCommand(options, _callback){
	def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: _callback])
    sendHubCommand(myhubAction)
}

private getPictureName(type) {
  def pictureUuid = java.util.UUID.randomUUID().toString().replaceAll('-', '')
  return "image" + "_$pictureUuid" + "_" + type + ".png"
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
 		sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        sendEvent(name:"temperature", value: makeTemperature(jsonObj.properties.temperature.value))
        sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity)
        sendEvent(name:"pressure", value: jsonObj.properties.atmosphericPressure.value / 1000)
        
        updateLastTime()
        checkNewDay()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def getWordByLang(id){
	return LANGUAGE_MAP[id][state.language]
}

def chartTotalTemperature() {
    httpGet(makeTotalURL("temperature", "Temperature")) { response ->
    	processImage(response, "temperature")
    }
}

def chartTotalHumidity() {
    httpGet(makeTotalURL("relativeHumidity", "Humidity")) { response ->
    	processImage(response, "humidity")
    }
}

def chartTotalPressure(){
    httpGet(makeTotalURL("atmosphericPressure", "Pressure")) { response ->
    	processImage(response, "pressure")
    }
}

def chartTemperature() {
    httpGet(makeURL("temperature", "Temperature")) { response ->
    	processImage(response, "temperature")
    }
}

def chartHumidity() {
    httpGet(makeURL("relativeHumidity", "Humidity")) { response ->
    	processImage(response, "humidity")
    }
}

def chartPressure(){
    httpGet(makeURL("atmosphericPressure", "Pressure")) { response ->
    	processImage(response, "pressure")
    }
}
