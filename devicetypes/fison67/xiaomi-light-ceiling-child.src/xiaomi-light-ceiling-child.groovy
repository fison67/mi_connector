/**
 *  Xiaomi Light Ceiling Child (v.0.0.2)
 *
 * MIT License
 *
 * Copyright (c) 2019 fison67@nate.com
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
	definition (name: "Xiaomi Light Ceiling Child", namespace: "fison67", author: "fison67", vid: "generic-rgb-color-bulb", ocfDeviceType: "oic.d.light") {
        capability "Switch"	
        capability "Light"
		capability "Color Control"
		capability "ColorTemperature"
        capability "Switch Level"
	}
    
	preferences {
		input name:	"smooth", type:"enum", title:"Select", options:["On", "Off"], description:"", defaultValue: "On"
        input name: "duration", title:"Duration" , type: "number", required: false, defaultValue: 500, description:""
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setStatus(key, data){
	log.debug "${key} >> ${data}"
    sendEvent(name:key, value: data )
}

def setLevel(brightness){
    def body = [
        "id": parent._getID(),
        "cmd": "changeBgBrightness",
        "data": brightness,
        "subData": getDuration()
    ]
    def options = parent.makeCommand(body)
    parent.sendCommand(options, null)
}

def setColor(color){
    def colors = color.hex
    if(colors == null){
    	def rgb = huesatToRGB(color.hue as Integer, color.saturation as Integer)
        colors = "rgb(${rgb[0]}, ${rgb[1]}, ${rgb[2]})"
    }
    
     def body = [
        "id": parent._getID(),
        "cmd": "bgColor",
        "data": colors,
        "subData": getDuration()
    ]
    
    def options = parent.makeCommand(body)
    parent.sendCommand(options, null)
}

def setColorTemperature(temperature){
    def body = [
        "id": parent._getID(),
        "cmd": "bgColor",
        "data": temperature + "K",
        "subData": getDuration()
    ]
    def options = parent.makeCommand(body)
    parent.sendCommand(options, null)
}

private hex(value, width=2) {
    def s = new BigInteger(Math.round(value).toString()).toString(16)
    while (s.size() < width) {
    	s = "0" + s
    }
    return s
}

def on(){
    def body = [
        "id": parent._getID(),
        "cmd": "changeBgPower",
        "data": "on",
        "subData": getDuration()
    ]
    def options = parent.makeCommand(body)
    parent.sendCommand(options, null)
}

def off(){
	def body = [
        "id": parent._getID(),
        "cmd": "changeBgPower",
        "data": "off",
        "subData": getDuration()
    ]
    def options = parent.makeCommand(body)
    parent.sendCommand(options, null)
}

def updated() {}


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
