/**
 *  Xiaomi Gateway2 (v.0.0.1)
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
	definition (name: "Xiaomi Gateway2", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Temperature Measurement"
        capability "Illuminance Measurement"
        capability "Actuator"
        capability "Configuration"
        capability "Power Meter"
        capability "Switch Level"
        capability "Refresh"
        
        attribute "lastCheckin", "Date"
        
        command "findChild"
        command "playIR", ["string"]
	}


	simulator {
	}

	preferences {
		input name:	"mode", type:"enum", title:"Mode", options:["Air Conditioner", "Socket"], description:"", defaultValue: "Air Conditioner"
	}

}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setExternalAddress(address){
	log.debug "External Address >> ${address}"
	state.externalAddress = address
}

def setInfo(String app_url, String id) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
}

def setStatus(params){
    log.debug "${params.key} : ${params.data}"
    
 	switch(params.key){
    case "power":
    	if(getMode() == "Air Conditioner"){
    		sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off") )
        }
    	break;    
    case "physicalPower":
    	if(getMode() == "Socket"){
    		sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off") )
        }
    	break;
    case "powerLoad":
    	sendEvent(name:"power", value: params.data)
        break;
    case "temperature":
    	sendEvent(name:"level", value: params.data)
        break;
    }
    
    updateLastTime()
}


def playIR(code){
	log.debug "Play IR >> ${code}"
    def body = [
        "id": state.id,
        "cmd": "playIR",
        "data": code
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	log.debug "Off >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": getCommand(),
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": getCommand(),
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setLevel(level){
	log.debug "setLevel >> ${state.id}, val=${level}"
    def body = [
        "id": state.id,
        "cmd": "temperature",
        "data": level
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
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

def findChild(){

    def options = [
     	"method": "GET",
        "path": "/devices/gateway/${state.id}/findChild",
        "headers": [
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ]
    ]
    
    sendCommand(options, null)
}

def getCommand(){
	return getMode() == "Socket" ? "physicalPower" : "power"
}

def getMode(){
	return settings.mode == null ? "Air Conditioner" : settings.mode
}
