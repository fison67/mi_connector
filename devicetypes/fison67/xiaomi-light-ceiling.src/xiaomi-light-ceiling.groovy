/**
 *  Xiaomi Light Ceiling(v.0.0.5)
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
 *
*/

import java.awt.Color
import groovy.json.JsonSlurper

metadata {
	definition (name: "Xiaomi Light Ceiling", namespace: "fison67", author: "fison67", mnmn:"SmartThings", vid: "generic-color-temperature-bulb-2200K-6500K", ocfDeviceType: "oic.d.light") {
        capability "Switch"						//"on", "off"
        capability "Actuator"
        capability "Refresh"
		capability "ColorTemperature"
        capability "Switch Level"
        capability "Light"

		attribute "mode", "enum", ["daylight", "moonlight"]
        attribute "lastOn", "string"
        attribute "lastOff", "string"
        
        attribute "lastCheckin", "Date"
         
        command "setTimeRemaining"
        command "stop"
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
    
    installMoon()
}

def _getServerURL(){
	return parent._getServerURL()
}

def _getID(){
	return state.id
}

def setStatus(params){
    log.debug "${params.key} >> " + params.data
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
        if(target){
            def colors = []
            if(params.data.contains(",")){
            	colors = params.data.split(",")
            	target.setStatus("color", String.format("#%02x%02x%02x", colors[0].toInteger(), colors[1].toInteger(), colors[2].toInteger()) )
            }else{
            	target.setStatus("color", params.data )
                colors = hex2Rgb(params.data)
            }
            
            float[] hsbValues = new float[3];
            def hueSat = Color.RGBtoHSB(colors[0].toInteger(), colors[1].toInteger(), colors[2].toInteger(),hsbValues)
            target.setStatus("hue", (hueSat[0] * 100) as int )
            target.setStatus("saturation", (hueSat[1] * 100) as int)
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
	log.debug "setBrightness >> ${state.id}, val=${brightness}"
    if(brightness == 0){
    	off()
    }else{
        def body = [
            "id": state.id,
            "cmd": "brightness",
            "data": brightness,
        	"subData": getDuration()
        ]
        def options = makeCommand(body)
        sendCommand(options, null)

    	setPowerByStatus(true)
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
	def childDevice =  addChildDevice("Xiaomi Light Ceiling Moon", "mi-connector-" + state.id  + "-moon" , null, [completedSetup: true, label: "Moon Mode", isComponent: false])
}

def installChild(){
	def backgroundID = "mi-connector-" + state.id  + "-child"
	def child = childDevices.find { it.deviceNetworkId == backgroundID }
    if(!child){
    	def childDevice =  addChildDevice("Xiaomi Light Ceiling Child", backgroundID , null, [completedSetup: true, label: "Background Light",  isComponent: false])
    }
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
        sendEvent(name:"level", value: jsonObj.properties.brightness as int)
        sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
    	sendEvent(name:"mode", value: jsonObj.properties.activeMode)
        
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
        sendEvent(name: "lastCheckin", value: now, displayed: false)
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
//	log.debug "Left Time >> ${state.timerCount}"
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
//    log.debug "Left Time >> ${state.timerCount} seconds"
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

def colorTemperatureToRGB(kelvin){
    def temp = kelvin / 100;
    def red, green, blue;
    if( temp <= 66 ){ 
        red = 255; 
        green = temp;
        green = 99.4708025861 * Math.log(green) - 161.1195681661;

        if( temp <= 19){
            blue = 0;
        } else {
            blue = temp-10;
            blue = 138.5177312231 * Math.log(blue) - 305.0447927307;
        }
    } else {
        red = temp - 60;
        red = 329.698727446 * Math.pow(red, -0.1332047592);
        
        green = temp - 60;
        green = 288.1221695283 * Math.pow(green, -0.0755148492 );

        blue = 255;
    }
    return [ clamp(red,   0, 255), clamp(green, 0, 255), clamp(blue,  0, 255) ]
}


def clamp( x, min, max ) {
    if(x<min){ return min; }
    if(x>max){ return max; }
    return x;
}
/*
def rgbToColorTemperature(red, blue){
	def temperature, testRGB;
    def epsilon=0.4;
    def minTemperature = 1000;
    def maxTemperature = 40000;
    while (maxTemperature - minTemperature > epsilon) {
        temperature = (maxTemperature + minTemperature) / 2;
        testRGB = colorTemperature2rgb(temperature);
        if ((testRGB.blue / testRGB.red) >= (blue / red)) {
          maxTemperature = temperature;
        } else {
          minTemperature = temperature;
        }
    }
    return Math.round(temperature);
}
*/
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

def hex2Rgb(String colorStr) {
    return [
        Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
        Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
        Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) 
    ]
}
