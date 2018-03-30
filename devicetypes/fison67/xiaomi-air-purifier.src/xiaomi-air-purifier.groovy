/**
 *  Xiaomi Air Purifier (v.0.0.1)
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
	definition (name: "Xiaomi Air Purifier", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Switch Level"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
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
        attribute "mode", "enum", ["auto", "silent", "favorite", "low", "medium", "high", "strong"]        
        
        attribute "lastCheckin", "Date"
         
        command "setSpeed"
        command "setStatus"
        command "refresh"
        command "any"
        
        command "on"
        command "off"
        
        command "setModeAuto"
        command "setModeMedium"
        command "setModeLow"
        command "setModeHigh"
        command "setModeStrong"
        command "setModeSilent"
        command "setModeFavorite"
        command "setModeIdle"
        
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
		input name:"model", type:"enum", title:"Select Model", options:["MiAirPurifier", "MiAirPurifier2", "MiAirPurifierPro", "MiAirPurifier2S"], description:"Select Your Airpurifier Model"
	}

	tiles {
		multiAttributeTile(name:"mode", type: "generic", width: 6, height: 4){
			tileAttribute ("device.mode", key: "PRIMARY_CONTROL") {
                attributeState "off", label:'\noff', action:"setModeAuto", icon:"http://blogfiles.naver.net/MjAxODAzMjdfMTk4/MDAxNTIyMTMyNzMxMjEz.BdXDvyyncHtsRwYxAHHWI4zCZaGxYkKAcCbrRYvRtEcg.HHz2i2rn7IdfCFJd-5heHMCllb0TJgXAq8dHtdM1beEg.PNG.shin4299/MiAirPurifier2S_off_tile.png?type=w1", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "auto", label:'\nauto', action:"setModeSilent", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#73C1EC", nextState:"modechange"
                attributeState "silent", label:'\nsilent', action:"setModeFavorite", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#6eca8f", nextState:"modechange"
                attributeState "favorite", label:'\nfavorite', action:"setModeAuto", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#ff9eb2", nextState:"modechange"
                attributeState "low", label:'\nlow', action:"setModeMedium", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#FFDE61", nextState:"modechange"
                attributeState "medium", label:'\nmedium', action:"setModeHigh", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#f9b959", nextState:"modechange"
                attributeState "high", label:'\nhigh', action:"setModeStrong", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#ff9eb2", nextState:"modechange"
                attributeState "strong", label:'\nstrong', action:"setModeAuto", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#db5764", nextState:"modechange"
                
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfMTk4/MDAxNTIyMTMyNzMxMjEz.BdXDvyyncHtsRwYxAHHWI4zCZaGxYkKAcCbrRYvRtEcg.HHz2i2rn7IdfCFJd-5heHMCllb0TJgXAq8dHtdM1beEg.PNG.shin4299/MiAirPurifier2S_off_tile.png?type=w1", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "modechange", label:'\n${name}', icon:"st.quirky.spotter.quirky-spotter-motion", backgroundColor:"#C4BBB5"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
            tileAttribute ("device.fanSpeed", key: "SLIDER_CONTROL") {
                attributeState "level", action:"FanSpeed.setFanSpeed"
            }            
		}
        standardTile("modemain", "device.mode", width: 2, height: 2) {
                state "off", label:'off', action:"setModeAuto", icon:"http://blogfiles.naver.net/MjAxODAzMjdfMTk4/MDAxNTIyMTMyNzMxMjEz.BdXDvyyncHtsRwYxAHHWI4zCZaGxYkKAcCbrRYvRtEcg.HHz2i2rn7IdfCFJd-5heHMCllb0TJgXAq8dHtdM1beEg.PNG.shin4299/MiAirPurifier2S_off_tile.png?type=w1", backgroundColor:"#ffffff", nextState:"turningOn"
                state "auto", label:'auto', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#73C1EC", nextState:"modechange"
                state "silent", label:'silent', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#6eca8f", nextState:"modechange"
                state "favorite", label:'favorite', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#ff9eb2", nextState:"modechange"
                state "low", label:'low', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#FFDE61", nextState:"modechange"
                state "medium", label:'medium', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#f9b959", nextState:"modechange"
                state "high", label:'high', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#ff9eb2", nextState:"modechange"
                state "strong", label:'strong', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfNzQg/MDAxNTIyMTMyNzMxMjEy.i1IvtTLdQ-Y3yHOyI0cwM0QKo8SobVo5vo0-zu72ZZkg.m7o9vNcIoiQBozog9FUXnE3w9O8U0kHeNxDeuWOfaWIg.PNG.shin4299/MiAirPurifier2S_on_tile.png?type=w1", backgroundColor:"#db5764", nextState:"modechange"
                
                state "turningOn", label:'${name}', action:"switch.off", icon:"http://blogfiles.naver.net/MjAxODAzMjdfMTk4/MDAxNTIyMTMyNzMxMjEz.BdXDvyyncHtsRwYxAHHWI4zCZaGxYkKAcCbrRYvRtEcg.HHz2i2rn7IdfCFJd-5heHMCllb0TJgXAq8dHtdM1beEg.PNG.shin4299/MiAirPurifier2S_off_tile.png?type=w1", backgroundColor:"#00a0dc", nextState:"turningOff"
                state "modechange", label:'${name}', icon:"st.quirky.spotter.quirky-spotter-motion", backgroundColor:"#C4BBB5"
        }
        
//-------------------------


        standardTile("switch", "device.switch", inactiveLabel: false, width: 2, height: 2) {
            state "on", label:'ON', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "off", label:'OFF', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
             
        	state "turningOn", label:'turningOn', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "turningOff", label:'turningOff', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        valueTile("pm25_label", "", decoration: "flat") {
            state "default", label:'PM2.5 \n㎍/㎥'
        }        
        valueTile("aqi_label", "", decoration: "flat") {
            state "default", label:'AQI \n㎍/㎥'
        }        
        valueTile("temp_label", "", decoration: "flat") {
            state "default", label:'온도'
        }
        valueTile("humi_label", "", decoration: "flat") {
            state "default", label:'습도'
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
		valueTile("aqi", "device.airQuality", decoration: "flat") {
        	state "default", label:'${currentValue}', unit:"㎍/㎥", backgroundColors:[
				[value: -1, color: "#bcbcbc"],
				[value: 0, color: "#bcbcbc"],
            	[value: 0.5, color: "#7EC6EE"],
            	[value: 15, color: "#51B2E8"],
            	[value: 50, color: "#e5c757"],
            	[value: 75, color: "#E40000"],
            	[value: 500, color: "#970203"]
            ]
        }        
        valueTile("temperature", "device.temperature") {
            state("val", label:'${currentValue}', defaultState: true, 
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
            state("val", label:'${currentValue}', defaultState: true, 
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
        
        valueTile("auto_label", "", decoration: "flat") {
            state "default", label:'Auto \nMode'
        }
        valueTile("silent_label", "", decoration: "flat") {
            state "default", label:'Silent \nMode'
        }
        valueTile("favorit_label", "", decoration: "flat") {
            state "default", label:'Favorite \nMode'
        }
        valueTile("low_label", "", decoration: "flat") {
            state "default", label:'Low \nMode'
        }
        valueTile("medium_label", "", decoration: "flat") {
            state "default", label:'Medium \nMode'
        }
        valueTile("high_label", "", decoration: "flat") {
            state "default", label:'High \nMode'
        }
        valueTile("strong_label", "", decoration: "flat") {
            state "default", label:'Strong \nMode'
        }
        valueTile("led_label", "", decoration: "flat") {
            state "default", label:'Brightness \nMode'
        }
        valueTile("buzzer_label", "", decoration: "flat") {
            state "default", label:'Buzzer \nMode'
        }
        valueTile("usage_label", "", decoration: "flat") {
            state "default", label:'Usage \nTime'
        }
        valueTile("remain_label", "", decoration: "flat") {
            state "default", label:'Remaining \nTime'
        }
        
        standardTile("mode1", "device.mode1") {
			state "default", label: "Auto", action: "setModeAuto", icon:"st.unknown.zwave.static-controller", backgroundColor:"#73C1EC"
		}
        standardTile("mode2", "device.mode2") {
			state "default", label: "Silent", action: "setModeSilent", icon:"st.quirky.spotter.quirky-spotter-sound-off", backgroundColor:"#6eca8f"
		}
        standardTile("mode3", "device.mode3") { 
			state "default", label: "Favor", action: "setModeFavorite", icon:"st.presence.tile.presence-default", backgroundColor:"#ff9eb2"
			state "notab", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab1"
			state "notab1", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab"
		}
        standardTile("mode4", "device.mode4") {
			state "default", label: "Low", action: "setModeLow", icon:"st.quirky.spotter.quirky-spotter-luminance-dark", backgroundColor:"#FFDE61"
			state "notab", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab1"
			state "notab1", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab"
		}
        standardTile("mode5", "device.mode5") {
			state "default", label: "Medium", action: "setModeMedium", icon:"st.quirky.spotter.quirky-spotter-luminance-light", backgroundColor:"#f9b959"
			state "notab", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab1"
			state "notab1", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab"
		}
        standardTile("mode6", "device.mode6") {
			state "default", label: "High", action: "setModeHigh", icon:"st.quirky.spotter.quirky-spotter-luminance-bright", backgroundColor:"#ff9eb2"
			state "notab", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab1"
			state "notab1", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab"
		}
        standardTile("mode7", "device.mode7") {
			state "default", label: "Strong", action: "setModeStrong", icon:"st.Weather.weather1", backgroundColor:"#db5764"
			state "notab", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab1"
			state "notab1", label: "N/A", action: "any", icon:"st.presence.house.secured", backgroundColor:"#bcbcbc", nextState:"notab"
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
            state("val", label:'${currentValue} days', defaultState: true, backgroundColor:"#bcbcbc")
        }
        
        valueTile("filter1_life", "device.filter1_life", width: 2, height: 1) {
            state("val", label:'${currentValue} days', defaultState: true, backgroundColor:"#bcbcbc")
        }



   	main (["modemain"])
	details(["mode", "switch", "pm25_label", "aqi_label", "temp_label", "humi_label", 
    "pm25_value", "aqi", "temperature", "humidity", 
    "auto_label", "silent_label", "favorit_label", "low_label", "medium_label", "high_label", 
    "mode1", "mode2", "mode3", "mode4", "mode5", "mode6", 
    "strong_label", "led_label", "buzzer_label", "usage_label", "f1_hour_used", 
    "mode7", "buzzer", "ledBrightness", "remain_label", "filter1_life"
    ])
        
	}
}

def updated() {
    if(model == "MiAirPurifier"){
		sendEvent(name:"airQuality", value: "N/A" )
		sendEvent(name:"mode3", value: "notab" )
		sendEvent(name:"temperature", value: "N/A" )
		sendEvent(name:"humidity", value: "N/A" )
		sendEvent(name:"mode4", value: "default" )
		sendEvent(name:"mode5", value: "default" )
		sendEvent(name:"mode6", value: "default" )
		sendEvent(name:"mode7", value: "default" )
        }
     else {
		sendEvent(name:"mode4", value: "notab" )
		sendEvent(name:"mode5", value: "notab" )
		sendEvent(name:"mode6", value: "notab" )
		sendEvent(name:"mode7", value: "notab" )
		sendEvent(name:"airQuality", value: 20 )
		sendEvent(name:"mode3", value: "default" )
		sendEvent(name:"temperature", value: 20 )
		sendEvent(name:"humidity", value: 40 )
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
    case "mode":
    	if(params.data == "idle") {
        }
    	else {
        sendEvent(name:"mode", value: params.data )
        }
    	break;
    case "pm2.5":
    	sendEvent(name:"fineDustLevel", value: params.data)
    	break;
    case "aqi":
    	sendEvent(name:"fineDustLevel", value: params.data)
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data +"%")
    	break;
    case "power":
    	if(params.data == "true") {
    	sendEvent(name:"switch", value:"on")
        }
        else if(params.data == "false") {
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
	    if(model == "MiAirPurifier"){
    	}
    	else {
        sendEvent(name:"temperature", value: tem +"°" )
        }
    	break;
    case "buzzer":
        sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "ledBrightness":
        sendEvent(name:"ledBrightness", value: params.data)
    	break;
    case "speed":
		def para = "${params.data}"
		String data = para
		def stf = Float.parseFloat(data)
		def speed = Math.round(stf*625/100)    
        sendEvent(name:"fanSpeed", value: speed)
    	break;
    case "led":
        sendEvent(name:"ledBrightness", value: (params.data == "true" ? "bright" : "off"))
    	break;
    case "f1_hour_used":
		def para = "${params.data}"
		String data = para
		def stf = Float.parseFloat(data)
		def use = Math.round(stf/24)    
    	sendEvent(name:"f1_hour_used", value: use)
        break;
    case "filter1_life":
		def para = "${params.data}"
		String data = para
		def stf = Float.parseFloat(data)
		def life = Math.round(stf*1.435)    
    	sendEvent(name:"filter1_life", value: life)
    	break;
    case "average_aqi":
    
    	sendEvent(name:"airQuality", value: params.data  + "㎍/㎥")
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

def setFanSpeed(level){
	def speed = Math.round(level/625*100)    
	log.debug "setSpeed >> ${state.id}, speed=" + speed
    if(model == "MiAirPurifier"){
    }
    else {
    def body = [
        "id": state.id,
        "cmd": "speed",
        "data": speed
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
	sendEvent(name: "level", value: speed)
	}
}
def setModeAuto(){
	log.debug "setModeAuto >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "auto"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeSilent(){
    log.debug "setModeSilent >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "silent"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeFavorite(){
	log.debug "setModeFavorite >> ${state.id}"
    if(model == "MiAirPurifier"){
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "low"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }
    else {
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "favorite"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }
}

def setModeLow(){
    log.debug "setModeSilent >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "low"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeMedium(){
    log.debug "setModeSilent >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "medium"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeHigh(){
    log.debug "setModeSilent >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "high"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeStrong(){
	log.debug "setModeIdle >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "strong"
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

def any(){
}

def ledOn(){
	log.debug "ledOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def ledOff(){
	log.debug "ledOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setBright(){
	log.debug "setBright >> ${state.id}"
    if(model == "MiAirPurifier"){
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }
    else {
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "bright"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }    
}

def setBrightDim(){
	log.debug "setDim >> ${state.id}"
        if(model == "MiAirPurifier"){
    def body = [
        "id": state.id,
        "cmd": "led",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }
    else {
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "brightDim"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    }    
}

def setBrightOff(){
	log.debug "setDim >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
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
