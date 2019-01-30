/**
 *  Xiaomi Humidifier(v.0.0.1)
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


metadata {
	definition (name: "Xiaomi Humidifier 3", namespace: "fison67", author: "fison67", ocfDeviceType: "oic.d.airpurifier") {
        capability "Switch"						//"on", "off"
        capability "Switch Level"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Refresh"
		capability "Sensor"

         
        attribute "mode", "enum", ["M1", "M2", "M3", "M4", "M5"]
        attribute "buzzer", "enum", ["on", "off"]
        attribute "ledBrightness", "enum", ["off", "dim", "bright"]
        attribute "water2", "enum", ["on", "off"]
        attribute "water", "number"
        attribute "childlock", "enum", ["on", "off"]        
        attribute "lastCheckin", "Date"

//------For Homebridge-----------
        command "humidifier3"
        command "noTemp"
        command "noHumi"
//-------------------------------        
        command "setMode1"
        command "setMode2"
        command "setMode3"
        command "setMode4"
        command "setMode5"
        
        command "buzzerOn"
        command "buzzerOff"
        command "childLockOn"
        command "childLockOff"
        
        command "setBright"
        command "setBrightDim"
        command "setBrightOff"
	}


	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"mode", type: "generic", width: 6, height: 4){
			tileAttribute ("device.mode", key: "PRIMARY_CONTROL") {
                attributeState "off", label:'\nOFF', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ2/MDAxNTIyMTUxNzIxMTk3.xeCR1k4pk0vDOozb43Lfo6g2fMC1a_VJFUpTQ071XRUg.dyhFTAUaCwWPUYc4hPUdGiuUI5yeRJ4QpP3kX802AlIg.PNG.shin4299/Humi_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "M1", label:'\nAuto Mode', action:"setMode2", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ1/MDAxNTIyMTUxNzIxMTk5.LTiuV1QSyPu6WgMB3uR7Bc-Hy19Uwgard5XKG5jj1JIg.XpdiwfmUg3Rz6IgIWyamtsrYeW0BJRqj28XyHRuADA0g.PNG.shin4299/Humi_tile_auto.png?type=w580", backgroundColor:"#73C1EC", nextState:"modechange"
                attributeState "M2", label:'\nlevel 1', action:"setMode3", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTE2/MDAxNTIyMTUxNzIxMTE3.wVp36he9l0up0SalfSgNMOff9y_U9F2wyPc5AfmK-nEg.coHcd4mj2byTBFzTWnc4yjKi7xbJb7QhfgBn9ASt5eUg.PNG.shin4299/Humi_tile_1.png?type=w580", backgroundColor:"#6eca8f", nextState:"modechange"
                attributeState "M3", label:'\nlevel 2', action:"setMode4", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjEz/MDAxNTIyMTUxNzIxMTk4.VfHSHeU2sS9J-T03zqc_sSjgO4ifOxiyBtGorUPxD2kg.dnC3xCu45F_153OJfUm0Pd1_HAWFp9DWVGHLagDqOSgg.PNG.shin4299/Humi_tile_2.png?type=w580", backgroundColor:"#FFDE61", nextState:"modechange"
                attributeState "M4", label:'\nlevel 3', action:"setMode5", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTA5/MDAxNTIyMTUxNzIxMTk3.r9waU9A5WmDgRz6p6eiGYTl67F1jo5HGcurD9i57Mj0g.a1R4bIefNK0gT-NdDFmYveohdkXxUuRgJIszH9Q38Ogg.PNG.shin4299/Humi_tile_3.png?type=w580", backgroundColor:"#f7ae0e", nextState:"modechange"
                attributeState "M5", label:'\nlevel 4', action:"setMode1", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTA5/MDAxNTIyMTUxNzIxMTk3.r9waU9A5WmDgRz6p6eiGYTl67F1jo5HGcurD9i57Mj0g.a1R4bIefNK0gT-NdDFmYveohdkXxUuRgJIszH9Q38Ogg.PNG.shin4299/Humi_tile_3.png?type=w580", backgroundColor:"#ff9eb2", nextState:"modechange"
                
                attributeState "modechange", label:'\n${name}', icon:"st.quirky.spotter.quirky-spotter-motion", backgroundColor:"#C4BBB5"
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ2/MDAxNTIyMTUxNzIxMTk3.xeCR1k4pk0vDOozb43Lfo6g2fMC1a_VJFUpTQ071XRUg.dyhFTAUaCwWPUYc4hPUdGiuUI5yeRJ4QpP3kX802AlIg.PNG.shin4299/Humi_tile_off.png?type=w580", backgroundColor:"#C4BBB5", nextState:"off"
			}
			tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
        		attributeState("humidity", label:'${currentValue}%', unit:"%", defaultState: true)
    		}            
			tileAttribute("device.temperature2", key: "SECONDARY_CONTROL") {
				attributeState("temperature2", label:'                ${currentValue}°', unit:"°", defaultState: true)
    		}            
		    tileAttribute ("device.level", key: "SLIDER_CONTROL") {
        		attributeState "level", action:"switch level.setLevel"
		    }
//            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
//    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
//            }
		}
        
		multiAttributeTile(name:"modem", type: "generic", width: 6, height: 4){
			tileAttribute ("device.mode", key: "PRIMARY_CONTROL") {
                attributeState "off", label:'OFF', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ2/MDAxNTIyMTUxNzIxMTk3.xeCR1k4pk0vDOozb43Lfo6g2fMC1a_VJFUpTQ071XRUg.dyhFTAUaCwWPUYc4hPUdGiuUI5yeRJ4QpP3kX802AlIg.PNG.shin4299/Humi_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "M1", label:'Auto Mode', action:"setMode2", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ1/MDAxNTIyMTUxNzIxMTk5.LTiuV1QSyPu6WgMB3uR7Bc-Hy19Uwgard5XKG5jj1JIg.XpdiwfmUg3Rz6IgIWyamtsrYeW0BJRqj28XyHRuADA0g.PNG.shin4299/Humi_tile_auto.png?type=w580", backgroundColor:"#73C1EC", nextState:"modechange"
                attributeState "M2", label:'level 1', action:"setMode3", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTE2/MDAxNTIyMTUxNzIxMTE3.wVp36he9l0up0SalfSgNMOff9y_U9F2wyPc5AfmK-nEg.coHcd4mj2byTBFzTWnc4yjKi7xbJb7QhfgBn9ASt5eUg.PNG.shin4299/Humi_tile_1.png?type=w580", backgroundColor:"#6eca8f", nextState:"modechange"
                attributeState "M3", label:'level 2', action:"setMode4", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjEz/MDAxNTIyMTUxNzIxMTk4.VfHSHeU2sS9J-T03zqc_sSjgO4ifOxiyBtGorUPxD2kg.dnC3xCu45F_153OJfUm0Pd1_HAWFp9DWVGHLagDqOSgg.PNG.shin4299/Humi_tile_2.png?type=w580", backgroundColor:"#FFDE61", nextState:"modechange"
                attributeState "M4", label:'level 3', action:"setMode5", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTA5/MDAxNTIyMTUxNzIxMTk3.r9waU9A5WmDgRz6p6eiGYTl67F1jo5HGcurD9i57Mj0g.a1R4bIefNK0gT-NdDFmYveohdkXxUuRgJIszH9Q38Ogg.PNG.shin4299/Humi_tile_3.png?type=w580", backgroundColor:"#f7ae0e", nextState:"modechange"
                attributeState "M5", label:'level 4', action:"setMode1", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTA5/MDAxNTIyMTUxNzIxMTk3.r9waU9A5WmDgRz6p6eiGYTl67F1jo5HGcurD9i57Mj0g.a1R4bIefNK0gT-NdDFmYveohdkXxUuRgJIszH9Q38Ogg.PNG.shin4299/Humi_tile_3.png?type=w580", backgroundColor:"#ff9eb2", nextState:"modechange"
                
                attributeState "modechange", label:'\n${name}', icon:"st.quirky.spotter.quirky-spotter-motion", backgroundColor:"#C4BBB5"
			}
		}
        
        
        standardTile("switch", "device.switch", inactiveLabel: false, width: 2, height: 2) {
            state "on", label:'ON', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "off", label:'OFF', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
             
        	state "turningOn", label:'turningOn', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "turningOff", label:'turningOff', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        valueTile("auto_label", "", decoration: "flat") {
            state "default", label:'Auto'
        }
        valueTile("level1_label", "", decoration: "flat") {
            state "default", label:'Level 1'
        }
        valueTile("level2_label", "", decoration: "flat") {
            state "default", label:'Level 2'
        }
        valueTile("level3_label", "", decoration: "flat") {
            state "default", label:'Level 3'
        }
        valueTile("level4_label", "", decoration: "flat") {
            state "default", label:'Level 4'
        }
        standardTile("mode1", "device.mode") {
			state "default", label: "Auto", action: "setMode1", icon:"st.unknown.zwave.static-controller", backgroundColor:"#73C1EC"
		}
        standardTile("mode2", "device.mode") {
			state "default", label: "Level1", action: "setMode2", icon:"st.quirky.spotter.quirky-spotter-luminance-dark", backgroundColor:"#6eca8f"
		}
        standardTile("mode3", "device.mode") {
			state "default", label: "Level2", action: "setMode3", icon:"st.quirky.spotter.quirky-spotter-luminance-light", backgroundColor:"#FFDE61"
		}
        standardTile("mode4", "device.mode") {
			state "default", label: "Level3", action: "setMode4", icon:"st.quirky.spotter.quirky-spotter-luminance-bright", backgroundColor:"#f7ae0e"
		}
        standardTile("mode5", "device.mode") {
			state "default", label: "Level4", action: "setMode5", icon:"st.quirky.spotter.quirky-spotter-luminance-bright", backgroundColor:"#ff9eb2"
		}
        
        valueTile("buzzer_label", "device.buzzer_label", decoration: "flat") {
            state "default", label: 'Buzzer'
        }        
        valueTile("led_label", "", decoration: "flat") {
            state "default", label:'LED'
        }        
        valueTile("refresh_label", "device.refresh_label", decoration: "flat") {
            state "default", label: 'Refresh'
        }        
        valueTile("lock_label", "device.dry_label", decoration: "flat") {
            state "default", label: 'Child Lock'
        }        
        valueTile("water_label", "", decoration: "flat") {
            state "default", label:'No Water \n Warning'
        }        
        standardTile("buzzer", "device.buzzer") {
            state "on", label:'Sound', action:"buzzerOff", icon: "st.custom.sonos.unmuted", backgroundColor:"#BAA7BC", nextState:"turningOff"
            state "off", label:'Mute', action:"buzzerOn", icon: "st.custom.sonos.muted", backgroundColor:"#d1cdd2", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"buzzerOff", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"buzzerOn", backgroundColor:"#BAA7BC", nextState:"turningOn"
        }
        standardTile("ledBrightness", "device.ledBrightness") {
            state "2", label: 'Bright', action: "setBrightDim", icon: "st.illuminance.illuminance.bright", backgroundColor: "#ff93ac", nextState:"dim"
            state "1", label: 'Dim', action: "setBrightOff", icon: "st.illuminance.illuminance.light", backgroundColor: "#ffc2cd", nextState:"off"
            state "0", label: 'Off', action: "setBright", icon: "st.illuminance.illuminance.dark", backgroundColor: "#d6c6c9", nextState:"bright"            
        }         
        standardTile("childlock", "device.childlock") {
            state "on", label: 'ON', action: "childLockOff", icon: "st.presence.house.secured",  backgroundColor: "#FFD16C", nextState:"off"
            state "off", label: 'OFF', action: "childLockOn", icon: "st.presence.house.unlocked", backgroundColor: "#c1baaa", nextState:"on"
        }
        standardTile("water2", "device.water2") {
            state "on", label: 'WATER', action: "noact", icon: "st.valves.water.open",  backgroundColor: "#73C1EC"
            state "off", label: 'NO WATER', action: "noact", icon: "st.valves.water.closed", backgroundColor: "#ff4732"
        }
        valueTile("checkin", "device.lastCheckin", width: 3, height: 1) {
            state("default", label:'${currentValue}', defaultState: true
        	)
        }
        valueTile("refresh", "device.refresh", decoration: "flat") {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }        
		
   	main (["modem"])
	details(["mode", "switch", "buzzer_label", "led_label", "lock_label", "water_label",
    			"buzzer", "ledBrightness", "childlock",  "water2",
    			"auto_label", "level1_label", "level2_label", "level3_label", "level4_label",  "refresh_label",
                "mode1", "mode2", "mode3", "mode4", "mode5", "refresh", 
                "checkin"
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
 
 	switch(params.key){
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data )
    	break;
    case "mode":
	state.mode = params.data
    def level = params.data as int
    	sendEvent(name:"mode", value: "M"+params.data)
    	sendEvent(name:"level", value: level*20)        
    	break;
    case "power":
    	if(params.data == "1") {
    	sendEvent(name:"switch", value:"on")
    	sendEvent(name:"mode", value:"M"+state.mode)		
        }
        else if(params.data == "0") {
    		sendEvent(name:"mode", value: "off")
	    	sendEvent(name:"switch", value:"off")
        }
    	break;
    case "temperature":
		def para = "${params.data}"
		String data = para
		def st = data.replace("C","");
		def stf = Float.parseFloat(st)
		def tem = Math.round(stf*10)/10
        sendEvent(name:"temperature", value: tem )
        sendEvent(name:"temperature2", value: "Temp: " + tem )
    	break;
    case "ledBrightness":
        sendEvent(name:"ledBrightness", value: params.data)
    	break;        
    case "targetHumidity":
        sendEvent(name:"level", value: params.data)
    	break;
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "1" ? "on" : "off") )
        break;
    case "childLock":
    	sendEvent(name:"childlock", value: (params.data == "1" ? "on" : "off") )
        break;
    case "water":
    	sendEvent(name:"water2", value: (params.data == "1" ? "on" : "off") )
    	sendEvent(name:"water", value: (params.data == "1" ? 100 : 0) )
        break;
    }
    
    def nowT = new Date().format("HH:mm:ss", location.timeZone)
    def nowD = new Date().format("yyyy-MM-dd", location.timeZone)
    sendEvent(name: "lastCheckin", value: nowD + "\n" + nowT)
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

def setMode1(){
	log.debug "setLevel 1>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 1
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMode2(){
	log.debug "setLevel 2>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 2
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMode3(){
	log.debug "setLevel 3>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 3
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMode4(){
	log.debug "setLevel 4>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 4
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setMode5(){
	log.debug "setLevel 5>> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": 5
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setLevel(level){
	log.debug "setLevel >> ${state.id}"
	def set = Math.ceil(level/20) as int
	log.debug "Math.ceil >> ${set}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": set
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBright(){
	log.debug "setBright >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "off"
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
	log.debug "setBrightOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "bright"
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
	log.debug "ON >> ${state.id}"
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

def childLockOn(){
	log.debug "childLockOn >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "childLock",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def childLockOff(){
	log.debug "childLockOff >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "childLock",
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
        if(jsonObj.state.power == 1){
       	sendEvent(name:"switch", value:"on")
    	sendEvent(name:"mode", value:"M"+ jsonObj.state.mode)
        } else {
       	sendEvent(name:"switch", value:"off")
    	sendEvent(name:"mode", value:"off")        
        state.mode = jsonObj.state.mode
        }
        sendEvent(name:"ledBrightness", value: jsonObj.state.ledBrightness)
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer == 1 ? "on" : "off"))
        sendEvent(name:"childlock", value: (jsonObj.state.childLock == 1 ? "on" : "off"))
        sendEvent(name:"water2", value: (jsonObj.state.water == 1 ? "on" : "off"))
        sendEvent(name:"water", value: (jsonObj.state.water == 1 ? 100 : 0))
	    
    def nowT = new Date().format("HH:mm:ss", location.timeZone)
    def nowD = new Date().format("yyyy-MM-dd", location.timeZone)
    sendEvent(name: "lastCheckin", value: nowD + "\n" + nowT)
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
