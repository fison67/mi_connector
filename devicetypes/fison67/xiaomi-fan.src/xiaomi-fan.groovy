/**
 *  Xiaomi Fan(v.0.0.1)
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
	definition (name: "Xiaomi Fan", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Switch Level"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Fan Speed"
		capability "Refresh"
		capability "Sensor"
         
        attribute "buzzer", "string"
        attribute "anglelevel", "string"
        attribute "ledBrightness", "string"
        attribute "speedlevel", "string"
        attribute "fanspeedstep", "enum", ["low", "medium", "high", "strong"]        
        attribute "setangle", "enum", ["off", "on", "30", "60", "90", "120"]        
        attribute "settimer", "enum", ["off", "15", "30", "60", "90", "120"]        
        attribute "setdirection", "enum", ["left", "right"]        
        attribute "fanmode", "enum", ["general", "natural"]        
        attribute "naturalLevel", "string"
        attribute "speed", "string"
        attribute "powerOffTime", "string"
        attribute "acPower", "string"
        attribute "batteryLevel", "string"
        attribute "childLock", "string"
        
        attribute "lastCheckin", "Date"
         
        command "on"
        command "off"
        command "generalOn"
        command "naturalOn"
        
        command "buzzerOn"
        command "buzzerOff"
        
        command "setBright"
        command "setBrightDim"
        command "setBrightOff"
        
        command "setFanSpeed1"
        command "setFanSpeed2"
        command "setFanSpeed3"
        command "setFanSpeed4"
        command "tempUp"
        command "tempDown"
        command "setFanNatural"
        command "setAngleLevel"
        command "setAngleOn"
        command "setAngleOff"
        command "setAngle30"
        command "setAngle60"
        command "setAngle90"
        command "setAngle120"
        command "setMoveLeft"
        command "setMoveRight"
        command "settimeroff"
        command "settimer15"
        command "settimer30"
        command "settimer60"
        command "settimer90"
        command "settimer120"
	}


	simulator {
	}

	tiles {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjIg/MDAxNTIyMzIzNDI2NjQ2.cPAScBLV_hQaqFRkRqjImmaqyFmY7FY23A23k-t8RZ4g.ORO7eIOdaPHIJwR3tMXLLvU741B6NrncFi2a29ZDWbwg.PNG.shin4299/Fan_tile_on.png?type=w580", backgroundColor:"#73C1EC", nextState:"turningOff"
                attributeState "off", label:'\n${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjkg/MDAxNTIyMzIzNDI2NjQ4.b5E7CPu8ljgF_eHdHFDmK7wLHQG6iymo2DErBeN2u3Ug.61d9mZ5QYaP-oUoIPnXaHA_rocGnrRxBArjSbjctQGwg.PNG.shin4299/Fan_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjkg/MDAxNTIyMzIzNDI2NjQ4.b5E7CPu8ljgF_eHdHFDmK7wLHQG6iymo2DErBeN2u3Ug.61d9mZ5QYaP-oUoIPnXaHA_rocGnrRxBArjSbjctQGwg.PNG.shin4299/Fan_tile_off.png?type=w580", backgroundColor:"#73C1EC", nextState:"turningOff"
                attributeState "turningOff", label:'\n${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNjIg/MDAxNTIyMzIzNDI2NjQ2.cPAScBLV_hQaqFRkRqjImmaqyFmY7FY23A23k-t8RZ4g.ORO7eIOdaPHIJwR3tMXLLvU741B6NrncFi2a29ZDWbwg.PNG.shin4299/Fan_tile_on.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
			}
		    tileAttribute("device.speedlevel", key: "VALUE_CONTROL") {
	        attributeState("VALUE_UP", action: "tempUp")
    	    attributeState("VALUE_DOWN", action: "tempDown")
    		}
            
            tileAttribute ("device.fanSpeed", key: "SLIDER_CONTROL") {
                attributeState "level", action:"FanSpeed.setFanSpeed"
            }            
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
   			attributeState("default", label:'${currentValue}')
          }
		}
        standardTile("switch2", "device.switch", inactiveLabel: false, width: 2, height: 2) {
            state "on", label:'ON', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfMTcw/MDAxNTIyMzIzNDI2NjQ3.-DR_CT7fGBUGj65di_Ku0jLCvA4oSgWbFSivfsbA26og.ajX0-he2ip3P3kI_0OqhYwSzKblR8zzIeEa4QtJfSHcg.PNG.shin4299/Fan_main_on.png?type=w580", backgroundColor:"#73C1EC", nextState:"turningOff"
            state "off", label:'OFF', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNDIg/MDAxNTIyMzIzNDI2NjM3.kLKELF4VIDpDZoWz3FGdj1IeNl5-1QTnTUj3PpD_O54g.coZVo_0F8xdhdKxWSuUH_0ldi7v-TpkTXDJtcEMpT34g.PNG.shin4299/Fan_main_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
             
        	state "turningOn", label:'turningOn', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfNDIg/MDAxNTIyMzIzNDI2NjM3.kLKELF4VIDpDZoWz3FGdj1IeNl5-1QTnTUj3PpD_O54g.coZVo_0F8xdhdKxWSuUH_0ldi7v-TpkTXDJtcEMpT34g.PNG.shin4299/Fan_main_off.png?type=w580", backgroundColor:"#73C1EC", nextState:"turningOff"
            state "turningOff", label:'turningOff', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjlfMTcw/MDAxNTIyMzIzNDI2NjQ3.-DR_CT7fGBUGj65di_Ku0jLCvA4oSgWbFSivfsbA26og.ajX0-he2ip3P3kI_0OqhYwSzKblR8zzIeEa4QtJfSHcg.PNG.shin4299/Fan_main_on.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        valueTile("mode_label", "", decoration: "flat") {
            state "default", label:'Mode'
        }
        valueTile("rotation_label", "", decoration: "flat") {
            state "default", label:'Rotation'
        }
        valueTile("speed_label", "", decoration: "flat", width: 4, height: 1) {
            state "default", label:'Fan Speed Control'
        }
        valueTile("temperature", "device.temperature") {
            state("val", label:'${currentValue}', defaultState: true, 
            )
        }
        valueTile("humidity", "device.humidity") {
            state("val", label:'${currentValue}', defaultState: true, 
            )
        }   
        valueTile("anglelevel", "device.anglelevel") {
            state("val", label:'${currentValue}', defaultState: true, 
            )
        }   
        standardTile("speed1", "device.fanspeedstep") {
			state "default", label: "Low", action: "setFanSpeed1", icon:"st.quirky.spotter.quirky-spotter-luminance-dark", backgroundColor:"#FFDE61"
		}
        standardTile("speed2", "device.fanspeedstep") {
			state "default", label: "Medium", action: "setFanSpeed2", icon:"st.quirky.spotter.quirky-spotter-luminance-light", backgroundColor:"#f9b959"
		}
        standardTile("speed3", "device.fanspeedstep") {
			state "default", label: "High", action: "setFanSpeed3", icon:"st.quirky.spotter.quirky-spotter-luminance-bright", backgroundColor:"#ff9eb2"
		}
        standardTile("speed4", "device.fanspeedstep") {
			state "default", label: "Strong", action: "setFanSpeed4", icon:"st.Weather.weather1", backgroundColor:"#db5764"
		}
        standardTile("angle", "device.setangle") {
            state "on", label:'ON', action:"setAngleOff", icon:"st.motion.motion.inactive", backgroundColor:"#b2cc68", nextState:"turningOff"
            state "off", label:'OFF', action:"setAngleOn", icon:"st.tesla.tesla-locked", backgroundColor:"#cad2b5", nextState:"turningOn"
             
        	state "turningOn", label:'turningOn', action:"setAngleOff", icon:"st.tesla.tesla-locked", backgroundColor:"#cad2b5", nextState:"turningOff"
            state "turningOff", label:'turningOff', action:"setAngleOn", icon:"st.motion.motion.inactive", backgroundColor:"#b2cc68", nextState:"turningOn"
        }
        
        valueTile("angle_label", "", decoration: "flat", width: 4, height: 1) {
            state "default", label:'Control Angle'
        }
        valueTile("head_label", "", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Direction'
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
        standardTile("headl", "device.setdirection") {
			state "default", label: "Left", action: "setMoveLeft", icon:"st.thermostat.thermostat-left", backgroundColor:"#bda1b0"
		}
        standardTile("headr", "device.setdirection") {
			state "default", label: "Right", action: "setMoveRight", icon:"st.thermostat.thermostat-right", backgroundColor:"#bda1b0"
		}
        standardTile("mode", "device.fanmode") {
            state "general", label:'general', action:"naturalOn", icon:"st.Appliances.appliances11", backgroundColor:"#73C1EC", nextState:"natural"
            state "natural", label:'natural', action:"generalOn", icon:"st.Outdoor.outdoor19", backgroundColor:"#6eca8f", nextState:"general"
             
            state "change", label:'....', action:"naturalOn", backgroundColor:"#d6c6c9"
        }

        valueTile("led_label", "", decoration: "flat") {
            state "default", label:'LED'
        }
        valueTile("buzzer_label", "", decoration: "flat") {
            state "default", label:'Buzzer'
        }        
        valueTile("timer_label", "", decoration: "flat", width: 1, height: 2) {
            state "default", label:'Set Timer'
        }
        
        standardTile("buzzer", "device.buzzer") {
            state "on", label:'Sound', action:"buzzerOff", icon: "st.custom.sonos.unmuted", backgroundColor:"#73C1EC", nextState:"turningOff"
            state "off", label:'Mute', action:"buzzerOn", icon: "st.custom.sonos.muted", backgroundColor:"#d1cdd2", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"buzzerOff", icon: "st.custom.sonos.muted", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"buzzerOn", icon: "st.custom.sonos.unmuted", backgroundColor:"#73C1EC", nextState:"turningOn"
        }
        
        standardTile("ledBrightness", "device.ledBrightness") {
            state "bright", label: 'Bright', action: "setBrightDim", icon: "st.illuminance.illuminance.bright", backgroundColor: "#ff93ac", nextState:"dim"
            state "dim", label: 'Dim', action: "setBrightOff", icon: "st.illuminance.illuminance.light", backgroundColor: "#ffc2cd", nextState:"off"
            state "off", label: 'Off', action: "setBright", icon: "st.illuminance.illuminance.dark", backgroundColor: "#d6c6c9", nextState:"bright"
        } 
        standardTile("tiemr0", "device.settimer") {
			state "default", label: "OFF", action: "settimeroff", icon:"st.Health & Wellness.health7", backgroundColor:"#c3d6d4"
		}
        standardTile("tiemr1", "device.settimer") {
			state "default", label: "15", action: "settimer15", icon:"st.Health & Wellness.health7", backgroundColor:"#d5eeec"
			state "t15", label: "15", action: "settimer15", icon:"st.Health & Wellness.health7", backgroundColor:"#db5764"
		}
        standardTile("tiemr2", "device.settimer") {
			state "default", label: "30", action: "settimer30", icon:"st.Health & Wellness.health7", backgroundColor:"#b9e4df"
			state "t30", label: "30", action: "settimer30", icon:"st.Health & Wellness.health7", backgroundColor:"#db5764"
		}
        standardTile("tiemr3", "device.settimer") {
			state "default", label: "60", action: "settimer60", icon:"st.Health & Wellness.health7", backgroundColor:"#abded9"
			state "t60", label: "60", action: "settimer60", icon:"st.Health & Wellness.health7", backgroundColor:"#db5764"
		}
        standardTile("tiemr4", "device.settimer") {
			state "default", label: "90", action: "settimer90", icon:"st.Health & Wellness.health7", backgroundColor:"#9dd9d2"
			state "t90", label: "90", action: "settimer90", icon:"st.Health & Wellness.health7", backgroundColor:"#db5764"
		}
        standardTile("tiemr5", "device.settimer") {
			state "default", label: "120", action: "settimer120", icon:"st.Health & Wellness.health7", backgroundColor:"#8fd3cc"
			state "t120", label: "120", action: "settimer120", icon:"st.Health & Wellness.health7", backgroundColor:"#db5764"
		}

   	main (["switch2"])
	details(["switch", "mode_label", "rotation_label", "speed_label", 
    "mode", "angle", "speed1", "speed2", "speed3", "speed4", 
    "head_label", "angle_label",  
     "headl", "headr", "angle1", "angle2", "angle3", "angle4",
    "buzzer_label", "led_label", "timer_label", "tiemr0", "tiemr1", "tiemr2", 
    "buzzer", "ledBrightness", "tiemr3", "tiemr4", "tiemr5"
    ])

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
    def now = new Date().format("HH:mm:ss", location.timeZone)
	def currenttemp = device.currentState('temperature')?.value
	def currenthumi = device.currentState('humidity')?.value
	def currentangle = device.currentState('anglelevel')?.value
    
 	switch(params.key){
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data + "%")
    	sendEvent(name:"lastCheckin", value: " 온도: " + currenttemp + "° 습도: " + params.data + " 회전: " + currentangle + "° (" + now + ")")
    	break;
    case "angleLevel":
        sendEvent(name:"anglelevel", value: params.data)
    	sendEvent(name:"lastCheckin", value: " 온도: " + currenttemp + "° 습도: " + currenthumi + " 회전: " + params.data + "° (" + now + ")")
    	break;        
    case "speedLevel":
        sendEvent(name:"fanSpeed", value: params.data)
		def para = params.data
		String data = para
		def stf = Float.parseFloat(data)
		def tem = Math.round((stf+12)/25)        
        sendEvent(name:"speedlevel", value: tem)
    	break;        
    case "naturalLevel":
		def para = params.data
        if(para == "0"){
        sendEvent(name:"fanmode", value: "general")
        }
        else {
		String data = para
		def stf = Float.parseFloat(data)
		def tem = Math.round((stf+12)/25)        
        sendEvent(name:"speedlevel", value: tem)        
        sendEvent(name:"fanmode", value: "natural")
        sendEvent(name:"fanSpeed", value: para)
        }
    	break;        
    case "angleEnable":
        sendEvent(name:"setangle", value: params.data)
    	break;        
        
    case "fanNatural":
        sendEvent(name:"fanSpeed", value: params.data)
    	break;        
    case "power":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "temperature":
		def para = "${params.data}"
		String data = para
		def st = data.replace("C","");
		def stf = Float.parseFloat(st)
		def tem = Math.round(stf)
        sendEvent(name:"temperature", value: tem)
    	sendEvent(name:"lastCheckin", value: " 온도:" + tem + "° 습도:" + currenthumi + " 회전:" + currentangle + "° (" + now + ")")        
    	break;
    }
}

def settimeroff() { 
	unschedule()
    sendEvent(name:"settimer", value: "default")

}
def settimer15() { 
    sendEvent(name:"settimer", value: "t15")
	unschedule()
	runIn(15*60, off)
}
def settimer30() { 
    sendEvent(name:"settimer", value: "t30")
	unschedule()
	runIn(30*60, off)
}
def settimer60() { 
    sendEvent(name:"settimer", value: "t60")
	unschedule()
	runIn(60*60, off)
}
def settimer90() { 
    sendEvent(name:"settimer", value: "t90")
	unschedule()
	runIn(90*60, off)
}
def settimer120() { 
    sendEvent(name:"settimer", value: "t120")
	unschedule()
	runIn(120*60, off)
}

def setFanSpeed(speed){
	log.debug "setFanSpeed >> ${state.id}"
	def currentState = device.currentValue("fanmode")    
    if(currentState =="natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": speed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
    else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": speed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
}    
def tempUp(){
	def currentSpeed = device.currentState("speedlevel")?.value
    if(currentSpeed == "1"){
    setFanSpeed2()
    } else if (currentSpeed == "2"){
    setFanSpeed3()
    } else if (currentSpeed == "3"){
    setFanSpeed4()
    } else {}
}

def tempDown(){
	def currentSpeed = device.currentState("speedlevel")?.value
    if(currentSpeed == "2"){
    setFanSpeed1()
    } else if (currentSpeed == "3"){
    setFanSpeed2()
    } else if (currentSpeed == "4"){
    setFanSpeed3()
    } else {}
}

def setFanSpeed1(){
	log.debug "setFanstep1 >> ${state.id}"
	def currentState = device.currentValue("fanmode")    
    if(currentState =="natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": 25
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
    else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": 25
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
}

def setFanSpeed2(){
	log.debug "setFanstep2 >> ${state.id}"
	def currentState = device.currentValue("fanmode")    
    if(currentState =="natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": 50
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
    else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": 50
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
}

def setFanSpeed3(){
	log.debug "setFanstep3 >> ${state.id}"
	def currentState = device.currentValue("fanmode")    
    if(currentState =="natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": 75
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
    else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": 75
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
}

def setFanSpeed4(){
	log.debug "setFanstep4 >> ${state.id}"
	def currentState = device.currentValue("fanmode")    
    if(currentState =="natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": 100
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
    else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": 100
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
}

def generalOn(){
	def ab = device.currentValue("fanSpeed")
	log.debug "generalOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanSpeed",
        "data": ab
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def naturalOn(){
	def ab = device.currentValue("fanSpeed")
	log.debug "naturalOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanNatural",
        "data": ab
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}


def setAngle30(){
	log.debug "setAngle30 >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "angleLevel",
        "data": 30
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setAngle60(){
	log.debug "setAngle60 >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "angleLevel",
        "data": 60
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setAngle90(){
	log.debug "setAngle90 >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "angleLevel",
        "data": 90
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setAngle120(){
	log.debug "setAngle120 >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "angleLevel",
        "data": 120
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setAngleOn(){
	log.debug "setAngleOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "angle",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setAngleOff(){
	log.debug "setAngleOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "angle",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMoveLeft(){
	log.debug "setMoveLeft >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "moveDirection",
        "data": "left"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMoveRight(){
	log.debug "setMoveRight >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "moveDirection",
        "data": "right"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBright(){
	log.debug "setBright >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "bright"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBrightDim(){
	log.debug "setDim >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "dim"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBrightOff(){
	log.debug "setLedOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def buzzerOn(){
	log.debug "buzzerOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def buzzerOff(){
	log.debug "buzzerOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	log.debug "On >> ${state.id}"
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
