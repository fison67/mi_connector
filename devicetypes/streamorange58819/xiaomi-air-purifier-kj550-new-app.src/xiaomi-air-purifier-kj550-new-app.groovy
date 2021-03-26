/**
 *  Xiaomi Air Purifier KJ550(v.0.0.1)
 *
 * MIT License
 *
 * Copyright (c) 2021 fison67@nate.com
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
	definition (name: "Xiaomi Air Purifier KJ550 New App", namespace: "streamorange58819", author: "fison67", mnmn:"fison67", ocfDeviceType:"oic.d.airpurifier") {
        capability "Switch"						
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Filter Status"
		capability "Fan Speed"
        capability "streamorange58819.anion"
		capability "streamorange58819.pmode"
        capability "streamorange58819.buzzer"
		capability "streamorange58819.led"
		capability "Refresh"
		capability "Sensor"
		capability "Dust Sensor" // fineDustLevel : PM 2.5   dustLevel : PM 10
               
        attribute "f1_hour_used", "number"
        attribute "filter1_life", "number"
        attribute "average_aqi", "number"     
        
        command "setLevel", ["number"]
        command "setStatus"        
	}

	simulator {}
	preferences {}
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

def setModel(model){
	state.model = model
	refresh()
}

def getModeValue(str){
	if(str == "auto"){
    	return 0
    }else if(str == "sleep"){
    	return 1
    }else if(str == "favorite"){
    	return 2
    }
}

def getModeStr(val){
	if(val == 0){
    	return "auto"
    }else if(val == 1){
    	return "sleep"
    }else if(val == 1){
    	return "favorite"
    }
}

def setStatus(params){
    log.debug "${params.key} : ${params.data}"
 
 	switch(params.key){
    case "mode":
        sendEvent(name:"pmode", value: getModeStr(params.data as int) )
    	break;
    case "pm2.5":
    	sendEvent(name:"fineDustLevel", value: params.data as int, unit:"μg/m^3")
    	break;
    case "relativeHumidity":
    	sendEvent(name:"humidity", value: params.data, unit:"%")
    	break;
    case "power":
    	sendEvent(name:"switch", value: params.data == "true" ? "on" : "off")
    	break;
    case "temperature":
        sendEvent(name:"temperature", value: Math.round(Float.parseFloat(params.data.replace("C",""))*10)/10, unit: "C" )
    	break;
    case "buzzer":
        sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off"))
    	break;;
    case "anion":
        sendEvent(name:"anion", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "fanLevel":
        sendEvent(name:"fanSpeed", value: calFanSpeed(params.data as int))
    	break;
    case "led":
        sendEvent(name:"led", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "filterLeft":
        sendEvent(name:"filterStatus", value: (params.data as int) == 0 ? "replace" : "normal")
    	break;
    }
}

def refresh(){
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

def calFanSpeed(level){
	if(level == 0){
    	return 0
	}else if(0 < level && level <= 25){
    	return 1
    }else if(25 < level && level <= 50){
    	return 2
    }else if(50 < level && level <= 75){
    	return 3
    }else if(75 < level && level <= 100){
    	return 4
    }
}

def calFanLevel(speed){
	if(speed == 0){
    	return 0
    }else if(speed == 1){
    	return 25
    }else if(speed == 2){
    	return 50
    }else if(speed == 3){
    	return 75
    }else if(speed == 4){
    	return 100
    }
}

def setFanSpeed(level){
    setLevel(calFanLevel(level))
}

def setLevel(level){
    sendCommandData("fanLevel", level)
}

def on(){ sendCommandData("power", "on") }
def off(){ sendCommandData("power", "off") }

def setBuzzer(power){ sendCommandData("buzzer", power) }
def buzzerOn(){ sendCommandData("buzzer", "on") }
def buzzerOff(){ sendCommandData("buzzer", "off") }

def setAnion(power){ sendCommandData("anion", power) }
def anionOn(){ sendCommandData("anion", "on") }
def anionOff(){ sendCommandData("anion", "off") }

def setAirPurifierMode(mode){ sendCommandData("changeMode", getModeValue(mode)) }

def setLed(power){ sendCommandData("led", power) }
def ledOn(){ sendCommandData("led", "on") }
def ledOff(){ sendCommandData("led", "off") }

def sendCommandData(cmd, data){
	def body = [
        "id": state.id,
        "cmd": cmd,
        "data": data
    ]
    sendCommand(makeCommand(body), null)
}

def installed(){
	sendEvent(name:"switch" , value:"off")
    sendEvent(name:"dustLevel", value: null)
    sendEvent(name:"airPurifierFanMode", value: "auto" )
    sendEvent(name:"supportedPmodes", value: ["auto", "sleep", "favorite"])
}

def updated() {}

def setExternalAddress(address){
	state.externalAddress = address
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
        if(jsonObj.properties["relativeHumidity"] != null){
         	sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity, unit: "%")
        }
        
        if(jsonObj.properties["temperature"] != null){
            sendEvent(name:"temperature", value: jsonObj.properties.temperature.value, unit: "C" )
        }
        if(jsonObj.properties["pm2.5"] != null && jsonObj.properties["pm2.5"] != ""){
            sendEvent(name:"fineDustLevel", value: jsonObj.properties["pm2.5"], unit:"μg/m^3")
        }
        if(jsonObj.properties["mode"] != null && jsonObj.properties["mode"] != ""){
            sendEvent(name:"pmode", value: getModeStr(jsonObj.properties["mode"]))
        }
        
		if(jsonObj.properties.fanLevel != null){
            sendEvent(name:"fanSpeed", value: calFanSpeed(jsonObj.properties.fanLevel))
        }
        
		sendEvent(name:"led", 	 value: jsonObj.properties.led == true ? "on" : "off")
		sendEvent(name:"anion", value: jsonObj.properties.anion == true ? "on" : "off")
		sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
        sendEvent(name:"buzzer", value: jsonObj.state.buzzer == true ? "on" : "off")
        
        if(jsonObj.state.filterLeft != null && jsonObj.state.filterLeft != ""){
        	sendEvent(name:"filterStatus", value: jsonObj.state.filterLeft == 0 ? "replace" : "normal")
        }

        
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
