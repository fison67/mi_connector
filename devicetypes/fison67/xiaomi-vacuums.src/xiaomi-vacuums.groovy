/**
 *  Xiaomi Vacuums (v.0.0.2)
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
	definition (name: "Xiaomi Vacuums", namespace: "fison67", author: "fison67") {
        capability "Switch"		
		capability "Fan Speed"
        capability "Battery"	
        capability "Refresh"	
        
        attribute "status", "string"
        attribute "cleanTime", "string"
        attribute "cleanArea", "NUMBER"
        attribute "in_cleaning", "string"
        
        attribute "mainBrushLeftLife", "NUMBER"
        attribute "sideBrushLeftLife", "NUMBER"
        attribute "filterLeftLife", "NUMBER"
        attribute "sensorLeftLife", "NUMBER"
        
        attribute "lastCheckin", "Date"
         
        command "find"
        command "clean"
        command "charge"
        command "paused"
        command "fanSpeed"
        command "spotClean"
        
        command "quiet"
        command "balanced"
        command "turbo"
        command "fullSpeed"
        command "setVolume"
        command "setVolumeWithTest"
	}

	simulator {}

	tiles {
		multiAttributeTile(name:"mode", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.mode", key: "PRIMARY_CONTROL") {
                attributeState "initiating", label:'${name}', backgroundColor:"#00a0dc", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"off"
                attributeState "charger-offline", label:'${name}', backgroundColor:"#000000", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum-ready.png?raw=true", action:"on"
                attributeState "waiting", label:'${name}',  backgroundColor:"#00a0dc", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"charge"
                attributeState "cleaning", label:'${name}', backgroundColor:"#4286f4", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_on.png?raw=true", action:"off"
                attributeState "returning", label:'${name}', backgroundColor:"#4e25a8", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_turning_off.png?raw=true", action:"on"
                attributeState "charging", label:'${name}',   backgroundColor:"#25a896", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"on"
                
                attributeState "charging-error", label:'${name}',  backgroundColor:"#ff2100", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"on"
                attributeState "paused", label:'${name}',  backgroundColor:"#09540d", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"on"
                
                attributeState "spot-cleaning", label:'${name}', backgroundColor:"#a0e812", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_on.png?raw=true", action:"off"
                attributeState "error", label:'${name}',   backgroundColor:"#ff2100", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_off.png?raw=true", action:"on"
                
                attributeState "shutting-down", label:'${name}',  backgroundColor:"#00a0dc", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_turning_off.png?raw=true", action:"on"
                attributeState "updating", label:'${name}',  backgroundColor:"#ffa0ea", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_turning_off.png?raw=true", action:"on"
                
                attributeState "docking", label:'${name}', backgroundColor:"#9049bc", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_turning_off.png?raw=true", action:"off"
                attributeState "zone-cleaning", label:'${name}',  backgroundColor:"#91f268", icon:"https://github.com/fison67/mi_connector/blob/master/icons/vacuum_on.png?raw=true", action:"off"
                
                attributeState "full", label:'${name}', backgroundColor:"#ffffff", icon:"st.Electronics.electronics1", action:"on"
                
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
            
		}
        
        standardTile("switch", "device.switch", inactiveLabel: false, width: 1, height: 1, canChangeIcon: true) {
            state "on", label:'${name}', action:"off", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "off", label:'${name}', action:"on", backgroundColor:"#ffffff", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"off", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "turningOff", label:'....', action:"on", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        
        valueTile("fanSpeed_label", "device.fanSpeed_label", decoration: "flat", width: 4, height: 1) {
            state "default", label:'Fan Speed: ${currentValue}'
        }
        valueTile("empty1_label", "", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Volume'
        }
        standardTile("quiet", "device.quiet") {
			state "default", label: "Quiet", action: "quiet", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfMTAy/MDAxNTIyMzIzNjE4NjE2.2N1NVfE2fmK85H1EhwK_gqEs0FK0qSaJ1KCimGnxZFcg.CAcpOhL3yJXAlvS-JoBcGz1Uf2UnjuICzGs4hBwwK8kg.PNG.shin4299/Fan_20.png?type=w580", backgroundColor:"#b1d6de"
		}
        standardTile("balanced", "device.balanced") {
			state "default", label: "Balanced", action: "balanced", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfMzIg/MDAxNTIyMzIzNjE4NjE2.8HySZX7X1Lb821PxhP96mahNs7dxuYcmDYqy-8bczT8g.wMnYS-sYxbbqXBFrK06w7fT_I6sBb1IcmznRVMOrjjEg.PNG.shin4299/Fan_60.png?type=w580", backgroundColor:"#b1d6de"
		}
        standardTile("turbo", "device.turbo") {
			state "default", label: "Turbo", action: "turbo", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNTgg/MDAxNTIyMzIzNDI2NjE2.86i1P_l290aYfdzh9fATsl3VA-dCVAba9ir_1Ym3mlIg.gyZmaDisBZAbtzzSg-55iwk2ie1ijd64x4ZTo5Jbu4Eg.PNG.shin4299/Fan_30.png?type=w580", backgroundColor:"#b1d6de"
		}
        standardTile("fullSpeed", "device.fullSpeed") {
			state "default", label: "Full Speed", action: "fullSpeed", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfMjIw/MDAxNTIyMzIzNjE4NjIx.t6DneqY6JyAZAicutP3NtV9Vf0wWGNAXWnVDIxnL_0gg.-5LlfL2aVTqW3ziuAXWOHFQ6C436d5-XZc_NVHxgS9Mg.PNG.shin4299/Fan_120.png?type=w580", backgroundColor:"#b1d6de"
		}
        
        standardTile("fanSpeed", "device.fanSpeed", inactiveLabel: false, width: 2, height: 2) {
            state "quiet", label:'Quiet', action:"balanced", backgroundColor:"#00a0dc", nextState:"balanced"
            state "balanced", label:'Balanced', action:"turbo", backgroundColor:"#1000ff", nextState:"turbo"
            state "turbo", label:'Turbo', action:"fullSpeed", backgroundColor:"#9a71f2", nextState:"fullSpeed"
            state "fullSpeed", label:'Max', action:"quiet", backgroundColor:"#aa00ff", nextState:"quiet"
        }
        
        standardTile("paused", "device.paused", width: 1, height: 1) {
            state "paused", label:'Pause', action:"paused", backgroundColor:"#00a0dc"
        }
        standardTile("restart", "device.restart", width: 1, height: 1) {
            state "restart", label:'Restart', action:"on", backgroundColor:"#09540d"
        }
            
        
        standardTile("charge", "device.charge", width: 1, height: 1 ) {
            state "charge", label:'Charge', action:"charge",  backgroundColor:"#25a896"
        }
        
        standardTile("spot", "device.spot", width: 1, height: 1 ) {
            state "spot", label:'Spot', action:"spotClean",  backgroundColor:"#2ca6e8"
        }
        
        standardTile("find", "device.find", width: 1, height: 1 ) {
            state "find", label:'Find Me', action:"find",  backgroundColor:"#1cffe8"
        }
        
        valueTile("battery", "device.battery",  height: 1, width: 1) {
            state "val", label:'${currentValue}%', defaultState: true,
            	backgroundColors:[
                    [value: 10, color: "#ff002a"],
                    [value: 20, color: "#f4425f"],
                    [value: 30, color: "#ef7085"],
                    [value: 40, color: "#ea8f9e"],
                    [value: 50, color: "#edadb7"],
                    [value: 60, color: "#a9aee8"],
                    [value: 70, color: "#7f87e0"],
                    [value: 80, color: "#505bd3"],
                    [value: 90, color: "#2131e0"]
                ]
        }
        
        controlTile("volume", "device.volume", "slider", height: 1, width: 2, inactiveLabel: false, range:"(0..100)") {
			state ("volume", label:'${currentValue}', action:"setVolumeWithTest")
		}
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
        
        
        valueTile("empty2_label", "", decoration: "flat", width: 4, height: 1) {
            state "default", label:''
        }
        valueTile("label_clean_time", "", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Clean Time'
        }
        valueTile("cleanTime", "device.cleanTime", decoration: "flat", width: 4, height: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("label_clean_area", "", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Clean Area'
        }
        valueTile("cleanArea", "device.cleanArea", decoration: "flat", width: 4, height: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("label_main_brush", "", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Main Brush'
        }
        valueTile("mainBrushLeftTime", "device.mainBrushLeftTime", decoration: "flat", width: 4, height: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("label_side_brush", "", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Side Brush'
        }
        valueTile("sideBrushLeftTime", "device.sideBrushLeftTime", decoration: "flat", width: 4, height: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("label_filter", "", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Filter'
        }
        valueTile("filterTime", "device.filterTime", decoration: "flat", width: 4, height: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("label_sensor", "", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Sensor'
        }
        valueTile("sensorTime", "device.sensorTime", decoration: "flat", width: 4, height: 1) {
            state "default", label:'${currentValue}'
        }
        main (["mode"])
      	details(["mode", "switch", "paused", "restart", "spot", "charge", "find", "fanSpeed_label", "empty1_label", "quiet", "balanced", "turbo", "fullSpeed", "volume", "battery", "refresh", 
        "empty2_label", "label_clean_time", "cleanTime", "label_clean_area", "cleanArea", "label_main_brush", "mainBrushLeftTime", "label_side_brush", "sideBrushLeftTime", "label_filter", "filterTime", "label_sensor", "sensorTime"])
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
	log.debug "${params.key} >> ${params.data}"
    
 	switch(params.key){
    case "mode":
    	sendEvent(name:"mode", value: params.data )
        if(params.data == "paused"){
    		sendEvent(name:"switch", value: "paused" )
        }
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data)
    	break;
    case "fanSpeed":
    	def val = params.data.toInteger()
        def _value
        switch(val){
        case 38:
        	_value = "Quiet"
        	break;
        case 60:
        	_value = "Balanced"
        	break;
        case 77:
        	_value = "Turbo"
        	break;
        case 90:
        	_value = "Full Speed"
        	break;
        }
    	sendEvent(name:"fanSpeed_label", value: _value )
    	break;
    case "cleaning":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off") )
       	sendEvent(name:"paused", value: params.data == "true" ? "paused" : "restart" )     
    	break;
    case "volume":
    	sendEvent(name:"volume", value: params.data )
    	break;
    case "mainBrushWorkTime":
    	def obj = getFilterLeftTime(params.data as float, 300)
    	sendEvent(name:"mainBrushLeftLife", value: obj[1], displayed: false)
        setValueTime2("mainBrushLeftTime", obj[0], obj[1])
    	break
    case "sideBrushWorkTime":
    	def obj = getFilterLeftTime(params.data as float, 200)
    	sendEvent(name:"sideBrushLeftLife", value: obj[1], displayed: false)
        setValueTime2("sideBrushLeftTime", obj[0], obj[1])
    	break
    case "sensorDirtyTime":
    	def obj = getFilterLeftTime(params.data as float, 30)
    	sendEvent(name:"sensorLeftLife", value: obj[1], displayed: false)
        setValueTime2("filterTime", obj[0], obj[1])
    	break
    case "filterWorkTime":
    	def obj = getFilterLeftTime(params.data as float, 150)
    	sendEvent(name:"filterLeftLife", value: obj[1], displayed: false)
        setValueTime2("sensorTime", obj[0], obj[1])
    	break
    case "cleanTime":
    	sendEvent(name:"cleanTime", value: formatSeconds(params.data as int), displayed: false)
    	break    
    case "cleanArea":
    	sendEvent(name:"cleanArea", value: params.data, displayed: false)
    	break
    }
    
    
    updateLastTime()
}

public String formatSeconds(int timeInSeconds){
    int secondsLeft = timeInSeconds % 3600 % 60;
    int minutes = Math.floor(timeInSeconds % 3600 / 60);
    int hours = Math.floor(timeInSeconds / 3600);

    String HH = hours < 10 ? "0" + hours : hours;
    String MM = minutes < 10 ? "0" + minutes : minutes;
    String SS = secondsLeft < 10 ? "0" + secondsLeft : secondsLeft;

    return HH + ":" + MM + ":" + SS;
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed: false)
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

def setVolume(volume){
	log.debug "setVolume >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "volume",
        "data": volume
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setVolumeWithTest(volume){
	log.debug "setVolume >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "volumeWithTest",
        "data": volume
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def quiet(){
    log.debug "quiet >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "quiet"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def balanced(){
    log.debug "balanced >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "balanced"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def turbo(){
    log.debug "turbo >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "turbo"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def fullSpeed(){
    log.debug "fullSpeed >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fullSpeed"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def spotClean(){
	log.debug "spotClean >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "spotClean"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    
    sendEvent(name:"spot", value: "on" )
}

def charge(){
	log.debug "charge >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "charge"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def paused(){
	log.debug "paused >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "pause"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def start(){
    log.debug "start >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "start"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def find(){
    log.debug "find >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "find"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
		log.debug "On >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "clean"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "stop"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

/*
def timer(mSecond, function){
	def now = new Date()
	def runTime = new Date(now.getTime() + mSecond)
	runOnce(runTime, function);
}
*/

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        
        sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        
        sendEvent(name:"mode", value: jsonObj.state.state)
        
        sendEvent(name:"switch", value: (jsonObj.properties.cleaning ? "on" : "off") )
       	sendEvent(name:"paused", value: jsonObj.properties.cleaning ? "paused" : "restart" )  
        
        def mainBrush = getFilterLeftTime(jsonObj.properties.mainBrushWorkTime, 300)
        sendEvent(name:"mainBrushLeftLife", value: mainBrush[1], displayed: false)
        setValueTime2("mainBrushLeftTime", mainBrush[0], mainBrush[1])
        
        def sideBrush = getFilterLeftTime(jsonObj.properties.sideBrushWorkTime, 200)
        sendEvent(name:"sideBrushLeftLife", value: sideBrush[1], displayed: false)
        setValueTime2("sideBrushLeftTime", sideBrush[0], sideBrush[1])
        
        def sensor = getFilterLeftTime(jsonObj.properties.sensorDirtyTime, 30)
        sendEvent(name:"sensorLeftLife", value: sensor[1], displayed: false)
        setValueTime2("sensorTime", sensor[0], sensor[1])
        
        def filter = getFilterLeftTime(jsonObj.properties.filterWorkTime, 150)
        sendEvent(name:"filterLeftLife", value: filter[1], displayed: false)
        setValueTime2("filterTime", filter[0], filter[1])
        
        sendEvent(name:"cleanArea", value: jsonObj.properties.cleanArea, displayed: false)
        sendEvent(name:"cleanTime", value: formatSeconds(jsonObj.properties.cleanTime), displayed: false)
        
        def fanSpeed;
        switch(jsonObj.state.fanSpeed){
        case 38:
        	fanSpeed = "Quiet"
        	break;
        case 60:
        	fanSpeed = "Balanced"
        	break;
        case 77:
        	fanSpeed = "Turbo"
        	break;
        case 90:
        	fanSpeed = "Full Speed"
        	break;
        }
    	sendEvent(name:"fanSpeed_label", value: fanSpeed )
        
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}
/*
def setValueTime(type, time, baseTime){
    def tmp = getFilterLeftTime(time, baseTime)
    setValueTime2(type, tmp[0], tmp[1])
}
*/
def setValueTime2(type, time, percent){
    sendEvent(name:type, value: "Left: ${time} Hour,   ${percent}%", displayed: false)
}

def getFilterLeftTime(time, baseTime){
    def leftHour = Math.round(( (baseTime * 60 * 60) - time ) / 60 / 60)
    def percent = Math.round( leftHour / baseTime * 100 )
    return [leftHour, percent]
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
