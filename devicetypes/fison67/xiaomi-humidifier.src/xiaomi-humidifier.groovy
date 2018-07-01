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

@Field 
LANGUAGE_MAP = [
    "temp": [
        "Korean": "온도",
        "English": "Temp"
    ],
    "tarH": [
        "Korean": "목표습도",
        "English": "Target"
    ],
    "buz": [
        "Korean": "부저음",
        "English": "Buzzer"
    ],
    "dry": [
        "Korean": "건조\n모드",
        "English": "Dry\nMode"
    ],
    "utime": [
        "Korean": "사용\n시간",
        "English": "Usage\nTime"
    ],
    "wDep": [
        "Korean": "물양",
        "English": "WD"
    ]
]


metadata {
	definition (name: "Xiaomi Humidifier", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Switch Level"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Refresh"
		capability "Sensor"
         
        attribute "mode", "enum", ["auto", "silent", "medium", "high"]
        attribute "buzzer", "enum", ["on", "off"]
        attribute "ledBrightness", "enum", ["off", "dim", "bright"]
        attribute "water2", "string"
        attribute "water", "number"
        attribute "use_time", "string"
        attribute "dry", "enum", ["on", "off"]
        
        attribute "lastCheckin", "Date"
         
        command "setModeOn"
        command "setModeAuto"
        command "setModeSilent"
        command "setModeHigh"
        command "setModeMedium"
        
        command "buzzerOn"
        command "buzzerOff"
        command "setDryOn"
        command "setDryOff"
        command "dummy"
        
        command "setBright"
        command "setBrightDim"
        command "setBrightOff"
	}


	simulator {
	}
	preferences {
		input name:"model", type:"enum", title:"Select Model", options:["Humidifier1", "Humidifier2"], description:"Select Your Humidifier Model(Humidifier 1: N/A Water Depth and Dry Mode, Humidifier 2: N/A LED Brightness Control and Target Humidity)"
	        input name: "selectedLang", title:"Select a language" , type: "enum", required: true, options: ["English", "Korean"], defaultValue: "English", description:"Language for DTH"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"mode", type: "generic", width: 6, height: 4){
			tileAttribute ("device.mode", key: "PRIMARY_CONTROL") {
                attributeState "off", label:'\nOFF', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ2/MDAxNTIyMTUxNzIxMTk3.xeCR1k4pk0vDOozb43Lfo6g2fMC1a_VJFUpTQ071XRUg.dyhFTAUaCwWPUYc4hPUdGiuUI5yeRJ4QpP3kX802AlIg.PNG.shin4299/Humi_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "auto", label:'\nAuto Mode', action:"setModeSilent", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ1/MDAxNTIyMTUxNzIxMTk5.LTiuV1QSyPu6WgMB3uR7Bc-Hy19Uwgard5XKG5jj1JIg.XpdiwfmUg3Rz6IgIWyamtsrYeW0BJRqj28XyHRuADA0g.PNG.shin4299/Humi_tile_auto.png?type=w580", backgroundColor:"#73C1EC", nextState:"modechange"
                attributeState "silent", label:'\nSilent Mode', action:"setModeMedium", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTE2/MDAxNTIyMTUxNzIxMTE3.wVp36he9l0up0SalfSgNMOff9y_U9F2wyPc5AfmK-nEg.coHcd4mj2byTBFzTWnc4yjKi7xbJb7QhfgBn9ASt5eUg.PNG.shin4299/Humi_tile_1.png?type=w580", backgroundColor:"#6eca8f", nextState:"modechange"
                attributeState "medium", label:'\nMedium Mode', action:"setModeHigh", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjEz/MDAxNTIyMTUxNzIxMTk4.VfHSHeU2sS9J-T03zqc_sSjgO4ifOxiyBtGorUPxD2kg.dnC3xCu45F_153OJfUm0Pd1_HAWFp9DWVGHLagDqOSgg.PNG.shin4299/Humi_tile_2.png?type=w580", backgroundColor:"#FFDE61", nextState:"modechange"
                attributeState "high", label:'\nHigh Mode', action:"setModeAuto", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTA5/MDAxNTIyMTUxNzIxMTk3.r9waU9A5WmDgRz6p6eiGYTl67F1jo5HGcurD9i57Mj0g.a1R4bIefNK0gT-NdDFmYveohdkXxUuRgJIszH9Q38Ogg.PNG.shin4299/Humi_tile_3.png?type=w580", backgroundColor:"#ff9eb2", nextState:"modechange"
                
                attributeState "modechange", label:'\n${name}', icon:"st.quirky.spotter.quirky-spotter-motion", backgroundColor:"#C4BBB5"
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ2/MDAxNTIyMTUxNzIxMTk3.xeCR1k4pk0vDOozb43Lfo6g2fMC1a_VJFUpTQ071XRUg.dyhFTAUaCwWPUYc4hPUdGiuUI5yeRJ4QpP3kX802AlIg.PNG.shin4299/Humi_tile_off.png?type=w580", backgroundColor:"#C4BBB5", nextState:"off"

                attributeState "off1", label:'\nOFF', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNzYg/MDAxNTIyNjcwODg0MTI2.STxrAj6ogps95LmvuOjB0BMA85vmV7nSQkoZh-8tJY0g.lJrICUhnvXTE7HoJC1GR7gzwJxrxfDqyXd7NF59h0psg.PNG.shin4299/Humi1_main_off.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn1"
                attributeState "auto1", label:'\nAuto Mode', action:"setModeSilent", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMjE0/MDAxNTIyNjcwODgzOTY3.xc9NXAkOenPbwUR2bf0vvO7P-RdA5vcjzNw7_Vu-CHgg.ggBUsFmbIRItbDqU6xoj_lxlr_jJpjFcVyxSomZAi1gg.PNG.shin4299/Humi1_main_auto.png?type=w3", backgroundColor:"#73C1EC", nextState:"modechange"
                attributeState "silent1", label:'\nSilent Mode', action:"setModeMedium", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMjUg/MDAxNTIyNjcwODgzNTQ2.9lTumWoRhiheYDV3v6EQamiC0ZFldzA5_0YfjzFm-gIg.lTYpe6wUUeEuPBwTzogwTM1sRX43POOIF7jKjCAV92Yg.PNG.shin4299/Humi1_main_1.png?type=w3", backgroundColor:"#6eca8f", nextState:"modechange"
                attributeState "medium1", label:'\nMedium Mode', action:"setModeHigh", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMTI4/MDAxNTIyNjcwODgzNjgy.PPQVgYG3pUKSCPYtX7quyWlju10JYSXMlIC0g1v0NoYg.7Dv1AoIGQmVRoID5ek8DualZuy-q2C--6gt6aTINZncg.PNG.shin4299/Humi1_main_2.png?type=w3", backgroundColor:"#FFDE61", nextState:"modechange"
                attributeState "high1", label:'\nHigh Mode', action:"setModeAuto", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMjQ2/MDAxNTIyNjcwODgzODM4.5obz2ySIyMlI1c_AjC0dfYiLKwYGHS_RFwnUNr5kmt4g.OjvrT0NKffJis6ff4n37PEzsR5b89Ya_OazNRFUkdmIg.PNG.shin4299/Humi1_main_3.png?type=w3", backgroundColor:"#ff9eb2", nextState:"modechange"
                
                attributeState "turningOn1", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNzYg/MDAxNTIyNjcwODg0MTI2.STxrAj6ogps95LmvuOjB0BMA85vmV7nSQkoZh-8tJY0g.lJrICUhnvXTE7HoJC1GR7gzwJxrxfDqyXd7NF59h0psg.PNG.shin4299/Humi1_main_off.png?type=w3", backgroundColor:"#C4BBB5", nextState:"off1"
			}
			tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
        		attributeState("humidity", label:'${currentValue}', unit:"%", defaultState: true)
    		}            
			tileAttribute("device.temperature2", key: "SECONDARY_CONTROL") {
				attributeState("temperature2", label:'         ${currentValue}°', unit:"°", defaultState: true)
    		}            
			tileAttribute("device.water2", key: "SECONDARY_CONTROL") {
        		attributeState("water2", label:'                                ${currentValue}%', unit:"%", defaultState: true)
    		}            
			tileAttribute("device.target", key: "SECONDARY_CONTROL") {
        		attributeState("target", label:'                                                              ${currentValue}:', defaultState: true)
    		}            
		    tileAttribute ("device.level", key: "SLIDER_CONTROL", range:"(30..80)") {
        		attributeState "level", action:"switch level.setLevel"
		    }
//            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
//    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
//            }
		}
        
		multiAttributeTile(name:"modem", type: "generic", width: 6, height: 4){
			tileAttribute ("device.mode", key: "PRIMARY_CONTROL") {
                attributeState "off", label:'OFF', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTI5/MDAxNTIyMTUxNzIxMTE0.jV8vnwjhiMGM3cMj0AOAkpXxMn67VHVSCOI0Oifaw_gg.caD0We4bMaw4zDle-ZElMaJ5J7X9XtgJK8r273B441cg.PNG.shin4299/Humi_main_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "auto", label:'Auto Mode', action:"setModeSilent", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjQz/MDAxNTIyMTUxNzIxMTEy.N1MKnCLLEKvjsBsc8AOhUiqWpGqFIjjBIdFVuDAUT48g.Ms4kVngzHK7Ce0IzTKi9KKmU1DULsGBNMXU23xZofPUg.PNG.shin4299/Humi_main_auto.png?type=w580", backgroundColor:"#73C1EC", nextState:"modechange"
                attributeState "silent", label:'Silent Mode', action:"setModeMedium", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTAy/MDAxNTIyMTUxNzIxMTEy.F_FV8JiIWNtgg9TA-JmpykMS3nFMJ7SZwAnbQdbrxG4g.tlD1AuFoeWgHaPY_Wc7IVoMll6tGuyZZpteoyuvLYLIg.PNG.shin4299/Humi_main_1.png?type=w580", backgroundColor:"#6eca8f", nextState:"modechange"
                attributeState "medium", label:'Medium Mode', action:"setModeHigh", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjIx/MDAxNTIyMTUxNzIxMTE2.C_mh1eH-qS9v7JDao8rTggW8ISf_JD4tumQ-TSpnUBcg.W0tNgspXKTM8X-EWHu3BL_OePZjGve43W4ZM83oMBLcg.PNG.shin4299/Humi_main_2.png?type=w580", backgroundColor:"#FFDE61", nextState:"modechange"
                attributeState "high", label:'High Mode', action:"setModeAuto", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfODUg/MDAxNTIyMTUxNzIxMTEy.bgJ3fJQMIqt3A60PiRitHuTih18yk3cVZezGoNnLsU0g.7njxuU4uNsJ-VUbgrkkDaoUFFq9Cy85N7oOz9Yx8DYQg.PNG.shin4299/Humi_main_3.png?type=w580", backgroundColor:"#ff9eb2", nextState:"modechange"
                
                attributeState "modechange", label:'${name}', icon:"st.quirky.spotter.quirky-spotter-motion", backgroundColor:"#C4BBB5"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTI5/MDAxNTIyMTUxNzIxMTE0.jV8vnwjhiMGM3cMj0AOAkpXxMn67VHVSCOI0Oifaw_gg.caD0We4bMaw4zDle-ZElMaJ5J7X9XtgJK8r273B441cg.PNG.shin4299/Humi_main_off.png?type=w580", backgroundColor:"#C4BBB5", nextState:"off"

                attributeState "off1", label:'OFF', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNzYg/MDAxNTIyNjcwODg0MTI2.STxrAj6ogps95LmvuOjB0BMA85vmV7nSQkoZh-8tJY0g.lJrICUhnvXTE7HoJC1GR7gzwJxrxfDqyXd7NF59h0psg.PNG.shin4299/Humi1_main_off.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn1"
                attributeState "auto1", label:'Auto Mode', action:"setModeSilent", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMjE0/MDAxNTIyNjcwODgzOTY3.xc9NXAkOenPbwUR2bf0vvO7P-RdA5vcjzNw7_Vu-CHgg.ggBUsFmbIRItbDqU6xoj_lxlr_jJpjFcVyxSomZAi1gg.PNG.shin4299/Humi1_main_auto.png?type=w3", backgroundColor:"#73C1EC", nextState:"modechange"
                attributeState "silent1", label:'Silent Mode', action:"setModeMedium", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMjUg/MDAxNTIyNjcwODgzNTQ2.9lTumWoRhiheYDV3v6EQamiC0ZFldzA5_0YfjzFm-gIg.lTYpe6wUUeEuPBwTzogwTM1sRX43POOIF7jKjCAV92Yg.PNG.shin4299/Humi1_main_1.png?type=w3", backgroundColor:"#6eca8f", nextState:"modechange"
                attributeState "medium1", label:'Medium Mode', action:"setModeHigh", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMTI4/MDAxNTIyNjcwODgzNjgy.PPQVgYG3pUKSCPYtX7quyWlju10JYSXMlIC0g1v0NoYg.7Dv1AoIGQmVRoID5ek8DualZuy-q2C--6gt6aTINZncg.PNG.shin4299/Humi1_main_2.png?type=w3", backgroundColor:"#FFDE61", nextState:"modechange"
                attributeState "high1", label:'High Mode', action:"setModeAuto", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMjQ2/MDAxNTIyNjcwODgzODM4.5obz2ySIyMlI1c_AjC0dfYiLKwYGHS_RFwnUNr5kmt4g.OjvrT0NKffJis6ff4n37PEzsR5b89Ya_OazNRFUkdmIg.PNG.shin4299/Humi1_main_3.png?type=w3", backgroundColor:"#ff9eb2", nextState:"modechange"
                
                attributeState "turningOn1", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNzYg/MDAxNTIyNjcwODg0MTI2.STxrAj6ogps95LmvuOjB0BMA85vmV7nSQkoZh-8tJY0g.lJrICUhnvXTE7HoJC1GR7gzwJxrxfDqyXd7NF59h0psg.PNG.shin4299/Humi1_main_off.png?type=w3", backgroundColor:"#C4BBB5", nextState:"off1"				
			}
		}
        
        
        standardTile("switch", "device.switch", inactiveLabel: false, width: 2, height: 2) {
            state "on", label:'ON', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "off", label:'OFF', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
             
        	state "turningOn", label:'turningOn', action:"switch.off", icon:"st.Appliances.appliances17", backgroundColor:"#00a0dc", nextState:"turningOff"
            state "turningOff", label:'turningOff', action:"switch.on", icon:"st.Appliances.appliances17", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        valueTile("auto_label", "", decoration: "flat") {
            state "default", label:'Auto \nMode'
        }
        valueTile("silent_label", "", decoration: "flat") {
            state "default", label:'Silent \nMode'
        }
        valueTile("medium_label", "", decoration: "flat") {
            state "default", label:'Medium \nMode'
        }
        valueTile("high_label", "", decoration: "flat") {
            state "default", label:'High \nMode'
        }
        standardTile("mode1", "device.mode") {
			state "default", label: "Auto", action: "setModeAuto", icon:"st.unknown.zwave.static-controller", backgroundColor:"#73C1EC"
		}
        standardTile("mode2", "device.mode") {
			state "default", label: "Silent", action: "setModeSilent", icon:"st.quirky.spotter.quirky-spotter-luminance-dark", backgroundColor:"#6eca8f"
		}
        standardTile("mode3", "device.mode") {
			state "default", label: "Medium", action: "setModeMedium", icon:"st.quirky.spotter.quirky-spotter-luminance-light", backgroundColor:"#FFDE61"
		}
        standardTile("mode4", "device.mode") {
			state "default", label: "High", action: "setModeHigh", icon:"st.quirky.spotter.quirky-spotter-luminance-bright", backgroundColor:"#ff9eb2"
		}
        
        valueTile("buzzer_label", "device.buzzer_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }        
        valueTile("led_label", "", decoration: "flat") {
            state "default", label:'LED'
        }        
        valueTile("time_label", "device.time_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }        
        valueTile("dry_label", "device.dry_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }        
        valueTile("update_label", "", decoration: "flat") {
            state "default", label:'last \nupdate'
        }        
        standardTile("buzzer", "device.buzzer") {
            state "on", label:'Sound', action:"buzzerOff", icon: "st.custom.sonos.unmuted", backgroundColor:"#BAA7BC", nextState:"turningOff"
            state "off", label:'Mute', action:"buzzerOn", icon: "st.custom.sonos.muted", backgroundColor:"#d1cdd2", nextState:"turningOn"
             
        	state "turningOn", label:'....', action:"buzzerOff", backgroundColor:"#d1cdd2", nextState:"turningOff"
            state "turningOff", label:'....', action:"buzzerOn", backgroundColor:"#BAA7BC", nextState:"turningOn"
        }
        standardTile("ledBrightness", "device.ledBrightness") {
            state "bright", label: 'Bright', action: "setBrightDim", icon: "st.illuminance.illuminance.bright", backgroundColor: "#ff93ac", nextState:"dim"
            state "dim", label: 'Dim', action: "setBrightOff", icon: "st.illuminance.illuminance.light", backgroundColor: "#ffc2cd", nextState:"off"
            state "off", label: 'Off', action: "setBright", icon: "st.illuminance.illuminance.dark", backgroundColor: "#d6c6c9", nextState:"bright"
            
            state "bright2", label: 'Bright', action: "dummy", icon: "st.illuminance.illuminance.bright", backgroundColor: "#ff93ac", nextState:"bright2"
            state "dim2", label: 'Dim', action: "dummy", icon: "st.illuminance.illuminance.light", backgroundColor: "#ffc2cd", nextState:"dim2"
            state "off2", label: 'Off', action: "dummy", icon: "st.illuminance.illuminance.dark", backgroundColor: "#d6c6c9", nextState:"off2"
        }         
        valueTile("use_time", "device.use_time", width: 2, height: 1) {
            state("val", label:'${currentValue}', defaultState: true
        	)
        }
        standardTile("dry", "device.dry") {
            state "on", label: 'ON', action: "setDryOff", icon: "st.vents.vent-open",  backgroundColor: "#FFD16C"
            state "off", label: 'OFF', action: "setDryOn", icon: "st.vents.vent", backgroundColor: "#c1baaa"
            state "dummy", label: 'N/A', action: "dummy", icon: "st.presence.house.secured", backgroundColor: "#d1cdd2", nextState:"dummy"
        }
        valueTile("checkin", "device.lastCheckin", width: 2, height: 1) {
            state("default", label:'${currentValue}', defaultState: true
        	)
        }
        valueTile("refresh", "device.refresh", decoration: "flat") {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }        
		
   	main (["modem"])
	details(["mode", "switch", "auto_label", "silent_label", "medium_label", "high_label", "mode1", "mode2", "mode3", "mode4", 
    		 "buzzer_label", "led_label", "dry_label", "time_label", "use_time",
                 "buzzer", "ledBrightness", "dry",  "refresh", "checkin"])


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
    	if(model == "Humidifier1") {
    	sendEvent(name:"mode", value: params.data + "1" )
        } else { 
    	sendEvent(name:"mode", value: params.data )
	}
    	break;
    case "power":
    	if(params.data == "true") {
    	sendEvent(name:"switch", value:"on")
        }
        else if(params.data == "false") {
    		if(model == "Humidifier1") {
    		sendEvent(name:"mode", value: "off1")
	    	sendEvent(name:"switch", value:"off")
        	} else { 
    		sendEvent(name:"mode", value: "off")
	    	sendEvent(name:"switch", value:"off")
		}
        }
        else { }
    	break;
    case "temperature":
		def para = "${params.data}"
		String data = para
		def st = data.replace("C","");
		def stf = Float.parseFloat(st)
		def tem = Math.round(stf*10)/10
        sendEvent(name:"temperature", value: tem )
        sendEvent(name:"temperature2", value: state.temp + ": " + tem )
    	break;
    case "useTime":
		def para = "${params.data}"
		String data = para
		def stf = Float.parseFloat(data)
		def hour = Math.round(stf/3600)
		int leftday = Math.floor(stf/3600/24)
		int lefthour = hour - leftday*24
        sendEvent(name:"use_time", value: leftday + "d " + lefthour + "h" )
    	break;
    case "ledBrightness":
    	if(model == "Humidifier1"){
        sendEvent(name:"ledBrightness", value: params.data)
        } else {
        sendEvent(name:"ledBrightness", value: params.data+"2")
        }
    	break;        
    case "targetHumidity":
        sendEvent(name:"level", value: params.data)
    	break;
    case "depth":
		def para = "${params.data}"
		String data = para
		def stf = Float.parseFloat(data)
		def water = Math.round(stf/12*10)    
        sendEvent(name:"water", value: water )
        sendEvent(name:"water2", value: state.wdep + ": " + water )
    	break;
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off") )
        break;
    case "dry":
    	sendEvent(name:"dry", value: params.data )
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

def setLevel(level){
	log.debug "setLevel >> ${state.id}"
	def setHumi = Math.round(level/10)*10
    def body = [
        "id": state.id,
        "cmd": "targetHumidity",
        "data": setHumi
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
	log.debug "setBrightOff >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
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

def setModeHigh(){
    log.debug "setModeHight >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "hight"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setModeMedium(){
    log.debug "setModeMedium >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "mode",
        "data": "medium"
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

def setDryOn(){
	log.debug "Dry ON >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "dry",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setDryOff(){
	log.debug "Dry Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "dry",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}



def updated() {
    refresh()
    setLanguage(settings.selectedLang)
}

def setLanguage(language){
    log.debug "Languge >> ${language}"
	state.language = language
	state.wdep = LANGUAGE_MAP["wDep"][language]
	state.temp = LANGUAGE_MAP["temp"][language]
//	state.tarH = LANGUAGE_MAP["tarH"][language]
	
        sendEvent(name:"buzzer_label", value: LANGUAGE_MAP["buz"][language] )
        sendEvent(name:"time_label", value: LANGUAGE_MAP["utime"][language] )
        sendEvent(name:"dry_label", value: LANGUAGE_MAP["dry"][language] )
	sendEvent(name:"target", value: LANGUAGE_MAP["tarH"][language] )
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
    	if(model == "Humidifier1") {
		if(jsonObj.properties.power == true){
			sendEvent(name:"mode", value: jsonObj.state.mode + "1")
			sendEvent(name:"switch", value: "on" )
		} else {
			sendEvent(name:"mode", value: "off1" )
			sendEvent(name:"switch", value: "off" )
		}		
       		sendEvent(name:"water2", value: "N/A" )
		sendEvent(name:"dry", value: "dummy" )
        	sendEvent(name:"ledBrightness", value: jsonObj.state.ledBrightness)
        } else {
		if(jsonObj.properties.power == true){
			sendEvent(name:"mode", value: jsonObj.state.mode)
			sendEvent(name:"switch", value: "on" )
		} else {
			sendEvent(name:"mode", value: "off" )
			sendEvent(name:"switch", value: "off" )
		}
        	sendEvent(name:"ledBrightness", value: jsonObj.state.ledBrightness + "2")
	    	sendEvent(name:"dry", value: jsonObj.state.dry )
	        sendEvent(name:"water", value: Math.round(jsonObj.properties.depth/12*10))
	        sendEvent(name:"water2", value: state.wdep + ": " + Math.round(jsonObj.properties.depth/12*10))
        }    
        sendEvent(name:"temperature", value: jsonObj.properties.temperature.value)
        sendEvent(name:"temperature2", value: state.temp + ": " + jsonObj.properties.temperature.value)
        sendEvent(name:"relativeHumidity", value: jsonObj.properties.relativeHumidity)
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer == true ? "on" : "off"))
        sendEvent(name:"level", value: jsonObj.properties.targetHumidity)
	    
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
