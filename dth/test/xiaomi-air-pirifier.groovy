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
import groovy.transform.Field
import java.text.DateFormat

@Field 
LANGUAGE_MAP = [
    "temperature": [
        "Korean": "온도",
        "English": "Temperature"
    ],
    "humidity": [
        "Korean": "습도",
        "English": "Humidity"
    ],
    "min": [
        "Korean": "최저",
        "English": "Min"
    ],
    "max": [
        "Korean": "최고",
        "English": "Max"
    ],
    "avg": [
        "Korean": "평균",
        "English": "Avg"
    ],
    "totalTemperature": [
        "Korean": "종합 온도",
        "English": "Total Temperature"
    ],
    "totalHumidity": [
        "Korean": "종합 습도",
        "English": "Total Humidity"
    ]
]

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
         
        command "setLanguage" 
        
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
        input name: "selectedLang", title:"Select a language" , type: "enum", required: true, options: ["English", "Korean"], defaultValue: "English", description:"Language for DTH"
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
        valueTile("temp_label", "device.temp_label", decoration: "flat") {
            state("val", label:'${currentValue}', defaultState: true)
        }
        valueTile("humi_label", "device.humi_label", decoration: "flat") {
            state "val", label:'${currentValue}', defaultState: true
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
            state "default", label:'LED\nBrightness'
        }
        valueTile("buzzer_label", "", decoration: "flat") {
            state "default", label:'Buzzer'
        }
        valueTile("usage_label", "", decoration: "flat") {
            state "default", label:'Filter\nLife'
        }
        valueTile("refresh", "device.refresh", decoration: "flat") {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
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
            state("val", label:'Usage ${currentValue}d', defaultState: true, backgroundColor:"#bcbcbc")
        }
        
        valueTile("filter1_life", "device.filter1_life", width: 2, height: 1) {
            state("val", label:'Remain ${currentValue}d', defaultState: true, backgroundColor:"#bcbcbc")
        }

		htmlTile(name:"pm25HtmlTile", action:"getGraphPm25HTML", 
        	whitelist:["code.jquery.com", "ajax.googleapis.com"], width:6, height:3){
        }
		htmlTile(name:"temperatureHtmlTile", action:"getGraphTemperatureHTML", 
        	whitelist:["code.jquery.com", "ajax.googleapis.com"], width:6, height:3){
        }
        htmlTile(name:"humidityHtmlTile", action:"getGraphHumidityHTML", 
        	whitelist:["code.jquery.com", "ajax.googleapis.com"], width:6, height:3){
        }
        htmlTile(name:"totalTemperatureHtmlTile", action:"getGraphTotalTemperatureHTML", 
        	whitelist:["code.jquery.com", "ajax.googleapis.com"], width:6, height:4){
        }
        htmlTile(name:"totalHumidityHtmlTile", action:"getGraphTotalHumidityHTML", 
        	whitelist:["code.jquery.com", "ajax.googleapis.com"], width:6, height:4){
        }
        htmlTile(name:"totalPM25HtmlTile", action:"getGraphTotalPM25HTML", 
        	whitelist:["code.jquery.com", "ajax.googleapis.com"], width:6, height:4){
        }
        

   	main (["modemain"])
	details(["mode", "switch", "pm25_label", "aqi_label", "temp_label", "humi_label", 
    "pm25_value", "aqi", "temperature", "humidity", 
    "auto_label", "silent_label", "favorit_label", "low_label", "medium_label", "high_label", 
    "mode1", "mode2", "mode3", "mode4", "mode5", "mode6", 
    "strong_label", "buzzer_label", "led_label", "usage_label", "f1_hour_used", 
    "mode7", "buzzer", "ledBrightness", "refresh", "filter1_life", 
    "pm25HtmlTile", "temperatureHtmlTile", "humidityHtmlTile", "totalTemperatureHtmlTile", "totalHumidityHtmlTile", "totalPM25HtmlTile"
    ])
        
	}
}

