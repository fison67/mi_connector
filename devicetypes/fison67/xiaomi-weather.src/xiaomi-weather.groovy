/**
 *  Xiaomi Weather (v.0.0.1)
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
	definition (name: "Xiaomi Weather", namespace: "fison67", author: "fison67") {
        capability "Sensor"						
         
        attribute "battery", "string"
        attribute "temperature", "string"
        attribute "humidity", "string"
        attribute "airPressure", "string"
        
        attribute "lastCheckin", "Date"
        
        command "refresh"
	}


	simulator {
	}

	tiles {
		multiAttributeTile(name:"temperature", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState "val", label:'${currentValue}', icon:"st.Weather.weather2", 
                    backgroundColors:[
                        [value: 10, color: "#fc97ab"],
                        [value: 20, color: "#979afc"],
                        [value: 30, color: "#565bf7"],
                        [value: 40, color: "#edb1b7"],
                        [value: 50, color: "#ed959e"],
                        [value: 60, color: "#ed808b"],
                        [value: 70, color: "#e25a68"],
                        [value: 80, color: "#dd3042"]
                    ]
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
         valueTile("humidity", "device.humidity", width: 2, height: 2, unit: "%") {
            state("val", label:'${currentValue}%', defaultState: true, 
            	backgroundColors:[
                    [value: 10, color: "#bfbdef"],
                    [value: 20, color: "#a29fea"],
                    [value: 30, color: "#8582e0"],
                    [value: 40, color: "#6c68e2"],
                    [value: 50, color: "#524ed3"],
                    [value: 60, color: "#3d39cc"],
                    [value: 70, color: "#221dc6"],
                    [value: 80, color: "#0d06e2"]
                ]
            )
        }
        
        
        valueTile("airPressure", "device.airPressure", width: 2, height: 2) {
            state("val", label:'${currentValue}kpa', defaultState: true, backgroundColor:"#00a0dc")
        }
        
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state "val", label:'${currentValue}%', defaultState: true
        }
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
        
        main (["temperature"])
      	details(["temperature", "humidity", "airPressure", "battery", "refresh"])
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
    case "temperature":
    	sendEvent(name:"temperature", value: params.data.replace("C",""))
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data)
    	break;
    case "atmosphericPressure":
    	sendEvent(name:"airPressure", value: params.data.replace("Pa","").replace(",","").toInteger() / 1000)
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


def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
 		sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        sendEvent(name:"temperature", value: jsonObj.properties.temperature.value)
        sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity)
        sendEvent(name:"airPressure", value: jsonObj.properties.atmosphericPressure.value / 1000)
        
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {
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
