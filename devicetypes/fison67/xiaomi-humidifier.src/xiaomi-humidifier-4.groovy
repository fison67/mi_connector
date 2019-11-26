/**
 *  Xiaomi Humidifier4 (v.0.0.1)
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
import groovy.transform.Field


metadata {
	definition (name: "Xiaomi Humidifier 4", namespace: "fison67", author: "fison67") {
        capability "Switch"						
        capability "Switch Level"
		capability "Sensor"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Refresh"
         
        attribute "gear", "enum", [1,2,3,4]
        attribute "buzzer", "enum", ["on", "off"]
        attribute "led", "enum",  ["on", "off"]
        attribute "water", "enum",  ["full", "empty"]
        attribute "waterTank", "enum", ["detached", "attached"]
        attribute "lastCheckin", "Date"
    
        command "setGear", ["number"]
        command "gear1"
        command "gear2"
        command "gear3"
        command "gear4"
        command "buzzerOn"
        command "buzzerOff"
        command "ledOn"
        command "ledOff"
	}

	simulator {}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "off", label:'OFF', action:"on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ2/MDAxNTIyMTUxNzIxMTk3.xeCR1k4pk0vDOozb43Lfo6g2fMC1a_VJFUpTQ071XRUg.dyhFTAUaCwWPUYc4hPUdGiuUI5yeRJ4QpP3kX802AlIg.PNG.shin4299/Humi_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "on", label:'ON', action:"off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ1/MDAxNTIyMTUxNzIxMTk5.LTiuV1QSyPu6WgMB3uR7Bc-Hy19Uwgard5XKG5jj1JIg.XpdiwfmUg3Rz6IgIWyamtsrYeW0BJRqj28XyHRuADA0g.PNG.shin4299/Humi_tile_auto.png?type=w580", backgroundColor:"#73C1EC", nextState:"modechange"
                
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ2/MDAxNTIyMTUxNzIxMTk3.xeCR1k4pk0vDOozb43Lfo6g2fMC1a_VJFUpTQ071XRUg.dyhFTAUaCwWPUYc4hPUdGiuUI5yeRJ4QpP3kX802AlIg.PNG.shin4299/Humi_tile_off.png?type=w580", backgroundColor:"#C4BBB5", nextState:"off"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ1/MDAxNTIyMTUxNzIxMTk5.LTiuV1QSyPu6WgMB3uR7Bc-Hy19Uwgard5XKG5jj1JIg.XpdiwfmUg3Rz6IgIWyamtsrYeW0BJRqj28XyHRuADA0g.PNG.shin4299/Humi_tile_auto.png?type=w580", backgroundColor:"#C4BBB5", nextState:"on"
			}
			tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
        		attributeState("humidity", label:'${currentValue}%', unit:"%", defaultState: true)
    		}            
			tileAttribute("device.temperature", key: "SECONDARY_CONTROL") {
				attributeState("temperature", label:'                ${currentValue}°', unit:"°", defaultState: true)
    		}            
		    tileAttribute ("device.level", key: "SLIDER_CONTROL") {
        		attributeState "level", action:"switch level.setLevel"
		    }
		}
        valueTile("waterLabel", "", decoration: "flat", width: 3, height: 1) {
            state "default", label:'Water'
        }
        valueTile("water", "device.water", decoration: "flat", width: 3, height: 1) {
            state "default", label: '${currentValue}'
        }  
        valueTile("waterTankLabel", "", decoration: "flat", width: 3, height: 1) {
            state "default", label:'WaterTank'
        }
        valueTile("waterTank", "device.waterTank", decoration: "flat", width: 3, height: 1) {
            state "default", label: '${currentValue}'
        }       
        standardTile("buzzer", "device.buzzer", width: 1, height: 1) {
            state "on", label:'Sound', action:"buzzerOff", icon: "st.custom.sonos.unmuted", backgroundColor:"#BAA7BC", nextState:"turningOff"
            state "off", label:'Mute', action:"buzzerOn", icon: "st.custom.sonos.muted", backgroundColor:"#d1cdd2", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"buzzerOff", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"buzzerOn", backgroundColor:"#BAA7BC", nextState:"turningOn"
        }
        standardTile("led", "device.led", width: 1, height: 1) {
            state "on", label:'Bright', action:"ledOff", icon: "st.illuminance.illuminance.bright", backgroundColor:"#ff93ac", nextState:"turningOff"
            state "off", label:'OFF', action:"ledOn", icon: "st.illuminance.illuminance.dark", backgroundColor:"#d6c6c9", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"ledOff", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"ledOn", backgroundColor:"#BAA7BC", nextState:"turningOn"
        }
        standardTile("gear1", "device.gear1", width: 1, height: 1) {
            state "default", label:'Low', action:"gear1", backgroundColor:"#6eca8f"
        }
        standardTile("gear2", "device.gear2", width: 1, height: 1) {
            state "default", label:'Medium', action:"gear2", backgroundColor:"#FFDE61"
        }
        standardTile("gear3", "device.gear3", width: 1, height: 1) {
            state "default", label:'High', action:"gear3", backgroundColor:"#f7ae0e"
        }
        standardTile("gear4", "device.gear4", width: 1, height: 1) {
            state "default", label:'Humidity', action:"gear4", backgroundColor:"#ff9eb2"
        }
        valueTile("refresh", "device.refresh", decoration: "flat", width: 1, height: 1) {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }        
		
        main (["switch"])
        details(["switch", "waterLabel", "water", "waterTankLabel", "waterTank", "buzzer", "led", "gear1", "gear2", "gear3", "gear4", "refresh"])
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
    	sendEvent(name:"switch", value: params.data == "true" ? "on" : "off")
    	break;
    case "led":
        sendEvent(name:"led", value: params.data == "true" ? "on" : "off")
    	break;     
    case "buzzer":
    	sendEvent(name:"buzzer", value: params.data == "true" ? "on" : "off" )
        break;
    case "gear":
    	sendEvent(name:"mode", value: params.data as int)
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data as int)
    	break;
    case "temperature":
        sendEvent(name:"temperature", value: Math.round(Float.parseFloat(params.data.replace("C",""))*10)/10 )
    	break;   
    case "targetHumidity":
        sendEvent(name:"level", value: params.data as int)
    	break;
    case "water":
    	sendEvent(name:"water", value: (params.data == "true" ? "empty" : "full") )
        break;
    case "waterTank":
    	sendEvent(name:"waterTank", value: (params.data == "true" ? "attached" : "detached") )
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

def setGear(level){
    def body = [
        "id": state.id,
        "cmd": "changeGear",
        "data": level
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def gear1(){ setGear(1) }
def gear2(){ setGear(2) }
def gear3(){ setGear(3) }
def gear4(){ setGear(4) }

def buzzerOn(){
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "on"
    ]
    def options = makeCommand(body)
}

def buzzerOff(){
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "off"
    ]
    def options = makeCommand(body)
}

def setLevel(level){
	def set = Math.ceil(level/20) as int
    def body = [
        "id": state.id,
        "cmd": "targetHumidity",
        "data": set
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def ledOn(){
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def ledOff(){
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	def body = [
        "id": state.id,
        "cmd": "power",
        "data": "off"
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
        
       	sendEvent(name:"switch", value: jsonObj.state.power ? "on" : "off")
        sendEvent(name:"led", value: (jsonObj.state.led ? "on" : "off"))
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer ? "on" : "off"))
        sendEvent(name:"water", value: (jsonObj.state.water ? "empty" : "full"))
        sendEvent(name:"waterTank", value: (jsonObj.state.water ? "attached" : "detached"))
    	sendEvent(name:"gear", value: jsonObj.state.gear)
        sendEvent(name:"temperature", value: jsonObj.properties.temperature.value)
        sendEvent(name:"relativeHumidity", value: jsonObj.properties.relativeHumidity)
        sendEvent(name:"level", value: targetHumidity)
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
