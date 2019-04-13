/**
 *  Xiaomi Vibration Sensor (v.0.0.1)
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
	definition (name: "Xiaomi Vibration Sensor", namespace: "fison67", author: "fison67") {
        capability "Sensor"			
        capability "PushableButton"
        capability "Battery"
        capability "Refresh"
         
        attribute "final_tilt_angle", "string"
        attribute "coordination", "string"
        attribute "bed_activity", "string"
        
        attribute "lastCheckin", "Date"
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
	log.debug "Mi Connector >> ${params.key} : ${params.data}"
    
 	switch(params.key){
    case "action":
    	if(params.data == "vibrate") {
			sendEvent(name:"pushed", value:1, isStateChange: true, descriptionText: "Vibrate")
        } else if(params.data == "tilt") {
			sendEvent(name:"pushed", value:2, isStateChange: true, descriptionText: "tilt")
        } else if(params.data == "final_tilt_angle"){
			sendEvent(name:"pushed", value:3, isStateChange: true, descriptionText: "final_tilt_angle")
            sendEvent(name: "final_tilt_angle", value: params.subData as int)
        } else if(params.data == "coordination"){
			sendEvent(name:"pushed", value:4, isStateChange: true, descriptionText: "coordination")
            sendEvent(name: "coordination", value: params.subData)
        } else if(params.data == "bed_activity"){
			sendEvent(name:"pushed", value:5, isStateChange: true, descriptionText: "bed_activity")
            sendEvent(name: "bed_activity", value: params.subData as int)
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
