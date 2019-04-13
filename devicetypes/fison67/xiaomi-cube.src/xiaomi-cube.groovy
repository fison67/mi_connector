/**
 *  Xiaomi Cube (v.0.0.3)
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
	definition (name: "Xiaomi Cube", namespace: "fison67", author: "fison67") {
		capability "PushableButton"
        capability "Sensor"			
        capability "Battery"
		capability "Refresh"
         
        attribute "lastCheckin", "Date"
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
	log.debug "${params.key} >> ${params.data}" + (params.subData != "" ? " (" + params.subData + ")" : "")
    
 	switch(params.key){
    case "action":
        if(params.data == "alert") {
        	sendEvent(name:"pushed", value:1, isStateChange: true, descriptionText: "Alert")
        } else if(params.data == "flip90") {
			sendEvent(name:"pushed", value:2, isStateChange: true, descriptionText: "Flip 90")
        } else if(params.data == "flip180") {
			sendEvent(name:"pushed", value:3, isStateChange: true, descriptionText: "Flip 180")
        } else if(params.data == "move") {
			sendEvent(name:"pushed", value:4, isStateChange: true, descriptionText: "Move")
        } else if(params.data == "tap_twice") {
			sendEvent(name:"pushed", value:5, isStateChange: true, descriptionText: "Tap Twice")
        } else if(params.data == "shake_air") {
			sendEvent(name:"pushed", value:6, isStateChange: true, descriptionText: "Shake Air")
        } else if(params.data == "free_fall") {
        	sendEvent(name:"pushed", value:7, isStateChange: true, descriptionText: "Free Fall")
        }  else if(params.data == "rotate") {
        	sendEvent(name:"pushed", value:8, isStateChange: true, descriptionText: "Cube is rotated " + params.subData + " degrees.")
        }
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data)
    	break;
    }
    
    updateLastTime()
}

def updateLastTime(){
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

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        
       	sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
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