mappings {
      path("/getGraphPm25HTML") {action: [ GET: "getGraphPm25HTML" ]}
      path("/getGraphTemperatureHTML") {action: [ GET: "getGraphTemperatureHTML" ]}
      path("/getGraphHumidityHTML") {action: [ GET: "getGraphHumidityHTML" ]}
      path("/getGraphTotalTemperatureHTML") {action: [ GET: "getGraphTotalTemperatureHTML" ]}
      path("/getGraphTotalHumidityHTML") {action: [ GET: "getGraphTotalHumidityHTML" ]}
      path("/getGraphTotalPM25HTML") {action: [ GET: "getGraphTotalPM25HTML" ]}
}

def updated() {
	refresh()
    setLanguage(settings.selectedLang)
}

def setLanguage(language){
    log.debug "Languge >> ${language}"
	state.language = language
    
    sendEvent(name:"temp_label", value: LANGUAGE_MAP["temperature"][language] )
    sendEvent(name:"humi_label", value: LANGUAGE_MAP["humidity"][language] )
}

def setExternalAddress(address){
	state.externalAddress = address
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
    log.debug "setModeHigh >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "high"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeStrong(){
	log.debug "setModeStrong >> ${state.id}"
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
	log.debug "setBrightOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
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

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
    if(model == "MiAirPurifier"){
		sendEvent(name:"airQuality", value: "N/A" )
		sendEvent(name:"mode3", value: "notab" )
		sendEvent(name:"temperature", value: "N/A" )
		sendEvent(name:"humidity", value: "N/A" )
		sendEvent(name:"mode4", value: "default" )
		sendEvent(name:"mode5", value: "default" )
		sendEvent(name:"mode6", value: "default" )
		sendEvent(name:"mode7", value: "default" )    
	        if(jsonObj.properties.aqi != null && jsonObj.properties.aqi != ""){
        		sendEvent(name:"fineDustLevel", value: jsonObj.properties.aqi)
        	}
	        if(jsonObj.properties.pm25 != null && jsonObj.properties.pm25 != ""){
        		sendEvent(name:"fineDustLevel", value: jsonObj.properties.aqi)
        	}
        }
     else {
		sendEvent(name:"mode4", value: "notab" )
		sendEvent(name:"mode5", value: "notab" )
		sendEvent(name:"mode6", value: "notab" )
		sendEvent(name:"mode7", value: "notab" )
	    sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity + "%" )
    	sendEvent(name:"temperature", value: jsonObj.properties.temperature.value  )
	        if(jsonObj.properties.aqi != null && jsonObj.properties.aqi != ""){
        		sendEvent(name:"fineDustLevel", value: jsonObj.properties.aqi)
        	}
        	if(jsonObj.properties.averageAqi != null && jsonObj.properties.averageAqi != ""){
        		sendEvent(name:"airQuality", value: jsonObj.properties.averageAqi)
        	}
        }
		if(jsonObj.properties.power == true){
			sendEvent(name:"mode", value: jsonObj.state.mode)
			sendEvent(name:"switch", value: "on" )
		} else {
			sendEvent(name:"mode", value: "off" )
			sendEvent(name:"switch", value: "off" )
		}
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer == true ? "on" : "off"))
        
        if(jsonObj.state.filterLifeRemaining != null && jsonObj.state.filterLifeRemaining != ""){
    		sendEvent(name:"filter1_life", value: jsonObj.state.filterLifeRemaining )
        }
        if(jsonObj.state.filterHoursUsed != null && jsonObj.state.filterHoursUsed != ""){
    		sendEvent(name:"f1_hour_used", value: Math.round(jsonObj.state.filterHoursUsed/24) )
        }
        if(jsonObj.properties.ledBrightness != null && jsonObj.properties.ledBrightness != ""){
        	sendEvent(name:"ledBrightness", value: jsonObj.properties.ledBrightness)
        }
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

def getGraphPm25HTML(){
	httpGet(makeParams("pm2.5")) { resp ->
    
    	def map = parseResponseNormalData(resp)
        def content = htmlNormalContent(map.datas, map.labels, "PM2.5", "#00ffff")
        
		render contentType: "text/html", data: content, status: 200
    }
}

