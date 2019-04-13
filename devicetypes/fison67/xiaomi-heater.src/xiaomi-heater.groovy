/**
 *  Xiaomi Heater(v.0.0.1)
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
import groovy.transform.Field

@Field 
LANGUAGE_MAP = [
    "temp": [
        "Korean": "온도",
        "English": "Temp"
    ],
    "tarT": [
        "Korean": "목표온도",
        "English": "Target"
    ]
]


metadata {
	definition (name: "Xiaomi Heater", namespace: "fison67", author: "fison67") {
		capability "Actuator"
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
		capability "Thermostat"
		capability "Thermostat Mode"
		capability "Thermostat Fan Mode"
		capability "Thermostat Heating Setpoint"
		capability "Thermostat Operating State"
		capability "Configuration"
		capability "Health Check"
		capability "Refresh"
		capability "Sensor"

        attribute "childlock", "enum", ["on", "off"]        
        attribute "buzzer", "enum", ["on", "off"]        
        attribute "ledBrightness", "enum", ["off", "dim", "bright"]
        attribute "lastCheckin", "Date"

		command "Xiaomiheater"
        command "noTemp"
        command "noHumi"
		command "lowerHeatingSetpoint"
		command "raiseHeatingSetpoint"
		command "lowerCoolSetpoint"
		command "raiseCoolSetpoint"
		command "childLockOn"
		command "childLockOff"
        command "buzzerOn"
        command "buzzerOff"
        command "setBright"
        command "setBrightDim"
        command "setBrightOff"         
		command "settimer"        
        command "starttimer"
        command "stoptimer"
        command "setLevel"
        command "setTimer"
        command "setHeatingSetpoint"
        command "setThermostatMode"
         
	}


	simulator {
	}
    
	preferences {
        input name: "selectedLang", title:"Select a language" , type: "enum", required: true, options: ["English", "Korean"], defaultValue: "English", description:"Language for DTH"
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
    	sendEvent(name:"humidity", value: params.data )
    	break;
    case "power":
        sendEvent(name:"thermostatMode", value: (params.data == "true" ? "heat" : "off"))
        sendEvent(name:"mode2", value: (params.data == "true" ? state.st : "off"))
    	break;
    case "temperature":
	    def Settemp = device.currentValue('heatingSetpoint')
        state.st = Settemp
	    log.debug "state.st ${state.st}"
   		def para = "${params.data}"
		String data = para
		def st = data.replace("C","");
		def stf = Float.parseFloat(st)
		def tem = Math.round(stf*100)/10 as float
        sendEvent(name:"temperature", value: tem )
        sendEvent(name:"thermostatOperatingState", value: (tem > Settemp ? "idle" : "heating" ))
    	break;    
    //case "targetTemperature":
      //  sendEvent(name:"level", value: params.data)
        case "targetTemperature":
	    def Curtemp = device.currentValue('temperature')
		def para = "${params.data}"
		String data = para
		def st = data.replace("C","");
		def stf = Float.parseFloat(st)
		def tem = Math.round(stf*10)/10
        sendEvent(name:"heatingSetpoint", value: tem)
        sendEvent(name:"thermostatOperatingState", value: (tem <= Curtemp ? "idle" : "heating" ))
        sendEvent(name:"mode2", value: (device.currentValue('thermostatMode') == "heat" ? tem : "off"))
    	break;
    case "ledBrightness":
        sendEvent(name:"ledBrightness", value: params.data)
    	break;        
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off") )
        break;
    case "childLock":
    	sendEvent(name:"childlock", value: (params.data == "on" ? "on" : "off") )
        break;
    case "powerOffTime":
    	state.rsec = params.data as int
    	state.trmin = (state.rsec/60) as int
        state.rhour = (state.trmin/60) as int
        state.rmin = state.trmin - state.rhour*60
    	sendEvent(name:"remaintime", value: "${state.rhour}h ${state.rmin}min")
        break;
        
    }
    
    def nowT = new Date().format("HH:mm:ss", location.timeZone)
    def nowD = new Date().format("yyyy-MM-dd", location.timeZone)
    sendEvent(name: "lastCheckin", value: nowD + "\n" + nowT)
}

def settimer(setmin){
	log.debug "setmin >> ${setmin}"
    state.setmin = setmin
	state.hour = (setmin/60) as int
    state.min = setmin - state.hour*60
	log.debug "st.hour >> ${state.hour}, st.min >> ${state.min}"
    sendEvent(name: "settime", value: "${state.hour}h ${state.min}min")
    sendEvent(name: "settimer", value: setmin )
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

def raiseHeatingSetpoint() {
    def currentSettemp = device.currentValue('heatingSetpoint')
	log.debug ".current.heatingSetpoint >> ${currentSettemp}"
    if (currentSettemp <= 31) {
    	alterSetpoint(true, currentSettemp)
    }
}

def lowerHeatingSetpoint() {
    def currentSettemp = device.currentValue('heatingSetpoint')
	log.debug ".current.heatingSetpoint >> ${currentSettemp}"
    if (currentSettemp >= 17) {
    	alterSetpoint(false, currentSettemp)
    }
}

def alterSetpoint(raise, currentSettemp) {
	log.debug "alterSetpoint >> ${currentSettemp}"
    def crst = currentSettemp as int
	log.debug "crst >> ${crst}"
    if (raise == true) {
    	log.debug "true"
		log.debug "crst >> ${crst}"
    	state.value = crst + 1
    } else {
    	log.debug "nottrue"
		state.value = crst - 1
    }
    def value = state.value
    setLevel(value)
}

def setLevel(level){
	log.debug "tarSetpoint >> ${level}"
    def tartemp = level as int
    def body = [
        "id": state.id,
        "cmd": "targetTemperature",
        "data": tartemp
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setHeatingSetpoint(level){
	log.debug "tarSetpoint >> ${level}"
    def tartemp = level as int
    def body = [
        "id": state.id,
        "cmd": "targetTemperature",
        "data": tartemp
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def starttimer(){
	def setsec = state.setmin*60
	log.debug "starttimer >> statemin: ${state.min}, setsec >> ${setsec}"
	setTimer(setsec)
}

def stoptimer(){
	//def setsec = state.setmin*0
	setTimer(0)
}

def setTimer(timesec){
	log.debug "setTimer >> ${state.id}, sec >> ${timesec}"
    def body = [
        "id": state.id,
        "cmd": "powerOffTime",
        "data": timesec
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setThermostatMode(value){
	log.debug "google >> ${value}"
    def mode = value == "off" ? "off" : "on"
	log.debug "googlem >> ${mode}"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": mode
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def heat(){
	log.debug "ON >> ${state.id}"
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

def childLockOn(){
	log.debug "childLockOn >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "childLock",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def childLockOff(){
	log.debug "childLockOff >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "childLock",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def powerofftime(){
	log.debug "powerOffTime >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "powerOffTime",
        "data": 7200
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

def setBright(){
	log.debug "setBright >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": 0
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBrightDim(){
	log.debug "setDim >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": 1
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBrightOff(){
	log.debug "setBrightOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": 2
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def updated() {
    refresh()
    setLanguage(settings.selectedLang)
}

def setLanguage(language){
    log.debug "Languge >> ${language}"
	state.language = language
	state.temp = LANGUAGE_MAP["temp"][language]
	sendEvent(name:"target", value: LANGUAGE_MAP["tarT"][language] )
}

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
    	
        sendEvent(name:"temperature", value: jsonObj.properties.temperature.value * 10 as int)
        sendEvent(name:"relativeHumidity", value: jsonObj.properties.relativeHumidity)
        sendEvent(name:"heatingSetpoint", value: jsonObj.properties.targetTemperature.value)
        sendEvent(name:"thermostatMode", value: (jsonObj.properties.power== "true" ? "heat" : "off"))
	    
        def nowT = new Date().format("HH:mm:ss", location.timeZone)
        def nowD = new Date().format("yyyy-MM-dd", location.timeZone)
    	sendEvent(name: "lastCheckin", value: nowD + "\n" + nowT, displayed: false)
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}


def sendCommand(options, _callback){
	def myhubAction = new hubitat.device.HubAction(options, null, [callback: _callback])
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
