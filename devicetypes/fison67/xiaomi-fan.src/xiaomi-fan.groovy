/**
 *  Xiaomi Fan(v.0.0.2)
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
        "English": "Tem"
    ],
    "hum": [
        "Korean": "습도",
        "English": "Humi"
    ],
    "buz": [
        "Korean": "부저음",
        "English": "Buzzer"
    ],
    "angle": [
        "Korean": "회전",
        "English": "Ang"
    ],
    "mode": [
        "Korean": "모드",
        "English": "Mode"
    ],
    "con": [
        "Korean": "회전 각도",
        "English": "Control Angle"
    ],
    "direc": [
        "Korean": "좌우 이동",
        "English": "Direction"
    ]
]

metadata {
	definition (name: "Xiaomi Fan", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Switch Level"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Refresh"
		capability "Sensor"
		capability "Battery"
		capability "Timed Session"
		capability "Power Source"
         
        attribute "buzzer", "string"
        attribute "anglelevel", "string"
        attribute "ledBrightness", "string"
        attribute "speedlevel", "string"
        attribute "processTimer", "number"
        attribute "fanspeedstep", "enum", ["low", "medium", "high", "strong"]        
        attribute "setangle", "enum", ["off", "on", "30", "60", "90", "120"]        
        attribute "settimer", "enum", ["off", "15", "30", "60", "90", "120"]        
        attribute "setdirection", "enum", ["left", "right"]        
        attribute "fanmode", "enum", ["general", "natural"]        
        attribute "naturalLevel", "string"
        attribute "speed", "string"
        attribute "powerOffTime", "string"
        attribute "childLock", "string"
        attribute "setTimeRemaining", "number"
        
        attribute "lastCheckin", "Date"
         
        command "setTimeRemaining"
        command "multiatt"
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
        command "setdirectionfault"
        command "setMoveLeft"
        command "setMoveRight"
        command "settimeroff"
	}


	simulator {
	}
	preferences {
        input name: "selectedLang", title:"Select a language" , type: "enum", required: true, options: ["English", "Korean"], defaultValue: "English", description:"Language for DTH"
        
		input name: "historyDayCount", type:"number", title: "Day for History Graph", description: "", defaultValue:1, displayDuringSetup: true
		input name: "historyTotalDayCount", type:"number", title: "Total Day for History Graph", description: "0 is max", defaultValue:7, range: "2..7", displayDuringSetup: true
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
    
 	switch(params.key){
    case "temperature":
		def para = "${params.data}"
		String data = para
		def st = data.replace("C","");
		def stf = Float.parseFloat(st)
		int tem = Math.round(stf)
	state.currenttemp = tem
	multiatt()
    	break;
    case "relativeHumidity":
        state.currenthumi = params.data
        multiatt()
    	break;
    case "angleLevel":
        state.currentangle = params.data
        multiatt()
    	break;        
    case "speedLevel":
        sendEvent(name:"level", value: params.data)
		def para = params.data
		String data = para
		def stf = Float.parseFloat(data)
		int tem = Math.round((stf+12)/25)        
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
			sendEvent(name:"level", value: para)
        }
    	break;        
    case "angleEnable":
        sendEvent(name:"setangle", value: params.data)
        sendEvent(name:"setdirection", value: params.data)
    	break;        
        
    case "fanNatural":
        sendEvent(name:"level", value: params.data)
    	break;        
    case "acPower":
    	state.acPower = (params.data == "on" ? "☈: " : "✕: ")
	multiatt()
    	break;        
    case "batteryLevel":
		state.batteryLe = params.data	
        sendEvent(name:"battery", value: params.data)
		multiatt()
    	break;
    case "power":
    	state.power = (params.data == "true" ? "on" : "off")
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "buzzer":
    	sendEvent(name:"buzzer", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "ledBrightness":
    	sendEvent(name:"ledBrightness", value: params.data )
    	break;
    }
}
//----------------------------
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

def msToTime(duration) {
    def seconds = (duration%60).intValue()
    def minutes = ((duration/60).intValue() % 60).intValue()
    def hours = ( (duration/(60*60)).intValue() %24).intValue()

    hours = (hours < 10) ? "0" + hours : hours
    minutes = (minutes < 10) ? "0" + minutes : minutes
    seconds = (seconds < 10) ? "0" + seconds : seconds

    return hours + ":" + minutes + ":" + seconds
}

def timter(){
	if(state.timerCount > 0){
    	state.timerCount = state.timerCount - 30;
        if(state.timerCount <= 0){
        	if(state.power == "on"){
        		off()
            }
        }else{
        	runIn(30, timter)
        }
        updateTimer()
    }
}

def updateTimer(){
    def timeStr = msToTime(state.timerCount)
	log.debug "Left time >> ${timeStr}"
    sendEvent(name:"leftTime", value: "${timeStr}")
    sendEvent(name:"timeRemaining", value: Math.round(state.timerCount/60))
}

def processTimer(second){
	if(state.timerCount == null){
    	state.timerCount = second;
    	runIn(30, timter)
    }else if(state.timerCount == 0){
		state.timerCount = second;
    	runIn(30, timter)
    }else{
    	state.timerCount = second
    }
    log.debug "Time >> ${state.timerCount}"
    updateTimer()
}

def stop() { 
	unschedule()
	log.debug "Timer Off"
	state.timerCount = 0
	updateTimer()
}
def setTimeRemaining(time) { 
	log.debug "Timer ${time}Min >> ${state.timerCount}"
    processTimer(time * 60)
}


def setLevel(level){
	log.debug "setFanSpeed >> ${state.id}"
    state.fanSpeed = level
	def currentState = device.currentValue("fanmode")    
    if(currentState =="natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": level
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
    else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": level
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
    sendEvent(name:"speedlevel", value: 1)
    state.fanSpeed = 25
    if(currentState == "natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": state.fanSpeed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	} else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": state.fanSpeed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
}

def setFanSpeed2(){
	log.debug "setFanstep2 >> ${state.id}"
    sendEvent(name:"speedlevel", value: 2)
    state.fanSpeed = 50
	def currentState = device.currentValue("fanmode")    
    if(currentState =="natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": state.fanSpeed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
    else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": state.fanSpeed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
}

def setFanSpeed3(){
	log.debug "setFanstep3 >> ${state.id}"
	def currentState = device.currentValue("fanmode")    
    sendEvent(name:"speedlevel", value: 3)
    state.fanSpeed = 75
    if(currentState =="natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": state.fanSpeed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
    else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": state.fanSpeed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
}

def setFanSpeed4(){
	log.debug "setFanstep4 >> ${state.id}"
	def currentState = device.currentValue("fanmode")    
    sendEvent(name:"speedlevel", value: 4)
    state.fanSpeed = 100
    if(currentState =="natural"){
    	def body = [
        	"id": state.id,
        	"cmd": "fanNatural",
        	"data": state.fanSpeed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
    else {
    	def body = [
        	"id": state.id,
        	"cmd": "fanSpeed",
        	"data": state.fanSpeed
    	]
    	def options = makeCommand(body)
    	sendCommand(options, null)
	}
}

def generalOn(){
	log.debug "generalOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanSpeed",
        "data": state.fanSpeed == null ? 25 : state.fanSpeed
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def naturalOn(){
	log.debug "naturalOn >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "fanNatural",
        "data": state.fanSpeed == null ? 25 : state.fanSpeed
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
    refresh()
    setLanguage(settings.selectedLang)
}

def setLanguage(language){
    log.debug "Languge >> ${language}"
	state.language = language
	state.hum = LANGUAGE_MAP["hum"][language]
	state.temp = LANGUAGE_MAP["temp"][language]
	state.angle = LANGUAGE_MAP["angle"][language]
	
	sendEvent(name:"mode_label", value: LANGUAGE_MAP["mode"][language] )
	sendEvent(name:"rotation_label", value: LANGUAGE_MAP["angle"][language] )
	sendEvent(name:"buzzer_label", value: LANGUAGE_MAP["buz"][language] )
	sendEvent(name:"angle_label", value: LANGUAGE_MAP["con"][language] )
	sendEvent(name:"head_label", value: LANGUAGE_MAP["direc"][language] )
}

def setdirectionfault() {
}

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        state.currenthumi = jsonObj.properties.relativeHumidity == null ? "" : jsonObj.properties.relativeHumidity
        int temp = jsonObj.properties.temperature.value
        state.currenttemp = temp
        state.currentangle = jsonObj.properties.angleLevel
        state.acPower = (jsonObj.properties.acPower == "on" ? "☈: " : "✕: ") 
        state.batteryLe = jsonObj.state.batteryLevel == null ? "" : jsonObj.state.batteryLevel
        sendEvent(name:"setangle", value: jsonObj.properties.angleEnable)
        sendEvent(name:"setdirection", value: jsonObj.properties.angleEnable)
        sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
        sendEvent(name:"buzzer", value: (jsonObj.state.buzzer == true ? "on" : "off"))
        sendEvent(name:"ledBrightness", value: jsonObj.state.ledBrightness)
        sendEvent(name:"battery", value: state.batteryLe)
	if( jsonObj.properties.naturalLevel > 0 ) {
		sendEvent(name:"fanmode", value: "natural")
		String data = jsonObj.properties.naturalLevel
		def stf = Float.parseFloat(data)
		def tem = Math.round((stf+12)/25)        
        sendEvent(name:"speedlevel", value: tem)        
        sendEvent(name:"level", value: jsonObj.properties.naturalLevel)
	} else {
		sendEvent(name:"fanmode", value: "general")
		String data = jsonObj.properties.speedLevel
		def stf = Float.parseFloat(data)
		def tem = Math.round((stf+12)/25)        
        sendEvent(name:"speedlevel", value: tem)        
        sendEvent(name:"level", value: jsonObj.properties.speedLevel)
	}
        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        sendEvent(name: "lastCheckin", value: now)
	multiatt()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def multiatt(){
    sendEvent(name:"lastCheckin", value: state.temp +": " + state.currenttemp + "° " + state.hum + ": " + state.currenthumi + "% " + state.angle + ": " + state.currentangle + "°" + " AC" + state.acPower + state.batteryLe + "%")
//	for new smartthings app	
	sendEvent(name:"temperature", value: state.currenttemp, unit: "C")
	sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity == null ? "" : jsonObj.properties.relativeHumidity)
	sendEvent(name:"battery", value:state.batteryLe == null ? "" : state.batteryLe)
	sendEvent(name:"powerSource", value: (state.acPower == "☈: " ? "dc" : "battery"))
}
def sendCommand(options, _callback){
	def myhubAction = new hubitat.device.HubAction(options, null, [callback: _callback])
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


def makeURL(type, name){
	def sDate
    def eDate
	use (groovy.time.TimeCategory) {
      def now = new Date()
      def day = settings.historyDayCount == null ? 1 : settings.historyDayCount
      sDate = (now - day.days).format( 'yyyy-MM-dd HH:mm:ss', location.timeZone )
      eDate = now.format( 'yyyy-MM-dd HH:mm:ss', location.timeZone )
    }
	return [
        uri: "http://${state.externalAddress}",
        path: "/devices/history/${state.id}/${type}/${sDate}/${eDate}/image",
        query: [
        	"name": name
        ]
    ]
}

def makeTotalURL(type, name){
	def sDate
    def eDate
	use (groovy.time.TimeCategory) {
      def now = new Date()
      def day = (settings.historyTotalDayCount == null ? 7 : settings.historyTotalDayCount) - 1
      sDate = (now - day.days).format( 'yyyy-MM-dd', location.timeZone )
      eDate = (now + 1.days).format( 'yyyy-MM-dd', location.timeZone )
    }
	return [
        uri: "http://${state.externalAddress}",
        path: "/devices/history/${state.id}/${type}/${sDate}/${eDate}/total/image",
        query: [
        	"name": name
        ]
    ]
}

def processImage(response, type){
	if (response.status == 200 && response.headers.'Content-Type'.contains("image/png")) {
        def imageBytes = response.data
        if (imageBytes) {
            try {
                storeImage(getPictureName(type), imageBytes)
            } catch (e) {
                log.error "Error storing image ${name}: ${e}"
            }
        }
    } else {
        log.error "Image response not successful or not a jpeg response"
    }
}

private getPictureName(type) {
  def pictureUuid = java.util.UUID.randomUUID().toString().replaceAll('-', '')
  return "image" + "_$pictureUuid" + "_" + type + ".png"
}

def chartTotalTemperature() {
    httpGet(makeTotalURL("temperature", "Temperature")) { response ->
    	processImage(response, "temperature")
    }
}

def chartTotalHumidity() {
    httpGet(makeTotalURL("relativeHumidity", "Humidity")) { response ->
    	processImage(response, "humidity")
    }
}

def chartTemperature() {
    httpGet(makeURL("temperature", "Temperature")) { response ->
    	processImage(response, "temperature")
    }
}

def chartHumidity() {
    httpGet(makeURL("relativeHumidity", "Humidity")) { response ->
    	processImage(response, "humidity")
    }
}
