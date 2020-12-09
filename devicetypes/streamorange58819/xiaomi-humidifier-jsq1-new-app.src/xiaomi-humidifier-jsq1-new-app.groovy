/**
 *  Xiaomi Humidifier Jsq1 New App(v.0.0.2)
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
import groovy.transform.Field


metadata {
	definition (name: "Xiaomi Humidifier Jsq1 New App", namespace: "streamorange58819", author: "fison67", mnmn:"fison67", vid: "4b7e9c8c-3597-3061-874e-2daf4c2a6115", ocfDeviceType: "oic.d.airpurifier") {
        capability "Switch"		
		capability "Sensor"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "streamorange58819.led"
        capability "streamorange58819.buzzer"
        capability "streamorange58819.targetHumidity"
        capability "streamorange58819.watertank"
        capability "streamorange58819.waterstatus"
        capability "streamorange58819.gear"
	}
}

def installed(){
    sendEvent(name: "temperature", value: 20, unit:"C")
    sendEvent(name: "humidity", value: 40, unit:"%")
    sendEvent(name: "targetHumidity", value: 40, unit:"%")
    sendEvent(name: "led", value: "on")
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "buzzer", value: "on")
    sendEvent(name:"gear", value: 1)
    sendEvent(name:"waterstatus", value:"enough")
    sendEvent(name:"waterTank", value: "mounted")
}

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
    case "power":
    	sendEvent(name:"switch", value: params.data == "true" ? "on" : "off")
    	break
    case "led":
        sendEvent(name:"led", value: params.data == "true" ? "on" : "off")
    	break   
    case "buzzer":
    	sendEvent(name:"buzzer", value: params.data == "true" ? "on" : "off" )
        break
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data as int, unit:"%")
    	break;
    case "temperature":
        sendEvent(name:"temperature", value: params.data.replace("C",""), unit:"C" )
    	break
    case "targetHumidity":
        sendEvent(name:"targetHumidity", value: params.data as int, unit:"%")
    	break
    case "gear":
        sendEvent(name:"gear", value: params.data as int)
    	break
    case "water":
        sendEvent(name:"waterstatus", value: params.data == "true" ? "enough" : "not enough")
    	break
    case "waterTank":
        sendEvent(name:"waterTank", value: params.data == "true" ? "mounted" : "unmounted")
    	break
    }
}

def refresh(){
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

def setGear(value){
	sendCommand(makePayload("changeGear", value), null)
}

def setBuzzer(power){
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

def setTargetHumidity(humidity){
	log.debug "setTargetHumidity: ${humidity}"
	sendCommand(makePayload("targetHumidity",humidity), null)
}

def setLed(power){
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

def on(){
	sendCommand(makePayload("power", "on"), null)
}

def off(){
	sendCommand(makePayload("power", "off"), null)
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
        
       	sendEvent(name:"switch", value: jsonObj.state.power ? "on" : "off")
        sendEvent(name:"led", value: (jsonObj.state.led ? "on" : "off"))
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer ? "on" : "off"))
        sendEvent(name:"temperature", value: jsonObj.properties.temperature.value, unit: "C")
        sendEvent(name:"relativeHumidity", value: jsonObj.properties.relativeHumidity, unit: "%")
        sendEvent(name:"targetHumidity", value: jsonObj.properties.targetHumidity,  unit: "%")
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def makePayload(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
    return makeCommand(body)
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
