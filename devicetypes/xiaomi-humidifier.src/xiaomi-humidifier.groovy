/**
 *  Xiaomi Humidifier(v.0.0.1)
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
	definition (name: "Xiaomi Humidifier", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
         
        attribute "switch", "string"
        attribute "temperature", "string"
        attribute "humidity", "string"
        attribute "buzzer", "string"
        attribute "mode", "string"
        attribute "ledBrightness", "string"
        attribute "limit_humidity", "string"
        attribute "use_time", "string"
        attribute "dry", "string"
        
        
        attribute "lastCheckin", "Date"
         
        command "on"
        command "off"
        
        command "setModeSilent"
        command "setModeHight"
        command "setModeMedium"
        
        command "buzzerOn"
        command "buzzerOff"
        
        command "setBright"
        command "setBrightDim"
        command "setBrightOff"
	}


	simulator {
	}

	tiles {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"off", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"off", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
        valueTile("temperature", "device.temperature", width: 2, height: 2, unit: "°C") {
            state("val", label:'${currentValue}', defaultState: true, 
            	backgroundColors:[
                    [value: 5, color: "#153591"],
                    [value: 10, color: "#1e9cbb"],
                    [value: 20, color: "#90d2a7"],
                    [value: 30, color: "#44b621"],
                    [value: 40, color: "#f1d801"],
                    [value: 70, color: "#d04e00"],
                    [value: 90, color: "#bc2323"]
                ]
            )
        }
        
        valueTile("humidity", "device.humidity", width: 2, height: 2, unit: "%") {
            state("val", label:'${currentValue}', defaultState: true, 
            	backgroundColors:[
                    [value: 10, color: "#153591"],
                    [value: 30, color: "#1e9cbb"],
                    [value: 40, color: "#90d2a7"],
                    [value: 50, color: "#44b621"],
                    [value: 60, color: "#f1d801"],
                    [value: 80, color: "#d04e00"],
                    [value: 90, color: "#bc2323"]
                ]
            )
        }
        
        valueTile("limit_humidity", "device.limit_humidity", width: 2, height: 2) {
            state("val", label:'${currentValue}', defaultState: true, 
            	backgroundColors:[
                    [value: 30, color: "#153591"],
                    [value: 40, color: "#1e9cbb"],
                    [value: 50, color: "#90d2a7"],
                    [value: 60, color: "#44b621"],
                    [value: 70, color: "#f1d801"],
                    [value: 80, color: "#d04e00"]
                ]
        	)
        }
        
        standardTile("mode", "device.mode", width: 2, height: 2, canChangeIcon: true) {
            state "idle", label: 'Idle', action: "setModeSilent", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", nextState:"silent"
            state "silent", label: 'Silent', action: "setModeMedium", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", nextState:"medium"
            state "medium", label: 'Medium', action: "setModeHight", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", nextState:"hight"
            state "hight", label: 'Hight', action: "setModeMedium", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", nextState:"silent"
        }
        
        standardTile("buzzer", "device.buzzer", inactiveLabel: false, width: 2, height: 2, canChangeIcon: true) {
            state "on", label:'Sound', action:"buzzerOff", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "off", label:'Mute', action:"buzzerOn", backgroundColor:"#ffffff", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"buzzerOff", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "turningOff", label:'....', action:"buzzerOn", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        
        standardTile("ledBrightness", "device.ledBrightness", width: 2, height: 2, canChangeIcon: true) {
            state "bright", label: 'Bright', action: "setBrightDim", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", nextState:"dim"
            state "dim", label: 'Dim', action: "setBrightOff", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", nextState:"off"
            state "off", label: 'Off', action: "setBright", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState:"bright"
        } 
        
        valueTile("use_time", "device.use_time", width: 2, height: 2) {
            state("val", label:'${currentValue}', defaultState: true
        	)
        }
        
        standardTile("dry", "device.dry", width: 2, height: 2, canChangeIcon: true) {
            state "dry", label: 'Dry',  backgroundColor: "#00a0dc"
            state "noDry", label: 'No Dry', backgroundColor: "#ffffff"
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
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data + "%")
    	break;
    case "power":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "temperature":
        sendEvent(name:"temperature", value: params.data)
    	break;
    case "limit_hum":
        sendEvent(name:"limit_hum", value: params.data)
    	break;
    case "use_time":
        sendEvent(name:"use_time", value: (params.data.toInteger() / 60 / 60) + "h" )
    	break;
    case "dry":
    	sendEvent(name:"dry", value: (params.data == "" ? "noDry" : "dry") )
        break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def setBright(){
	log.debug "setBright >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "bright"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBrightDim(){
	log.debug "setDim >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "brightDim"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBrightOff(){
	log.debug "setDim >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeSilent(){
    log.debug "setModeSilent >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "silent"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeHight(){
    log.debug "setModeHight >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "hight"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeMedium(){
    log.debug "setModeMedium >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "medium"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def buzzerOn(){
	log.debug "buzzerOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def buzzerOff(){
	log.debug "buzzerOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	log.debug "Off >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "power",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
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
