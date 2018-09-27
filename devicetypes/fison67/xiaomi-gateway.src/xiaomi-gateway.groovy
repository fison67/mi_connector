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
        capability "Alarm"
        
		capability "Color Control"
        capability "Switch Level"
        capability "Light"
         
        attribute "lastCheckin", "Date"
        
        command "findChild"
        command "chartPower"
        command "chartIlluminance"
        
     	command "sirenByID"
	}


	simulator {
	}

	preferences {
		input name:"mode", type:"enum", title:"Alarm Setthing?", options:["Both", "Siren", "Strobe"], description:"What will you do when you turn on?"
		input name:"selectedColor", type:"string", title:"Strobe Color", description:"Type a color (Hex Value)"
		input name:"selectedBrightness", type:"number", title:"Strobe Brightness", range: "0..100", defaultValue:100, description:""
		input name:"volume", type:"number", title:"Siren Volume", range: "0..100", defaultValue:10, description:"Gateway Siren Volume(0 ~ 100)"
		input name:"alarm", type:"enum", title:"Siren Type", required: false, options: ["Police Car#1", "Police Car#2", "Accident", "Count Down", "Ghost", "Sniper Rifle", "Battle", 
        "Air Raid", "Bark", "Door", "Knock", "Amuse", "Alarm Clock", "Clock MiMix", "Clock Enthusiastic", "Guitar Classic", "Ice World Piano",
        "Leisure Time", "ChildHood", "Morning StreamLiet", "MusicBox", "Orange", "Thinker"]
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
            state "default", label:"", action:"siren", icon:"st.Entertainment.entertainment3"
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
    	sendEvent(name:"illuminance", value: params.data.replace(" lx","").replace(",","") as int)
    	break;
    }
    
    updateLastTime()
}

def siren(){
    def sirenIndex = 0
    def sirenType = settings.alarm
    if(sirenType != null){
    	switch(sirenType){
        case "Police Car#1":
        	sirenIndex = 0
        	break;
        case "Police Car#2":
        	sirenIndex = 1
        	break;
        case "Accident":
        	sirenIndex = 2
        	break;
        case "Count Down":
        	sirenIndex = 3
        	break;
        case "Ghost":
        	sirenIndex = 4
        	break;
        case "Sniper Rifle":
        	sirenIndex = 5
        	break;
        case "Battle":
        	sirenIndex = 6
        	break;
        case "Air Raid":
        	sirenIndex = 7
        	break;
        case "Bark":
        	sirenIndex = 8
        	break;
        case "Door":
        	sirenIndex = 10
        	break;
        case "Knock":
        	sirenIndex = 11
        	break;
        case "Amuse":
        	sirenIndex = 12
        	break;
        case "Alarm Clock":
        	sirenIndex = 13
        	break;
        case "Clock MiMix":
        	sirenIndex = 20
        	break;
        case "Clock Enthusiastic":
        	sirenIndex = 21
        	break;
        case "Guitar Classic":
        	sirenIndex = 22
        	break;
        case "Ice World Piano":
        	sirenIndex = 23
        	break;
        case "Leisure Time":
        	sirenIndex = 24
        	break;
        case "ChildHood":
        	sirenIndex = 25
        	break;
        case "Morning StreamLiet":
        	sirenIndex = 26
        	break;
        case "MusicBox":
        	sirenIndex = 27
        	break;
        case "Orange":
        	sirenIndex = 28
        	break;
        case "Thinker":
        	sirenIndex = 29
        	break;
        }
    }
	log.debug "Request a siren >> ${sirenType}(${sirenIndex})" 
    sendCommand( makeCommand( makeAlarmContent(sirenIndex) ) , null)
    sendEvent(name:"switch", value: "on")
}

def strobe(){
	def color = state.selectedColor
    if(color == null){
    	color = "#f44259"
    }
	setColorByHex(color)
    setLevel(settings.selectedBrightness as int)
}

def both(){
	strobe()
    siren()
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

def offSiren(){
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
	log.debug "setColor >> ${state.id} >> ${color.hex}"
    setColorByHex(color.hex)
}

def setColorByHex(color){
    def body = [
        "id": state.id,
        "cmd": "color",
        "data": color
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	def mode = settings.mode
    if(mode == null){
    	mode = "Strobe"
    }
    
    log.debug "Truning On. Mode >> ${mode}"
    
    if(mode == "Strobe"){
   	 	ledOn()
    	strobe()
    }else if(mode == "Siren"){
    	siren()
    }else if(mode == "Both"){
    	ledOn()
    	strobe()
    	siren()
    }
}

def off(){
	ledOff()
    offSiren()
    sendEvent(name:"switch", value: "off")
}

def ledOn(){
	log.debug "LED On >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def ledOff(){
    log.debug "LED Off >> ${state.id}"
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
    log.debug options
    return options
}

def sirenByID(id){
    sendCommand( makeCommand( makeAlarmContent(id) ) , null)
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
