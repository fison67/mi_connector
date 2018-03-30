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
         
        attribute "switch", "string"
        attribute "pm25", "string"
        attribute "battery", "string"
        attribute "usb_state", "string"
        
        attribute "lastCheckin", "Date"
     
        
        command "on"
        command "off"
        
	}


	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"off", icon:"https://postfiles.pstatic.net/MjAxODAzMjZfMzAw/MDAxNTIyMDQxNTc1NjIx.CQDEOYh7wDWwPWjLSIrAp9Kaak_e3uV070XumPqCjBUg.GU36EBE7o_IO-SnjHAEv-SBtZBCFmCMN7cJCnFnkC6kg.PNG.fuls/pm2_75.png?type=w773", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"on", icon:"https://postfiles.pstatic.net/MjAxODAzMjZfMzAw/MDAxNTIyMDQxNTc1NjIx.CQDEOYh7wDWwPWjLSIrAp9Kaak_e3uV070XumPqCjBUg.GU36EBE7o_IO-SnjHAEv-SBtZBCFmCMN7cJCnFnkC6kg.PNG.fuls/pm2_75.png?type=w773", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"off", icon:"https://postfiles.pstatic.net/MjAxODAzMjZfMzAw/MDAxNTIyMDQxNTc1NjIx.CQDEOYh7wDWwPWjLSIrAp9Kaak_e3uV070XumPqCjBUg.GU36EBE7o_IO-SnjHAEv-SBtZBCFmCMN7cJCnFnkC6kg.PNG.fuls/pm2_75.png?type=w773", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"on", icon:"https://postfiles.pstatic.net/MjAxODAzMjZfMzAw/MDAxNTIyMDQxNTc1NjIx.CQDEOYh7wDWwPWjLSIrAp9Kaak_e3uV070XumPqCjBUg.GU36EBE7o_IO-SnjHAEv-SBtZBCFmCMN7cJCnFnkC6kg.PNG.fuls/pm2_75.png?type=w773", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
         valueTile("pm25", "device.pm25", width: 2, height: 2, unit: "") {
            state("val", label:'${currentValue}', defaultState: true, 
            	backgroundColors:[
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
        
        
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state("val", label:'${currentValue}', defaultState: true, backgroundColor:"#00a0dc")
        }
        
        valueTile("usb_state", "device.usb_state", width: 2, height: 2) {
            state("val", label:'${currentValue}', defaultState: true, backgroundColor:"#00a0dc")
        }
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
    	sendEvent(name:"pm25", value: params.data + "㎍/㎥")
    	break;
    case "aqi":
    	sendEvent(name:"pm25", value: params.data + "㎍/㎥")
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data + "%")
    	break;
    case "power":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "temperature":
        sendEvent(name:"temperature", value: params.data)
    	break;
    case "battery":
    	sendEvent(name:"battery", value: params.data)
        break;
    case "usb_state":
    	sendEvent(name:"usb_state", value: params.data)
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def refresh(){
	log.debug "Refresh"
    def options = [
     	"method": "GET",
        "path": "/get?id=${state.id}",
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

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
//        setStatus(jsonObj.state)
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
