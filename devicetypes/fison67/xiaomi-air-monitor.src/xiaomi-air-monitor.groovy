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
import groovy.transform.Field

@Field 
LANGUAGE_MAP = [
    "display": [
        "Korean": "화면\n모드",
        "English": "Display\nMode"
    ],
    "night": [
        "Korean": "취침\n모드",
        "English": "Night\nMode"
    ],
    "power": [
        "Korean": "전원",
        "English": "Power\nSource"
    ],
    "refresh": [
        "Korean": "새로\n고침",
        "English": "Refresh"
    ],
    "begin": [
        "Korean": "취침 모드\n시작 시간",
        "English": "Night Mode\nBegin Time"
    ],
    "end": [
        "Korean": "취침 모드\n종료 시간",
        "English": "Night Mode\nEnd Time"
    ],
    "setup": [
        "Korean": " 시간 \n 설정후 \n 누름",
        "English": " Tap \n After \n Time \n Set "
    ]
]

metadata {
	definition (name: "Xiaomi Air Monitor", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Battery"
		capability "Refresh"
		capability "Sensor"
		capability "Power Source"
		capability "Dust Sensor" // fineDustLevel : PM 2.5   dustLevel : PM 10

        attribute "clock", "enum", ["on", "off"]
        attribute "night", "enum", ["on", "off"]
        attribute "setbeap", "enum", ["am", "pm"]
        attribute "setendap", "enum", ["am", "pm"]
        
        attribute "lastCheckin", "Date"
     
        command "clockOn"
        command "clockOff"
        command "nightOn"
        command "nightOff"
        command "setBeHour"
        command "setEndHour"
        command "setBeMin"
        command "setEndMin"
        command "setBePm"
        command "setBeAm"
        command "setEndPm"
        command "setEndAm"
        command "setUpTime"
        
	}


	simulator {
	}
	preferences {
	        input name: "selectedLang", title:"Select a language" , type: "enum", required: true, options: ["English", "Korean"], defaultValue: "English", description:"Language for DTH"
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
        
        valueTile("display_label", "device.display_label", decoration: "flat") {
            state "default", label:'${currentValue}'
        }        
        valueTile("night_label", "device.night_label", decoration: "flat") {
            state "default", label:'${currentValue}'
        }
        valueTile("power_label", "device.power_label", decoration: "flat") {
            state "default", label:'${currentValue}'
        }
        valueTile("refresh_label", "device.refresh_label", decoration: "flat") {
            state "default", label:'${currentValue}'
        }
        valueTile("timeset_label", "device.timeset_label", decoration: "flat", width: 1, height: 2) {
            state "default", label:'${currentValue}', action:"setUpTime"
        }
        
		standardTile("clock", "device.clock") {
            state "on", label:'Clock', action:"clockOff", icon:"st.Office.office6", backgroundColor:"#ff9eb2", nextState:"off"
            state "off", label:'PM2.5', action:"clockOn", icon:"st.unknown.zwave.static-controller", backgroundColor:"#73C1EC", nextState:"on"
        }
        standardTile("night", "device.night") {
            state "on", label:'Night', action:"nightOff", icon: "st.Weather.weather4", backgroundColor:"#7a88bc", nextState:"off"
            state "off", label:'Day', action:"nightOn", icon: "st.Weather.weather14", backgroundColor:"#f9b959", nextState:"on"
        }
        
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state("val", label:'${currentValue}%', defaultState: true, backgroundColor:"#00a0dc")
        }
        
        standardTile("powerSource", "device.powerSource") {
            state "dc", label:'USB', icon:"st.quirky.spotter.quirky-spotter-plugged", backgroundColor:"#96CEB4"
            state "battery", label:'Battery', icon:"https://www.shareicon.net/data/128x128/2015/03/06/3189_battery_32x32.png", backgroundColor:"#abaf9e"
        }
        
        standardTile("refresh", "device.refresh") {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh", backgroundColor:"#A7ADBA"
        }
        valueTile("setbe_label", "device.setbe_label", decoration: "flat", width: 2, height: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("setend_label", "device.setend_label", decoration: "flat", width: 2, height: 1) {
            state "default", label:'${currentValue}'
        }

        standardTile("setbeap", "device.setbeap", decoration: "flat") {
            state "am", label:"am", action:"setBePm", icon:"st.Weather.weather14", backgroundColor:"#edd884", nextState:"am.."
            state "pm", label:"pm", action:"setBeAm", icon:"st.Weather.weather4", backgroundColor:"#7a88bc", nextState:"pm.."

            state "am..", label:"..", action:"setBePm", icon:"st.Weather.weather14", backgroundColor:"#edd884", nextState:"pm.."
            state "pm..", label:"..", action:"setBeAm", icon:"st.Weather.weather4", backgroundColor:"#7a88bc", nextState:"am.."
        }
        standardTile("setendap", "device.setendap", decoration: "flat") {
            state "am", label:"am", action:"setEndPm", icon:"st.Weather.weather14", backgroundColor:"#edd884", nextState:"am.."
            state "pm", label:"pm", action:"setEndAm", icon:"st.Weather.weather4", backgroundColor:"#7a88bc", nextState:"pm.."

            state "am..", label:"..", action:"setEndPm", icon:"st.Weather.weather14", backgroundColor:"#edd884", nextState:"pm.."
            state "pm..", label:"..", action:"setEndPm", icon:"st.Weather.weather4", backgroundColor:"#7a88bc", nextState:"am.."
        }
        
        valueTile("setbe_hour", "device.setbe_hour", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue}h', action:"setBeHour"
            }
        valueTile("setend_hour", "device.setend_hour", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue}h', action:"setEndHour"
        }
        valueTile("setbe_min", "device.setbe_min", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue}min', action:"setBeMin"
        }
        valueTile("setend_min", "device.setend_min", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue}min', action:"setEndMin"
        }
        standardTile("setup_be", "device.setup_be", inactiveLabel: false, decoration: "flat") {
            state "default", label:"Set\nUp", action:"setupBe", icon:"st.secondary.refresh"
        }
        standardTile("setup_end", "device.setup_end", inactiveLabel: false, decoration: "flat") {
            state "default", label:"Set\nUp", action:"setupEnd", icon:"st.secondary.refresh"
        }
        
        main (["pm25"])
		details(["fineDustLevel", "switch", "display_label", "night_label", "power_label", "refresh_label", 
    		"clock", "night", "powerSource", "refresh",
            "setbe_label", "setbe_hour", "setbe_min", "setbeap", "timeset_label", 
            "setend_label", "setend_hour", "setend_min", "setendap"])
		
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
    case "nightState":
    	sendEvent(name:"night", value: (params.data == "on" ? "on" : "off"))
    	break;
    case "timeState":
    	sendEvent(name:"clock", value: (params.data == "on" ? "on" : "off"))
    	break;
    case "nightBeginTime":
    	break;
    case "nightEndTime":
    	break;
    case "sensorState":
    	sendEvent(name:"sensor", value: (params.data == "on" ? "on" : "off"))
    	break;
    }
    
    updateLastTime()
}
def setBeHour() {
	if(device.currentValue('setbe_hour') == null){
    sendEvent(name: "setbe_hour", value: 1 )
    } else {
	def h = device.currentValue('setbe_hour')
	int be = Float.parseFloat(h)    
	state.setBeHour = be
	if(state.setBeHour >= 12) {
    state.setBeHour = 1
    } else {
	state.setBeHour = state.setBeHour + 1
    }
	log.debug "setbe_hour '${state.setBeHour}'"    
    sendEvent(name: "setbe_hour", value: state.setBeHour)
    }
}    
    
