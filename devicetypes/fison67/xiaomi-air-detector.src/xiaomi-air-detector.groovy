/**
 *  Xiaomi Air Detector (v.0.0.2)
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
import groovy.transform.Field

@Field 
LANGUAGE_MAP = [
]

metadata {
	definition (name: "Xiaomi Air Detector", namespace: "fison67", author: "fison67") {
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
		capability "Carbon Dioxide Measurement"
		capability "Refresh"
		capability "Sensor"
		capability "Power Source"
        
        attribute "lastCheckin", "Date"
        attribute "co2notice", "enum", ["notice", "unnotice"]  
		attribute "tvocLevel", "number"
		attribute "fineDustLevel", "number"
	}


	simulator {
	}
	preferences {
		input "co2homekit", "number", title:"CO2 Notice for Homekit", defaultValue: 1500, description:"홈킷 CO2농도 경고 최저값 설정", range: "*..*"
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
    case "pm2.5":
    	sendEvent(name:"fineDustLevel", value: params.data as float)
    	break;
    case "temperature":
    	sendEvent(name:"temperature", value: Float.parseFloat(params.data.replace("C","").replace(" ","")))
    	break;
    case "tvoc":
    	sendEvent(name:"tvocLevel", value: params.data as float)
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data as float)
    	break;
    case "co2":
    	def co2ppm = params.data as int
    	sendEvent(name:"carbonDioxide", value: co2ppm)
        sendEvent(name:"co2notice", value: ( co2ppm >= state.co2notice ? "notice" : "unnotice"))        
    	break;
    case "battery":
    	sendEvent(name:"battery", value: params.data)
        break;
    }
    
    updateLastTime()
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed: false)
}

def refresh(){
	log.debug "Refresh"
    def options = [
     	"method": "GET",
        "path": "/devices/get/${state.id}",
        "headers": [
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ]
    ]
    sendCommand(options, callback)
}


def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
		state.BeginTime = jsonObj.state.nightBeginTime
		state.EndTime = jsonObj.state.nightEndTime
		sendEvent(name:"fineDustLevel", value: jsonObj.state.aqi )
		sendEvent(name:"temperature", value: jsonObj.state.temperature )
		sendEvent(name:"humidity", value: jsonObj.state.humidity )
		sendEvent(name:"carbonDioxide", value: jsonObj.state.co2 )
		sendEvent(name:"tvocLevel", value: jsonObj.state.tvoc )
		updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {
	state.co2notice = co2homekit	
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
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}
