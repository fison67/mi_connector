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
	definition (name: "Xiaomi Heater2", namespace: "fison67", author: "fison67") {

		capability "Actuator"
		capability "Temperature Measurement"
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
         
	}


	simulator {
	}
    
	preferences {
        input name: "selectedLang", title:"Select a language" , type: "enum", required: true, options: ["English", "Korean"], defaultValue: "English", description:"Language for DTH"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"temperature", type:"generic", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("temperature", label:'${currentValue}°', icon: "st.alarm.temperature.normal",
						backgroundColors:[
                        // Fahrenheit color set
                        [value: 0, color: "#153591"],
                        [value: 5, color: "#1e9cbb"],
                        [value: 10, color: "#90d2a7"],
                        [value: 15, color: "#44b621"],
                        [value: 20, color: "#f1d801"],
                        [value: 25, color: "#d04e00"],
                        [value: 30, color: "#bc2323"],
                        [value: 44, color: "#1e9cbb"],
                        [value: 59, color: "#90d2a7"],
                        [value: 74, color: "#44b621"],
                        [value: 84, color: "#f1d801"],
                        [value: 95, color: "#d04e00"],
                        [value: 96, color: "#bc2323"]
						// Celsius color set (to switch, delete the 13 lines above anmd remove the two slashes at the beginning of the line below)
                        //[value: 0, color: "#153591"], [value: 7, color: "#1e9cbb"], [value: 15, color: "#90d2a7"], [value: 23, color: "#44b621"], [value: 28, color: "#f1d801"], [value: 35, color: "#d04e00"], [value: 37, color: "#bc2323"]
                    ]
				)
			}
            tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
        		attributeState("humidity", label:'${currentValue}%', unit:"%", defaultState: true)
    		}   
		}
		standardTile("thermostatMode", "device.thermostatMode") {
			state "off", action: "thermostatMode.heat", icon: "st.thermostat.heating-cooling-off", backgroundColor:"#dbd9d9", nextState: "..."
			state "heat", action: "thermostatMode.off", icon: "st.thermostat.heat", backgroundColor:"#f9c240", nextState: "..."
			state "...", label: "Updating...", backgroundColor: "#d8c597", nextState:"..."
		}
		standardTile("thermostatMode2", "device.mode2", width:2, height:2, inactiveLabel: false, decoration: "flat") {
			state "default", action: "thermostatMode.off", label:'${currentValue}°', icon: "st.thermostat.heat", nextState: "...", backgroundColor:"#f9b30e"
			state "off", action: "thermostatMode.heat", label: "OFF", icon: "st.thermostat.heating-cooling-off", nextState: "...", backgroundColor:"#ffffff"
			state "...", label: "Updating...", nextState:"..."
		}
        standardTile("thermostatFanMode", "device.thermostatFanMode", width:2, height:2, inactiveLabel: false, decoration: "flat") {
			state "auto", action:"thermostatFanMode.fanOn", nextState:"...", icon: "st.thermostat.fan-auto"
			state "on", action:"thermostatFanMode.fanAuto", nextState:"...", icon: "st.thermostat.fan-on"
			state "...", label: "Updating...", nextState:"...", backgroundColor:"#ffffff"
		}
		standardTile("lowerHeatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", action:"lowerHeatingSetpoint", icon:"st.thermostat.thermostat-left"
		}
		valueTile("heatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", label:'${currentValue}° heat', backgroundColor:"#ffffff"
		}
		standardTile("raiseHeatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", action:"raiseHeatingSetpoint", icon:"st.thermostat.thermostat-right"
		}
		standardTile("thermostatOperatingState", "device.thermostatOperatingState") {
			state "heating", label:'Heating', action: "", icon:"st.vents.vent-open", backgroundColor:"#f984a3"
			state "idle", label:'Idle', action: "", icon:"st.vents.vent", backgroundColor: "#e8ccd3"
		}
		standardTile("refresh", "device.thermostatMode") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("buzzer", "device.buzzer") {
            state "on", label:'Sound', action:"buzzerOff", icon: "st.custom.sonos.unmuted", backgroundColor:"#64c5fc", nextState:"turningOff"
            state "off", label:'Mute', action:"buzzerOn", icon: "st.custom.sonos.muted", backgroundColor:"#adc2ce", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"buzzerOff", backgroundColor:"#adc2ce", nextState:"turningOff"
            state "turningOff", label:'....', action:"buzzerOn", backgroundColor:"#64c5fc", nextState:"turningOn"
        }
        standardTile("ledBrightness", "device.ledBrightness") {
            state "0", label: 'Bright', action: "setBrightDim", icon: "st.illuminance.illuminance.bright", backgroundColor: "#e266ff", nextState:"dim"
            state "1", label: 'Dim', action: "setBrightOff", icon: "st.illuminance.illuminance.light", backgroundColor: "#eea5ff", nextState:"off"
            state "2", label: 'Off', action: "setBright", icon: "st.illuminance.illuminance.dark", backgroundColor: "#d0ccd1", nextState:"bright"            
        }
        standardTile("childlock", "device.childlock") {
            state "on", label:'Lock', action:"childLockOff", icon: "st.presence.house.secured", backgroundColor:"#96f259", nextState:"turningOff"
            state "off", label:'Unlock', action:"childLockOn", icon: "st.presence.house.unlocked", backgroundColor:"#c5ccc1", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"childLockOff", backgroundColor:"#c5ccc1", nextState:"turningOff"
            state "turningOff", label:'....', action:"childLockOn", backgroundColor:"#96f259", nextState:"turningOn"
        }        
        valueTile("settimertile", "device.settimer", height: 2, width: 1) {
            state "default", label:'OFF\nTime\nSet', backgroundColor:"#ffffff"
        }
		controlTile("settimer", "device.settimer", "slider", height: 1, width: 2, range:"(1..480)") {
	    	state "settimer", action:"settimer"
		}
        valueTile("settime", "device.settime", height: 1, width: 2, decoration: "flat") {
            state "default", label:'${currentValue}'
        }        
        valueTile("remaintime", "device.remaintime", height: 1, width: 2, decoration: "flat") {
            state "default", label:'${currentValue}'
        }        
        standardTile("starttimer", "device.starttimer") {
			state "default", label: "START", action: "starttimer", icon:"st.Health & Wellness.health7", backgroundColor:"#c7bbc9"
		}
        valueTile("remaintimertile", "device.settimer", height: 1, width: 2) {
            state "default", label:'Remain Time >>', backgroundColor:"#ffffff"
        }
        standardTile("stoptimer", "device.stoptimer") {
			state "default", label: "STOP", action: "stoptimer", icon:"st.Health & Wellness.health7", backgroundColor:"#c7bbc9"
		}

        main "thermostatMode2"
		details(["temperature", "lowerHeatingSetpoint", "heatingSetpoint", "raiseHeatingSetpoint", "thermostatMode", "thermostatOperatingState",
				 "buzzer", "ledBrightness", "childlock", "refresh", "settimertile", "settimer", "settime", "starttimer", "remaintimertile", "remaintime", "stoptimer"])
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
        sendEvent(name:"thermostatOperatingState", value: (tem >= Settemp ? "idle" : "heating" ))
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

def setLevel(value){
	log.debug "tarSetpoint >> ${value}"
    def body = [
        "id": state.id,
        "cmd": "targetTemperature",
        "data": value
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

def setTimer(level){
	log.debug "setLevel >> ${state.id}, sec >> ${level}"
    def body = [
        "id": state.id,
        "cmd": "powerOffTime",
        "data": level
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
	    
        def nowT = new Date().format("HH:mm:ss", location.timeZone)
        def nowD = new Date().format("yyyy-MM-dd", location.timeZone)
    	sendEvent(name: "lastCheckin", value: nowD + "\n" + nowT, displayed: false)
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
