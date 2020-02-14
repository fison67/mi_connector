/**
 *  Xiaomi Philips Sread (v.0.0.1)
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

metadata {
	definition (name: "Xiaomi Philips Sread", namespace: "fison67", author: "fison67") {
        capability "Switch"						
        capability "Actuator"
        capability "Refresh"
        capability "Switch Level"
        capability "Light"
        
        attribute "mode", "string"
        attribute "eyeCare", "string"
        attribute "lastOn", "string"
        attribute "lastOff", "string"
        attribute "lastCheckin", "Date"
        
        command "eyeCareOn"
        command "eyeCareOff"
        command "modeStudy"
        command "modeReading"
        command "modePhone"
        
        
	}

	preferences {
	}

	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTU5/MDAxNTIyMTUzOTk3MTgy.j2vWDdDUen5w1lVKthaUgjRTk8EU0X1DTzLkIurRAyMg.Me30JNZPejyeC_GrQ1rffZvzaiUWYxHjLCyVkMjGCHYg.PNG.shin4299/Yeelight_mo_tile_on.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfNTAg/MDAxNTIyMTUzOTk3MTcy.XnqpwsxugesLmLzedrSispSEYs8dm3M18Y_UAx5M1icg.XC0qikMbzjwWsl_gQnSoQUnFwzsT78q-9rihSuWvVGEg.PNG.shin4299/Yeelight_mo_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfNTAg/MDAxNTIyMTUzOTk3MTcy.XnqpwsxugesLmLzedrSispSEYs8dm3M18Y_UAx5M1icg.XC0qikMbzjwWsl_gQnSoQUnFwzsT78q-9rihSuWvVGEg.PNG.shin4299/Yeelight_mo_tile_off.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'\n${name}', action:"switch.ofn", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTU5/MDAxNTIyMTUzOTk3MTgy.j2vWDdDUen5w1lVKthaUgjRTk8EU0X1DTzLkIurRAyMg.Me30JNZPejyeC_GrQ1rffZvzaiUWYxHjLCyVkMjGCHYg.PNG.shin4299/Yeelight_mo_tile_on.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"

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
        
        valueTile("eyeCareOn", "device.eyeCareOn", width: 2, height: 2, decoration: "flat") {
            state "default", label:'EyeCare ON', action:"eyeCareOn"
        }   
        valueTile("eyeCareOff", "device.eyeCareOff", width: 2, height: 2, decoration: "flat") {
            state "default", label:'EyeCare OFF', action:"eyeCareOff"
        }   
        valueTile("modeStudy", "device.modeStudy", width: 2, height: 2, decoration: "flat") {
            state "default", label:'Study', action:"modeStudy"
        }   
        valueTile("modeReading", "device.modeReading", width: 2, height: 2, decoration: "flat") {
            state "default", label:'Reading', action:"modeReading"
        }   
        valueTile("modePhone", "device.modePhone", width: 2, height: 2, decoration: "flat") {
            state "default", label:'Phone', action:"modePhone"
        }   
        valueTile("mode", "device.mode", decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue}'
        }
        
        main (["switch"])
        details(["switch", "refresh", "lastOn_label", "lastOn", "lastOff_label","lastOff", "eyeCareOn", "eyeCareOff", "modeStudy", "modeReading", "modePhone", "mode" ])       
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
    case "power":
        if(params.data == "true"){
            sendEvent(name:"switch", value: "on")
            sendEvent(name: "lastOn", value: now)
        } else {
            sendEvent(name:"switch", value: "off")
            sendEvent(name: "lastOff", value: now)
        }
    	break;
    case "brightness":
    	sendEvent(name:"level", value: params.data as int)
    	break;
    case "eyeCare":
    	sendEvent(name:"eyeCare", value: params.data == "true" ? "on" : "off")
    	break
    case "mode":
    	sendEvent(name:"mode", value: params.data)
    	break
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
    def body = [
        "id": state.id,
        "cmd": "brightness",
        "data": brightness
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	log.debug "ON"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "OFF"
	def body = [
        "id": state.id,
        "cmd": "power",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def eyeCareOn(){
	log.debug "EYECARE ON"
    def body = [
        "id": state.id,
        "cmd": "eyeCare",
        "data": true
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def eyeCareOff(){
	log.debug "EYECARE OFF"
	def body = [
        "id": state.id,
        "cmd": "eyeCare",
        "data": false
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def modeStudy(){
	log.debug "modeStudy"
	def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "study"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def modeReading(){
	log.debug "modeReading"
	def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "reading"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def modePhone(){
	log.debug "modePhone"
	def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "phone"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        
        sendEvent(name:"level", value: jsonObj.properties.brightness)
        sendEvent(name:"switch", value: jsonObj.properties.power ? "on" : "off")
        sendEvent(name: "lastCheckin", value: new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone))
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
