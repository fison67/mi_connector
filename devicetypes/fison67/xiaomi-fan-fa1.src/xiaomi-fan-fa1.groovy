/**
 *  Xiaomi Fan Fa1(v.0.0.1)
 *
 * MIT License
 *
 * Copyright (c) 2020 fison67@nate.com
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
	definition (name: "Xiaomi Fan Fa1", namespace: "fison67", author: "fison67", vid: "generic-switch", ocfDeviceType: "oic.d.fan") {
        capability "Switch"					
		capability "Fan Speed"
        capability "Switch Level"
		capability "Refresh"
         
        attribute "buzzer", "enum", ["on", "off"]    
        attribute "childLock", "enum", ["on", "off"]    
        
        attribute "angleHorizontalEnable", "enum", ["on", "off"]    
        attribute "angleVerticalEnable", "enum", ["on", "off"]    
        
        attribute "angleHorizontalLevel", "number"
        attribute "angleVerticalLevel", "number"
        
        
        command "buzzer", ["string"]
        command "childLock", ["string"]
        command "angleHorizontalEnable", ["string"]
        command "angleVerticalEnable", ["string"]
        command "angleHorizontalLevel", ["number"]
        command "angleVerticalLevel", ["number"]
	}


	simulator { }
	preferences {
    
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
    case "power":
        sendEvent(name:"switch", value: params.data  == "true" ? "on" : "off")
    	break;         
    case "fanSpeed":
        sendEvent(name:"fanSpeed", value: makeFanSpeed2(params.data as int))
    	break;     
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "childLock":
    	sendEvent(name:"childLock", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "angleHorizontalLevel":
    	sendEvent(name:"angleHorizontalLevel", value: params.data as int)
    	break;
    case "angleVerticalLevel":
    	sendEvent(name:"angleVerticalLevel", value: params.data as int)
    	break;
    }
    updateLastTime()
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def refresh(){
	log.debug "Refresh"
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

def setFanSpeed(speed){
    requestCommand("fanSpeed", makeFanSpeed(speed))
}

def makeFanSpeed(speed){
	def result = 100
    if(0 <= speed && speed < 20){
    	result = 1
    }else if(20 <= speed && speed < 40){
    	result = 2
    }else if(40 <= speed && speed < 60){
    	result = 3
    }else if(60 <= speed && speed < 80){
    	result = 4
    }else if(80 <= speed && speed < 100){
    	result = 5
    }
    return result
}

def makeFanSpeed2(value){
	if(value == 1){
    	return 20
    }else if(value == 2){
    	return 40
    }else if(value == 3){
    	return 60
    }else if(value == 4){
    	return 80
    }else if(value == 5){
    	return 100
    }
}

def requestCommand(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	requestCommand("power", "on")
}

def off(){
	requestCommand("power", "off")
}

def childLock(cmd){
	requestCommand("childLock", cmd == "on" ? true : false)
}

def buzzer(cmd){
	requestCommand("buzzer", cmd == "on" ? true : false)
}

def angleHorizontalEnable(cmd){
	requestCommand("angleHorizontalEnable", cmd == "on" ? true : false)
}

def angleVerticalEnable(cmd){
	requestCommand("angleVerticalEnable", cmd == "on" ? true : false)
}

def angleHorizontalLevel(level){
	if(level > 120){
    	return
    }
	requestCommand("angleHorizontalLevel", level)
}

def angleVerticalLevel(level){
	if(level > 90){
    	return
    }
	requestCommand("angleVerticalLevel", level)
}

def updated() {
    refresh()
    setLanguage(settings.selectedLang)
}

def setLanguage(language){}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
        sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer == true ? "on" : "off"))
	
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
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}