def getGraphTemperatureHTML() {
    httpGet(makeParams("temperature")) { resp ->
    
    	def map = parseResponseNormalData(resp)
        def content = htmlNormalContent(map.datas, map.labels, LANGUAGE_MAP["temperature"][state.language], "blue")
        
		render contentType: "text/html", data: content, status: 200
    }
}

def getGraphHumidityHTML() {
	httpGet(makeParams("relativeHumidity")) { resp ->
    
    	def map = parseResponseNormalData(resp)
        def content = htmlNormalContent(map.datas, map.labels, LANGUAGE_MAP["humidity"][state.language], "red")
        
		render contentType: "text/html", data: content, status: 200
    }
}

def getGraphTotalTemperatureHTML(){
	
	httpGet(makeTotalParams("temperature")) { resp ->
    
    	def map = paraseResponseTotalData(resp)
        def content = htmlTotalContent(map.labels, map.mins, map.maxs, map.avgs, LANGUAGE_MAP["totalTemperature"][state.language])
        
		render contentType: "text/html", data: content, status: 200
    }
}

def getGraphTotalHumidityHTML(){

	httpGet(makeTotalParams("relativeHumidity")) { resp ->
    
    	def map = paraseResponseTotalData(resp)
        def content = htmlTotalContent(map.labels, map.mins, map.maxs, map.avgs, LANGUAGE_MAP["totalHumidity"][state.language])
        
		render contentType: "text/html", data: content, status: 200
    }
}

def getGraphTotalPM25HTML(){

	httpGet(makeTotalParams("pm2.5")) { resp ->
    
    	def map = paraseResponseTotalData(resp)
        def content = htmlTotalContent(map.labels, map.mins, map.maxs, map.avgs, "PM2.5")
        
		render contentType: "text/html", data: content, status: 200
    }
}

def makeParams(type){
	def sDate
    def eDate
	use (groovy.time.TimeCategory) {
      def now = new Date()
      
      sDate = (now - 1.days).format( 'yyyy-MM-dd 00:00:00', location.timeZone )
      eDate = now.format( 'yyyy-MM-dd 23:59:59', location.timeZone )
    }
    log.debug sDate

    def params = [
        uri: "http://${state.externalAddress}",
        path: "/devices/history/${state.id }/${type}/${sDate}/${eDate}"
    ]
    return params
}

def makeTotalParams(type){
	def sDate
    def eDate
	use (groovy.time.TimeCategory) {
      def now = new Date()
      //TimeZone.getTimeZone("Asia/Seoul")
      sDate = (now - 7.days).format( 'yyyy-MM-dd 00:00:00', location.timeZone )
      eDate = now.format( 'yyyy-MM-dd 23:59:59', location.timeZone )
    }

    def params = [
        uri: "http://${state.externalAddress}",
        path: "/devices/history/${state.id}/${type}/${sDate}/${eDate}/total"
    ]
    return params
}

def parseResponseNormalData(resp){
	def returnMap = [:]

	def jsonObj = new JsonSlurper().parseText(resp.getData().toString())
    def dataList = jsonObj.data
    def labels = []
    def datas = []

    dataList.reverseEach {
        labels.push("\"" + it.date + "\"")
        datas.push(it.value)
    }
    
    returnMap['labels'] = labels
    returnMap['datas'] = datas
    
    return returnMap
}

def paraseResponseTotalData(resp){
	def returnMap = [:]

	def jsonObj = new JsonSlurper().parseText(resp.getData().toString())
    def dataList = jsonObj.data

    def labels = []
    def minDatas = []
    def maxDatas = []
    def avgDatas = []

    dataList.each {
        labels.push("\"" + it.date2.substring(5,10) + "\"")
        minDatas.push(it.min)
        maxDatas.push(it.max)
        avgDatas.push(it.avg)
    }
    
    returnMap['labels'] = labels
    returnMap['mins'] = minDatas
    returnMap['maxs'] = maxDatas
    returnMap['avgs'] = avgDatas
    
    return returnMap
}

