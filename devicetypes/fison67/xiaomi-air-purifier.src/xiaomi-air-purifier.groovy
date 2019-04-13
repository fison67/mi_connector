/**
 *  Xiaomi Air Purifier (v.0.0.4)
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
	definition (name: "Xiaomi Air Purifier", namespace: "fison67", author: "fison67") {
        capability "Switch"						
        capability "SwitchLevel"
		capability "TemperatureMeasurement"
        capability "RelativeHumidityMeasurement"	
	//	capability "FilterStatus"
	//	capability "FanControl"
		capability "Refresh"
		
		attribute "buzzer", "enum", ["on", "off"]        
        attribute "ledBrightness", "enum", ["bright", "dim", "off"]        
        attribute "f1_hour_used", "number"
        attribute "filter1_life", "number"
        attribute "average_aqi", "number"
        attribute "mode", "enum", ["auto", "silent", "favorite", "low", "medium", "high", "strong"]      
		attribute "fineDustLevel", "number" // PM 2.5  
        attribute "dustLevel", "number" // PM 10
        attribute "lastCheckin", "Date"
		
        command "setSpeed"
        command "setStatus"
        command "setModeAuto"
        command "setModeMedium"
        command "setModeLow"
        command "setModeHigh"
        command "setModeStrong"
        command "setModeSilent"
        command "setModeFavorite"
        command "setModeIdle"
        
        command "buzzerOn"
        command "buzzerOff"
        
        command "ledOn"
        command "ledOff"
        
        command "setBright"
        command "setBrightDim"
        command "setBrightOff"
        
        command "resetFilter"
	}
	
	preferences {
		input name:"model", type:"enum", title:"Select Model", options:["MiAirPurifier", "MiAirPurifier2", "MiAirPurifierPro", "MiAirPurifier2S"], description:"Select Your Airpurifier Model"
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

def setStatus(params){
    log.debug "${params.key} : ${params.data}"
 
 	switch(params.key){
    case "mode":
    	if(params.data == "idle") {
        } else {
        	state.lastMode = params.data
        	sendEvent(name:"mode", value: params.data )
        }
    	break;
    case "pm2.5":
    	sendEvent(name:"fineDustLevel", value: params.data as int)
    	break;
    case "aqi":
    	sendEvent(name:"fineDustLevel", value: params.data as int)
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data as int)
    	break;
    case "power":
    	if(params.data == "true") {
    		sendEvent(name:"switch", value:"on")
            sendEvent(name:"mode", value: state.lastMode)
        } else if(params.data == "false") {
            sendEvent(name:"mode", value: "off")
            sendEvent(name:"switch", value:"off")
        }
    	break;
    case "temperature":
		def st = params.data.replace("C","");
		def stf = Float.parseFloat(st)
		def tem = Math.round(stf*10)/10
	    if(model == "MiAirPurifier"){
    	} else {
        	sendEvent(name:"temperature", value: tem as int)
        }
    	break;
    case "buzzer":
        sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "ledBrightness":
        sendEvent(name:"ledBrightness", value: params.data)
    	break;
    case "speed":
		def stf = Float.parseFloat(params.data)
        sendEvent(name:"fanSpeed", value: Math.round(stf*625/100) )
    	break;
    case "led":
        sendEvent(name:"ledBrightness", value: (params.data == "true" ? "bright" : "off"))
    	break;
    case "f1_hour_used":
		def stf = Float.parseFloat(params.data)
    	sendEvent(name:"f1_hour_used", value: Math.round(stf/24)     )
        break;
    case "filter1_life":
		def stf = Float.parseFloat(params.data)
    	sendEvent(name:"filter1_life", value: Math.round(stf*1.45)  )
    	break;
    case "average_aqi":
    	sendEvent(name:"airQuality", value: params.data )
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
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
def setFanSpeed(level){
	def speed = Math.round(level/625*100)    
	log.debug "setSpeed >> ${state.id}, speed=" + speed
    if(model == "MiAirPurifier"){
    }else {
		def body = [
			"id": state.id,
			"cmd": "speed",
			"data": speed
		]
		def options = makeCommand(body)
		sendCommand(options, null)
		sendEvent(name: "level", value: speed)
	}
}
def setModeAuto(){
	log.debug "setModeAuto >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "auto"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeSilent(){
    log.debug "setModeSilent >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "silent"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeFavorite(){
	log.debug "setModeFavorite >> ${state.id}"
    if(model == "MiAirPurifier"){
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "low"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }
    else {
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "favorite"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }
}

def setModeLow(){
    log.debug "setModeSilent >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "low"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeMedium(){
    log.debug "setModeSilent >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "medium"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeHigh(){
    log.debug "setModeHigh >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "high"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeStrong(){
	log.debug "setModeStrong >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "strong"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def buzzerOn(){
	log.debug "buzzerOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def buzzerOff(){
	log.debug "buzzerOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def ledOn(){
	log.debug "ledOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def ledOff(){
	log.debug "ledOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def resetFilter(){
	log.debug "resetFilter >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "resetFilter",
        "data": ""
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBright(){
	log.debug "setBright >> ${state.id}"
    if(model == "MiAirPurifier"){
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }
    else {
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "bright"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }    
}

def setBrightDim(){
	log.debug "setDim >> ${state.id}"
        if(model == "MiAirPurifier"){
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }
    else {
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "brightDim"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }    
}

def setBrightOff(){
	log.debug "setBrightOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	log.debug "On >> ${state.id}"
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


def updated() {
    refresh()
}

def setLanguage(language){
	state.language = language
}

def setExternalAddress(address){
	state.externalAddress = address
}

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
    if(model == "MiAirPurifier"){
		if(jsonObj.properties.aqi != null && jsonObj.properties.aqi != ""){
			sendEvent(name:"fineDustLevel", value: jsonObj.properties.aqi)
		}
		if(jsonObj.properties.pm25 != null && jsonObj.properties.pm25 != ""){
			sendEvent(name:"fineDustLevel", value: jsonObj.properties.aqi)
		}
     } else {
	    sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity )
    	sendEvent(name:"temperature", value: jsonObj.properties.temperature.value  )
	        if(jsonObj.properties.aqi != null && jsonObj.properties.aqi != ""){
        		sendEvent(name:"fineDustLevel", value: jsonObj.properties.aqi)
        	}
        	if(jsonObj.properties.averageAqi != null && jsonObj.properties.averageAqi != ""){
        		sendEvent(name:"airQuality", value: jsonObj.properties.averageAqi)
        	}
        }
		if(jsonObj.properties.power == true){
			sendEvent(name:"mode", value: jsonObj.state.mode)
			sendEvent(name:"switch", value: "on" )
		} else {
			sendEvent(name:"mode", value: "off" )
			sendEvent(name:"switch", value: "off" )
		}
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer == true ? "on" : "off"))
        
        if(jsonObj.state.filterLifeRemaining != null && jsonObj.state.filterLifeRemaining != ""){
    		sendEvent(name:"filter1_life", value: Math.round(jsonObj.state.filterLifeRemaining*1.45) )    
        }
        if(jsonObj.state.filterHoursUsed != null && jsonObj.state.filterHoursUsed != ""){
    		sendEvent(name:"f1_hour_used", value: Math.round(jsonObj.state.filterHoursUsed/24) )
        }
        if(jsonObj.properties.ledBrightness != null && jsonObj.properties.ledBrightness != ""){
        	sendEvent(name:"ledBrightness", value: jsonObj.properties.ledBrightness)
        }
        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        sendEvent(name: "lastCheckin", value: now)

    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
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
