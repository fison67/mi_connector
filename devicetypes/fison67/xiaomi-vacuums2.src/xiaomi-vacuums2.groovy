/**
 *  Xiaomi Vacuums2 (v.0.0.1)
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
	definition (name: "Xiaomi Vacuums2", namespace: "fison67", author: "fison67") {
        capability "Switch"		
        capability "Battery"	
        capability "Refresh"	
        
        attribute "status", "string"
        attribute "clean_time", "string"
        attribute "clean_area", "string"
        attribute "in_cleaning", "string"
        attribute "main_brush_work_time", "string"
        attribute "side_brush_work_time", "string"
        attribute "filterWorkTime", "string"
        attribute "sensorDirtyTime", "string"
        
        attribute "lastCheckin", "Date"
         
        command "find"
        command "clean"
        command "charge"
        command "paused"
        command "fanSpeed"
        command "spotClean"
        
        command "quiet"
        command "balanced"
        command "turbo"
        command "fullSpeed"
        command "mop"
        command "setVolume"
        command "setVolumeWithTest"
	}

	simulator {}

	tiles {
		multiAttributeTile(name:"mode", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.mode", key: "PRIMARY_CONTROL") {
                attributeState "initiating", label:'${name}', backgroundColor:"#00a0dc", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"off"
                attributeState "charger-offline", label:'${name}', backgroundColor:"#000000", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum-ready.png?raw=true", action:"on"
                attributeState "waiting", label:'${name}',  backgroundColor:"#00a0dc", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"charge"
                attributeState "cleaning", label:'${name}', backgroundColor:"#4286f4", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_on.png?raw=true", action:"off"
                attributeState "returning", label:'${name}', backgroundColor:"#4e25a8", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_turning_off.png?raw=true", action:"on"
                attributeState "charging", label:'${name}',   backgroundColor:"#25a896", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"on"
                
                attributeState "charging-error", label:'${name}',  backgroundColor:"#ff2100", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"on"
                attributeState "paused", label:'${name}',  backgroundColor:"#09540d", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"on"
                
                attributeState "spot-cleaning", label:'${name}', backgroundColor:"#a0e812", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_on.png?raw=true", action:"off"
                attributeState "error", label:'${name}',   backgroundColor:"#ff2100", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"on"
                
                attributeState "shutting-down", label:'${name}',  backgroundColor:"#00a0dc", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_turning_off.png?raw=true", action:"on"
                attributeState "updating", label:'${name}',  backgroundColor:"#ffa0ea", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_turning_off.png?raw=true", action:"on"
                
                attributeState "docking", label:'${name}', backgroundColor:"#9049bc", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_turning_off.png?raw=true", action:"off"
                attributeState "zone-cleaning", label:'${name}',  backgroundColor:"#91f268", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_on.png?raw=true", action:"off"
                
                attributeState "full", label:'${name}', backgroundColor:"#ffffff", icon:"st.Electronics.electronics1", action:"on"
                
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
            
		}
        
        standardTile("switch", "device.switch", inactiveLabel: false, width: 2, height: 2, canChangeIcon: true) {
            state "on", label:'${name}', action:"off", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "off", label:'${name}', action:"on", backgroundColor:"#ffffff", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"off", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "turningOff", label:'....', action:"on", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        
        standardTile("fanSpeed", "device.fanSpeed", inactiveLabel: false, width: 2, height: 2) {
            state "quiet", label:'Quiet', action:"balanced", backgroundColor:"#00a0dc", nextState:"balanced"
            state "balanced", label:'Balanced', action:"turbo", backgroundColor:"#1000ff", nextState:"turbo"
            state "turbo", label:'Turbo', action:"fullSpeed", backgroundColor:"#9a71f2", nextState:"fullSpeed"
            state "fullSpeed", label:'Max', action:"quiet", backgroundColor:"#aa00ff", nextState:"mop"
            state "mop", label:'Mop', action:"mop", backgroundColor:"#aa00ff", nextState:"quiet"
        }
        
        standardTile("paused", "device.paused", width: 2, height: 2) {
            state "paused", label:'paused', action:"paused", backgroundColor:"#00a0dc", nextState:"restart"
            state "restart", label:'${name}', action:"on", backgroundColor:"#09540d"
        }
        
        standardTile("charge", "device.charge", width: 2, height: 2 ) {
            state "charge", label:'Charge', action:"charge",  backgroundColor:"#25a896"
        }
        
        standardTile("spot", "device.spot", width: 2, height: 2 ) {
            state "spot", label:'Spot', action:"spotClean",  backgroundColor:"#2ca6e8"
        }
        
        standardTile("find", "device.find", width: 2, height: 2 ) {
            state "find", label:'Find Me', action:"find",  backgroundColor:"#1cffe8"
        }
        
        valueTile("battery", "device.battery",  height: 2, width: 2) {
            state "val", label:'${currentValue}%', defaultState: true,
            	backgroundColors:[
                    [value: 10, color: "#ff002a"],
                    [value: 20, color: "#f4425f"],
                    [value: 30, color: "#ef7085"],
                    [value: 40, color: "#ea8f9e"],
                    [value: 50, color: "#edadb7"],
                    [value: 60, color: "#a9aee8"],
                    [value: 70, color: "#7f87e0"],
                    [value: 80, color: "#505bd3"],
                    [value: 90, color: "#2131e0"]
                ]
        }
        
        controlTile("volume", "device.volume", "slider", height: 2, width: 2, inactiveLabel: false, range:"(0..100)") {
			state ("volume", label:'${currentValue}', action:"setVolumeWithTest")
		}
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
        
        main (["mode"])
      	details(["mode", "switch", "paused", "fanSpeed", "spot", "charge", "find", "battery", "volume", "refresh"])
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
        if(params.data == "paused"){
    		sendEvent(name:"switch", value: "paused" )
        }
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data)
    	break;
    case "fanSpeed":
    	def val = params.data.toInteger()
        def _value
        switch(val){
        case 101:
        	_value = "quiet"
        	break;
        case 102:
        	_value = "balanced"
        	break;
        case 103:
        	_value = "turbo"
        	break;
        case 104:
        	_value = "fullSpeed"
        	break;
        case 105:
        	_value = "mop"
        	break;
        }
    	sendEvent(name:"fanSpeed", value: _value )
    	break;
    case "cleaning":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off") )
       	sendEvent(name:"paused", value: params.data == "true" ? "paused" : "restart" )     
    	break;
    case "volume":
    	sendEvent(name:"volume", value: params.data )
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

def setVolume(volume){
	log.debug "setVolume >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "volume",
        "data": volume
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setVolumeWithTest(volume){
	log.debug "setVolume >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "volumeWithTest",
        "data": volume
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def quiet(){
    log.debug "quiet >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanSpeed",
        "data": 101
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def balanced(){
    log.debug "balanced >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanSpeed",
        "data": 102
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def turbo(){
    log.debug "turbo >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanSpeed",
        "data": 103
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def fullSpeed(){
    log.debug "fullSpeed >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanSpeed",
        "data": 104
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def mop(){
    log.debug "mop >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanSpeed",
        "data": 105
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
    
    sendEvent(name:"spot", value: "on" )
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
        "cmd": "pause"
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

def find(){
    log.debug "find >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "find"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
		log.debug "On >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "clean"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "stop"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

/*
def timer(mSecond, function){
	def now = new Date()
	def runTime = new Date(now.getTime() + mSecond)
	runOnce(runTime, function);
}
*/

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        
        sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        
        sendEvent(name:"mode", value: jsonObj.state.state)
        log.debug (jsonObj.properties.cleaning ? "on" : "off")
        sendEvent(name:"switch", value: (jsonObj.properties.cleaning ? "on" : "off") )
       	sendEvent(name:"paused", value: jsonObj.properties.cleaning ? "paused" : "restart" )  
        
        def fanSpeed;
        switch(jsonObj.state.fanSpeed){
        case 38:
        	fanSpeed = "quiet"
        	break;
        case 60:
        	fanSpeed = "balanced"
        	break;
        case 77:
        	fanSpeed = "turbo"
        	break;
        case 90:
        	fanSpeed = "fullSpeed"
        	break;
        }
    	sendEvent(name:"fanSpeed", value: fanSpeed )
        
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
