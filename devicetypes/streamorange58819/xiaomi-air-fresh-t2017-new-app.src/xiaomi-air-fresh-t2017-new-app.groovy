/**
 *  Xiaomi Air Fresh T2017 New App(v.0.0.1)
 *
 * MIT License
 *
 * Copyright (c) 2021 fison67@nate.com
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
	definition (name: "Xiaomi Air Fresh T2017 New App", namespace: "streamorange58819", author: "fison67", mnmn:"fison67", vid:"889c5d30-b10d-33a5-bbd7-5f978fcde5c8", ocfDeviceType: "oic.d.airpurifier") {
		capability "Switch"						
        capability "Temperature Measurement"
        capability "Carbon Dioxide Measurement"
		capability "Dust Sensor"
		capability "Fan Speed"
		capability "Refresh"
		capability "streamorange58819.pmode"
		capability "streamorange58819.heater"
		capability "streamorange58819.heaterlevel"
		capability "streamorange58819.heaterstatus"
        capability "streamorange58819.led"
        capability "streamorange58819.buzzer"
		capability "streamorange58819.childLock"
	}
}

// parse events into attributes
def parse(String description) {}

def installed(){
    sendEvent(name: "dustLevel", value: null)
    sendEvent(name:"supportedPmodes", value: ["auto", "sleep", "favorite"])
}

def setInfo(String app_url, String id) {
	state.app_url = app_url
    state.id = id
}

def setStatus(params){
    log.debug "${params.key} : ${params.data}"
 
 	switch(params.key){
    case "power":
        sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "pm2.5":
        sendEvent(name:"fineDustLevel", value: params.data as int, unit:"\u03bcg/m^3")
    	break;
    case "temperature":
        sendEvent(name:"temperature", value: "${params.data}".replace("C","") as float, unit: "C")
    	break;    
    case "led":
        sendEvent(name:"led", value: (params.data == "true" ? "on" : "off") )
    	break;        
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off") )
        break;
    case "childLock":
        sendEvent(name:"childlock", value: (params.data == "true" ? "locked" : "unlocked"))
        break;
    case "carbonDioxide":
    	sendEvent(name:"carbonDioxide", value: params.data as int, unit: "ppm")
    	break
    case "mode":
        sendEvent(name:"pmode", value: params.data == "favourite" ? "favorite" : params.data)
        break
    case "favoriteLevel":
        sendEvent(name:"fanSpeed", value: getSTFanSpeed(params.data as int))
        break
    case "heater":
        sendEvent(name:"heater", value: (params.data == "true" ? "on" : "off"))
        break
    case "heaterLevel":
        sendEvent(name:"heaterlevel", value: getSTHeaterLevel(params.data))
        break
    case "heaterStatus":
        sendEvent(name:"heaterstatus", value: (params.data == "true" ? "on" : "off"))
        break
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

def on(){ sendCommand(makePayload("power", "on"), null) }
def off(){ sendCommand(makePayload("power", "off"), null) }

def setChildLock(locked){
	if(locked == "locked"){
    	childLockOn()
    }else{
    	childLockOff()
    }
}

def childLockOn(){ sendCommand(makePayload("childLock", "on"), null) }
def childLockOff(){ sendCommand(makePayload("childLock", "off"), null) }

def setBuzzer(power){
	if(power == "on"){
    	buzzerOn()
    }else{
    	buzzerOff()
    }
}

def buzzerOn(){ sendCommand(makePayload("buzzer", "on"), null) }
def buzzerOff(){ sendCommand(makePayload("buzzer", "off"), null) }

def setLed(power){
	if(power == "on"){
    	ledOn()
    }else{
    	ledOff()
    }
}

def ledOn(){ sendCommand(makePayload("led", "on"), null) }
def ledOff(){ sendCommand(makePayload("led", "off"), null) }

def setHeater(power){
	if(power == "on"){
    	heaterOn()
    }else{
    	heaterOff()
    }
}

def heaterOn(){ sendCommand(makePayload("heater", "on"), null) }
def heaterOff(){ sendCommand(makePayload("heater", "off"), null) }

def setHeaterLevel(level){ sendCommand(makePayload("heaterLevel", getHeaterLevelValue(level)), null) }

def setAirPurifierMode(mode){ sendCommand(makePayload("changeMode", mode), null) }

def setFanSpeed(speed){ sendCommand(makePayload("favoriteLevel", getFanSpeed(speed)), null) }

def getFanSpeed(speed){
	if(speed == 0){
    	return 60
    }else if(speed == 1){
    	return 120
    }else if(speed == 2){
    	return 180
    }else if(speed == 3){
    	return 240
    }else if(speed == 4){
    	return 300
    }
}

def getSTHeaterLevel(level){
	if(level == "low"){
    	return 1
    }else if(level == "medium"){
    	return 2
    }else if(level == "high"){
    	return 3
    }
}

def getHeaterLevelValue(level){
	if(level == 1){
    	return "low"
    }else if(level == 2){
    	return "medium"
    }else if(level == 3){
    	return "high"
    }
}

def getSTFanSpeed(speed){
	if(60 == speed){
    	return 0
    }else if(60 < speed && speed <= 120){
    	return 1
    }else if(120 < speed && speed <= 180){
    	return 2
    }else if(180 < speed && speed <= 240){
    	return 3
    }else if(240 < speed && speed <= 300){
    	return 4
    }
}

def updated() {
    refresh()
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
    	
        sendEvent(name:"temperature", value: jsonObj.properties.temperature.value as int)
        
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def makePayload(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
    return makeCommand(body)
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
