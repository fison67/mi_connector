/**
 *  Xiaomi Air Purifier (v.0.0.9)
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

metadata {
	definition (name: "Xiaomi Air Purifier1 New App", namespace: "streamorange58819", author: "fison67", mnmn:"fison67", vid:"42f0f9b4-0933-33c5-84aa-10ab770bc021", ocfDeviceType:"oic.d.airpurifier") {
        capability "Switch"						
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Filter Status"
		capability "Fan Speed"
		capability "streamorange58819.pmode"
        capability "streamorange58819.buzzer"
		capability "streamorange58819.led"
		capability "Refresh"
		capability "Sensor"
        capability "Fine Dust Sensor"
         
        attribute "buzzer", "enum", ["on", "off"]        
        attribute "f1_hour_used", "number"
        attribute "filter1_life", "number"
        attribute "average_aqi", "number"
        attribute "mode", "enum", ["auto", "silent", "favorite", "low", "medium", "high", "strong"]        
        
        command "setLevel", ["number"]
        
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

	simulator {}
	preferences {}
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

def setModel(model){
	state.model = model
	refresh()
}

def setStatus(params){
    log.debug "${params.key} : ${params.data}"
 
 	switch(params.key){
    case "mode":
        sendEvent(name:"pmode", value: params.data )
    	break;
    case "pm2.5":
    	sendEvent(name:"fineDustLevel", value: params.data as Integer, unit: "㎍/㎥")
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data, unit:"%")
    	break;
    case "power":
    	sendEvent(name:"switch", value: params.data == "true" ? "on" : "off")
    	break;
    case "temperature":
        sendEvent(name:"temperature", value: Math.round(Float.parseFloat(params.data.replace("C",""))*10)/10, unit: "C" )
    	break;
    case "buzzer":
        sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "favoriteLevel":
		def stf = Float.parseFloat(params.data)
        def level = Math.round(stf*6.25)   
	    if(state.model == "zhimi.airpurifier.v3"){
        	level = Math.round(stf*7.14)
        }
        state.lastLevel = level
        sendEvent(name:"fanSpeed", value: calFanSpeed(level))
    	break;
    case "led":
        sendEvent(name:"led", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "f1_hour_used":
		def stf = Float.parseFloat(params.data)
		def use = Math.round(stf/24)    
    	sendEvent(name:"f1_hour_used", value: state.usage + " " + use + state.day )
        break;
    case "filter1_life":
		def stf = Float.parseFloat(params.data)
		def life = Math.round(stf*1.45)    
    	sendEvent(name:"filter1_life", value: state.remain + " " + life + state.day )
        sendEvent(name:"filterStatus", value: life == 0 ? "replace" : "normal")
    	break;
    case "average_aqi":
    	sendEvent(name:"average_aqi", value: params.data as Integer)
    	break;
    }
}

def refresh(){
    def options = [
     	"method": "GET",
        "path": "/devices/get/${state.id}",
        "headers": [
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ]
    ]
    sendCommand(options, callback)
}

def calFanSpeed(level){
	if(level == 0){
    	return 0
	}else if(0 < level && level <= 25){
    	return 1
    }else if(25 < level && level <= 50){
    	return 2
    }else if(50 < level && level <= 75){
    	return 3
    }else if(75 < level && level <= 100){
    	return 4
    }
}

def calFanLevel(speed){
	if(speed == 0){
    	return 0
    }else if(speed == 1){
    	return 25
    }else if(speed == 2){
    	return 50
    }else if(speed == 3){
    	return 75
    }else if(speed == 4){
    	return 100
    }
}

def setFanSpeed(level){
	def speed = calFanLevel(level)
    if(level > 0){
    	setLevel(speed)
    }else{
    	setModeAuto()
    }
}

def setLevel(level){
	def speed = Math.round(level/6.25)   
    if(model == "MiAirPurifier3"){
        speed = Math.round(level/7.14)
    }
    sendCommandData("speed", speed)
}

def on(){ sendCommandData("power", "on") }
def off(){ sendCommandData("power", "off") }

def setAirPurifierMode(mode){ sendCommandData("mode", mode) }

def setBuzzer(power){ sendCommandData("buzzer", power) }
def buzzerOn(){ sendCommandData("buzzer", "on") }
def buzzerOff(){ sendCommandData("buzzer", "off") }

def setLed(power){ 
	if(power == "on"){
    	ledOn()
    }else{
    	ledOff()
    }
}
def ledOn(){ sendCommandData("ledBrightness", "bright") }
def ledOff(){ sendCommandData("ledBrightness", "off") }

def resetFilter(){
    sendCommandData("resetFilter", "")
}

def sendCommandData(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
    sendCommand(makeCommand(body), null)
}

def installed(){
	sendEvent(name:"switch" , value:"off")
    sendEvent(name:"airPurifierFanMode", value: "idle" )
}

def updated() {}

def setExternalAddress(address){
	state.externalAddress = address
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
        if(jsonObj.properties["relativeHumidity"] != null){
         	sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity, unit: "%")
        }
        
        if(jsonObj.properties["temperature"] != null){
            sendEvent(name:"temperature", value: jsonObj.properties.temperature.value, unit: "C" )
        }
        if(jsonObj.state.aqi != null && jsonObj.state.aqi != ""){
            sendEvent(name:"airQuality", value: jsonObj.state.aqi as Integer)
        }
        if(jsonObj.state.averageAqi != null && jsonObj.state.averageAqi != ""){
            sendEvent(name:"average_aqi", value: jsonObj.state.averageAqi)
        }
        if(jsonObj.properties["pm2.5"] != null && jsonObj.properties["pm2.5"] != ""){
            sendEvent(name:"fineDustLevel", value: jsonObj.properties["pm2.5"] as Integer, unit:"㎍/㎥")
        }
        if(jsonObj.properties["mode"] != null && jsonObj.properties["mode"] != ""){
            sendEvent(name:"pmode", value: jsonObj.properties["mode"])
        }
        
		if(jsonObj.properties.favoriteLevel != null && jsonObj.properties.favoriteLevel != ""){
        	def level = Math.round(jsonObj.properties.favoriteLevel*6.25)   
            if(model == "zhimi.airpurifier.v3"){
                level = Math.round(jsonObj.properties.favoriteLevel*7.14)
            }
//            sendEvent(name:"level", value: level)
            sendEvent(name:"fanSpeed", value: calFanSpeed(level))
        }
        if(jsonObj.properties.modes != null){
        	def modes = jsonObj.properties.modes
            def results = []
            for(def z=0; z<modes.size(); z++){
            	results.push(modes[z].id)
            }
            sendEvent(name:"supportedPmodes", value: results)
        }
        
		sendEvent(name:"led", 	 value: jsonObj.properties.led == true ? "on" : "off")
		sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
        sendEvent(name:"buzzer", value: jsonObj.state.buzzer == true ? "on" : "off")
        
        if(jsonObj.state.filterLifeRemaining != null && jsonObj.state.filterLifeRemaining != ""){
        	def life = Math.round(jsonObj.state.filterLifeRemaining*1.45) 
//    		sendEvent(name:"filter1_life", value: state.remain + " " + life + state.day )    
        	sendEvent(name:"filterStatus", value: life == 0 ? "replace" : "normal")
        }
        if(jsonObj.state.filterHoursUsed != null && jsonObj.state.filterHoursUsed != ""){
//    		sendEvent(name:"f1_hour_used", value: state.usage + " " + Math.round(jsonObj.state.filterHoursUsed/24) + state.day )
        }
        if(jsonObj.properties.ledBrightness != null && jsonObj.properties.ledBrightness != ""){
        	sendEvent(name:"ledBrightness", value: jsonObj.properties.ledBrightness)
        }
        
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def sendCommand(options, _callback){
	def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: _callback])
    sendHubCommand(myhubAction)
}

def makeCommand(body){
	def options = [
     	"method": "POST",
        "path": "/control",
        "headers": [
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}
