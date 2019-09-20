/**
 *  Xiaomi Power Plug2 (v.0.0.1)
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
	definition (name: "Xiaomi Power Plug2", namespace: "fison67", author: "fison67", vid: "generic-switch-power", ocfDeviceType: "oic.d.smartplug") {
        capability "Switch"
        capability "Power Meter"
        capability "Temperature Measurement"
        capability "Sensor"
        capability "Outlet"
        capability "Refresh"
        
        attribute "led", "string"
		attribute "usb", "string"
        attribute "lastCheckin", "Date"
        
        command "usbOn"
        command "usbOff"
        command "ledOn"
        command "ledOff"
	}

	simulator { }
    
    preferences {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNTUg/MDAxNTIyNjcwODg1MTU2.KfRiLw6Uei1mX7djpXxo0jtKlsAWLOyz04yVtEU9yZsg.3A6PUr6aM1nn2mIaD4Rt7ws_bDZi9dKlzVJJLUoiLSAg.PNG.shin4299/plug_main_on.png?type=w3", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMTcy/MDAxNTIyNjcwODg0OTI5.Y6YSf8yKOH56h1RsLl0MbgFyHqqGw-E-XXQ6wG_g950g.vr4pyhi92iDk-u6pisNPGdGeTkJxaidmPe5y1rW-cAEg.PNG.shin4299/plug_main_off.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMTcy/MDAxNTIyNjcwODg0OTI5.Y6YSf8yKOH56h1RsLl0MbgFyHqqGw-E-XXQ6wG_g950g.vr4pyhi92iDk-u6pisNPGdGeTkJxaidmPe5y1rW-cAEg.PNG.shin4299/plug_main_off.png?type=w3", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNTUg/MDAxNTIyNjcwODg1MTU2.KfRiLw6Uei1mX7djpXxo0jtKlsAWLOyz04yVtEU9yZsg.3A6PUr6aM1nn2mIaD4Rt7ws_bDZi9dKlzVJJLUoiLSAg.PNG.shin4299/plug_main_on.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.power", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Meter: ${currentValue} w\n ',icon: "st.Health & Wellness.health9")
            }
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'\nUpdated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        valueTile("power", "device.power", width:2, height:2, inactiveLabel: false, decoration: "flat" ) {
        	state "power", label: 'Meter\n${currentValue} w', action: "power", defaultState: true
		}
        
        standardTile("usb", "device.usb", width: 2, height: 2, canChangeIcon: true) {
            state "off", label: '${name}', action: "usbOn", icon: "st.switches.light.off", backgroundColor: "#FFFFFF", nextState: "turningOn"
            state "on", label: '${name}', action: "usbOff", icon: "st.switches.light.on", backgroundColor: "#00A0DC", nextState: "turningOff"
            state "turningOn", label: '${name}', action: "usbOff", icon: "st.switches.light.off", backgroundColor: "#00A0DC", nextState: "turningOff"
            state "turningOff", label: '${name}', action: "usbOn", icon: "st.switches.light.off", backgroundColor: "#FFFFFF", nextState: "turningOn"
        }
        
        standardTile("led", "device.led", width: 2, height: 2, canChangeIcon: true) {
            state "off", label: '${name}', action: "ledOn", icon: "st.switches.light.off", backgroundColor: "#FFFFFF", nextState: "turningOn"
            state "on", label: '${name}', action: "ledOff", icon: "st.switches.light.on", backgroundColor: "#00A0DC", nextState: "turningOff"
            state "turningOn", label: '${name}', action: "ledOff", icon: "st.switches.light.off", backgroundColor: "#00A0DC", nextState: "turningOff"
            state "turningOff", label: '${name}', action: "ledOn", icon: "st.switches.light.off", backgroundColor: "#FFFFFF", nextState: "turningOn"
        }
        
        valueTile("temperature", "device.temperature", inactiveLabel: false) {
            state "temperature", label:'${currentValue}°'
        }
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
        
        main (["switch"])
        details(["switch", "usb", "led", "power", "temperature", "refresh"])
        
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setExternalAddress(address){
	log.debug "External Address >> ${address}"
	state.externalAddress = address
}

def setInfo(String app_url, String id) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
}

def setStatus(params){
    log.debug "${params.key} >> ${params.data}"
 
 	switch(params.key){
    case "power":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break
    case "powerLoad":
    	sendEvent(name:"power", value: params.data.replace(" w", "").replace(",", ""))
    	break
    case "usbPower":
    	sendEvent(name:"usb", value: (params.data == "true" ? "on" : "off"))
    	break
    case "led":
    	sendEvent(name:"led", value: (params.data == "true" ? "on" : "off"))
    	break
    case "temperature":
    	sendEvent(name:"temperature", value: params.data as double)
    	break
    }
    
    updateLastTime()
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

def ledOn(){
	log.debug "ledOn >> ${state.id}"
    control("led", "on")
}

def ledOff(){
    log.debug "ledOff >> ${state.id}"
    control("led", "off")
}

def usbOn(){
    log.debug "usbOn >> ${state.id}"
    control("usbPower", "on")
}

def usbOff(){
    log.debug "ledOff >> ${state.id}"
    control("usbPower", "off")
}

def control(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
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
        	"HOST": parent._getServerURL(),
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
        
		try{ sendEvent(name:"power", value: jsonObj.properties.powerLoad.value) }catch(err){}
        
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
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}
