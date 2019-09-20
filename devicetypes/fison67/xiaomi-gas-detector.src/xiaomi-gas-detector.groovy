/**
 *  Xiaomi Gas Dectector (v.0.0.1)
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
	definition (name: "Xiaomi Gas Detector", namespace: "fison67", author: "fison67") {
        capability "Sensor"
        capability "Carbon Monoxide Detector"    //"detected", "clear", "tested"
        capability "Refresh"
        attribute "density", "string"        
        attribute "lastCheckin", "Date"
        
	}

	simulator {
	}

	tiles {
		multiAttributeTile(name:"gas", type: "generic", width: 6, height: 4){
			tileAttribute ("device.carbonMonoxide", key: "PRIMARY_CONTROL") {
               	attributeState "clear", label:'${name}', icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMTg0/MDAxNTIyNjcwOTc2ODE1.2rSncv314VWU8irUYinoIi9JLQ3muxYJOVv0zNi_hpsg.ti_b998of00LFlzxjoNnD6Y2zAhq-I2Np7KvWXRaEHMg.PNG.shin4299/gas_main_off.png?type=w3" , backgroundColor:"#ffffff"
            	attributeState "detected", label:'${name}', icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMTI3/MDAxNTIyNjcwOTc2OTQ3.BhACHbETMGGIUQohpJx2USQ_QwLmvOtHMkTe5tTQBzgg.2BXHQDUXhu0f5GCsZ5IFwBvdDJY0DTXmPvs4YjVD6K4g.PNG.shin4299/gas_main_on.png?type=w3" , backgroundColor:"#e86d13"
			}
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
        valueTile("density", "device.density", width: 2, height: 2) {
            state ("val", label:'${currentValue}㎍/㎥', unit:"㎍/㎥", defaultState: true, 
            	backgroundColors:[
                    [value: 00, color: "#fde9e5"],
                    [value: 1000, color: "#600e00"]
                ]
             )
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
    case "gasDetected":
    	sendEvent(name:"carbonMonoxide", value: (params.data == "true" ? "detected" : "clear") )
    	break;
    case "density":
    	sendEvent(name:"density", value: params.data, unit:"㎍/㎥")
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
        
        sendEvent(name:"density", value: jsonObj.properties.density, unit:"㎍/㎥")
        
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
