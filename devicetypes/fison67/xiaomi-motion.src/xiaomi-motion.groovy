/**
 *  Xiaomi Motion (v.0.0.1)
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
	definition (name: "Xiaomi Motion", namespace: "fison67", author: "fison67") {
        capability "Motion Sensor"
        capability "Illuminance Measurement"
        capability "Configuration"
        capability "Sensor"
         
        attribute "battery", "string"
        
        attribute "lastCheckin", "Date"
	command "reset"	
         
	}


	simulator {
	}
	preferences {
		input "motionReset", "number", title: "Motion Reset Time", description: "", value:120, displayDuringSetup: true
	}


	tiles(scale: 2) {
		multiAttributeTile(name:"motion", type: "generic", width: 6, height: 4){
			tileAttribute ("device.motion", key: "PRIMARY_CONTROL") {
				attributeState "active", label:'motion', icon:"https://postfiles.pstatic.net/MjAxODAzMjNfMjc4/MDAxNTIxNzM3NjEwOTA4.AVNFyqM4bd-a1VMujIbLN9MVBYFb75X0jROHPuG9pKkg.U6TX1CZoDPe-8odhwyt1YYSrS37jddX3EldEMxd56k0g.PNG.fuls/Motion_active_75.png?type=w773", backgroundColor:"#00a0dc"
				attributeState "inactive", label:'no motion', icon:"https://postfiles.pstatic.net/MjAxODAzMjNfMjcy/MDAxNTIxNzM3NjEwOTA4.q1xS4KkstlJxdvxeTeS-cPZ44Bppv766hjez9tb5vZ4g.ap9JW3w27LXOUH_z2cPFXX6LUmL-fY4CRa7M6XxWWx0g.PNG.fuls/Motion_inactive_75.png?type=w773", backgroundColor:"#ffffff"
			}
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
        valueTile("illuminance", "device.illuminance", width: 2, height: 2) {
            state "val", label:'${currentValue}', defaultState: true,
                backgroundColors:[
                    [value: 100, color: "#153591"],
                    [value: 200, color: "#1e9cbb"],
                    [value: 300, color: "#90d2a7"],
                    [value: 600, color: "#44b621"],
                    [value: 900, color: "#f1d801"],
                    [value: 1200, color: "#d04e00"],
                    [value: 1500, color: "#bc2323"]
                ]
        }
        
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state "val", label:'${currentValue}', defaultState: true
        }
                standardTile("reset", "device.reset", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", action:"reset", label: "Reset Motion", icon:"st.motion.motion.active"
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
 	switch(params.key){
    case "motion":
        sendEvent(name:"motion", value: (params.data == "true" ? "active" : null) )
        if (settings.motionReset == null || settings.motionReset == "" ) settings.motionReset = 120
        if (params.data == "true") runIn(settings.motionReset, stopMotion)
		
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data + "%")
    	break;
    case "illuminance":
    	log.debug "illuminance >> ${params.data}"
    	sendEvent(name:"illuminance", value: params.data )
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        setStatus(jsonObj.state)
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {
}

def stopMotion() {
   sendEvent(name:"motion", value:"inactive")
}

def reset() {
   sendEvent(name:"motion", value:"inactive")
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