def htmlNormalContent(datas, labels, name, color){
	def html = """
		<!DOCTYPE html>
			<html>
				<head>
                	<meta http-equiv="refresh" content="8">
                    <meta http-equiv="cache-control" content="max-age=0"/>
                    <meta http-equiv="cache-control" content="no-cache"/>
                    <meta http-equiv="expires" content="0"/>
                    <meta http-equiv="pragma" content="no-cache"/>
                    <meta name="viewport" content="width = device-width, user-scalable=no, initial-scale=1.0"> 
                    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
					<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js"></script>
					<script type="text/javascript">
                    
                    	jQuery( document ).ready(function() {
							initChart();
                        });
                        
                        function initChart(){
                        	
                            var datas = ${datas};
                            var labels = ${labels};
                            
                            var datasets = [{
                                label: '${name}',
                                backgroundColor: '${color}',
                                borderColor: '${color}',
                                data: datas,
                                fill: false,
                            }]
                            
                            var config = {
                                type: 'line',
                                data: {
                                    labels: labels,
                                    datasets: datasets
                                },
                                options: {
                                    maintainAspectRatio: false,
                                    responsive: true,
                                    legend: {
                                        display: true,
                                        position: "bottom",
                                        labels: {
                                            boxWidth: 10,
                                        }
                                    },
                                    title:{
                                        display:true,
                                        text:''
                                    },
                                    tooltips: {
                                        mode: 'index',
                                        intersect: false,
                                    },
                                    hover: {
                                        mode: 'nearest',
                                        intersect: false
                                    },
                                    scales: {
                                        xAxes: [{
                                            display: false,
                                            scaleLabel: {
                                                display: true,
                                                labelString: 'Date'
                                            }
                                        }],
                                        yAxes: [{
                                            display: true,
                                            scaleLabel: {
                                                display: false,
                                                labelString: 'Value'
                                            }
                                        }]
                                    }
                                }
                            };
                            
    						new Chart(document.getElementById("chart"), config);
                        }
                        
					</script>
				</head>
				<body>
					<canvas id="chart" width="200" height="150"></canvas>
				</body>
			</html>
		"""
	return html
}

def htmlTotalContent(labels, mins, maxs, avgs, titleStr){
	def minStr = LANGUAGE_MAP["min"][state.language]
	def maxStr = LANGUAGE_MAP["max"][state.language]
	def avgStr = LANGUAGE_MAP["avg"][state.language]
    
	def html = """
		<!DOCTYPE html>
			<html>
				<head>
                	<meta http-equiv="refresh" content="8">
                    <meta http-equiv="cache-control" content="max-age=0"/>
                    <meta http-equiv="cache-control" content="no-cache"/>
                    <meta http-equiv="expires" content="0"/>
                    <meta http-equiv="pragma" content="no-cache"/>
                    <meta name="viewport" content="width = device-width, user-scalable=no, initial-scale=1.0"> 
                    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
					<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js"></script>
					<script type="text/javascript">
                    
                    	jQuery( document ).ready(function() {
							initChart();
                        });
                        
                        function initChart(){
                        	
                            var chartData = {
                                labels: ${labels},
                                datasets: [{
                                    type: 'bar',
                                    label: '${minStr}',
                                    backgroundColor: 'blue',
                                    fill: false,
                                    data: ${mins},
                                    borderColor: 'white',
                                    borderWidth: 2
                                }, {
                                    type: 'bar',
                                    label: '${maxStr}',
                                    backgroundColor: 'red',
                                    data: ${maxs},
                                    borderColor: 'white',
                                    borderWidth: 2
                                }, {
                                    type: 'bar',
                                    label: '${avgStr}',
                                    backgroundColor: 'green',
                                    data: ${avgs},
                                    borderColor: 'white',
                                    borderWidth: 2
                                }]
                            };
                            
                            var config = {
                                type: 'bar',
                                data: chartData,
                                options: {
                                    responsive: true,
                                    title: {
                                        display: true,
                                        text: '${titleStr}'
                                    },
                                    tooltips: {
                                        mode: 'index',
                                        intersect: true
                                    }
                                }
                            }
                            
    						new Chart(document.getElementById("chart"), config);
                        }
                        
					</script>
				</head>
				<body>
					<canvas id="chart" width="200" height="120"></canvas>
				</body>
			</html>
		"""
	return html
}
