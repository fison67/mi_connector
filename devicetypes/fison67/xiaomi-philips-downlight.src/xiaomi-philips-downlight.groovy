/**
 *  Xiaomi Philips Downlight (v.0.0.2)
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
	definition (name: "Xiaomi Philips Downlight", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Actuator"
        capability "Refresh"
		capability "ColorTemperature"
        capability "Switch Level"
        capability "Light"
        
        attribute "lastOn", "string"
        attribute "lastOff", "string"
        attribute "lastCheckin", "Date"
         
        command "setScene1"
        command "setScene2"
        command "setScene3"
        command "setScene4"
	}

	preferences {
		input name:	"smooth", type:"enum", title:"Select", options:["On", "Off"], description:"", defaultValue: "On"
        input name: "duration", title:"Duration" , type: "number", required: false, defaultValue: 500, description:""
	}

	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTU5/MDAxNTIyMTUzOTk3MTgy.j2vWDdDUen5w1lVKthaUgjRTk8EU0X1DTzLkIurRAyMg.Me30JNZPejyeC_GrQ1rffZvzaiUWYxHjLCyVkMjGCHYg.PNG.shin4299/Yeelight_mo_tile_on.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfNTAg/MDAxNTIyMTUzOTk3MTcy.XnqpwsxugesLmLzedrSispSEYs8dm3M18Y_UAx5M1icg.XC0qikMbzjwWsl_gQnSoQUnFwzsT78q-9rihSuWvVGEg.PNG.shin4299/Yeelight_mo_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfNTAg/MDAxNTIyMTUzOTk3MTcy.XnqpwsxugesLmLzedrSispSEYs8dm3M18Y_UAx5M1icg.XC0qikMbzjwWsl_gQnSoQUnFwzsT78q-9rihSuWvVGEg.PNG.shin4299/Yeelight_mo_tile_off.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.ofn", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTU5/MDAxNTIyMTUzOTk3MTgy.j2vWDdDUen5w1lVKthaUgjRTk8EU0X1DTzLkIurRAyMg.Me30JNZPejyeC_GrQ1rffZvzaiUWYxHjLCyVkMjGCHYg.PNG.shin4299/Yeelight_mo_tile_on.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"

			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}')
            }
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"switch level.setLevel"
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
			state "default", label: "Bright", action: "setScene1", backgroundColor:"#f2aebc"
		}
        standardTile("scene2", "device.scene") {
			state "default", label: "TV", action: "setScene2", backgroundColor:"#f77993"
		}
        standardTile("scene3", "device.scene") {
			state "default", label: "Warm", action: "setScene3", backgroundColor:"#f75475"
		}
        standardTile("scene4", "device.scene") {
			state "default", label: "Midnight", action: "setScene4", backgroundColor:"#f72751"
		}
        valueTile("modeName", "device.modeName", decoration: "flat", width: 2, height: 1) {
            state "default", label:'${currentValue}'
        }
        controlTile("colorTemperature", "device.colorTemperature", "slider", height: 1, width: 2, range:"(2700..6500)") {
	    	state "colorTemperature", action:"setColorTemperature"
		}
        
   	main (["switch"])
	details(["switch", "refresh", "lastOn_label", "lastOn", "lastOff_label","lastOff", "scene1", "scene2", "scene3", "scene4", "modeName", "colorTemperature" ])       
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
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
 	switch(params.key){
    case "colorTemperature":
    	sendEvent(name:"colorTemperature", value: params.data as int)
    	break
    case "power":
        if(params.data == "true"){
            sendEvent(name:"switch", value: "on")
            sendEvent(name: "lastOn", value: now, displayed: false)
        } else {
            sendEvent(name:"switch", value: "off")
            sendEvent(name: "lastOff", value: now, displayed: false)
        }
    	break;
    case "brightness":
    	sendEvent(name:"level", value: params.data )
    	break;
    case "scene":
        sendEvent(name:"modeName", value: getModeName(params.data as int) )
    	break;
    }
    sendEvent(name: "lastCheckin", value: now, displayed: false)
}

def getModeName(val){
	def name
	switch(val){
    case 0:
    	name = "Custom"
        break;
    case 1:
		name = "Bright"
    	break;
	case 2:
		name = "TV"
		break;
	case 3:
		name = "Warm"
        break;
    case 4:
		name = "Midnight"
        break;
    }
	return name
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
    def body = [
        "id": state.id,
        "cmd": "brightness",
        "data": brightness,
        "subData": getDuration()
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setColor(color){
	log.debug "setColor >> ${state.id} >> ${color.hex}"
    def body = [
        "id": state.id,
        "cmd": "color",
        "data": color.hex,
        "subData": getDuration()
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
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

def setScene1(){
	log.debug "setScene1 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 1
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene2(){
	log.debug "setScene2 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 2
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene3(){
	log.debug "setScene3 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 3
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene4(){
	log.debug "setScene4 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 4
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}


def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        def colorRGB = colorTemperatureToRGB(jsonObj.state.color.values[0])
        String hex = String.format("#%02x%02x%02x", (int)colorRGB[0], (int)colorRGB[1], (int)colorRGB[2]);  
    	sendEvent(name:"color", value: hex )
        sendEvent(name:"level", value: jsonObj.state.brightness)
        sendEvent(name:"switch", value: jsonObj.state.power == true ? "on" : "off")
        sendEvent(name:"modeName", value: getModeName(jsonObj.state.scene) )
	    
        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        sendEvent(name: "lastCheckin", value: now, displayed: false)
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}


def updated() {}

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
