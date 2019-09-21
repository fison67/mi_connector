/**
 *  Xiaomi Dehumidifier (v.0.0.2)
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
	definition (name: "Xiaomi Dehumidifier", namespace: "fison67", author: "fison67", ocfDeviceType: "oic.d.airpurifier") {
        capability "Switch"						
		capability "Sensor"
        capability "Switch Level"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Water Sensor"     //  ["dry", "wet"]
		capability "Refresh"

         
        attribute "mode", "enum", ["on", "auto", "dry_cloth"]
        attribute "buzzer", "enum", ["on", "off"]
        attribute "led", "enum", ["off", "on"]
        attribute "childlock", "enum", ["on", "off"]     
        attribute "lastCheckin", "Date"
      
        command "setModeOn"
        command "setModeAuto"
        command "setModeDryCloth"
        
        command "buzzerOn"
        command "buzzerOff"
        command "childLockOn"
        command "childLockOff"
        
        command "setLedOn"
        command "setLedOff"
	}


	simulator {
	}

	tiles(scale: 2) {
    	multiAttributeTile(name:"switch", type: "generic", width: 6, height: 2){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
              	attributeState "on", label:'${name}', action:"switch.off",  backgroundColor:"#00a0dc", nextState:"turningOff", icon:"https://github.com/fison67/LG-Connector/blob/master/icons/lg-dehumidifier-off.png?raw=true"
                attributeState "off", label:'${name}', action:"switch.on", backgroundColor:"#ffffff", nextState:"turningOn", icon:"https://github.com/fison67/LG-Connector/blob/master/icons/lg-dehumidifier-off.png?raw=true"
                
                attributeState "turningOn", label:'${name}', action:"switch.off", backgroundColor:"#00a0dc", nextState:"turningOff", icon:"httpshttps://github.com/fison67/LG-Connector/blob/master/icons/lg-dehumidifier-off.png?raw=true"
                attributeState "turningOff", label:'${name}', action:"switch.on", backgroundColor:"#ffffff", nextState:"turningOn", icon:"https://github.com/fison67/LG-Connector/blob/master/icons/lg-dehumidifier-off.png?raw=true"
			}
            tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
        		attributeState("humidity", label:'${currentValue}%', unit:"%", defaultState: true)
    		}            
			tileAttribute("device.temperature2", key: "SECONDARY_CONTROL") {
				attributeState("temperature2", label:'                ${currentValue}°', unit:"°", defaultState: true)
    		}            
		}
        valueTile("mode_label", "", decoration: "flat") {
            state "default", label:'Mode'
        }
        valueTile("led_label", "", decoration: "flat") {
            state "default", label:'Led'
        }
        valueTile("buzzer_label", "", decoration: "flat") {
            state "default", label:'Buzzer'
        }
        valueTile("childLock_label", "", decoration: "flat") {
            state "default", label:'Lock'
        }
        valueTile("water_label", "", decoration: "flat") {
            state "default", label:'Water'
        }
        valueTile("temp_label", "", decoration: "flat") {
            state "default", label:''
        }
        standardTile("modeOn", "device.modeOn") {
			state "default", label: "Auto", action: "setModeOn", icon:"st.unknown.zwave.static-controller", backgroundColor:"#73C1EC"
		}
        standardTile("modeAuto", "device.modeAuto") {
			state "default", label: "Level1", action: "setModeAuto", icon:"st.quirky.spotter.quirky-spotter-luminance-dark", backgroundColor:"#6eca8f"
		}
        standardTile("modeDryCloth", "device.modeDryCloth") {
			state "default", label: "Level2", action: "setModeDryCloth", icon:"st.quirky.spotter.quirky-spotter-luminance-light", backgroundColor:"#FFDE61"
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
        standardTile("mode", "device.mode") {
            state "on", label:'ON', action:"setModeAuto", backgroundColor:"#f7ae0e", nextState:"modechange"
            state "auto", label:'AUTO', action:"setModeDryCloth", backgroundColor:"#73C1EC", nextState:"modechange"
            state "dry_cloth", label:'Dry Cloth', action:"setModeOn", backgroundColor:"#6eca8f", nextState:"modechange"

            state "modechange", label:'${name}', icon:"st.quirky.spotter.quirky-spotter-motion", backgroundColor:"#C4BBB5"
        }
        standardTile("buzzer", "device.buzzer") {
            state "on", label:'Sound', action:"buzzerOff", icon: "st.custom.sonos.unmuted", backgroundColor:"#BAA7BC", nextState:"turningOff"
            state "off", label:'Mute', action:"buzzerOn", icon: "st.custom.sonos.muted", backgroundColor:"#d1cdd2", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"buzzerOff", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"buzzerOn", backgroundColor:"#BAA7BC", nextState:"turningOn"
        }
        standardTile("led", "device.led") {
            state "on", label: 'ON', action: "ledOff", icon: "st.illuminance.illuminance.light", backgroundColor: "#ffc2cd", nextState:"turningOff"
            state "off", label: 'OFF', action: "ledOn", icon: "st.illuminance.illuminance.dark", backgroundColor: "#d6c6c9", nextState:"turningOn"    
            
        	state "turningOn", label:'....', action:"ledOff", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"ledOn", backgroundColor:"#BAA7BC", nextState:"turningOn"       
        }         
        standardTile("childlock", "device.childlock") {
            state "on", label: 'ON', action: "childLockOff", icon: "st.presence.house.secured",  backgroundColor: "#FFD16C", nextState:"turningOff"
            state "off", label: 'OFF', action: "childLockOn", icon: "st.presence.house.unlocked", backgroundColor: "#c1baaa", nextState:"turningOn"
            
        	state "turningOn", label:'....', action:"childLockOff", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"childLockOn", backgroundColor:"#BAA7BC", nextState:"turningOn"   
        }
        standardTile("water", "device.water") {
            state "dry", label: 'DRY', icon: "st.valves.water.open",  backgroundColor: "#73C1EC"
            state "wet", label: 'WET', icon: "st.valves.water.closed", backgroundColor: "#ff4732"
        }
        controlTile("level", "device.level", "slider", height: 1, width: 1, inactiveLabel: false, range:"(40..60)") {
			state "level", action:"setLevel"
		}
        valueTile("checkin", "device.lastCheckin", width: 5, height: 1) {
            state("default", label:'${currentValue}', defaultState: true
        	)
        }
        valueTile("refresh", "device.refresh", decoration: "flat") {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }        
		
        main (["modem"])
        details(["switch", "mode_label", "led_label", "buzzer_label", "childLock_label", "water_label", "refresh_label",
                    "mode", "led", "buzzer", "childlock",  "water", "level", "checkin", "refresh"
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
    case "auto":
    // targethumidity
        sendEvent(name:"level", value: params.data as int)
    	break;
    case "mode":
		state.mode = params.data
    	sendEvent(name:"mode", value: params.data)
    	break;
    case "power":
        sendEvent(name:"switch", value: params.data)
    	break;
    case "temperature":
		def stf = Float.parseFloat(params.data.replace("C",""))
		def tem = Math.round(stf*10)/10
        sendEvent(name:"temperature", value: tem )
        sendEvent(name:"temperature2", value: "Temp: " + tem )
    	break;
    case "led":
        sendEvent(name:"led", value: params.data)
    	break;     
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "1" ? "on" : "off") )
        break;
    case "childLock":
    	sendEvent(name:"childlock", value: params.data )
        break;
    case "tankFull":
    	sendEvent(name:"water", value: params.data == "on" ? "wet" : "dry" )
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
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ]
    ]
    sendCommand(options, callback)
}

def setModeOn(){
	log.debug "setModeOn"
    def body = [
        "id": state.id,
        "cmd": "changeMode",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeAuto(){
	log.debug "setModeAuto"
    def body = [
        "id": state.id,
        "cmd": "changeMode",
        "data": "auto"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeDryCloth(){
	log.debug "setModeDryCloth"
    def body = [
        "id": state.id,
        "cmd": "changeMode",
        "data": "dry_cloth"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setLevel(level){
	log.debug "setLevel >> ${level}"
    def value = 40
    if(40 <= level && level < 50){
    	value = 40
    }else if(50 <= level && level < 60){
    	value = 50
    }else{
    	value = 60
    }
    
    def body = [
        "id": state.id,
        "cmd": "changeAuto",
        "data": value
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setLedOn(){
	log.debug "setLedOn"
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setLedOff(){
	log.debug "setLedOff"
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def buzzerOn(){
	log.debug "buzzerOn"
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def buzzerOff(){
	log.debug "buzzerOff"
    def body = [
        "id": state.id,
        "cmd": "buzzer",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	log.debug "on"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "off"
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
       
       	sendEvent(name:"switch", value:"on")
    	sendEvent(name:"mode", value: jsonObj.state.mode)
        sendEvent(name:"led", value: jsonObj.state.led)
	    
        sendEvent(name: "lastCheckin", value: new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone))
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
