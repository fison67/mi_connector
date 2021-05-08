/**
 *  Xiaomi Fan P New App(v.0.0.1)
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
	definition (name: "Xiaomi Fan P New App", namespace: "streamorange58819", author: "fison67", mnmn:"fison67", vid:"76ec0945-f753-3557-b29c-e43f67bc164c", ocfDeviceType: "oic.d.fan") {
        capability "Switch"					
		capability "Fan Speed"
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
    log.debug "Notify [${params.key}] >> ${params.data}"
    switch(params.key){
    case "power":
    	sendEvent(name: "switch", value: (params.data == "true" ? "on" : "off"))
    	break
    case "led":
    	sendEvent(name: "led", value: (params.data == "true" ? "on" : "off"))
    	break
    case "childLock":
    	sendEvent(name: "childlock", value: (params.data == "true" ? "locked" : "unlocked"))
    	break
    case "mode":
        sendEvent(name: "xiaomifanmode", value: params.data == "0" ? "normal" : "natural")
    	break
    case "fanLevel":
    	sendEvent(name: "fanSpeed", value: getFanSpeedValue(params.data as int))
    	break
    case "angleEnable":
    	sendEvent(name: "rotationangle", value: (params.data == "true" ? "on" : "off"))
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
	if(speed <= 2){
    	return 1
    }else if(speed == 3){
    	return 2
    }else if(speed == 4){
    	return 3
    }
}

def setFanSpeed(speed){
    control("fanLevel", getSpeedControlValue(speed))
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

def naturalMode(){ control("changeMode", "natural") }
def generalMode(){ control("changeMode", "general") }

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
        
        sendEvent(name:"switch", value: (jsonObj.properties.power ? "on" : "off"))
        sendEvent(name:"childlock", value: (jsonObj.properties.childLock ? "locked" : "unlocked"))
        sendEvent(name:"led", value: (jsonObj.properties.led ? "on" : "off"))
		sendEvent(name:"xiaomifanmode", value: jsonObj.properties.naturalLevel > 0 ? "natural" : "general")
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer ? "on" : "off"))
	
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
