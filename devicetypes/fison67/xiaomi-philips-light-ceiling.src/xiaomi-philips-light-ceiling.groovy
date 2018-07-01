/**
 *  Xiaomi Philips Light Ceiling(v.0.0.2)
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
	definition (name: "Xiaomi Philips Light Ceiling", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
		capability "Color Control"
        capability "Switch Level"
        capability "Health Check"
        capability "Light"

        attribute "lastOn", "string"
        attribute "lastOff", "string"
        
        attribute "lastCheckin", "Date"
         
        command "setAutoColorOn"
        command "setAutoColorOff"
        command "setSmartNightLightOn"
        command "setSmartNightLightOff"
        
        command "setScene1"
        command "setScene2"
        command "setScene3"
        command "setScene4"
	}


	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'\n${name}', action:"switch.off", icon:"https://github.com/fison67/mi_connector/raw/master/icons/xiaomi_ceil_on.png", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'\n${name}', action:"switch.on", icon:"https://github.com/fison67/mi_connector/raw/master/icons/xiaomi_ceil_off.png", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"https://github.com/fison67/mi_connector/raw/master/icons/xiaomi_ceil_on.png", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'\n${name}', action:"switch.on", icon:"https://github.com/fison67/mi_connector/raw/master/icons/xiaomi_ceil_off.png", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}')
            }
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"switch level.setLevel"
            }
            
            tileAttribute ("device.color", key: "COLOR_CONTROL") {
                attributeState "color", action:"setColor"
            }
		}
		multiAttributeTile(name:"switch2", type: "lighting"){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'ON', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjY3/MDAxNTIyMTUzOTg0NzMx.eymIqPh2CSLBt1h5rgVRyqZWaBgm-AXOiRe3crmav1Ug.4ZSrZUCtOjWYraxmPAWV9RoLe0Rnnw1XRB54a5gNLs0g.PNG.shin4299/Yeelight_main_on.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'OFF', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfODQg/MDAxNTIyMTUzOTg0NzIw.61z5mx6FESuZ_PGX9lLn4SE62-DwhdwvZKLuoxwRQQYg.iyatTTFzMSQ8X_BAxMTqsd9mp2QSmArqO5jAKhkctUEg.PNG.shin4299/Yeelight_main_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfODQg/MDAxNTIyMTUzOTg0NzIw.61z5mx6FESuZ_PGX9lLn4SE62-DwhdwvZKLuoxwRQQYg.iyatTTFzMSQ8X_BAxMTqsd9mp2QSmArqO5jAKhkctUEg.PNG.shin4299/Yeelight_main_off.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.ofn", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjY3/MDAxNTIyMTUzOTg0NzMx.eymIqPh2CSLBt1h5rgVRyqZWaBgm-AXOiRe3crmav1Ug.4ZSrZUCtOjWYraxmPAWV9RoLe0Rnnw1XRB54a5gNLs0g.PNG.shin4299/Yeelight_main_on.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"

			}
        }
        
        valueTile("refresh", "device.refresh", width: 2, height: 2, decoration: "flat") {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }        
        valueTile("lastOn_label", "", decoration: "flat") {
            state "default", label:'Last\nON'
        }
        valueTile("lastOn", "device.lastOn", decoration: "flat", width: 3, height: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("lastOff_label", "", decoration: "flat") {
            state "default", label:'Last\nOFF'
        }
        valueTile("lastOff", "device.lastOff", decoration: "flat", width: 3, height: 1) {
            state "default", label:'${currentValue}'
        }
        
        standardTile("autoColor", "device.autoColor") {
			state "on", label: "On", action: "setAutoColorOff", backgroundColor:"#ff9eb2", nextState:"off"
			state "off", label: "Off", action: "setAutoColorOn", backgroundColor:"#bcbcbc", nextState:"on"
		}
        
        standardTile("smartNightLight", "device.smartNightLight") {
			state "on", label: "On", action: "setSmartNightLightOff", backgroundColor:"#ff9eb2", nextState:"off"
			state "off", label: "Off", action: "setSmartNightLightOn", backgroundColor:"#bcbcbc", nextState:"on"
		}
        
        standardTile("scene", "device.scene") {
			state "1", label: "1", action: "setScene1", backgroundColor:"#ff9eb2", nextState:"2"
			state "2", label: "2", action: "setScene2", backgroundColor:"#bcbcbc", nextState:"3"
			state "3", label: "3", action: "setScene3", backgroundColor:"#ff9eb2", nextState:"4"
			state "4", label: "4", action: "setScene4", backgroundColor:"#bcbcbc", nextState:"1"
		}
        
   	main (["switch2"])
	details(["switch", "refresh", "lastOn_label", "lastOn", "lastOff_label","lastOff", "colorTemp", "autoColor", "smartNightLight", "scene" ])       
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
    log.debug "Status >> " + params.data
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
 	switch(params.key){
    case "power":
        if(params.data == "true"){
            sendEvent(name:"switch", value: "on")
            sendEvent(name: "lastOn", value: now)
        } else {
            sendEvent(name:"switch", value: "off")
            sendEvent(name: "lastOff", value: now)
        }
    	break;
    case "color":
    	def colors = params.data.split(",")
        String hex = String.format("#%02x%02x%02x", colors[0].toInteger(), colors[1].toInteger(), colors[2].toInteger());  
    	sendEvent(name:"color", value: hex )
    	break;
    case "brightness":
    	sendEvent(name:"level", value: params.data )
    	break;
    case "smartNightLight":
    	sendEvent(name:"smartNightLight", value: params.data == "true" ? "on" : "off")
    	break;
    case "autoColorTemperature":
    	sendEvent(name:"autoColor", value: params.data == "true" ? "on" : "off")
    	break;
    }
    
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

def setLevel(brightness){
	log.debug "setBrightness >> ${state.id}, val=${brightness}"
    def body = [
        "id": state.id,
        "cmd": "brightness",
        "data": brightness
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setColor(color){
	log.debug "setColorTemperature >> ${state.id}"
    log.debug "${color}"
    
    def body = [
        "id": state.id,
        "cmd": "color",
        "data": color.hex
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

def setAutoColorOn(){
	log.debug "setAutoColorOn >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "autoColor",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setAutoColorOff(){
	log.debug "setAutoColorOff >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "autoColor",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setSmartNightLightOn(){
	log.debug "setSmartNightLightOn >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "smartNightLight",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setSmartNightLightOff(){
	log.debug "setSmartNightLightOff >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "smartNightLight",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene1(){
	log.debug "setScene1 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 1
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene2(){
	log.debug "setScene2 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 2
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene3(){
	log.debug "setScene3 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 3
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene4(){
	log.debug "setScene4 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 4
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}


def updated() {}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        def colors = jsonObj.properties.color.values
        String hex = String.format("#%02x%02x%02x", colors[0].toInteger(), colors[1].toInteger(), colors[2].toInteger());  
    	sendEvent(name:"color", value: hex )
        sendEvent(name:"level", value: jsonObj.properties.brightness)
        sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
	    
        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        sendEvent(name: "lastCheckin", value: now)
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
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
