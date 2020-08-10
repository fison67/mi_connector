/**
 *  Xiaomi Fan P(v.0.0.1)
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
	definition (name: "Xiaomi Fan P", namespace: "fison67", author: "fison67", vid: "generic-switch", ocfDeviceType: "oic.d.fan") {
        capability "Switch"						//"on", "off"
		capability "Fan Speed"
        capability "Switch Level"
		capability "Refresh"
         
        attribute "led",  "enum", ["on", "off"]    
        attribute "buzzer", "enum", ["on", "off"]    
        attribute "childLock", "enum", ["on", "off"]    
        attribute "angleEnable", "enum", ["on", "off"]    
        attribute "angleLevel", "number"
        attribute "fanSpeed", "number"
        attribute "powerOffTime", "number"
        
        attribute "lastCheckin", "Date"
        
        command "buzzerOn"
        command "buzzerOff"
        
        command "setFanSpeed1"
        command "setFanSpeed2"
        command "setFanSpeed3"
        command "setFanSpeed4"
       
        command "setAngleOn"
        command "setAngleOff"
        command "setAngle30"
        command "setAngle60"
        command "setAngle90"
        command "setAngle120"
        command "setAngle140"
        
        command "setMoveLeft"
        command "setMoveRight"
	}


	simulator { }
	preferences {
    
	}

	tiles {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjIg/MDAxNTIyMzIzNDI2NjQ2.cPAScBLV_hQaqFRkRqjImmaqyFmY7FY23A23k-t8RZ4g.ORO7eIOdaPHIJwR3tMXLLvU741B6NrncFi2a29ZDWbwg.PNG.shin4299/Fan_tile_on.png?type=w580", backgroundColor:"#73C1EC", nextState:"turningOff"
                attributeState "off", label:'\n${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjkg/MDAxNTIyMzIzNDI2NjQ4.b5E7CPu8ljgF_eHdHFDmK7wLHQG6iymo2DErBeN2u3Ug.61d9mZ5QYaP-oUoIPnXaHA_rocGnrRxBArjSbjctQGwg.PNG.shin4299/Fan_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjkg/MDAxNTIyMzIzNDI2NjQ4.b5E7CPu8ljgF_eHdHFDmK7wLHQG6iymo2DErBeN2u3Ug.61d9mZ5QYaP-oUoIPnXaHA_rocGnrRxBArjSbjctQGwg.PNG.shin4299/Fan_tile_off.png?type=w580", backgroundColor:"#73C1EC", nextState:"turningOff"
                attributeState "turningOff", label:'\n${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjIg/MDAxNTIyMzIzNDI2NjQ2.cPAScBLV_hQaqFRkRqjImmaqyFmY7FY23A23k-t8RZ4g.ORO7eIOdaPHIJwR3tMXLLvU741B6NrncFi2a29ZDWbwg.PNG.shin4299/Fan_tile_on.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
			}
              
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"setFanSpeed"
            }            
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
   			attributeState("default", label:'${currentValue}')
          }
		}
        
        standardTile("angle", "device.setangle") {
            state "on", label:'ON', action:"setAngleOff", icon:"st.motion.motion.inactive", backgroundColor:"#b2cc68", nextState:"turningOff"
            state "off", label:'OFF', action:"setAngleOn", icon:"st.tesla.tesla-locked", backgroundColor:"#cad2b5", nextState:"turningOn"
             
            state "turningOn", label:'turningOn', action:"setAngleOff", icon:"st.tesla.tesla-locked", backgroundColor:"#cad2b5", nextState:"turningOff"
            state "turningOff", label:'turningOff', action:"setAngleOn", icon:"st.motion.motion.inactive", backgroundColor:"#b2cc68", nextState:"turningOn"
        }
        
        valueTile("angleLevel", "device.angleLevel", decoration: "flat", width: 3, height: 1) {
            state "default", label:'${currentValue}'
        }
        standardTile("angle1", "device.setangle") {
			state "default", label: "30°", action: "setAngle30", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfMTAy/MDAxNTIyMzIzNjE4NjE2.2N1NVfE2fmK85H1EhwK_gqEs0FK0qSaJ1KCimGnxZFcg.CAcpOhL3yJXAlvS-JoBcGz1Uf2UnjuICzGs4hBwwK8kg.PNG.shin4299/Fan_20.png?type=w580", backgroundColor:"#b1d6de"
		}
        standardTile("angle2", "device.setangle") {
			state "default", label: "60°", action: "setAngle60", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfMzIg/MDAxNTIyMzIzNjE4NjE2.8HySZX7X1Lb821PxhP96mahNs7dxuYcmDYqy-8bczT8g.wMnYS-sYxbbqXBFrK06w7fT_I6sBb1IcmznRVMOrjjEg.PNG.shin4299/Fan_60.png?type=w580", backgroundColor:"#b1d6de"
		}
        standardTile("angle3", "device.setangle") {
			state "default", label: "90°", action: "setAngle90", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNTgg/MDAxNTIyMzIzNDI2NjE2.86i1P_l290aYfdzh9fATsl3VA-dCVAba9ir_1Ym3mlIg.gyZmaDisBZAbtzzSg-55iwk2ie1ijd64x4ZTo5Jbu4Eg.PNG.shin4299/Fan_30.png?type=w580", backgroundColor:"#b1d6de"
		}
        standardTile("angle4", "device.setangle") {
			state "default", label: "120°", action: "setAngle120", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfMjIw/MDAxNTIyMzIzNjE4NjIx.t6DneqY6JyAZAicutP3NtV9Vf0wWGNAXWnVDIxnL_0gg.-5LlfL2aVTqW3ziuAXWOHFQ6C436d5-XZc_NVHxgS9Mg.PNG.shin4299/Fan_120.png?type=w580", backgroundColor:"#b1d6de"
		}
        standardTile("angle5", "device.setangle") {
			state "default", label: "140°", action: "setAngle140", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfMjIw/MDAxNTIyMzIzNjE4NjIx.t6DneqY6JyAZAicutP3NtV9Vf0wWGNAXWnVDIxnL_0gg.-5LlfL2aVTqW3ziuAXWOHFQ6C436d5-XZc_NVHxgS9Mg.PNG.shin4299/Fan_120.png?type=w580", backgroundColor:"#b1d6de"
		}
        valueTile("refresh", "device.refresh", decoration: "flat") {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }      
        standardTile("buzzer", "device.buzzer") {
            state "on", label:'Sound', action:"buzzerOff", icon: "st.custom.sonos.unmuted", backgroundColor:"#f9b959", nextState:"turningOff"
            state "off", label:'Mute', action:"buzzerOn", icon: "st.custom.sonos.muted", backgroundColor:"#d1cdd2", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"buzzerOff", icon: "st.custom.sonos.muted", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"buzzerOn", icon: "st.custom.sonos.unmuted", backgroundColor:"#f9b959", nextState:"turningOn"
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
    case "power":
        sendEvent(name:"switch", value: params.data  == "true" ? "on" : "off")
    	break;       
    case "led":
        sendEvent(name:"led", value: params.data  == "true" ? "on" : "off")
    	break;      
    case "fanSpeed":
        sendEvent(name:"fanSpeed", value: params.data as int)
    	break;        
    case "angleEnable":
        sendEvent(name:"angleEnable", value: params.data == "true" ? "on" : "off")
    	break;        
    case "angleLevel":
        sendEvent(name:"angleLevel", value: params.data as int)
    	break;   
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "childLock":
    	sendEvent(name:"childLock", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "powerOffTime":
        sendEvent(name:"powerOffTime", value: params.data as int)
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
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ]
    ]
    sendCommand(options, callback)
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

def setFanSpeed(speed){
    requestCommand("fanSpeed", speed)
}

def setFanSpeed1(){
	setFanSpeed(25)
}

def setFanSpeed2(){
	setFanSpeed(50)
}

def setFanSpeed3(){
	setFanSpeed(75)
}

def setFanSpeed4(){
	setFanSpeed(100)
}

def requestCommand(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def angleLevel(level){
    requestCommand("angleLevel", level)
}

def setAngle30(){
    angleLevel(30)
}

def setAngle60(){
    angleLevel(60)
}

def setAngle90(){
    angleLevel(90)
}

def setAngle120(){
    angleLevel(120)
}

def setAngle140(){
    angleLevel(140)
}

def setAngleOn(){
    requestCommand("angle", "on")
}

def setAngleOff(){
    requestCommand("angle", "off")
}

def moveDirection(direction){
    requestCommand("moveDirection", direction)
}

def setMoveLeft(){
    requestCommand("moveDirection", "left")
}

def setMoveRight(){
    requestCommand("moveDirection", "right")
}

def buzzerOn(){
    requestCommand("buzzer", "on")
}

def buzzerOff(){
    requestCommand("buzzer", "off")
}

def on(){
	requestCommand("power", "on")
}

def off(){
	requestCommand("power", "off")
}

def updated() {
    refresh()
    setLanguage(settings.selectedLang)
}

def setLanguage(language){}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
        sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer == true ? "on" : "off"))
	
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
