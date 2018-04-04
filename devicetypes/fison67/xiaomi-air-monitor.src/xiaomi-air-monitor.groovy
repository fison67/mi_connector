/**
 *  Xiaomi Air Monitor (v.0.0.1)
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
	definition (name: "Xiaomi Air Monitor", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Battery"
	capability "Refresh"
	capability "Sensor"
	capability "Power Source"
	capability "Dust Sensor" // fineDustLevel : PM 2.5   dustLevel : PM 10

        attribute "switch", "string"
        attribute "pm25", "string"
        attribute "usb_state", "string"
        
        attribute "lastCheckin", "Date"
     
        command "refresh"
        command "on"
        command "off"
        
	}


	simulator {
	}

	tiles {
		multiAttributeTile(name:"fineDustLevel", type: "generic", width: 6, height: 4){
			tileAttribute ("device.fineDustLevel", key: "PRIMARY_CONTROL") {
                attributeState "default", label:'${currentValue}㎍/㎥', unit:"㎍/㎥", backgroundColors:[
			[value: -1, color: "#C4BBB5"],
            		[value: 0, color: "#7EC6EE"],
            		[value: 15, color: "#51B2E8"],
            		[value: 50, color: "#e5c757"],
            		[value: 75, color: "#E40000"],
            		[value: 500, color: "#970203"]
            		]
			}
            
            tileAttribute("device.battery", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Battery: ${currentValue}%\n')
            }		
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'\nLast Update: ${currentValue}')
            }
		}
		valueTile("pm25", "device.fineDustLevel", decoration: "flat", width: 2, height: 2) {
        	state "default", label:'${currentValue}㎍/㎥', icon:"http://postfiles9.naver.net/MjAxODA0MDNfMjkw/MDAxNTIyNzI3NjY0Mzk0.yVQdGxRJMGFrGQLVzb-OUThZptHXIBmTaMEZO3LoipAg.v0Rw0_zvHr7wBk-VeH5KQxNry_zUOz4aXUn6I1QQ9xkg.PNG.shin4299/pm25_on.png?type=w3", unit:"㎍/㎥", backgroundColors:[
			[value: -1, color: "#C4BBB5"],
            		[value: 0, color: "#7EC6EE"],
            		[value: 15, color: "#51B2E8"],
            		[value: 50, color: "#e5c757"],
            		[value: 75, color: "#E40000"],
            		[value: 500, color: "#970203"]
            ]
        }
        
        standardTile("switch", "device.switch", inactiveLabel: false, width: 2, height: 2) {
            state "on", label:'ON', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "off", label:'OFF', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
             
        	state "turningOn", label:'turningOn', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "turningOff", label:'turningOff', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state("val", label:'${currentValue}%', defaultState: true, backgroundColor:"#00a0dc")
        }
        
        standardTile("powerSource", "device.powerSource", width: 2, height: 2) {
            state "dc", label:'USB', icon:"st.quirky.spotter.quirky-spotter-plugged", backgroundColor:"#00a0dc"
            state "battery", label:'Battery', icon:"https://www.shareicon.net/data/128x128/2015/03/06/3189_battery_32x32.png", backgroundColor:"#ffffff"
        }
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
   	main (["pm25"])
	details(["fineDustLevel", "switch", "powerSource", "refresh"])
		
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
    	sendEvent(name:"fineDustLevel", value: params.data)
    	break;
    case "aqi":
    	sendEvent(name:"fineDustLevel", value: params.data)
    	break;
    case "power":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "battery":
    	sendEvent(name:"battery", value: params.data)
        break;
    case "usb_state":
    	sendEvent(name:"powerSource", value: (params.data == "on" ? "dc" : "battery"))
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

//[state:[batteryLevel:100, charging:true, aqi:7, power:true], properties:[batteryLevel:100, pm2.5:7, charging:true, power:true]]
def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj

		sendEvent(name:"switch", value: (jsonObj.state.power == true ? "on" : "off") )
		sendEvent(name:"fineDustLevel", value: jsonObj.state.aqi )
		sendEvent(name:"powerSource", value: (jsonObj.state.charging == true ? "dc" : "battery") )
		sendEvent(name:"battery", value: jsonObj.state.batteryLevel )
		updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {
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
