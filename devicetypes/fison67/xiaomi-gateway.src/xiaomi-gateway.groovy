/**
 *  Xiaomi Gateway (v.0.0.1)
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
	definition (name: "Xiaomi Gateway", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
         
        attribute "switch", "string"
        attribute "color", "string"
        attribute "brightness", "string"
        
        attribute "lastCheckin", "Date"
         
        command "setColor"
        command "setBrightness"
        
        command "on"
        command "off"
        
        command "playMusic"
        command "stopMusic"
        
        command "alarmPoliceCar1"
        command "alarmPoliceCar2"
        command "alarmAccident"
        command "alarmCountdown"
        command "alarmGhost"
        command "alarmSniperRifle"
        command "alarmBattle"
        command "alarmAirRaid"
        command "alarmBark"
        
        command "bellDoor"
        command "bellKnock"
        command "bellAmuse"
        command "bellAlarmClock"
        
        command "alarmClockMiMix"
        command "alarmClockEnthusiastic"
        command "alarmClockGuitarClassic"
        command "alarmClockIceWorldPiano"
        command "alarmClockLeisureTime"
        command "alarmClockChildHood"
        command "alarmClockMorningStreamLiet"
        command "alarmClockMusicBox"
        command "alarmClockOrange"
        command "alarmClockThinker"
        
        command "alarmCustom"
	}


	simulator {
	}

	preferences {
		input name:"volume", type:"number", title:"Volume", range: "0..100", defaultValue:50, description:"Gateway Alarm Volume"
	}

	tiles {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"off", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"off", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}')
            }
            
            tileAttribute ("device.brightness", key: "SLIDER_CONTROL") {
                attributeState "brightness", action:"setBrightness"
            }
            tileAttribute ("device.color", key: "COLOR_CONTROL") {
                attributeState "color", action:"setColor"
            }
		}
        
        standardTile("stopMusic", "device.stopMusic", inactiveLabel: false, width: 2, height: 2) {
            state "stop", label:'STOP', action:"stopMusic", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc"
        }
        
       	standardTile("alarm1", "device.alarm1", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmPoliceCar1", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm2", "device.alarm2", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmPoliceCar2", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm3", "device.alarm3", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmAccident", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm4", "device.alarm4", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmAccident", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm5", "device.alarm5", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmCountdown", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm6", "device.alarm6", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmGhost", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm7", "device.alarm7", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmSniperRifle", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm8", "device.alarm8", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmBattle", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm9", "device.alarm9", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmAirRaid", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm10", "device.alarm10", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmBark", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm11", "device.alarm11", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"bellDoor", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm12", "device.alarm12", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"bellKnock", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm13", "device.alarm13", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"bellAmuse", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm14", "device.alarm14", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"bellAlarmClock", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm15", "device.alarm15", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockMiMix", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm16", "device.alarm16", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockEnthusiastic", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm17", "device.alarm17", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockGuitarClassic", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm18", "device.alarm18", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockIceWorldPiano", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm19", "device.alarm19", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockLeisureTime", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm20", "device.alarm20", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockChildHood", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm21", "device.alarm21", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockMorningStreamLiet", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm22", "device.alarm22", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockMusicBox", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm23", "device.alarm23", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockOrange", icon:"st.Entertainment.entertainment3"
        }
        standardTile("alarm24", "device.alarm24", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmClockThinker", icon:"st.Entertainment.entertainment3"
        }
        
        standardTile("alarm25", "device.alarm25", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"alarmCustom", icon:"st.Entertainment.entertainment3"
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
 	switch(params.key){
    case "power":
    	log.debug "MI >> power " + (params.data == "true" ? "on" : "off")
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off") )
    	break;
    case "color":
    	def colors = params.data.split(",")
        String hex = String.format("#%02x%02x%02x", colors[0].toInteger(), colors[1].toInteger(), colors[2].toInteger());  
    	sendEvent(name:"color", value: hex )
    	break;
    case "brightness":
    	sendEvent(name:"brightness", value: params.data )
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def playMusic(id, volume){
	log.debug "playMusic >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "playMusic",
        "data": id,
        "subData": volume
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def stopMusic(){
	log.debug "stopMusic >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "stopMusic"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBrightness(brightness){
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
	log.debug "setColor >> ${state.id}"
    log.debug "${color.hex}"
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



def makeAlarmContent(id){
	def body = [
        "id": state.id,
        "cmd": "playMusic",
        "data": id,
        "subData": settings.volume
    ]
    return body
}

def alarmPoliceCar1(){
	log.debug "alarmPoliceCar1 >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(0) ) , null)
}

def alarmPoliceCar2(){
	log.debug "alarmPoliceCar2 >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(1) ) , null)
}

def alarmAccident(){
	log.debug "alarmAccident >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(2) ) , null)
}

def alarmCountdown(){
	log.debug "alarmCountdown >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(3) ) , null)
}

def alarmGhost(){
	log.debug "alarmGhost >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(4) ) , null)
}

def alarmSniperRifle(){
	log.debug "alarmSniperRifle >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(5) ) , null)
}

def alarmBattle(){
	log.debug "alarmBattle >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(6) ) , null)
}

def alarmAirRaid(){
	log.debug "alarmAirRaid >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(7) ) , null)
}

def alarmBark(){
	log.debug "alarmBark >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(8) ) , null)
}

def bellDoor(){
	log.debug "bellDoor >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(10) ) , null)
}

def bellKnock(){
	log.debug "bellKnock >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(11) ) , null)
}

def bellAmuse(){
	log.debug "bellAmuse >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(12) ) , null)
}

def bellAlarmClock(){
	log.debug "bellAlarmClock >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(13) ) , null)
}

def alarmClockMiMix(){
	log.debug "alarmClockMiMix >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(20) ) , null)
}

def alarmClockEnthusiastic(){
	log.debug "alarmClockEnthusiastic >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(21) ) , null)
}

def alarmClockGuitarClassic(){
	log.debug "alarmClockGuitarClassic >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(22) ) , null)
}

def alarmClockIceWorldPiano(){
	log.debug "alarmClockIceWorldPiano >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(23) ) , null)
}

def alarmClockLeisureTime(){
	log.debug "alarmClockLeisureTime >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(24) ) , null)
}

def alarmClockChildHood(){
	log.debug "alarmClockChildHood >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(25) ) , null)
}

def alarmClockMorningStreamLiet(){
	log.debug "alarmClockMorningStreamLiet >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(26) ) , null)
}

def alarmClockMusicBox(){
	log.debug "alarmClockMusicBox >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(27) ) , null)
}

def alarmClockOrange(){
	log.debug "alarmClockOrange >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(28) ) , null)
}

def alarmClockThinker(){
	log.debug "alarmClockThinker >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(29) ) , null)
}

def alarmCustom(){
	log.debug "alarmCustom >> ${state.id}"
    sendCommand( makeCommand( makeAlarmContent(10001) ) , null)
}
