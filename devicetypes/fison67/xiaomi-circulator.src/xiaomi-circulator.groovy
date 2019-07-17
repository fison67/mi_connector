/**
 *  Xiaomi Circulator(v.0.0.1)
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
LANGUAGE_MAP = []

metadata {
	definition (name: "Xiaomi Circulator", namespace: "fison67", author: "fison67", vid: "generic-switch", ocfDeviceType: "oic.d.fan") {
        capability "Switch"						
		capability "Fan Speed"
        capability "Switch Level"
        
		capability "Refresh"
		capability "Sensor"
         
        attribute "rotateUpDown", "enum", ["on", "off"]
        attribute "rotateRL", "enum", ["on", "off"]
        attribute "mode", "enum", ["normal", "child"]
        attribute "speedlevel", "string"
        attribute "lastCheckin", "Date"
        attribute "setTimeRemaining", "number"
        
        
        command "setTimeRemaining"
        
        command "speedUp"
        command "speedDown"
        
        command "modeNormal"  
        command "modeChild"    
		command "setRotateUpDownOn"
		command "setRotateUpDownOff"
		command "setRotateRLOn"
        command "setRotateRLOff"
	}

	simulator { }
	preferences { }

	tiles {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjIg/MDAxNTIyMzIzNDI2NjQ2.cPAScBLV_hQaqFRkRqjImmaqyFmY7FY23A23k-t8RZ4g.ORO7eIOdaPHIJwR3tMXLLvU741B6NrncFi2a29ZDWbwg.PNG.shin4299/Fan_tile_on.png?type=w580", backgroundColor:"#73C1EC", nextState:"turningOff"
                attributeState "off", label:'\n${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjkg/MDAxNTIyMzIzNDI2NjQ4.b5E7CPu8ljgF_eHdHFDmK7wLHQG6iymo2DErBeN2u3Ug.61d9mZ5QYaP-oUoIPnXaHA_rocGnrRxBArjSbjctQGwg.PNG.shin4299/Fan_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjkg/MDAxNTIyMzIzNDI2NjQ4.b5E7CPu8ljgF_eHdHFDmK7wLHQG6iymo2DErBeN2u3Ug.61d9mZ5QYaP-oUoIPnXaHA_rocGnrRxBArjSbjctQGwg.PNG.shin4299/Fan_tile_off.png?type=w580", backgroundColor:"#73C1EC", nextState:"turningOff"
                attributeState "turningOff", label:'\n${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjIg/MDAxNTIyMzIzNDI2NjQ2.cPAScBLV_hQaqFRkRqjImmaqyFmY7FY23A23k-t8RZ4g.ORO7eIOdaPHIJwR3tMXLLvU741B6NrncFi2a29ZDWbwg.PNG.shin4299/Fan_tile_on.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
			}
                        
		    tileAttribute("device.speedlevel", key: "VALUE_CONTROL") {
                attributeState("VALUE_UP", action: "speedUp")
                attributeState("VALUE_DOWN", action: "speedDown")
    		}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
                attributeState("default", label:'${currentValue}')
            }

		}
        standardTile("rotateUpDown", "device.rotateUpDown", height: 2, width: 2) {
            state "on", label:'Up-Dn', action:"setRotateUpDownOff", icon:"st.Appliances.appliances11", backgroundColor:"#73C1EC", nextState:"off"
            state "off", label:'Off', action:"setRotateUpDownOn", icon:"st.Outdoor.outdoor19", backgroundColor:"#6eca8f", nextState:"on"
        }
        standardTile("rotateRL", "device.rotateRL", height: 2, width: 2) {
            state "on", label:'R-L', action:"setRotateRLOff", icon:"st.Appliances.appliances11", backgroundColor:"#73C1EC", nextState:"off"
            state "off", label:'Off', action:"setRotateRLOn", icon:"st.Outdoor.outdoor19", backgroundColor:"#6eca8f", nextState:"on"
        }
        standardTile("mode", "device.mode", height: 2, width: 2) {
            state "normal", label: 'Normal', action: "modeChild", icon: "st.illuminance.illuminance.bright", backgroundColor: "#ff93ac", nextState:"child"
            state "child", label: 'Child', action: "modeNormal", icon: "st.illuminance.illuminance.light", backgroundColor: "#ffc2cd", nextState:"normal"
        } 
           
        controlTile("fanSpeed", "device.setFanSpeed", "slider", height: 1, width: 1, range:"(1..32)") {
            state "fanSpeed", action:"setFanSpeed"
		}
        
        valueTile("fanSpeed2", "device.fanSpeed", decoration: "flat", height: 1, width: 3) {
            state "fanSpeed", label:'Speed: ${currentValue}'
        }   
        
        standardTile("blank_area", "", height: 1, width: 2, decoration: "flat") {
			state "default", label: ""
		}
        
        valueTile("refresh", "device.refresh", decoration: "flat") {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }   
        
        valueTile("refresh", "device.refresh", decoration: "flat") {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }     
        
        valueTile("timer_label", "device.leftTime", decoration: "flat", width: 3, height: 1) {
            state "default", label:'Set Timer: ${currentValue}'
        }
        controlTile("time", "device.timeRemaining", "slider", height: 1, width: 1, range:"(1..120)") {
            state "time", action:"setTimeRemaining"
		}
        
        standardTile("tiemr0", "device.timeRemaining") {
			state "default", label: "OFF", action: "stop", icon:"st.Health & Wellness.health7", backgroundColor:"#c7bbc9"
		}

        
        main (["switch"])
        details(["switch",  "rotateUpDown", "rotateRL", "mode", "fanSpeed2", "fanSpeed", "blank_area", "timer_label", "time", "tiemr0", "refresh"])

	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setInfo(String app_url, String id) {
	state.app_url = app_url
    state.id = id
}

def setStatus(params){
    log.debug "Status >> ${params.key}(${params.data})"
    
 	switch(params.key){ 
    case "fanSpeed":
        sendEvent(name:"fanSpeed", value: params.data as int)
    	break;     
    case "power":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "mode":
    	sendEvent(name:"mode", value: params.data )
    	break
    case "rotateRL":
    	sendEvent(name:"rotateRL", value: (params.data == "true" ? "on" : "off"))
    	break
    case "rotateUpDown":
    	sendEvent(name:"rotateUpDown", value: (params.data == "true" ? "on" : "off"))
    	break
    }
    
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

def msToTime(duration) {
    def seconds = (duration%60).intValue()
    def minutes = ((duration/60).intValue() % 60).intValue()
    def hours = ( (duration/(60*60)).intValue() %24).intValue()

    hours = (hours < 10) ? "0" + hours : hours
    minutes = (minutes < 10) ? "0" + minutes : minutes
    seconds = (seconds < 10) ? "0" + seconds : seconds

    return hours + ":" + minutes + ":" + seconds
}

def timter(){
	if(state.timerCount > 0){
    	state.timerCount = state.timerCount - 30;
        if(state.timerCount <= 0){
        	if(state.power == "on"){
        		off()
            }
        }else{
        	runIn(30, timter)
        }
        updateTimer()
    }
}

def updateTimer(){
    def timeStr = msToTime(state.timerCount)
	log.debug "Left time >> ${timeStr}"
    sendEvent(name:"leftTime", value: "${timeStr}")
    sendEvent(name:"timeRemaining", value: Math.round(state.timerCount/60))
}

def processTimer(second){
	if(state.timerCount == null){
    	state.timerCount = second;
    	runIn(30, timter)
    }else if(state.timerCount == 0){
		state.timerCount = second;
    	runIn(30, timter)
    }else{
    	state.timerCount = second
    }
    log.debug "Time >> ${state.timerCount}"
    updateTimer()
}

def stop() { 
	unschedule()
	log.debug "Timer Off"
	state.timerCount = 0
	updateTimer()
}

def setTimeRemaining(time) { 
	log.debug "Timer ${time}Min >> ${state.timerCount}"
    processTimer(time * 60)
}

def speedUp(){
	def currentSpeed = device.currentValue("fanSpeed")
    currentSpeed++
    if(currentSpeed > 32){
    	currentSpeed = 32
    }	
    setFanSpeed(currentSpeed)
}

def speedDown(){
	def currentSpeed = device.currentValue("fanSpeed")
    currentSpeed--
    if(currentSpeed < 0){
    	currentSpeed = 0
    }	
    setFanSpeed(currentSpeed)
}

def setFanSpeed(speed){
    def body = [
        "id": state.id,
        "cmd": "fanSpeed",
        "data": speed
    ]
    def options = makeCommand(body)
    sendCommand(options, null) 
}   

def modeNormal(){
    command("changeMode", "normal")
}

def modeChild(){
	command("changeMode", "child")
}

def setRotateUpDownOn(){
	command("rotateUpDown", "on")
}

def setRotateUpDownOff(){
	command("rotateUpDown", "off")
}

def setRotateRLOn(){
	command("rotateRL", "on")
}

def setRotateRLOff(){
	command("rotateRL", "off")
}

def on(){
	command("power", "on")
}

def off(){
	command("power", "off")
}

def command(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def updated() {
    refresh()
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
        sendEvent(name:"switch", value: jsonObj.properties.power ? "on" : "off")
        sendEvent(name:"mode", value: jsonObj.properties.mode)
        sendEvent(name:"rotateRL", value: jsonObj.properties.rotateRL ? "on" : "off")
        sendEvent(name:"rotateUpDown", value: jsonObj.properties.rotateUpDown ? "on" : "off")
        sendEvent(name:"fanSpeed", value: jsonObj.properties.fanSpeed)

        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        sendEvent(name: "lastCheckin", value: now)

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
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}
