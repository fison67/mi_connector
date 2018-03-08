/**
 *  Xiaomi Vacuums (v.0.0.1)
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
	definition (name: "Xiaomi Vacuums", namespace: "fison67", author: "fison67") {
        capability "Switch"						
         
        attribute "switch", "string"
        
        attribute "status", "string"
        attribute "battery", "string"
        attribute "clean_time", "string"
        attribute "clean_area", "string"
        attribute "in_cleaning", "string"
        attribute "main_brush_work_time", "string"
        attribute "side_brush_work_time", "string"
        attribute "filterWorkTime", "string"
        attribute "sensorDirtyTime", "string"
        
        
        
        attribute "lastCheckin", "Date"
         
        command "localOn"
        command "localOff"
        command "on"
        command "off"
        
        command "charge"
        command "fanSpeed"
        command "spotClean"
	}

	simulator {}

	tiles {
		multiAttributeTile(name:"status", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
                attributeState "initiating", label:'${name}',   backgroundColor:"#00a0dc"
                attributeState "charger-offline", label:'${name}', backgroundColor:"#ffffff"
                attributeState "waiting", label:'${name}',  backgroundColor:"#00a0dc"
                attributeState "cleaning", label:'${name}', backgroundColor:"#ffffff"
                attributeState "returning", label:'${name}', backgroundColor:"#00a0dc"
                attributeState "charging", label:'${name}',   backgroundColor:"#ffffff"
                
                attributeState "charging-error", label:'${name}',  backgroundColor:"#00a0dc"
                attributeState "paused", label:'${name}',  backgroundColor:"#ffffff"
                
                attributeState "spot-cleaning", label:'${name}', backgroundColor:"#00a0dc"
                attributeState "error", label:'${name}',   backgroundColor:"#ffffff"
                
                attributeState "shutting-down", label:'${name}',  backgroundColor:"#00a0dc"
                attributeState "updating", label:'${name}',  backgroundColor:"#ffffff"
                
                attributeState "docking", label:'${name}', backgroundColor:"#00a0dc"
                attributeState "zone-cleaning", label:'${name}',  backgroundColor:"#ffffff"
                
                attributeState "full", label:'${name}', backgroundColor:"#ffffff"
                
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
        standardTile("spot", "device.spot", width: 2, height: 2, decoration: "flat") {
            state "on", label:'Spot Clean', action:"spotClean",  backgroundColor:"#ffffff"
        }
        
        standardTile("charge", "device.charge", width: 2, height: 2, decoration: "flat") {
            state "on", label:'Charge', action:"charge",  backgroundColor:"#ffffff"
        }
        
        standardTile("paused", "device.paused", width: 2, height: 2, decoration: "flat") {
            state "on", label:'Pause', action:"paused", backgroundColor:"#ffffff"
        }
        
        standardTile("start", "device.start", width: 2, height: 2, decoration: "flat") {
            state "on", label:'Start', action:"start", backgroundColor:"#ffffff"
        }
        
        valueTile("battery", "device.battery",  height: 2, width: 2) {
            state "val", label:"${currentValue}"
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
	log.debug "Key >> ${params.key}"
    
 	switch(params.key){
    case "state":
    	sendEvent(name:"status", value: params.data )
    	break;
    case "battery":
    	sendEvent(name:"battery", value: params.data )
    	break;
    case "fanSpeed":
    	sendEvent(name:"fanSpeed", value: params.data )
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def fanSpeed(speed){
    log.debug "fanSpeed >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanSpeed",
        "data": speed
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def spotClean(){
	log.debug "spotClean >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "spotClean"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def charge(){
	log.debug "charge >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "charge"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def paused(){
	log.debug "paused >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "paused"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def start(){
    log.debug "start >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "start"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def localOn(){
	log.debug "On >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "clean"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def localOff(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "stop"
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
