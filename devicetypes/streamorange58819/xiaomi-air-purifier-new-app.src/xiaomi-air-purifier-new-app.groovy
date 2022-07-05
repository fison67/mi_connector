/**
 *  Xiaomi Air Purifier New App(v.0.0.3)
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
import groovy.transform.Field
import java.text.DateFormat

metadata {
	definition (name: "Xiaomi Air Purifier New App", namespace: "streamorange58819", mnmn: "fison67", author: "fison67", vid: "bed896bc-cb00-3ae8-9439-e65ca96f5adb", ocfDeviceType:"oic.d.airpurifier") {
        capability "Switch"						
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "streamorange58819.pmode"
        capability "streamorange58819.led"
        capability "streamorange58819.buzzer"
		capability "streamorange58819.childLock"
		capability "Filter Status"
		capability "Fan Speed"
		capability "Dust Sensor" // fineDustLevel : PM 2.5   dustLevel : PM 10
		capability "Sensor"
		capability "Refresh"
        
        command "setStatus"
	}

	preferences {}
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

def getModeStr(type){
	if(type == 0){
    	return "auto"
    }else if(type == 1){
    	return "sleep"
    }else if(type == 2){
    	return "favorite"
    }else if(type == 3){
    	return "none"
    }
}

def getModeInt(mode){
	if(mode == "auto"){
    	return 0
    }else if(mode == "sleep"){
    	return 1
    }else if(mode == "favorite"){
    	return 2
    }else if(mode == "none"){
    	return 3
    }
}

def setAirPurifierMode(mode){
	log.debug "setAirPurifierMode >> ${mode}"
    sendCommandData("changeMode", getModeInt(mode))
}

def setLed(power){
	if(power == "on"){
    	ledOn()
    }else if(power == "off"){
    	ledOff()
    }
}

def ledOn(){
    sendCommandData("led", "on")
}

def ledOff(){
    sendCommandData("led", "off")
}

def setBuzzer(power){
	if(power == "on"){
    	buzzerOn()
    }else if(power == "off"){
    	buzzerOff()
    }
}

def buzzerOn(){
    sendCommandData("buzzer", "on")
}

def buzzerOff(){
    sendCommandData("buzzer", "off")
}

def setChildLock(status){
	if(status == "locked" || status == "on"){
    	childLock()
    }else if(status == "unlocked" || status == "off"){
    	childUnlock()
    }
}

def childLock(){
    sendCommandData("childLock", "on")
}

def childUnlock(){
    childUnlock("childLock", "off")
}

def setStatus(params){
    log.debug "${params.key} : ${params.data}"
 
 	switch(params.key){
    case "power":
        sendEvent(name:"switch", value: params.data == "true" ? "on" : "off")
    	break;
    case "buzzer":
        sendEvent(name:"buzzer", value: params.data == "true" ? "on" : "off")
    	break;
    case "childLock":
        sendEvent(name:"childLock", value: (params.data == "true" ? "on" : "off"))
    	break
    case "led":
        sendEvent(name:"led", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "fanLevel":
    	sendEvent(name:"fanSpeed", value: getFanSpeed(params.data as int))
    	break;
    case "mode":
        sendEvent(name:"pmode", value: getModeStr(params.data as int))
    	break;
    case "pm2.5":
    	sendEvent(name:"fineDustLevel", value: params.data as int, unit:"μg/m^3")
    	break;
    case "aqi":
    	sendEvent(name:"fineDustLevel", value: params.data as int, unit:"μg/m^3")
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data as int, unit: "%")
    	break;
    case "temperature":
    	sendEvent(name:"temperature", value: Math.round(Float.parseFloat(params.data.replace("C",""))*10)/10, unit:"C" )
    	break;
    case "filterLife":
		def life = params.data as int
        sendEvent(name:"filterStatus", value: life == 0 ? "replace" : "normal")
    	break;
    }
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

def getFanSpeed(speed){
	def result = speed + 1
    return result
}

def setFanSpeed(speed){
	log.debug "setFanSpeed >> ${speed}"
	if(speed == 0){
		off()    
    }else if(speed == 1){
    	sendCommandData("fanLevel", 0)
    }else if(speed == 2){
    	sendCommandData("fanLevel", 1)
    }else if(speed == 3){
    	sendCommandData("fanLevel", 2)
    }else if(speed == 4){
    	sendCommandData("fanLevel", 3)
    }
}

def on(){
    sendCommandData("power", "on")
}

def off(){
    sendCommandData("power", "off")
}

def sendCommandData(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
	def options = makeCommand(body)
    sendCommand(options, null)
}

def installed(){
	sendEvent(name:"filterStatus", value: "normal")
	sendEvent(name:"dustLevel" , value: null)
    sendEvent(name:"supportedPmodes", value: ["auto", "sleep", "favorite"])
}

def updated() {}

def setExternalAddress(address){
	state.externalAddress = address
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
        if(jsonObj.properties["relativeHumidity"] != null){
        	sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity, unit:"%" )
        }
        if(jsonObj.properties["temperature"] != null){
        	sendEvent(name:"temperature", value: jsonObj.properties.temperature.value, unit:"C"  )
        }
        if(jsonObj.properties["pm2.5"] != null){
        	sendEvent(name:"fineDustLevel", value: jsonObj.properties["pm2.5"])
        }
        if(jsonObj.properties["aqi"] != null){
        	sendEvent(name:"fineDustLevel", value: jsonObj.properties["aqi"])
        }
        
		if(jsonObj.state["mode"] != null){
        	sendEvent(name:"pmode", value: getModeStr(jsonObj.state.mode))
        }
        if(jsonObj.properties["power"] != null){
        	sendEvent(name:"switch", value: jsonObj.properties.power ? "on" : "off")
        }
        if(jsonObj.state["buzzer"] != null){
        	sendEvent(name:"buzzer", value: (jsonObj.state.buzzer == true ? "on" : "off"))
        }
        if(jsonObj.state["childLock"] != null){
        	sendEvent(name:"childLock", value: (jsonObj.state.childLock == true ? "on" : "off"))
        }
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
