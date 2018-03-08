/**
 *  Xiaomi Power Strip (v.0.0.1)
 *
 *  Authors
 *   - fison67@nate.com
 *  Copyright 2018
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
import groovy.json.JsonSlurper

metadata {
	definition (name: "Xiaomi Power Strip", namespace: "fison67", author: "fison67") {
        capability "Switch"						
         
        attribute "status", "string"
        attribute "switch", "string"
        attribute "mode", "string"
        
        attribute "lastCheckin", "Date"
        
        command "localOn"
        command "localOff"
        command "on"
        command "off"
        
        command "setModeGreen"
        command "setModeNormal"
	}

	simulator { }

	tiles {
		multiAttributeTile(name:"status", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"localOff", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"localOn", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"localOff", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"localOn", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
        standardTile("mode", "device.mode", width: 2, height: 2, canChangeIcon: true) {
            state "normal", label: 'Normal', action: "setModeGreen", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", nextState:"green"
            state "green", label: 'Green', action: "setModeNormal", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", nextState:"normal"
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
    log.debug "${params.key} >> ${params.data}"
 
 	switch(params.key){
    case "mode":
    	sendEvent(name:"mode", value: params.data )
    	break;
    case "power":
    	sendEvent(name:"status", value: (params.data == "true" ? "on" : "off"))
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def setModeGreen(){
	log.debug "setModeGreen >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "green"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeNormal(){
	log.debug "setModeNormal >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "normal"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def localOn(){
	log.debug "Off >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def localOff(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "power",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	localOn()
}

def off(){
	localOff()
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