def setEndHour() {
	if(device.currentValue('setend_hour') == null){
    sendEvent(name: "setend_hour", value: 1 )
    } else {
	def h = device.currentValue('setend_hour')
	int be = Float.parseFloat(h)    
	state.setEndHour = be
	if(state.setEndHour >= 12) {
    state.setEndHour = 1
    } else {
	state.setEndHour = state.setEndHour + 1
    }
	log.debug "setend_hour '${state.setEndHour}'"    
    sendEvent(name: "setend_hour", value: state.setEndHour)
    }
}    
        
def setBeMin() {
	if(device.currentValue('setbe_min') == null){
    sendEvent(name: "setbe_min", value: 10 )
    } else {
	def h = device.currentValue('setbe_min')
	int be = Float.parseFloat(h)    
	state.setBeMin = be
	if(state.setBeMin >= 50) {
    state.setBeMin = 0
    } else {
	state.setBeMin = state.setBeMin + 10
    }
	log.debug "setBeMin '${state.setBeMin}'"    
    sendEvent(name: "setbe_min", value: state.setBeMin)
    }
}

def setEndMin() {
	if(device.currentValue('setend_min') == null){
    sendEvent(name: "setend_min", value: 10 )
    } else {
	def h = device.currentValue('setend_min')
	int be = Float.parseFloat(h)    
	state.setEndMin = be
	if(state.setEndMin >= 50) {
    state.setEndMin = 0
    } else {
	state.setEndMin = state.setEndMin + 10
    }
	log.debug "setEndMin '${state.setEndMin}'"    
    sendEvent(name: "setend_min", value: state.setEndMin)
    }
}

