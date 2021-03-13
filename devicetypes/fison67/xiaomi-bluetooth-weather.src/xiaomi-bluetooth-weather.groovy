/**
 *  Xiaomi Bluetooth Temperature & Humidity (v.0.0.3)
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
import java.text.DateFormat

metadata {
	definition (name: "Xiaomi Bluetooth Weather", namespace: "fison67", author: "fison67", vid: "SmartThings-smartthings-Xiaomi_Temperature_Humidity_Sensor", ocfDeviceType: "oic.d.thermostat") {
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Sensor"
        capability "Battery"
        capability "Refresh"
	}

	simulator {}
    
	preferences {
		input name: "temperatureType", title:"Select a type" , type: "enum", required: true, options: ["C", "F"], defaultValue: "C"
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

def setExternalAddress(address){
	log.debug "External Address >> ${address}"
	state.externalAddress = address
}

def setStatus(params){
    def data = new JsonSlurper().parseText(params.data)
    log.debug data
    
    if(data.batteryLevel){
    	sendEvent(name:"battery", value: data.batteryLevel)
    }
    if(data.sensor != null){
        sendEvent(name:"temperature", value: makeTemperature(data.sensor.temperature), unit: (temperatureType == null ? "C" : temperatureType))
    	sendEvent(name:"humidity", value: data.sensor.relativeHumidity, unit:"%")
    }else{
    	if(data.temperature != null){
        	sendEvent(name:"temperature", value: makeTemperature(data.temperature), unit: (temperatureType == null ? "C" : temperatureType))
        }
        if(data.relativeHumidity != null){
        	sendEvent(name:"humidity", value: data.relativeHumidity, unit:"%")
        }
    }
}

def makeTemperature(temperature){
	if(temperatureType == "F"){
    	return ((temperature * 9 / 5) + 32)
    }else{
    	return temperature
    }
}

def updated() {}
