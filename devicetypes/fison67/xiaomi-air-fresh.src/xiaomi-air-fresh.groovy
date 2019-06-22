/**
 *  Xiaomi Air Fresh(v.0.0.1)
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
import java.text.DateFormat

metadata {
	definition (name: "Xiaomi Air Fresh", namespace: "fison67", author: "fison67") {
        capability "Switch"						
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Carbon Dioxide Measurement"
        
		capability "Filter Status"
		capability "Air Quality Sensor"
		capability "Fan Speed"
		capability "Refresh"
		capability "Sensor"
		capability "Dust Sensor" // fineDustLevel : PM 2.5   dustLevel : PM 10
         
        attribute "switch", "string"
        attribute "buzzer", "enum", ["on", "off"]        
        attribute "ledBrightness", "enum", ["bright", "dim", "off"]        
        attribute "f1_hour_used", "number"
        attribute "filter1_life", "number"
        attribute "average_aqi", "number"
        attribute "mode", "enum", ["auto", "silent", "interval", "low", "middle", "strong"]        
        
        attribute "lastCheckin", "Date"
         
        command "setModeAuto"
        command "setModeSilent"
        command "setModeInterval"
        command "setModeLow"
        command "setModeMiddle"
        command "setModeStrong"
        
        command "buzzerOn"
        command "buzzerOff"
        
        command "ledOn"
        command "ledOff"
        
        command "setBright"
        command "setBrightDim"
        command "setBrightOff"
	}


	simulator {
	}
	preferences {
        
	}

	tiles {
		multiAttributeTile(name:"mode", type: "generic", width: 6, height: 4){
			tileAttribute ("device.mode", key: "PRIMARY_CONTROL") {
                attributeState "off", label:'\noff', action:"setModeAuto", icon:"http://blogfiles.naver.net/MjAxODAzMjdfMTk4/MDAxNTIyMTMyNzMxMjEz.BdXDvyyncHtsRwYxAHHWI4zCZaGxYkKAcCbrRYvRtEcg.HHz2i2rn7IdfCFJd-5heHMCllb0TJgXAq8dHtdM1beEg.PNG.shin4299/MiAirPurifier2S_off_tile.png?type=w1", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "auto", label:'\nauto', action:"setModeSilent", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#73C1EC", nextState:"modechange"
                attributeState "silent", label:'\nsilent', action:"setModeInterval", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#6eca8f", nextState:"modechange"
                attributeState "interval", label:'\ninterval', action:"setModeLow", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#ff9eb2", nextState:"modechange"
                attributeState "low", label:'\nlow', action:"setModeMiddle", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#FFDE61", nextState:"modechange"
                attributeState "middle", label:'\nmedium', action:"setModeStrong", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#f9b959", nextState:"modechange"
                attributeState "strong", label:'\nstrong', action:"setModeAuto", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#db5764", nextState:"modechange"
                
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfMTk4/MDAxNTIyMTMyNzMxMjEz.BdXDvyyncHtsRwYxAHHWI4zCZaGxYkKAcCbrRYvRtEcg.HHz2i2rn7IdfCFJd-5heHMCllb0TJgXAq8dHtdM1beEg.PNG.shin4299/MiAirPurifier2S_off_tile.png?type=w1", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "modechange", label:'\n${name}', icon:"st.quirky.spotter.quirky-spotter-motion", backgroundColor:"#C4BBB5"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
            /*
            tileAttribute ("device.fanSpeed", key: "SLIDER_CONTROL") {
                attributeState "level", action:"FanSpeed.setFanSpeed"
            }    
            */
		}
        
        standardTile("switch", "device.switch", inactiveLabel: false, width: 2, height: 2) {
            state "on", label:'ON', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "off", label:'OFF', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
             
        	state "turningOn", label:'turningOn', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "turningOff", label:'turningOff', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        valueTile("pm25_label", "", decoration: "flat") {
            state "default", label:'PM2.5 \n㎍/㎥'
        }        
        valueTile("co2_label", "", decoration: "flat") {
            state "default", label:'CO2 \nppm'
        }        
        valueTile("temp_label", "device.temp_label", decoration: "flat") {
            state "default", label:'Temperature'
        }
        valueTile("humi_label", "device.humi_label", decoration: "flat") {
            state "default", label:'Humidity'
        }
		valueTile("pm25_value", "device.fineDustLevel", decoration: "flat") {
        	state "default", label:'${currentValue}', unit:"㎍/㎥", backgroundColors:[
				[value: -1, color: "#C4BBB5"],
            	[value: 0, color: "#7EC6EE"],
            	[value: 15, color: "#51B2E8"],
            	[value: 50, color: "#e5c757"],
            	[value: 75, color: "#E40000"],
            	[value: 500, color: "#970203"]
            ]
        }
		valueTile("carbonDioxide", "device.carbonDioxide", decoration: "flat") {
        	state "default", label:'${currentValue}', unit:"ppm", backgroundColors:[
				[value: -1, color: "#bcbcbc"],
				[value: 400, color: "#bcbcbc"],
            	[value: 600, color: "#7EC6EE"],
            	[value: 800, color: "#51B2E8"],
            	[value: 1000, color: "#e5c757"],
            	[value: 1300, color: "#E40000"],
            	[value: 1600, color: "#970203"]
            ]
        }        
        valueTile("temperature", "device.temperature") {
            state("val", label:'${currentValue}°', defaultState: true, 
            	backgroundColors:[
                    [value: -1, color: "#bcbcbc"],
                    [value: 0, color: "#bcbcbc"],
                    [value: 0.1, color: "#153591"],
                    [value: 5, color: "#153591"],
                    [value: 10, color: "#1e9cbb"],
                    [value: 20, color: "#90d2a7"],
                    [value: 30, color: "#44b621"],
                    [value: 40, color: "#f1d801"],
                    [value: 70, color: "#d04e00"],
                    [value: 90, color: "#bc2323"]
                ]
            )
        }
        valueTile("humidity", "device.humidity") {
            state("val", label:'${currentValue}%', defaultState: true, 
            	backgroundColors:[
                    [value: -1, color: "#bcbcbc"],
                    [value: 0, color: "#bcbcbc"],
                    [value: 10, color: "#153591"],
                    [value: 30, color: "#1e9cbb"],
                    [value: 40, color: "#90d2a7"],
                    [value: 50, color: "#44b621"],
                    [value: 60, color: "#f1d801"],
                    [value: 80, color: "#d04e00"],
                    [value: 90, color: "#bc2323"]
                ]
            )
        }   
        
        valueTile("auto_label", "device.auto_label", decoration: "flat") {
            state "default", label:'Auto'
        }
        valueTile("silent_label", "device.silent_label", decoration: "flat") {
            state "default", label:'Silent'
        }
        valueTile("favorit_label", "device.favorit_label", decoration: "flat") {
            state "default", label:'Interval'
        }
        valueTile("low_label", "device.low_label", decoration: "flat") {
            state "default", label:'Low'
        }
        valueTile("medium_label", "device.medium_label", decoration: "flat") {
            state "default", label:'Middle'
        }
        valueTile("high_label", "device.high_label", decoration: "flat") {
            state "default", label:'Strong'
        }
        valueTile("refresh_label", "device.refresh_label", decoration: "flat") {
            state "default", label:'Refresh'
        }
        valueTile("led_label", "device.led_label", decoration: "flat") {
            state "default", label:'Led'
        }
        valueTile("buzzer_label", "device.buzzer_label", decoration: "flat") {
            state "default", label:'Buzzer'
        }
        valueTile("usage_label", "device.usage_label", decoration: "flat") {
            state "default", label:'Used'
        }
        valueTile("filter_label", "device.filter_label", decoration: "flat") {
            state "default", label:'Filter Used'
        }
        standardTile("refresh", "device.refresh") {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh", backgroundColor:"#A7ADBA"
        }        
        
        standardTile("mode1", "device.mode1") {
			state "default", label: "Auto", action: "setModeAuto", icon:"st.unknown.zwave.static-controller", backgroundColor:"#73C1EC"
		}
        standardTile("mode2", "device.mode2") {
			state "default", label: "Silent", action: "setModeSilent", icon:"st.quirky.spotter.quirky-spotter-sound-off", backgroundColor:"#6eca8f"
		}
        standardTile("mode3", "device.mode3") { 
			state "default", label: "Interval", action: "setModeInterval", icon:"st.presence.tile.presence-default", backgroundColor:"#ff9eb2"
		}
        standardTile("mode4", "device.mode4") {
			state "default", label: "Low", action: "setModeLow", icon:"st.quirky.spotter.quirky-spotter-luminance-dark", backgroundColor:"#FFDE61"
		}
        standardTile("mode5", "device.mode5") {
			state "default", label: "Middle", action: "setModeMiddle", icon:"st.quirky.spotter.quirky-spotter-luminance-light", backgroundColor:"#f9b959"
		}
        standardTile("mode6", "device.mode6") {
			state "default", label: "Strong", action: "setModeStrong", icon:"st.quirky.spotter.quirky-spotter-luminance-bright", backgroundColor:"#ff9eb2"
		}
        standardTile("buzzer", "device.buzzer") {
            state "on", label:'Sound', action:"buzzerOff", icon: "st.custom.sonos.unmuted", backgroundColor:"#BAA7BC", nextState:"turningOff"
            state "off", label:'Mute', action:"buzzerOn", icon: "st.custom.sonos.muted", backgroundColor:"#d1cdd2", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"buzzerOff", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"buzzerOn", backgroundColor:"#BAA7BC", nextState:"turningOn"
        }
        standardTile("ledBrightness", "device.ledBrightness") {
            state "bright", label: 'Bright', action: "setBrightDim", icon: "st.illuminance.illuminance.bright", backgroundColor: "#ff93ac", nextState:"change"
            state "dim", label: 'Dim', action: "setBrightOff", icon: "st.illuminance.illuminance.light", backgroundColor: "#ffc2cd", nextState:"change"
            state "off", label: 'Off', action: "setBright", icon: "st.illuminance.illuminance.dark", backgroundColor: "#d6c6c9", nextState:"change"
            state "change", label:'....', action:"setBrightOff", backgroundColor:"#d6c6c9"
        }         
        valueTile("f1_hour_used", "device.f1_hour_used", width: 2, height: 1) {
            state("val", label:'${currentValue}', defaultState: true, backgroundColor:"#bcbcbc")
        }
        valueTile("filter1_life", "device.filter1_life", width: 2, height: 1) {
            state("val", label:'${currentValue}', defaultState: true, backgroundColor:"#bcbcbc")
        }
        main (["mode"])
        details(["mode", "switch", "pm25_label", "co2_label", "temp_label", "humi_label", 
        "pm25_value", "carbonDioxide", "temperature", "humidity", 
        "auto_label", "silent_label", "favorit_label", "low_label", "medium_label", "high_label", 
        "mode1", "mode2", "mode3", "mode4", "mode5", "mode6", 
        "led_label", "buzzer_label", "refresh_label", "usage_label", "f1_hour_used", 
        "buzzer", "ledBrightness", "refresh", "filter_label", "filter1_life"
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
    log.debug "Status >> ${params.key} [${params.data}]"
 
 	switch(params.key){
    case "mode":
        state.lastMode = params.data
        sendEvent(name:"mode", value: params.data )
    	break;
    case "pm2.5":
    	sendEvent(name:"fineDustLevel", value: params.data)
    	break;
    case "aqi":
    	sendEvent(name:"fineDustLevel", value: params.data)
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data)
    	break;
    case "power":
    	if(params.data == "true") {
    		sendEvent(name:"switch", value:"on")
            sendEvent(name:"mode", value: state.lastMode)
        }
        else if(params.data == "false") {
            sendEvent(name:"mode", value: "off")
            sendEvent(name:"switch", value:"off")
        }
    	break;
    case "temperature":
		def stf = Float.parseFloat(params.data.replace("C",""))
        sendEvent(name:"temperature", value: Math.round(stf*10)/10 )
    	break;
    case "buzzer":
        sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "ledBrightness":
        sendEvent(name:"ledBrightness", value: params.data)
    	break;
    case "co2":
    	sendEvent(name:"carbonDioxide", value: params.data as int)
    	break
    case "filterHoursUsed":
		def use = Math.round(Float.parseFloat(params.data)/24)    
    	sendEvent(name:"f1_hour_used", value: use )
        break;
    case "filterLifeRemaining":
		def life = Math.round(Float.parseFloat(params.data)*1.45)    
    	sendEvent(name:"filter1_life", value: life )
    	break;
    case "averageAqi":
    	sendEvent(name:"airQuality", value: params.data )
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed: false)
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
def setFanSpeed(level){
	def speed = Math.round(level/625*100)    
	log.debug "setSpeed >> ${state.id}, speed=" + speed
   
    def body = [
        "id": state.id,
        "cmd": "speed",
        "data": speed
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
	sendEvent(name: "level", value: speed)
}

def setCommand(cmd, mode){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": mode
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeAuto(){
	log.debug "setModeAuto >> ${state.id}"
    setCommand("mode", "auto")
}

def setModeSilent(){
    log.debug "setModeSilent >> ${state.id}"
    setCommand("mode", "silent")
}

def setModeInterval(){
	log.debug "setModeInterval >> ${state.id}"
    setCommand("mode", "interval")
}

def setModeLow(){
    log.debug "setModeLow >> ${state.id}"
    setCommand("mode", "low")
}

def setModeMiddle(){
    log.debug "setModeMiddle >> ${state.id}"
    setCommand("mode", "middle")
}

def setModeStrong(){
    log.debug "setModeStrong >> ${state.id}"
    setCommand("mode", "strong")
}

def buzzerOn(){
	log.debug "buzzerOn >> ${state.id}"
    setCommand("buzzer", "on")
}

def buzzerOff(){
	log.debug "buzzerOff >> ${state.id}"
    setCommand("buzzer", "off")
}

def ledOn(){
	log.debug "ledOn >> ${state.id}"
    setCommand("led", "on")
}

def ledOff(){
	log.debug "ledOff >> ${state.id}"
    setCommand("led", "off")
}

def setBright(){
	log.debug "setBright >> ${state.id}"
    setCommand("ledBrightness", "bright")
}

def setBrightDim(){
	log.debug "setDim >> ${state.id}"
    setCommand("ledBrightness", "dim")
}

def setBrightOff(){
	log.debug "setBrightOff >> ${state.id}"
    setCommand("ledBrightness", "off")
}

def on(){
	log.debug "On >> ${state.id}"
    setCommand("power", "on")
}

def off(){
	log.debug "Off >> ${state.id}"
    setCommand("power", "off")
}

def updated() {
    refresh()
}

def setExternalAddress(address){
	state.externalAddress = address
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
	//	sendEvent(name:"airQuality", value: "N/A" )
	    
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