def setUpTime(){
	log.debug "setUpTime >> ${state.id}"
	if(device.currentValue('setbeap') == 'am'){
    		if(device.currentValue('setbe_hour') == '12'){
        	state.beHour = 23
            } else { state.beHour = Integer.parseInt(device.currentValue('setbe_hour')) - 1
            }
     } else {
     		if(device.currentValue('setbe_hour') == '12'){
        	state.beHour = 11
            } else { state.beHour = Integer.parseInt(device.currentValue('setbe_hour')) + 11
            }
     }
    if(device.currentValue('setendap') == 'am'){
    		if(device.currentValue('setend_hour') == '12'){
        	state.endHour = 23
            } else { state.endHour = Integer.parseInt(device.currentValue('setend_hour')) - 1
            }
     } else {
     		if(device.currentValue('setend_hour') == '12'){
        	state.endHour = 11
            } else { state.endHour = Integer.parseInt(device.currentValue('setend_hour')) + 11
            }
     }
    def beHour = state.beHour
    def endHour = state.endHour
    def beMin = Integer.parseInt(device.currentValue('setbe_min'))
    def endMin = Integer.parseInt(device.currentValue('setend_min'))
    
	log.debug "setUpTime >> Begin ${beHour}h ${beMin}min End ${endHour}h ${endMin}min"
    log.debug "setUpTime >> Begin ${device.currentValue('setbeap')}h ${device.currentValue('setbe_hour')}min End ${endHour}h ${endMin}min"
    def body = [
        "id": state.id,
        "cmd": "nightTime",
        "data": [ 
				"beginHour": beHour, 
				"beginMinute": beMin, 
				"endHour": endHour, 
				"endMinute": endMin
				]
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
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
def clockOn(){
	log.debug "Clock Mode On >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "timeState",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}
def clockOff(){
	log.debug "Clock Mode Off >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "timeState",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}
def nightOn(){
	log.debug "Night Mode On >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "nightState",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}
def nightOff(){
	log.debug "Night Mode Off >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "nightState",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBeAm() {
	sendEvent(name:"setbeap", value: "am")
    log.debug "setBeginAP >> am"
}

def setBePm() {
	sendEvent(name:"setbeap", value: "pm" )
    log.debug "setBeginAP >> pm"
}

def setEndAm() {
	sendEvent(name:"setendap", value: "am")
    log.debug "setEndAP >> am"
}

def setEndPm() {
	sendEvent(name:"setendap", value: "pm")
    log.debug "setEndAP >> pm"
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
		state.BeginTime = jsonObj.state.nightBeginTime
		state.EndTime = jsonObj.state.nightEndTime
		sendEvent(name:"switch", value: (jsonObj.state.power == true ? "on" : "off") )
		sendEvent(name:"fineDustLevel", value: jsonObj.state.aqi )
		sendEvent(name:"powerSource", value: (jsonObj.state.charging == true ? "dc" : "battery") )
		sendEvent(name:"battery", value: jsonObj.state.batteryLevel )
		sendEvent(name:"powerSource", value: (jsonObj.state.charging == true ? "dc" : "battery") )
		sendEvent(name:"clock", value: jsonObj.state.timeState )
		sendEvent(name:"night", value: jsonObj.state.nightState )
		updateLastTime()
        beginTime()
        endTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def beginTime() {
    def minutes = ((state.BeginTime/60).intValue() % 60).intValue()
    def hours = ((state.BeginTime/(60*60)).intValue() %24).intValue()
    log.debug "refresh begin time '${hours}'/'${minutes}'"    
	if(hours < 11) {
		sendEvent(name:"setbeap", value: "am" )
		sendEvent(name:"setbe_hour", value: hours + 1 )
		sendEvent(name:"setbe_min", value: minutes )
    } else if(hours == 11) {
		sendEvent(name:"setbeap", value: "pm" )
		sendEvent(name:"setbe_hour", value: hours + 1 )
		sendEvent(name:"setbe_min", value: minutes )
    } else if(hours == 23) {
		sendEvent(name:"setbeap", value: "am" )
		sendEvent(name:"setbe_hour", value: hours - 11 )
		sendEvent(name:"setbe_min", value: minutes )
    } else if(hours == 24) {
		sendEvent(name:"setbeap", value: "am" )
		sendEvent(name:"setbe_hour", value: 1 )
		sendEvent(name:"setbe_min", value: minutes )
    } else {
		sendEvent(name:"setbeap", value: "pm" )
		sendEvent(name:"setbe_hour", value: hours - 11 )
		sendEvent(name:"setbe_min", value: minutes )
    }
}
    
def endTime() {
    def minutes = ((state.EndTime/60).intValue() % 60).intValue()
    def hours = ((state.EndTime/(60*60)).intValue() %24).intValue()
    log.debug "refresh end time '${hours}'/'${minutes}'"    

	if(hours < 11) {
		sendEvent(name:"setendap", value: "am" )
		sendEvent(name:"setend_hour", value: hours + 1 )
		sendEvent(name:"setend_min", value: minutes )
    } else if(hours == 11) {
		sendEvent(name:"setendap", value: "pm" )
		sendEvent(name:"setend_hour", value: hours + 1 )
		sendEvent(name:"setend_min", value: minutes )
    } else if(hours == 23) {
		sendEvent(name:"setendap", value: "am" )
		sendEvent(name:"setend_hour", value: hours - 11 )
		sendEvent(name:"setend_min", value: minutes )
    } else if(hours == 24) {
		sendEvent(name:"setendap", value: "am" )
		sendEvent(name:"setend_hour", value: 1 )
		sendEvent(name:"setend_min", value: minutes )
    } else {
		sendEvent(name:"setendap", value: "pm" )
		sendEvent(name:"setend_hour", value: hours - 11 )
		sendEvent(name:"setend_min", value: minutes )
    }
}
        


def updated() {
    refresh()
    setLanguage(settings.selectedLang)
}

def setLanguage(language){
    log.debug "Languge >> ${language}"
	state.language = language
	
	sendEvent(name:"display_label", value: LANGUAGE_MAP["display"][language] )
	sendEvent(name:"night_label", value: LANGUAGE_MAP["night"][language] )
	sendEvent(name:"power_label", value: LANGUAGE_MAP["power"][language] )
	sendEvent(name:"refresh_label", value: LANGUAGE_MAP["refresh"][language] )
	sendEvent(name:"setbe_label", value: LANGUAGE_MAP["begin"][language] )
	sendEvent(name:"setend_label", value: LANGUAGE_MAP["end"][language] )
	sendEvent(name:"timeset_label", value: LANGUAGE_MAP["setup"][language] )
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
