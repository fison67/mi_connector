/**
 *  Xiaomi Fan New App(v.0.0.1)
 *
 * MIT License
 *
 * Copyright (c) 2021 fison67@nate.com
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
	definition (name: "Xiaomi Fan New App", namespace: "streamorange58819", author: "fison67", mnmn:"fison67", vid: "29121ff7-527d-320a-a9b6-4662eb0cbeb7x", ocfDeviceType: "oic.d.fan") {
        capability "Switch"					
		capability "Fan Speed"
		capability "Power Source"
		capability "Refresh"
        capability "streamorange58819.xiaomifanmode"
        capability "streamorange58819.rotationangle"
        capability "streamorange58819.led"
        capability "streamorange58819.buzzer"
        capability "streamorange58819.childlock"
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
	if(params.key == "temperature"){
    	return
    }
    log.debug "Notify [${params.key}] >> ${params.data}"
    switch(params.key){
    case "power":
    	sendEvent(name: "switch", value: params.data == "true" ? "on" : "off")
    	break
    case "led":
    	sendEvent(name: "led", value: (params.data == "off" ? "off" : "on"))
    	break
    case "childLock":
    	sendEvent(name: "childlock", value: (params.data == "true" ? "locked" : "unlocked"))
    	break
    case "naturalLevel":
    	def fanNaturalEnable = params.data as int
        sendEvent(name: "xiaomifanmode", value: fanNaturalEnable == 0 ? "general" : "natural")
        if(fanNaturalEnable > 0){
    		sendEvent(name: "fanSpeed", value: getFanSpeedValue(params.data as int))
        }
    	break
    case "speedLevel":
        sendEvent(name: "xiaomifanmode", value: "general")
    	sendEvent(name: "fanSpeed", value: getFanSpeedValue(params.data as int))
    	break
    case "angleEnable":
    	sendEvent(name: "rotationangle", value: 0)
    	break
    case "angleLevel":
    	sendEvent(name: "rotationangle", value: getRotationAngle("on", params.data as int))
    	break
    }
}

def getFanSpeedValue(level){
	if(0 < level && level <= 25){
    	return 1
    }else if(25 < level && level <= 50){
    	return 2
    }else if(50 < level && level <= 75){
    	return 3
    }else if(75 < level && level <= 100){
    	return 4
    }else {
    	return 0
    }
}

def getXiaomiFanSpeed(){
	def status = device.currentValue("fanSpeed")
    return getSpeedControlValue(status)
}

def getSpeedControlName(){
	return device.currentValue("xiaomifanmode") == "natural" ? "fanNatural" : "fanSpeed"
}

def getSpeedControlValue(speed){
	if(speed == 0){
    	return 0
    }else if(speed == 1){
    	return 25
    }else if(speed == 2){
    	return 50
    }else if(speed == 3){
    	return 75
    }else if(speed == 4){
    	return 100
    }
}

def setFanSpeed(speed){
	log.debug "setFanSpeed: ${speed}"
    control(speedControlName, getSpeedControlValue(speed))
}

def on(){
	control("power", "on")
}

def off(){
	control("power", "off")
}

def setFanMode(mode){
	log.debug "setFanMode: " + mode
    if(mode == "natural"){
    	naturalMode()
    }else{
    	generalMode()
    }
}

def naturalMode(){ control("fanNatural", xiaomiFanSpeed) }
def generalMode(){ control("fanSpeed", xiaomiFanSpeed) }

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

def childLock(){ control("childLock", "on") }
def childUnlock(){ control("childLock", "off") }

def setRotationAngle(level){
	if(level == 0){
    	control("angle", "off")
    }else{
    	if(device.currentValue("rotationangle") != "on"){
    		control("angle", "on")
        }
    	control("angleLevel", level*30)
	}
}

def getRotationAngle(angle, level){
	if(angle == "off"){
    	return 0
    }
    if(level == 30){
    	return 1
    }else if(level == 60){
    	return 2
    }else if(level == 90){
    	return 3
    }else if(level == 120){
    	return 4
    }
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
       	log.debug jsonObj
        
        sendEvent(name:"switch", value: (jsonObj.properties.power ? "on" : "off"))
        sendEvent(name:"childlock", value: (jsonObj.properties.childLock ? "locked" : "unlocked"))
        sendEvent(name:"led", value: (jsonObj.properties.led ? "on" : "off"))
		sendEvent(name:"xiaomifanmode", value: jsonObj.properties.naturalLevel > 0 ? "natural" : "general")
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer ? "on" : "off"))
        sendEvent(name:"rotationangle", value: getRotationAngle(jsonObj.properties.angleEnable, jsonObj.properties.angleLevel))
        sendEvent(name:"powerSource", value: (jsonObj.properties.acPower == "on" ? "dc" : "battery"))
	
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
