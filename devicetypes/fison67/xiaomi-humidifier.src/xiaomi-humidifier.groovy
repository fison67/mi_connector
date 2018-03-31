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

metadata {
	definition (name: "Xiaomi Humidifier", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Refresh"
		capability "Sensor"
         
        attribute "mode", "enum", ["auto", "silent", "medium", "high"]
        attribute "buzzer", "enum", ["on", "off"]
        attribute "ledBrightness", "enum", ["off", "dim", "bright"]
        attribute "water", "string"
        attribute "use_time", "string"
        attribute "dry", "string"
        
        attribute "lastCheckin", "Date"
         
        command "on"
        command "off"
        
        command "setModeOn"
        command "setModeAuto"
        command "setModeSilent"
        command "setModeHigh"
        command "setModeMedium"
        
        command "buzzerOn"
        command "buzzerOff"
        
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
                attributeState "auto", label:'\nAuto Mode', action:"setModeSilent", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ1/MDAxNTIyMTUxNzIxMTk5.LTiuV1QSyPu6WgMB3uR7Bc-Hy19Uwgard5XKG5jj1JIg.XpdiwfmUg3Rz6IgIWyamtsrYeW0BJRqj28XyHRuADA0g.PNG.shin4299/Humi_tile_auto.png?type=w580", backgroundColor:"#73C1EC", nextState:"modechange"
                attributeState "silent", label:'\nSilent Mode', action:"setModeMedium", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTE2/MDAxNTIyMTUxNzIxMTE3.wVp36he9l0up0SalfSgNMOff9y_U9F2wyPc5AfmK-nEg.coHcd4mj2byTBFzTWnc4yjKi7xbJb7QhfgBn9ASt5eUg.PNG.shin4299/Humi_tile_1.png?type=w580", backgroundColor:"#6eca8f", nextState:"modechange"
                attributeState "medium", label:'\nMedium Mode', action:"setModeHigh", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjEz/MDAxNTIyMTUxNzIxMTk4.VfHSHeU2sS9J-T03zqc_sSjgO4ifOxiyBtGorUPxD2kg.dnC3xCu45F_153OJfUm0Pd1_HAWFp9DWVGHLagDqOSgg.PNG.shin4299/Humi_tile_2.png?type=w580", backgroundColor:"#FFDE61", nextState:"modechange"
                attributeState "high", label:'\nHigh Mode', action:"setModeAuto", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTA5/MDAxNTIyMTUxNzIxMTk3.r9waU9A5WmDgRz6p6eiGYTl67F1jo5HGcurD9i57Mj0g.a1R4bIefNK0gT-NdDFmYveohdkXxUuRgJIszH9Q38Ogg.PNG.shin4299/Humi_tile_3.png?type=w580", backgroundColor:"#ff9eb2", nextState:"modechange"
                
                attributeState "modechange", label:'\n${name}', icon:"st.quirky.spotter.quirky-spotter-motion", backgroundColor:"#C4BBB5"
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTQ2/MDAxNTIyMTUxNzIxMTk3.xeCR1k4pk0vDOozb43Lfo6g2fMC1a_VJFUpTQ071XRUg.dyhFTAUaCwWPUYc4hPUdGiuUI5yeRJ4QpP3kX802AlIg.PNG.shin4299/Humi_tile_off.png?type=w580", backgroundColor:"#C4BBB5", nextState:"off"
			}
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
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



        valueTile("temp_label", "", decoration: "flat") {
            state "default", label:'Temperature'
        }
        valueTile("humi_label", "", decoration: "flat") {
            state "default", label:'Humidity'
        }
        valueTile("water_label", "", decoration: "flat") {
            state "default", label:'Water'
        }

        valueTile("temperature", "device.temperature") {
            state("val", label:'${currentValue}°', defaultState: true, 
            	backgroundColors:[
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
        
        valueTile("water", "device.water") {
            state("val", label:'${currentValue}%', defaultState: true, 
            	backgroundColors:[
                    [value: 20, color: "#b3888c"],
                    [value: 20, color: "#d0d6e9"],
                    [value: 40, color: "#b8c2de"],
                    [value: 60, color: "#8a9ac8"],
                    [value: 80, color: "#5b71b2"],
                    [value: 100, color: "#2c499c"],
                ]
        	)
        }
        
        valueTile("buzzer_label", "", decoration: "flat") {
            state "default", label:'부저음'
        }        
        valueTile("led_label", "", decoration: "flat") {
            state "default", label:'LED'
        }        
        valueTile("time_label", "", decoration: "flat") {
            state "default", label:'사용 \n시간'
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
        }         
        valueTile("use_time", "device.use_time") {
            state("val", label:'${currentValue}', defaultState: true
        	)
        }
        standardTile("dry", "device.dry", width: 2, height: 2, canChangeIcon: true) {
            state "dry", label: 'Dry',  backgroundColor: "#00a0dc"
            state "noDry", label: 'No Dry', backgroundColor: "#ffffff"
        }
   	main (["modem"])
	details(["mode", "switch", "auto_label", "silent_label", "medium_label", "high_label", "mode1", "mode2", "mode3", "mode4", 
    		 "temp_label", "humi_label", "water_label", "buzzer_label", 
             "led_label", "time_label", "temperature", "humidity", "water", "buzzer", "ledBrightness", "use_time"])


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
    	sendEvent(name:"humidity", value: params.data + "%")
    	break;
    case "mode":
    	if(params.data == "auto") {
    	sendEvent(name:"mode", value: "auto" )
        }
//        else if(params.data == "idle") {
//    	sendEvent(name:"mode", value: "auto" )
//        }
        else if(params.data == "silent") {
    	sendEvent(name:"mode", value: "silent" )
        }
        else if(params.data == "medium") {
    	sendEvent(name:"mode", value: "medium" )
        }
        else if(params.data == "high") {
    	sendEvent(name:"mode", value: "high" )
        }
        else { }
    	break;
    case "power":
    	if(params.data == "true") {
    	sendEvent(name:"switch", value:"on")
        }
        else if(params.data == "false") {
    	sendEvent(name:"mode", value: "off")
    	sendEvent(name:"switch", value:"off")
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
    	break;
    case "ledBrightness":
        sendEvent(name:"ledBrightness", value: params.data)
    	break;        
    case "limit_hum":
        sendEvent(name:"limit_hum", value: params.data)
    	break;
    case "depth":
		def stf = Float.parseFloat(params.data)
		def water = Math.round(stf)    
	//	def water = Math.round(stf/12*10)    
        sendEvent(name:"water", value: water )
    	break;
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off") )
        break;
    case "dry":
    	sendEvent(name:"dry", value: (params.data == "" ? "noDry" : "dry") )
        break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
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
	log.debug "setDim >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "ledBrightness",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}


def setModeAuto(){
    log.debug "setModeSilent >> ${state.id}"
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
