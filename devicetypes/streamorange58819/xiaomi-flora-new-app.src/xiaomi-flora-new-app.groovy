/**
 *  Xiaomi Mi Flora New App (v.0.0.1)
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
import java.text.DateFormat

metadata {
	definition (name: "Xiaomi Flora New App", namespace: "streamorange58819", author: "fison67", ocfDeviceType: "oic.r.humidity", mnmn:"SmartThingsCommunity", vid:"22b64c67-79a6-313d-abb0-1b6592761da4") {
        capability "Sensor"
        capability "Temperature Measurement"
        capability "Illuminance Measurement"
        capability "streamorange58819.moisture"
        capability "streamorange58819.fertility"
	}

	simulator {}
    preferences {
		input name: "temperatureType", title:"Select a type" , type: "enum", required: true, options: ["C", "F"], defaultValue: "C"
	}
}


def setInfo(String app_url, String id) {
	state.app_url = app_url
    state.id = id
}

def parse(String description) {}
def setLanguage(language){}
def setExternalAddress(address){}

def setStatus(params){
	log.debug "${params.key} : ${params.data}"
    
    def data = new JsonSlurper().parseText(params.data)
    
    if(data.sensor != null){
        sendEvent(name:"temperature", value: makeTemperature(data.sensor.temperature), unit:temperatureType == null ? "C" : temperatureType)
        sendEvent(name:"illuminance", value: data.sensor.lux)
        sendEvent(name:"humidity", value: data.sensor.moisture, unit:"%")
        sendEvent(name:"fertility", value: data.sensor.fertility, unit:"μS/cm")
    }
    
    if(data.temperature != null){
        sendEvent(name:"temperature", value: makeTemperature(data.temperature), unit:temperatureType == null ? "C" : temperatureType)
    }
    if(data.moisture != null){
        sendEvent(name:"moisture", value: data.moisture, unit:"%")
    }
    if(data.lux != null){
        sendEvent(name:"illuminance", value: data.lux)
    }
    if(data.fertility != null){
        sendEvent(name:"fertility", value: data.fertility, unit:"μS/cm")
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
