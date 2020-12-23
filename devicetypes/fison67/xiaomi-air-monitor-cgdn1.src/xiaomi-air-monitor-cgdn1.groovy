/**
 *  Xiaomi Air Monitor CGDN1 (v.0.0.5)
 *
 * MIT License
 *
 * Copyright (c) 2020 fison67@nate.com
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
	definition (name: "Xiaomi Air Monitor CGDN1", namespace: "fison67", author: "fison67", ocfDeviceType: "oic.d.airpurifier") {
		capability "Dust Sensor"
        capability "Temperature Measurement"
        capability "Carbon Dioxide Measurement"
        capability "Relative Humidity Measurement"
		capability "Refresh"
		capability "Battery"
        capability "Power Source"
	}
}

def installed(){
	sendEvent(name:"fineDustLevel", value: 10)
	sendEvent(name:"dustLevel", value: 10)
	sendEvent(name:"temperature", value: 20, unit:"C")
	sendEvent(name:"humidity", value: 40, unit:"%")
	sendEvent(name:"battery", value: 100, unit:"%")
	sendEvent(name:"carbonDioxide", value: 900, unit:"ppm")
	sendEvent(name:"powerSource", value: "battery")
}
    
def updated() {}

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
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: "${params.data}" as int, unit: "%")
    	break;
    case "temperature":
        sendEvent(name:"temperature", value:  Float.parseFloat("${params.data}".replace("C","")), unit:"C" )
    	break;    
    case "carbonDioxide":
        sendEvent(name:"carbonDioxide", value: params.data as int, unit:"ppm" )
    	break;    
    case "battery":
        sendEvent(name:"battery", value: "${params.data}" as int , unit:"%" )
    	break;    
    case "pm2.5":
        sendEvent(name:"fineDustLevel", value: "${params.data}" as int )
    	break;    
    case "pm10":
        sendEvent(name:"dustLevel", value: "${params.data}" as int )
    	break;    
    case "charging":
        sendEvent(name:"powerSource", value: getPowerSource("${params.data}" as int) )
    	break;   
    }
}

def getPowerSource(data){
	if(data == 1){
    	return "mains"
    }else if(data == 2){
    	return "battery"
    }else {
    	return "unknown"
    }
}

def refresh(){
	log.debug "Refresh"
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

def callback(physicalgraph.device.HubResponse hubResponse){
    try {
        def msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}
