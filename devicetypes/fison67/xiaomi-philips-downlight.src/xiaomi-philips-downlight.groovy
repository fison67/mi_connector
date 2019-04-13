/**
 *  Xiaomi Philips Downlight (v.0.0.1)
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
	definition (name: "Xiaomi Philips Downlight", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Light"
        capability "Refresh"
		capability "ColorTemperature"
        capability "Switch Level"
        
        attribute "lastOn", "string"
        attribute "lastOff", "string"
        attribute "lastCheckin", "Date"
         
        command "setScene1"
        command "setScene2"
        command "setScene3"
        command "setScene4"
	}

	preferences {
		input name:	"smooth", type:"enum", title:"Select", options:["On", "Off"], description:"", defaultValue: "On"
        input name: "duration", title:"Duration" , type: "number", required: false, defaultValue: 500, description:""
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
	log.debug "${params.key} >> ${params.data}"
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
 	switch(params.key){
    case "color":
    	sendEvent(name:"color", value: params.data )
    	break;
    case "power":
        if(params.data == "true"){
            sendEvent(name:"switch", value: "on")
            sendEvent(name:"lastOn", value: now, displayed: false)
        } else {
            sendEvent(name:"switch", value: "off")
            sendEvent(name:"lastOff", value: now, displayed: false)
        }
    	break;
    case "brightness":
    	sendEvent(name:"level", value: params.data )
    	break;
    case "scene":
        sendEvent(name:"modeName", value: getModeName(params.data as int) )
    	break;
    }
    sendEvent(name: "lastCheckin", value: now, displayed: false)
}

def getModeName(val){
	def name
	switch(val){
    case 0:
    	name = "Custom"
        break;
    case 1:
		name = "Bright"
    	break;
	case 2:
		name = "TV"
		break;
	case 3:
		name = "Warm"
        break;
    case 4:
		name = "Midnight"
        break;
    }
	return name
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

def setLevel(brightness){
	log.debug "setBrightness >> ${state.id}, val=${brightness}"
    def body = [
        "id": state.id,
        "cmd": "brightness",
        "data": brightness,
        "subData": getDuration()
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setColorTemperature(colortemperature){
    def body = [
        "id": state.id,
        "cmd": "color",
        "data": colortemperature + "K",
        "subData": getDuration()
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    
    setPowerByStatus(true)	
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

def setScene1(){
	log.debug "setScene1 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 1
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene2(){
	log.debug "setScene2 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 2
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene3(){
	log.debug "setScene3 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 3
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene4(){
	log.debug "setScene4 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 4
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}


def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
		
        sendEvent(name:"color", value: jsonObj.properties.color)
        sendEvent(name:"level", value: jsonObj.state.brightness)
        sendEvent(name:"switch", value: jsonObj.state.power == true ? "on" : "off")
        sendEvent(name:"modeName", value: getModeName(jsonObj.state.scene) )
	    
        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        sendEvent(name: "lastCheckin", value: now, displayed: false)
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}


def updated() {}

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

def getDuration(){
	def smoothOn = settings.smooth == "" ? "On" : settings.smooth
    def duration = 500
    if(smoothOn == "On"){
        if(settings.duration != null){
            duration = settings.duration
        }
    }
    return duration
}
