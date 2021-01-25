/**
 *  Xiaomi Vacuums (v.0.0.4)
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
	definition (name: "Xiaomi Vacuums", namespace: "fison67", author: "fison67", vid:"generic-default-metadata") {
    	capability "Robot Cleaner Cleaning Mode"
        capability "Robot Cleaner Movement"
        capability "Switch"	
		capability "Fan Speed"
        capability "Battery"	
        capability "Refresh"	
        
        attribute "status", "string"
        attribute "mode", "string"
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

    preferences {
        input name: "offOption", title:"OFF Option" , type: "enum", required: true, options: ["off", "return"], defaultValue: "off"
	}
}

def installed(){

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
        sendEvent(name:"switch", value: (params.data == "cleaning" || params.data == "zone-cleaning") ? "on" : "off")
        def cleanMode = "stop"
        if(params.data == "zone-cleaning"){
        	cleanMode = "part"
        }else  if(params.data == "charging"){
        	cleanMode = "stop"
        }else {
        	cleanMode = "auto"
        }
        sendEvent(name:"robotCleanerCleaningMode", value: cleanMode)
        
        def cleanMovement = ""
        if(params.data == "cleaning" || params.data == "zone-cleaning" || params.data == "spot-cleaning"){
        	cleanMovement = "cleaning"
        }else if(params.data == "charging"){
        	cleanMovement = "charging"
        }else if(params.data == "returning" || params.data == "docking"){
        	cleanMovement = "homing"
        }else if(params.data == "charger-offline"){
        	cleanMovement = "powerOff"
        }else if(params.data == "error" || params.data == "charging-error"){
        	cleanMovement = "alarm"
        }
        if(cleanMovement != ""){
    		sendEvent(name:"robotCleanerMovement", value: cleanMovement )
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
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"), displayed: false )
       	sendEvent(name:"paused", value: params.data == "true" ? "paused" : "restart", displayed: false )     
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
    requestCommand("quiet")
}

def balanced(){
    requestCommand("balanced")
}

def turbo(){
    requestCommand("turbo")
}

def fullSpeed(){
    requestCommand("fullSpeed")
}

def spotClean(){
    requestCommand("spotClean")
    
    sendEvent(name:"spot", value: "on" )
}

def charge(){
    requestCommand("charge")
}

def paused(){
    requestCommand("pause")
}

def start(){
    requestCommand("start")
}

def find(){
    requestCommand("find")
}

def clean(){
    requestCommand("clean")
}

def on(){
    clean()
}

def off(){
	if(offOption == "off"){
        stop()
    }else{
    	charge()
    }
}

def stop(){
    requestCommand("stop")
}

def setRobotCleanerCleaningMode(mode){
	if(mode == "stop"){
    	stop()
    }else if(mode == "part"){
    	spotClean()
    }else{
    	on()
    }
}

def setRobotCleanerMovement(movement){
	log.debug "setRobotCleanerMovement: ${movement}"
	if(movemoent == "charging"){
    	charge()
    }else if(movement == "powerOff"){
    	off()
    }else if(movement == "cleaning"){
    	on()
    }
}

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

def setValueTime2(type, time, percent){
    sendEvent(name:type, value: "Left: ${time} Hour,   ${percent}%", displayed: false)
}

def getFilterLeftTime(time, baseTime){
    def leftHour = Math.round(( (baseTime * 60 * 60) - time ) / 60 / 60)
    def percent = Math.round( leftHour / baseTime * 100 )
    return [leftHour, percent]
}

def updated() {}

def requestCommand(cmd){
    def body = [
        "id": state.id,
        "cmd": cmd
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
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
