/**
 *  Xiaomi Fan P220 New App(v.0.0.2)
 *
 * MIT License
 *
 * Copyright (c) 2022 fison67@nate.com
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
	definition (name: "Xiaomi Fan P220 New App", namespace: "streamorange58819", author: "fison67", mnmn:"fison67", vid:"f6250efb-90f0-3ddb-a834-3f92c25626c7", ocfDeviceType: "oic.d.fan") {
        capability "Switch"				
        capability "streamorange58819.dmakermode"
		capability "Fan Speed"
        capability "streamorange58819.horizontalswing"
        capability "streamorange58819.verticalswing"	
		capability "Refresh"
        capability "streamorange58819.led"
        capability "streamorange58819.buzzer"
        capability "streamorange58819.childlock"
        capability "Relative Humidity Measurement"
        capability "Temperature Measurement"
	}
    
}

def installed(){
	sendEvent(name:"xiaomifanmode", value:"normal")
}

def setModel(model){
	state.model = model
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

def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setInfo(String app_url, String id) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
}

def setStatus(params){
    log.debug "Notify [${params.key}] >> ${params.data}"
    switch(params.key){
    case "power":
    	sendEvent(name: "switch", value: (params.data == "true" ? "on" : "off"))
        if(params.data == "false"){
    		sendEvent(name: "fanSpeed", value: 0)
        }else{
    		sendEvent(name: "fanSpeed", value: state.fanSpeed as int)
        }
    	break
    case "led":
    	sendEvent(name: "led", value: (params.data == "true" ? "on" : "off"))
    	break
    case "childLock":
    	sendEvent(name: "childlock", value: (params.data == "true" ? "locked" : "unlocked"))
    	break
    case "mode":
        sendEvent(name: "dmakermode", value: getModeStr(params.data as int))
    	break
    case "fanLevel":
    	sendEvent(name: "fanSpeed", value: params.data as int)
        state.fanSpeed = params.data
    	break
    case "angleHorizontalEnable":
    	if(params.data == "false"){
    		sendEvent(name: "horizontalswing", value: "off")
        }else{
        	sendEvent(name: "horizontalswing", value: state.horizontalswing)
        }
    	break
    case "angleHorizontalLevel":
        sendEvent(name: "horizontalswing", value: params.data)
        state.horizontalswing = params.data
    	break
    case "angleVerticalEnable":
    	if(params.data == "false"){
    		sendEvent(name: "verticalswing", value: "off")
        }else{
        	sendEvent(name: "verticalswing", value: state.verticalswing)
        }
    	break
    case "angleVerticalLevel":
        sendEvent(name: "verticalswing", value: params.data)
        state.verticalswing = params.data
    	break
    case "temperature":
    	sendEvent(name:"temperature", value: Math.round(Float.parseFloat(params.data.replace("C",""))*10)/10, unit:"C")
    	break
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data as int, unit:"%")
    	break
    }
}

def getFanSpeedValue(level){
	if(level == 1){
    	return 2
    }else if(level == 2){
    	return 3
    }else if(level == 3){
    	return 4
    }
}

def getSpeedControlValue(speed){
	if(speed == 0){
    	return 0
    }else if(speed == 1){
    	return 1
    }else if(speed == 2){
    	return 2
    }else if(speed == 3){
    	return 3
    }else if(speed == 4){
    	return 4
    }
}

def setFanSpeed(level){
	def speed = getSpeedControlValue(level)
    if(speed == 0){
    	off()
    }else{
    	if(device.currentValue("switch") == "off"){
        	on()
        }
    	control("fanLevel", speed)
    }
}

def on(){
	control("power", "on")
}

def off(){
	control("power", "off")
}

def setLed(value){
	if(value == "on"){ ledOn() }
    else if(value == "off"){ ledOff }
}
def ledOn(){ control("led", "on") }
def ledOff(){ control("led", "off") }


def setHorizontalSwingOff(){
	control("angleHorizontalEnable", "off")
}

def setVerticalSwing(value){
	if(value == "off"){
    	setVerticalSwingOff()
    }else if(value == "35"){
    	setVerticalSwing35()
    }else if(value == "65"){
    	setVerticalSwing65()
    }else if(value == "95"){
    	setVerticalSwing95()
    }
}

def processVerticalSwing(){ 
    control("angleVerticalEnable", "on")
}

def setVerticalSwing35(){
	processVerticalSwing()
	control("angleVerticalLevel", 35) 
}

def setVerticalSwing65(){ 
	processHorizontalSwing()
    control("angleVerticalLevel", 65) 
}

def setVerticalSwing95(){ 
	processVerticalSwing()
    control("angleVerticalLevel", 95) 
}

def setVerticalSwingOff(){
	control("angleVerticalEnable", "off")
}

def setHorizontalSwing(value){
	if(value == "off"){
    	setHorizontalSwingOff()
    }else if(value == "30"){
    	setHorizontalSwing30()
    }else if(value == "60"){
    	setHorizontalSwing60()
    }else if(value == "90"){
    	setHorizontalSwing90()
    }else if(value == "120"){
    	setHorizontalSwing120()
    }else if(value == "1240"){
    	setHorizontalSwing140()
    }
}

def processHorizontalSwing(){ 
    control("angleHorizontalEnable", "on")
}

def setHorizontalSwing30(){
	processHorizontalSwing()
	control("angleHorizontalLevel", 30) 
}

def setHorizontalSwing60(){ 
	processHorizontalSwing()
    control("angleHorizontalLevel", 60) 
}

def setHorizontalSwing90(){ 
	processHorizontalSwing()
    control("angleHorizontalLevel", 90) 
}

def setHorizontalSwing120(){ 
	processHorizontalSwing()
    control("angleHorizontalLevel", 120) 
}

def setHorizontalSwing140(){ 
	processHorizontalSwing()
    control("angleHorizontalLevel", 140) 
}

def getModeStr(value){
	if(value == 0){
    	return "straight"
    }else if(value == 1){
    	return "natural"
    }else if(value == 2){
    	return "smart"
    }else if(value == 3){
    	return "sleep"
    }
}

def setFanMode(mode){
    if(mode == "natural"){
    	modeNatural()
    }else if(mode == "straight"){
    	modeStraight()
    }else if(mode == "smart"){
    	modeSmart()
    }else if(mode == "sleep"){
    	modeSleep()
    }
}

def modeStraight(){ control("changeMode", 0) } 
def modeNatural(){ control("changeMode", 1) }
def modeSmart(){ control("changeMode", 2) }
def modeSleep(){ control("changeMode", 3) }

def setBuzzer(power){
	if(power == "on"){
    	buzzerOn()
    }else{
    	buzzerOff()
    }
}
def buzzerOn(){ control("buzzer", "on") }
def buzzerOff(){ control("buzzer", "off") }

def setChildLock(lock){
	if(lock == "locked"){
    	childLock()
    }else{
    	childUnlock()
    }
}

def childLock(){ control("buzzer", "on") }
def childUnlock(){ control("buzzer", "off") }

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
       	log.debug jsonObj
        
        sendEvent(name:"temperature", value: jsonObj.properties.temperature.value, unit:"C")
        sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity, unit:"%")
        sendEvent(name:"switch", value: (jsonObj.properties.power ? "on" : "off"))
        sendEvent(name:"childlock", value: (jsonObj.properties.childLock ? "locked" : "unlocked"))
        sendEvent(name:"led", value: (jsonObj.properties.led ? "on" : "off"))
		sendEvent(name:"dmakermode", value: getModeStr(jsonObj.properties.mode))
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer ? "on" : "off"))
        sendEvent(name:"fanSpeed", value: jsonObj.properties.fanLevel)
        
        if(!jsonObj.properties.angleHorizontalEnable){
        	sendEvent(name:"horizontalswing", value: "off")
        }else{
        	sendEvent(name:"horizontalswing", value: jsonObj.properties.angleHorizontalLevel.toString())
        }
        
        if(!jsonObj.properties.angleVerticalEnable){
        	sendEvent(name:"verticalswing", value: "off")
        }else{
        	sendEvent(name:"verticalswing", value: jsonObj.properties.angleVerticalLevel.toString())
        }
        
        state.fanSpeed = jsonObj.properties.fanLevel.toString()
        state.horizontalswing = jsonObj.properties.angleHorizontalLevel.toString()
        state.verticalswing = jsonObj.properties.angleVerticalLevel.toString()
	
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
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

def sendCommand(options, _callback){
	def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: _callback])
    sendHubCommand(myhubAction)
}

def makeCommand(body){
	return [
     	"method": "POST",
        "path": "/control",
        "headers": [
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ],
        "body":body
    ]
}
