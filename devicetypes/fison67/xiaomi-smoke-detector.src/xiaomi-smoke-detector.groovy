/**
 *  Xiaomi Smoke Detector (v.0.0.1)
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
	definition (name: "Xiaomi Smoke Detector", namespace: "fison67", author: "fison67") {
        capability "Sensor"
        capability "Smoke Detector"    //"detected", "clear", "tested"
         
        attribute "battery", "string"
        attribute "density", "string"        
        attribute "lastCheckin", "Date"
        
        command "refresh"
	}


	simulator {
	}

	tiles {
		multiAttributeTile(name:"smoke", type: "generic", width: 6, height: 4){
			tileAttribute ("device.smoke", key: "PRIMARY_CONTROL") {
               	attributeState "clear", label:'${name}', icon:"https://postfiles.pstatic.net/MjAxODAzMjZfMTkz/MDAxNTIyMDQzNDE0MzIx.Z7WbNCehVcAmt3mM5jdadJkR-TMqI200UzKfmjYjCwYg.dnE5kkFzbJ6cXAbbSJu5SwCUcv4x-cxM0UD3RQVcVAQg.PNG.fuls/Fire_Alarm_75.png?type=w773" , backgroundColor:"#ffffff"
            	attributeState "detected", label:'${name}', icon:"https://postfiles.pstatic.net/MjAxODAzMjZfMTkz/MDAxNTIyMDQzNDE0MzIx.Z7WbNCehVcAmt3mM5jdadJkR-TMqI200UzKfmjYjCwYg.dnE5kkFzbJ6cXAbbSJu5SwCUcv4x-cxM0UD3RQVcVAQg.PNG.fuls/Fire_Alarm_75.png?type=w773" , backgroundColor:"#e86d13"
			}
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
        valueTile("density", "device.density", width: 2, height: 2) {
            state ("val", label:'${currentValue}obs./m', defaultState: true, 
            	backgroundColors:[
                    [value: 00, color: "#fde9e5"],
                    [value: 1000, color: "#600e00"]
                ]
             )
        }
        
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state "val", label:'${currentValue}%', defaultState: true
        }
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
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
    case "smokeDetected":
    	sendEvent(name:"smoke", value: (params.data == "true" ? "detected" : "clear") )
    	break;
    case "density":
    	sendEvent(name:"density", value: params.data)
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

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        
        sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        sendEvent(name:"density", value: jsonObj.properties.density)
        
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {
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

def sendCommand(options, _callback){
	def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: _callback])
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
