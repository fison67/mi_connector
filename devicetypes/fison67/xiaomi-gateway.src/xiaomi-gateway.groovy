/**
 *  Xiaomi Gateway (v.0.0.2)
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
        capability "Illuminance Measurement"
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        
		capability "Color Control"
        capability "Switch Level"
        capability "Light"
         
        attribute "lastCheckin", "Date"
        
        command "findChild"
        command "chartPower"
        command "chartIlluminance"
        
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
		input name:"volume", type:"number", title:"Volume", range: "0..100", defaultValue:10, description:"Gateway Alarm Volume"
        input "historyDayCount", "number", title: "Maximum days for single graph", description: "", defaultValue:1, displayDuringSetup: true
		input "powerHistoryDataMaxCount", "number", title: "Maximum Power data count", description: "0 is max", defaultValue:100, displayDuringSetup: true
		input "illuminanceHistoryDataMaxCount", "number", title: "Maximum Illuminance data count", description: "0 is max", defaultValue:0, displayDuringSetup: true
	}

	tiles {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: false){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"off", icon:"https://github.com/fison67/mi_connector/blob/master/icons/gateway_on.png?raw=true", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"on", icon:"https://github.com/fison67/mi_connector/blob/master/icons/gateway_off.png?raw=true", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"off", icon:"https://github.com/fison67/mi_connector/blob/master/icons/gateway_on.png?raw=true", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"on", icon:"https://github.com/fison67/mi_connector/blob/master/icons/gateway_off.png?raw=true", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}')
            }
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"setLevel"
            }
            tileAttribute ("device.color", key: "COLOR_CONTROL") {
                attributeState "color", action:"setColor"
            }
		}
        
        valueTile("illuminance", "device.illuminance", width: 2, height: 2) {
            state "val", label:'${currentValue}lx', defaultState: true,
                backgroundColors:[
                    [value: 100, color: "#153591"],
                    [value: 200, color: "#1e9cbb"],
                    [value: 300, color: "#90d2a7"],
                    [value: 600, color: "#44b621"],
                    [value: 900, color: "#f1d801"],
                    [value: 1200, color: "#d04e00"],
                    [value: 1500, color: "#bc2323"]
                ]
        }
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
        
        standardTile("stopMusic", "device.stopMusic", inactiveLabel: false, width: 2, height: 2) {
            state "stop", label:'STOP', action:"stopMusic", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc"
        }
        
        standardTile("chartMode", "device.chartMode", width: 2, height: 1, decoration: "flat") {
			state "chartPower", label:'Power', nextState: "chartIlluminance", action: 'chartPower'
			state "chartIlluminance", label:'Illuminance', nextState: "chartPower", action: 'chartIlluminance'
		}
        
        carouselTile("history", "device.image", width: 6, height: 4) { }
        
        standardTile("findChild", "device.findChild", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"findChild", icon:"https://raw.githubusercontent.com/fison67/mi_connector/master/icons/find_child.png"
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
    log.debug "${params.key} : ${params.data}"
    
 	switch(params.key){
    case "power":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off") )
    	break;
    case "color":
    	def colors = params.data.split(",")
    	sendEvent(name:"color", value: String.format("#%02x%02x%02x", colors[0].toInteger(), colors[1].toInteger(), colors[2].toInteger()) )
    	break;
    case "brightness":
    	sendEvent(name:"level", value: params.data )
    	break;
    case "illuminance":
    	sendEvent(name:"illuminance", value: params.data.replace(" lx","") )
    	break;
    }
    
    updateLastTime()
}

def playMusic(id, volume){
	log.debug "playMusic >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "playMusic",
        "data": id,
        "subData": volume.toInteger()
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

def setLevel(brightness){
	log.debug "setLevel >> ${state.id}, val=${brightness}"
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

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)

		if(jsonObj.properties.illuminance != null){
        	sendEvent(name:"illuminance", value: jsonObj.properties.illuminance.value)
        }
        sendEvent(name:"brightness", value: jsonObj.state.brightness)
        sendEvent(name:"switch", value: jsonObj.state.brightness == 0 ? "off" : "on")
        sendEvent(name:"color", value: String.format("#%02x%02x%02x", jsonObj.state.rgb.red.toInteger(), jsonObj.state.rgb.green.toInteger(), jsonObj.state.rgb.blue.toInteger()) )
        
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
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

def playAlarmByID(id){
	log.debug("playAlarmByID >> " + id)
}

def makeAlarmContent(id){
	if(settings.volume == null){
    	settings.volume = 10
    }
	def body = [
        "id": state.id,
        "cmd": "playMusic",
        "data": id,
        "subData": settings.volume.toInteger()
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

def makeURL(type, name){
	def sDate
    def eDate
	use (groovy.time.TimeCategory) {
      def now = new Date()
      def day = settings.historyDayCount == null ? 1 : settings.historyDayCount
      sDate = (now - day.days).format( 'yyyy-MM-dd HH:mm:ss', location.timeZone )
      eDate = now.format( 'yyyy-MM-dd HH:mm:ss', location.timeZone )
    }
	return [
        uri: "http://${state.externalAddress}",
        path: "/devices/history/${state.id}/${type}/${sDate}/${eDate}/image",
        query: [
        	"name": name
        ]
    ]
}

def findChild(){

    def options = [
     	"method": "GET",
        "path": "/devices/gateway/${state.id}/findChild",
        "headers": [
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ]
    ]
    
    sendCommand(options, null)
}

def chartPower() {
	def url = makeURL("power", "Power")
    if(settings.powerHistoryDataMaxCount > 0){
    	url.query.limit = settings.powerHistoryDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "power")
    }
}

def chartIlluminance() {
	def url = makeURL("illuminance", "Illuminance")
    if(settings.illuminanceHistoryDataMaxCount > 0){
    	url.query.limit = settings.illuminanceHistoryDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "illuminance")
    }
}

def processImage(response, type){
	if (response.status == 200 && response.headers.'Content-Type'.contains("image/png")) {
        def imageBytes = response.data
        if (imageBytes) {
            try {
                storeImage(getPictureName(type), imageBytes)
            } catch (e) {
                log.error "Error storing image ${name}: ${e}"
            }
        }
    } else {
        log.error "Image response not successful or not a jpeg response"
    }
}

private getPictureName(type) {
  def pictureUuid = java.util.UUID.randomUUID().toString().replaceAll('-', '')
  return "image" + "_$pictureUuid" + "_" + type + ".png"
}
