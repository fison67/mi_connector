/**
 *  Xiaomi Heater New App(v.0.0.1)
 *
 * MIT License
 *
 * Copyright (c) 2020 fison67@nate.com
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
	definition (name: "Xiaomi Heater New App", namespace: "streamorange58819", author: "fison67", mnmn:"fison67", vid:"c330f481-969a-319e-854f-7eb3cbf555a2", ocfDeviceType: "oic.d.thermostat") {
		capability "Thermostat Mode"
		capability "Thermostat Cooling Setpoint"
		capability "Thermostat Heating Setpoint"		
        capability "Temperature Measurement"
        capability "streamorange58819.led"
        capability "streamorange58819.buzzer"
		capability "streamorange58819.childLock"
	}
}

// parse events into attributes
def parse(String description) {}

def installed(){
    sendEvent(name: "supportedThermostatModes", value: ["off", "heat"])
}

def setInfo(String app_url, String id) {
	state.app_url = app_url
    state.id = id
    
    sendEvent(name: "supportedThermostatModes", value: ["off", "heat"])
}

def setStatus(params){
    log.debug "${params.key} : ${params.data}"
 
 	switch(params.key){
    case "power":
        sendEvent(name:"thermostatMode", value: (params.data == "true" ? "heat" : "off"))
    	break;
    case "temperature":
        sendEvent(name:"temperature", value: Math.round(Float.parseFloat("${params.data}".replace("C",""))*100)/10 as float)
    	break;    
    case "targetTemperature":
        sendEvent(name:"heatingSetpoint", value: "${params.data}".replace("C","") as int)
    	break;
    case "led":
        sendEvent(name:"led", value: (params.data == "true" ? "on" : "off") )
    	break;        
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off") )
        break;
    case "childLock":
    	sendEvent(name:"childlock", value: (params.data == "on" ? "on" : "off") )
        break;
    case "powerOffTime":
        break;
    }
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

def heat(){
    sendCommand(makePayload("power", "on"), null)
}

def off(){
    sendCommand(makePayload("power", "off"), null)
}

def setThermostatMode(mode){
	log.debug "setThermostatMode " + mode
	switch(mode){
    case "heat":
    	heat()
    	break
    case "off":
    	off()
		break
    }
}

def setHeatingSetpoint(temperature) {
    sendCommand(makePayload("targetTemperature", temperature), null)
}

def makePayload(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
    return makeCommand(body)
}

def setChildLock(locked){
	log.debug "setChildLock: " + locked
	if(locked == "locked"){
    	childLockOn()
    }else{
    	childLockOff()
    }
}

def childLockOn(){
	sendCommand(makePayload("childLock", "on"), null)
}

def childLockOff(){
	sendCommand(makePayload("childLock", "off"), null)
}

def setBuzzer(power){
	log.debug "setBuzzer: " + power
	if(power == "on"){
    	buzzerOn()
    }else{
    	buzzerOff()
    }
}

def buzzerOn(){
	sendCommand(makePayload("buzzer", "on"), null)
}

def buzzerOff(){
	sendCommand(makePayload("buzzer", "off"), null)
}

def setLed(power){
	log.debug "setLed: " + power
	if(power == "on"){
    	ledOn()
    }else{
    	ledOff()
    }
}

def ledOn(){
    sendCommand(makePayload("led", "on"), null)
}

def ledOff(){
    sendCommand(makePayload("led", "off"), null)
}

def updated() {
    refresh()
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
    	
        sendEvent(name:"temperature", value: jsonObj.properties.temperature.value * 10 as int)
        sendEvent(name:"relativeHumidity", value: jsonObj.properties.relativeHumidity)
        sendEvent(name:"heatingSetpoint", value: jsonObj.properties.targetTemperature.value)
        sendEvent(name:"thermostatMode", value: (jsonObj.properties.power== "true" ? "heat" : "off"))
        
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
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}
