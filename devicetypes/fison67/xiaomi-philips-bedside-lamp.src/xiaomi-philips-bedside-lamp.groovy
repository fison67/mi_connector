/**
 *  Xiaomi Philips Bedside Lamp (v.0.0.2)
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

import java.awt.Color
import java.util.ArrayList

metadata {
	definition (name: "Xiaomi Philips Bedside Lamp", namespace: "fison67", author: "fison67", mnmn:"SmartThings", vid: "generic-rgb-color-bulb") {
        capability "Switch"						//"on", "off"
        capability "Light"
        capability "Actuator"
        capability "Refresh"
		capability "Color Control"
		capability "Color Temperature"
        capability "Switch Level"

        attribute "lastOn", "string"
        attribute "lastOff", "string"
        attribute "scene", "number"
        
        attribute "lastCheckin", "Date"
         
        command "setTimeRemaining"
        command "stop"
        
        command "setScene1"
        command "setScene2"
        command "setScene3"
        command "setScene4"
        command "setScene5"
	}
    
	preferences {
	}

	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"https://github.com/fison67/mi_connector/blob/master/icons/xiaomi-philips-bedside-lamp-on-50.png?raw=true", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"https://github.com/fison67/mi_connector/blob/master/icons/xiaomi-philips-bedside-lamp-off-50.png?raw=true", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"https://github.com/fison67/mi_connector/blob/master/icons/xiaomi-philips-bedside-lamp-on-50.png?raw=true", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.ofn", icon:"https://github.com/fison67/mi_connector/blob/master/icons/xiaomi-philips-bedside-lamp-off-50.png?raw=true", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}')
            }
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"switch level.setLevel"
            }
            tileAttribute ("device.color", key: "COLOR_CONTROL") {
                attributeState "color", action:"setColor"
            }
		}
        valueTile("refresh", "device.refresh", width: 2, height: 2, decoration: "flat") {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }        
        valueTile("lastOn_label", "", decoration: "flat") {
            state "default", label:'Last\nON'
        }
        valueTile("lastOn", "device.lastOn", decoration: "flat", width: 3, height: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("lastOff_label", "", decoration: "flat") {
            state "default", label:'Last\nOFF'
        }
        valueTile("lastOff", "device.lastOff", decoration: "flat", width: 3, height: 1) {
            state "default", label:'${currentValue}'
        }
        
        standardTile("scene1", "device.scene") {
			state "default", label: "Scene1", action: "setScene1", backgroundColor:"#2FFF13"
		}
        standardTile("scene2", "device.scene") {
			state "default", label: "Scene2", action: "setScene2", backgroundColor:"#CF10FF"
		}
        standardTile("scene3", "device.scene") {
			state "default", label: "Scene3", action: "setScene3", backgroundColor:"#FF2619"
		}
        standardTile("scene4", "device.scene") {
			state "default", label: "Scene4", action: "setScene4", backgroundColor:"#32F2FF"
		}
        standardTile("scene5", "device.scene") {
			state "default", label: "Scene5", action: "setScene5", backgroundColor:"#1B86FF"
		}
        standardTile("scene6", "device.scene") {
			state "default", label: "Scene6", action: "setScene6", backgroundColor:"#FFCF97"
		}
        
        controlTile("colorTemperature", "device.colorTemperature", "slider", height: 1, width: 2, range:"(2700..6500)") {
	    	state "colorTemperature", action:"setColorTemperature"
		}
        valueTile("timer_label", "device.leftTime", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Set Timer\n${currentValue}'
        }
        
        controlTile("time", "device.timeRemaining", "slider", height: 1, width: 1, range:"(0..120)") {
	    	state "time", action:"setTimeRemaining"
		}
        
        standardTile("tiemr0", "device.timeRemaining") {
			state "default", label: "OFF", action: "stop", icon:"st.Health & Wellness.health7", backgroundColor:"#c7bbc9"
		}
        
        main (["switch"])
        details(["switch", "refresh", "lastOn_label", "lastOn", "lastOff_label","lastOff", "scene1", "scene2", "scene3", "scene4", "scene5", "scene6", "colorTemperature", "timer_label", "time", "tiemr0" ])       
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def ping(){
	refresh()
}

def setInfo(String app_url, String id) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
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
    	break;
    case "colorTemperature":
    	sendEvent(name:"colorTemperature", value: params.data as int)
    	break;
    case "color":
    	def colors = params.data.split(",")
    	sendEvent(name:"color", value: String.format("#%02x%02x%02x", colors[0].toInteger(), colors[1].toInteger(), colors[2].toInteger())  )
        
        float[] hsbValues = new float[3];
		def hueSat = Color.RGBtoHSB(colors[0].toInteger(), colors[1].toInteger(), colors[2].toInteger(),hsbValues)
    	sendEvent(name:"hue", value: (hueSat[0] * 100) as int  )
    	sendEvent(name:"saturation", value: (hueSat[1] * 100) as int)
    	break;
    case "brightness":
    	sendEvent(name:"level", value: params.data as int)
    	break;
    case "scene":
    	sendEvent(name:"scene", value: params.data )
    	break;
    }
    
    sendEvent(name: "lastCheckin", value: now, displayed: false)
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
	log.debug "setBrightness >> ID(${state.id}), val=${brightness}"
    
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

def setColor(color){
	log.debug "setColor >> ${state.id} >> ${color}"
    def colors = color.hex
    if(colors == null){
        def srgb = hslToRGB(color.hue, color.saturation, 0.5)
        def shexColor = rgbToHex(srgb)
        colors = shexColor
    }
    
    def body = [
        "id": state.id,
        "cmd": "color",
        "data": colors,
        "subData": getDuration()
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    
    setPowerByStatus(true)
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

def setScene1(){
	log.debug "setScene1 >> ${state.id}"
	processSetScene(1)
}

def setScene2(){
	log.debug "setScene2 >> ${state.id}"
	processSetScene(2)
}

def setScene3(){
	log.debug "setScene3 >> ${state.id}"
	processSetScene(3)
}

def setScene4(){
	log.debug "setScene4 >> ${state.id}"
	processSetScene(4)
}

def setScene5(){
	log.debug "setScene5 >> ${state.id}"
	processSetScene(5)
}

def setScene6(){
	log.debug "setScene6 >> ${state.id}"
	processSetScene(6)
}


def processSetScene(data){
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": data
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}


public String hslToHex(float hue, float saturation, float brightness) {
    float h = Math.max( 0, Math.min( 360, hue ) )
    float s = Math.max( 0, Math.min( 100, saturation ) )
    float v = Math.max( 0, Math.min( 100, brightness ) )
    h /= 360
    s /= 100
    v /= 100
    
    Color cc = Color.getHSBColor( h, s, v )

    return String.format( "#%02x%02x%02x", cc.getRed(), cc.getGreen(), cc.getBlue() )
}

private hex(value, width=2) {
    def s = new BigInteger(Math.round(value).toString()).toString(16)
    while (s.size() < width) {
    	s = "0" + s
    }
    return s
}

def rgbToHex(rgb) {
    def r = hex(rgb.r)
    def g = hex(rgb.g)
    def b = hex(rgb.b)
    def hexColor = "#${r}${g}${b}"
    hexColor
}

def hslToRGB(var_h, var_s, var_l) {
    float h = var_h / 100
    float s = var_s / 100
    float l = var_l

    def r = 0
    def g = 0
    def b = 0

    if (s == 0) {
        r = l * 255
        g = l * 255
        b = l * 255
    } else {
        float var_2 = 0
    	if (l < 0.5) {
    		var_2 = l * (1 + s)
    	} else {
    		var_2 = (l + s) - (s * l)
		}

		float var_1 = 2 * l - var_2

        r = 255 * hueToRgb(var_1, var_2, h + (1 / 3))
        g = 255 * hueToRgb(var_1, var_2, h)
        b = 255 * hueToRgb(var_1, var_2, h - (1 / 3))
	}
    
    def rgb = [:]
    rgb = [r: r as int, g: g as int, b: b as int]

    return rgb
}

def hueToRgb(v1, v2, vh) {
if (vh < 0) { vh += 1 }
if (vh > 1) { vh -= 1 }
if ((6 * vh) < 1) { return (v1 + (v2 - v1) * 6 * vh) }
if ((2 * vh) < 1) { return (v2) }
if ((3 * vh) < 2) { return (v1 + (v2 - v1) * ((2 / 3 - vh) * 6)) }
return (v1)
}

def rgbToHSL(rgb) {
    def colors = rgb.split(",")
    
    def r = colors[0].toInteger() / 255
    def g = colors[1].toInteger() / 255
    def b = colors[2].toInteger() / 255
    def h = 0
    def s = 0
    def l = 0

    def var_min = [r,g,b].min()
    def var_max = [r,g,b].max()
    def del_max = var_max - var_min

    l = (var_max + var_min) / 2

    if (del_max == 0) {
        h = 0
        s = 0
    } else {
    	if (l < 0.5) { s = del_max / (var_max + var_min) }
    	else { s = del_max / (2 - var_max - var_min) }

        def del_r = (((var_max - r) / 6) + (del_max / 2)) / del_max
        def del_g = (((var_max - g) / 6) + (del_max / 2)) / del_max
        def del_b = (((var_max - b) / 6) + (del_max / 2)) / del_max

    	if (r == var_max) { h = del_b - del_g }
        else if (g == var_max) { h = (1 / 3) + del_r - del_b }
        else if (b == var_max) { h = (2 / 3) + del_g - del_r }

        if (h < 0) { h += 1 }
        if (h > 1) { h -= 1 }
    }
    
    def hsl = [:]
    hsl = [h: h * 100, s: s * 100, l: l]
    
//    sendEvent(name: "hue", value: h * 100, descriptionText: "Color has changed")
//    sendEvent(name: "saturation", value: s * 100, descriptionText: "Color has changed", displayed: false)

    hsl
}

def on(){
	log.debug "Off >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "power",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}


def updated() {}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
     	String hex = String.format("#%02x%02x%02x", jsonObj.state.colorRGB.red, jsonObj.state.colorRGB.blue, jsonObj.state.colorRGB.green);
        sendEvent(name:"color", value: hex )
        sendEvent(name:"colorTemperature", jsonObj.properties.colorTemperature[0] as int)
        sendEvent(name:"level", value: jsonObj.properties.brightness)
        sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
	    
        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        sendEvent(name: "lastCheckin", value: now)
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
