/**
 *  Xiaomi Light Ceiling(v.0.0.4)
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
 *
*/

import groovy.json.JsonSlurper

metadata {
	definition (name: "Xiaomi Light Ceiling", namespace: "fison67", author: "fison67") {
        capability "Switch"
        capability "Light"
		capability "Bulb"
        capability "Refresh"
		capability "ColorTemperature"
        capability "Switch Level"

        attribute "lastOn", "string"
        attribute "lastOff", "string"
        
        attribute "lastCheckin", "Date"
         
    command "setTimeRemaining", ["number"]
        command "stop"
	}

	simulator {
	}
	
	preferences {
		input name:	"smooth", type:"enum", title:"Select", options:["On", "Off"], description:"", defaultValue: "On"
        input name: "duration", title:"Duration" , type: "number", required: false, defaultValue: 500, description:""
        input name: "makeChild", title:"Make a background light" , type: "enum", options:["no", "yes"], required: true, defaultValue: "no"
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

def _getServerURL(){
	return parent._getServerURL()
}

def _getID(){
	return state.id
}

def setStatus(params){
//    log.debug params.key + ":" + params.data
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
 	switch(params.key){
    case "power":
        if(params.data == "true"){
            sendEvent(name:"switch", value: "on")
            sendEvent(name: "lastOn", value: now, displayed: false)
        } else {
            sendEvent(name:"switch", value: "off")
            sendEvent(name: "lastOff", value: now, displayed: false)
        }
    	break
    case "bgPower":
    	def target = getBackgroundLight()
        if(target){
    		target.setStatus("switch", params.data == "true" ? "on" : "off")
        }
    	break
    case "colorTemperature":
        sendEvent(name:"colorTemperature", value: params.data as int )
    	break
    case "bgColor":
    	def target = getBackgroundLight()
        log.debug target
        if(target){
    		target.setStatus("color", params.data)
        }
    	break
    case "bgColorTemperature":
    	def target = getBackgroundLight()
        if(target){
    		target.setStatus("colorTemperature", params.data as int)
        }
    	break
    case "brightness":
    	sendEvent(name:"level", value: params.data as int)
    	break
    case "bgBrightness":
    	def target = getBackgroundLight()
        if(target){
    		target.setStatus("level", params.data as int)
        }
    	break
    case "activeMode":
    	def target = getMoonLight()
        log.debug target
        if(target){
    		target.setStatus("switch", params.data == "daylight" ? "off" : "on")
        }
    	break
    }
    
    sendEvent(name: "lastCheckin", value: now, displayed: false)
}

def getBackgroundLight(){
    return childDevices.find { it.deviceNetworkId ==  "${device.deviceNetworkId}-child" }
}

def getMoonLight(){
    return childDevices.find { it.deviceNetworkId ==  "${device.deviceNetworkId}-moon" }
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

def setLevel(brightness){	
	if(brightness < 0){
		brightness = 0	
	}else if(brightness > 100){
		brightness = 100	
	}
	log.debug "setBrightness >> ${state.id}, val=${brightness}"
    if(brightness == 0){
    	off()
    }else{
    	setPowerByStatus(true)
		
        def body = [
            "id": state.id,
            "cmd": "brightness",
            "data": brightness,
        	"subData": getDuration()
        ]
        def options = makeCommand(body)
        sendCommand(options, null)

    }
}

def setColorTemperature(_colortemperature){
	def colortemperature = _colortemperature
	if(colortemperature < 2700){
    	colortemperature = 2700
    }else if(colortemperature > 6500){
    	colortemperature = 6500
    }
    
    def body = [
        "id": state.id,
        "cmd": "color",
        "data": colortemperature + "K",
        "subData": getDuration()
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    
    setPowerByStatus(true)	
}

def on(){
	log.debug "On >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on",
        "subData": getDuration()
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "power",
        "data": "off",
        "subData": getDuration()
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}


def updated() {
	if(settings.makeChild == "yes"){
    	installChild()
    }
}

def installMoon() {
    addChildDevice("Xiaomi Light Ceiling Moon", "mi-connector-" + state.id  + "-moon", [
        completedSetup: true,
        label         : "Moon Mode",
        isComponent   : false
    ])
}

def installChild(){
	def backgroundID = "mi-connector-" + state.id  + "-child"
	def child = childDevices.find { it.deviceNetworkId == backgroundID }
    if(!child){
        addChildDevice("Xiaomi Light Ceiling Child", backgroundID, [
            completedSetup: true,
            label         : "Background Light",
            isComponent   : false
        ])
    }
}

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
        sendEvent(name:"color", value: jsonObj.properties.color)
        sendEvent(name:"level", value: jsonObj.properties.brightness)
        sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
	    
        sendEvent("colorTemperature", jsonObj.properties.colorTemperature[0] as int)
        
        def target = getBackgroundLight()
        if(target){
    		target.setStatus("colorTemperature", jsonObj.properties.bgColorTemperature[0] as int)
    		target.setStatus("color", jsonObj.properties.bgColor)
    		target.setStatus("level", jsonObj.properties.bgBrightness as int)
    		target.setStatus("switch", jsonObj.properties.bgPower == true ? "on" : "off")
        }
        
        def moonTarget = getMoonLight()
        if(moonTarget){
    		sendEvent(name:"switch", value: jsonObj.properties.activeMode == "daylight" ? "off" : "on")
        }
        
        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        sendEvent(name: "lastCheckin", value: now)
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}


def sendCommand(options, _callback){
	def myhubAction = new hubitat.device.HubAction(options, null, [callback: _callback])
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

def msToTime(duration) {
    def seconds = (duration%60).intValue()
    def minutes = ((duration/60).intValue() % 60).intValue()
    def hours = ( (duration/(60*60)).intValue() %24).intValue()

    hours = (hours < 10) ? "0" + hours : hours
    minutes = (minutes < 10) ? "0" + minutes : minutes
    seconds = (seconds < 10) ? "0" + seconds : seconds

    return hours + ":" + minutes + ":" + seconds
}

def stop() { 
	unschedule()
	state.timerCount = 0
	updateTimer()
}

def timer(){
	if(state.timerCount > 0){
    	state.timerCount = state.timerCount - 30;
        if(state.timerCount <= 0){
        	if(device.currentValue("switch") == "on"){
        		off()
            }
        }else{
        	runIn(30, timer)
        }
        updateTimer()
    }
}

def updateTimer(){
    def timeStr = msToTime(state.timerCount)
    sendEvent(name:"leftTime", value: "${timeStr}")
    sendEvent(name:"timeRemaining", value: Math.round(state.timerCount/60))
}

def processTimer(second){
	if(state.timerCount == null){
    	state.timerCount = second;
    	runIn(30, timer)
    }else if(state.timerCount == 0){
		state.timerCount = second;
    	runIn(30, timer)
    }else{
    	state.timerCount = second
    }
    updateTimer()
}

def setTimeRemaining(time) { 
	if(time > 0){
        log.debug "Set a Timer ${time}Mins"
        processTimer(time * 60)
        setPowerByStatus(true)
    }
}

def setPowerByStatus(turnOn){
	if(device.currentValue("switch") == (turnOn ? "off" : "on")){
        if(turnOn){
        	on()
        }else{
        	off()
        }
    }
}

def getDuration(){
	def smoothOn = settings.smooth == "" ? "On" : settings.smooth
    def duration = 500
    if(smoothOn == "On"){
        if(settings.duration != null){
            duration = settings.duration
        }
    }
    return duration
}

def huesatToRGB(float hue, float sat) {
	while(hue >= 100) hue -= 100
	int h = (int)(hue / 100 * 6)
	float f = hue / 100 * 6 - h
	int p = Math.round(255 * (1 - (sat / 100)))
	int q = Math.round(255 * (1 - (sat / 100) * f))
	int t = Math.round(255 * (1 - (sat / 100) * (1 - f)))
	switch (h) {
		case 0: return [255, t, p]
		case 1: return [q, 255, p]
		case 2: return [p, 255, t]
		case 3: return [p, q, 255]
		case 4: return [t, p, 255]
		case 5: return [255, p, q]
	}
}
