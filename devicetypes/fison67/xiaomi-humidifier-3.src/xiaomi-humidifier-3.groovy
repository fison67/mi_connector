/**
 *  Xiaomi Humidifier(v.0.0.1)
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
	definition (name: "Xiaomi Humidifier 3", namespace: "fison67", author: "fison67", ocfDeviceType: "oic.d.airpurifier") {
        capability "Switch"						//"on", "off"
        capability "Switch Level"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Refresh"
		capability "Sensor"

         
        attribute "mode", "enum", ["M1", "M2", "M3", "M4", "M5"]
        attribute "buzzer", "enum", ["on", "off"]
        attribute "ledBrightness", "enum", ["off", "dim", "bright"]
        attribute "water2", "enum", ["on", "off"]
        attribute "water", "number"
        attribute "childlock", "enum", ["on", "off"]        
        attribute "lastCheckin", "Date"

//------For Homebridge-----------
        command "humidifier3"
        command "noTemp"
        command "noHumi"
//-------------------------------        
        command "setMode1"
        command "setMode2"
        command "setMode3"
        command "setMode4"
        command "setMode5"
        
        command "buzzerOn"
        command "buzzerOff"
        command "childLockOn"
        command "childLockOff"
        
        command "setBright"
        command "setBrightDim"
        command "setBrightOff"
	}


	simulator {
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
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data )
    	break;
    case "mode":
	state.mode = params.data
    def level = params.data as int
    	sendEvent(name:"mode", value: "M"+params.data)
    	sendEvent(name:"level", value: level*20)        
    	break;
    case "power":
    	if(params.data == "1") {
    	sendEvent(name:"switch", value:"on")
    	sendEvent(name:"mode", value:"M"+state.mode)		
        }
        else if(params.data == "0") {
    		sendEvent(name:"mode", value: "off")
	    	sendEvent(name:"switch", value:"off")
        }
    	break;
    case "temperature":
		def para = "${params.data}"
		String data = para
		def st = data.replace("C","");
		def stf = Float.parseFloat(st)
		def tem = Math.round(stf*10)/10
        sendEvent(name:"temperature", value: tem )
        sendEvent(name:"temperature2", value: "Temp: " + tem )
    	break;
    case "ledBrightness":
        sendEvent(name:"ledBrightness", value: params.data)
    	break;        
    case "targetHumidity":
        sendEvent(name:"level", value: params.data)
    	break;
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "1" ? "on" : "off") )
        break;
    case "childLock":
    	sendEvent(name:"childlock", value: (params.data == "1" ? "on" : "off") )
        break;
    case "water":
    	sendEvent(name:"water2", value: (params.data == "1" ? "on" : "off") )
    	sendEvent(name:"water", value: (params.data == "1" ? 100 : 0) )
        break;
    }
    
    def nowT = new Date().format("HH:mm:ss", location.timeZone)
    def nowD = new Date().format("yyyy-MM-dd", location.timeZone)
    sendEvent(name: "lastCheckin", value: nowD + "\n" + nowT)
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

def setMode1(){
	log.debug "setLevel 1>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 1
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMode2(){
	log.debug "setLevel 2>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 2
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMode3(){
	log.debug "setLevel 3>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 3
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMode4(){
	log.debug "setLevel 4>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 4
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMode5(){
	log.debug "setLevel 5>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 5
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setLevel(level){
	log.debug "setLevel >> ${state.id}"
	def set = Math.ceil(level/20) as int
	log.debug "Math.ceil >> ${set}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": set
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBright(){
	log.debug "setBright >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBrightDim(){
	log.debug "setDim >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "dim"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBrightOff(){
	log.debug "setBrightOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "bright"
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

def on(){
	log.debug "ON >> ${state.id}"
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

def childLockOn(){
	log.debug "childLockOn >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "childLock",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def childLockOff(){
	log.debug "childLockOff >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "childLock",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def updated() {
    refresh()
}

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        if(jsonObj.state.power == 1){
       	sendEvent(name:"switch", value:"on")
    	sendEvent(name:"mode", value:"M"+ jsonObj.state.mode)
        } else {
       	sendEvent(name:"switch", value:"off")
    	sendEvent(name:"mode", value:"off")        
        state.mode = jsonObj.state.mode
        }
        sendEvent(name:"ledBrightness", value: jsonObj.state.ledBrightness)
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer == 1 ? "on" : "off"))
        sendEvent(name:"childlock", value: (jsonObj.state.childLock == 1 ? "on" : "off"))
        sendEvent(name:"water2", value: (jsonObj.state.water == 1 ? "on" : "off"))
        sendEvent(name:"water", value: (jsonObj.state.water == 1 ? 100 : 0))
	    
    def nowT = new Date().format("HH:mm:ss", location.timeZone)
    def nowD = new Date().format("yyyy-MM-dd", location.timeZone)
    sendEvent(name: "lastCheckin", value: nowD + "\n" + nowT)
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
