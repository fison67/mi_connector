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
	definition (name: "Xiaomi Air Detector", namespace: "fison67", author: "fison67", vid: "SmartThings-smartthings-Xiaomi_Temperature_Humidity_Sensor", ocfDeviceType: "x.com.st.d.airqualitysensor") {
        capability "Air Quality Sensor"						//"on", "off"
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
		capability "Tvoc Measurement"
        capability "Carbon Dioxide Measurement"
        capability "Refresh"
		capability "Sensor"
		capability "Power Source"
		capability "Fine Dust Sensor" // fineDustLevel : PM 2.5   dustLevel : PM 10

        
        attribute "lastCheckin", "Date"
        attribute "co2notice", "enum", ["notice", "unnotice"]  
     
        command "noAQS"
        command "noSwitch"
       
	}


	simulator {
	}
	preferences {
		input "co2homekit", "number", title:"CO2 Notice for Homekit", defaultValue: 1500, description:"홈킷 CO2농도 경고 최저값 설정", range: "*..*"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"fineDustLevel", type: "generic", width: 3, height: 2){
			tileAttribute ("device.fineDustLevel", key: "PRIMARY_CONTROL") {
                attributeState "default", label:'${currentValue}㎍/㎥', unit:"㎍/㎥", backgroundColors:[
				[value: 12, color: "#adff00"],
            			[value: 36, color: "#f9d62e"],
            			[value: 56, color: "#fc913a"],
            			[value: 151, color: "#bf0000"],
            			[value: 250, color: "#800000"],
            			[value: 1000, color: "#400000"]
            		]
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'\nLast Update: ${currentValue}')
            }
	}
		valueTile("pm25", "device.fineDustLevel", decoration: "flat", width: 2, height: 2) {
        		state "default", label:'${currentValue}\n㎍/㎥', unit:"ppm", backgroundColors:[
				[value: 12, color: "#adff00"],
            			[value: 36, color: "#f9d62e"],
            			[value: 56, color: "#fc913a"],
            			[value: 151, color: "#bf0000"],
            			[value: 250, color: "#800000"],
            			[value: 1000, color: "#400000"]
            		]
        	}
		valueTile("tvoc", "device.tvocLevel", decoration: "flat", width: 2, height: 2) {
        		state "default", label:'${currentValue}\nppb', unit:"ppb", backgroundColors:[
				[value: 65, color: "#adff00"],
            			[value: 220, color: "#f9d62e"],
            			[value: 660, color: "#fc913a"],
            			[value: 2000, color: "#bf0000"],
            			[value: 10000, color: "#400000"]
            		]
        	}
 		valueTile("carbonDioxide", "device.carbonDioxide", width: 2, height: 2, inactiveLabel: false) {
 			state "carbonDioxide", label:'${currentValue}\nppm', unit:"CO2", backgroundColors: [
				[value: 1000, color: "#adff00"],
            			[value: 1500, color: "#f9d62e"],
            			[value: 2000, color: "#fc913a"],
            			[value: 2500, color: "#bf0000"],
            			[value: 5000, color: "#400000"]
 				]
 		}
		valueTile("temperature", "device.temperature", width: 2, height: 2, inactiveLabel: false) {
 			state("temperature", label: '${currentValue}°', backgroundColors: [
 				[value: 31, color: "#153591"],
 				[value: 44, color: "#1e9cbb"],
 				[value: 59, color: "#90d2a7"],
 				[value: 74, color: "#44b621"],
 				[value: 84, color: "#f1d801"],
 				[value: 95, color: "#d04e00"],
 				[value: 96, color: "#bc2323"]
 				]
 				)
 		}        
		valueTile("humidity", "device.humidity", width: 2, height: 2, inactiveLabel: false) {
 			state("humidity", label: '${currentValue}%', backgroundColors: [
 				[value: 20, color: "#f94d1d"],
 				[value: 40, color: "#ffb71e"],
 				[value: 60, color: "#ddf927"],
 				[value: 80, color: "#19ffeb"],
 				[value: 100, color: "#18cdff"]
 				]
 				)
 		}        
    		valueTile("tvoc_label", "device.tvoc_label", decoration: "flat", width: 2, height: 1) {
            		state "default", label:"tVOC"
        	}        
    		valueTile("pm25_label", "device.pm25_label", decoration: "flat", width: 2, height: 1) {
            		state "default", label:"PM2.5"
        	}        
    		valueTile("co2_label", "device.co2_label", decoration: "flat", width: 2, height: 1) {
            		state "default", label:"CO2"
        	}        
    		valueTile("temp_label", "device.temp_label", decoration: "flat", width: 2, height: 1) {
            		state "default", label:"Temperature"
        	}        
    		valueTile("humi_label", "device.humi_label", decoration: "flat", width: 2, height: 1) {
            		state "default", label:"Humidity"
        	}        
        	valueTile("refresh_label", "device.refresh_label", decoration: "flat", width: 2, height: 1) {
            		state "default", label:'Refresh'
        	}
        
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state("val", label:'${currentValue}%', defaultState: true, backgroundColor:"#00a0dc")
        }
	standardTile("refresh", "device.thermostatMode", width: 2, height: 2) {
		state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
	}
	standardTile("co2notice", "device.co2notice", width: 2, height: 2) {
		state "unnotice", icon:"st.secondary.refresh"
		state "notice", icon:"st.secondary.refresh"
	}        

        
        main (["fineDustLevel"])
		details(["fineDustLevel", "pm25_label", "tvoc_label", "co2_label", "pm25", "tvoc", "carbonDioxide", "temp_label", "humi_label", "refresh_label", "temperature", "humidity", "refresh"])
		
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
    	sendEvent(name:"carbonDioxide", value: params.data as int)
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
    sendEvent(name: "lastCheckin", value: now)
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


def callback(physicalgraph.device.HubResponse hubResponse){
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
	def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: _callback])
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
