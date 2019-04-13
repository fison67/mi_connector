/**
 *  Xiaomi Power Plug (v.0.0.2)
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
	definition (name: "Xiaomi Power Plug", namespace: "fison67", author: "fison67") {
        capability "Actuator"
        capability "Switch"
        capability "Power Meter"
        capability "Energy Meter"
        capability "Configuration"
        capability "Refresh"
        capability "Sensor"
        capability "Outlet"
        
        attribute "Volt", "string"
        attribute "temp", "string"
        attribute "lastCheckin", "Date"
        
        command "chartPower"
        command "chartPowerMeter"
        command "chartEnergyMeter"
	}

	simulator { }
    
    preferences {
		input "historyDayCount", "number", title: "Day for History Graph", description: "", defaultValue:1, displayDuringSetup: true
		input "historyPowerDataMaxCount", "number", title: "Power Graph Data Max Count", description: "0 is max", defaultValue:100, displayDuringSetup: true
		input "historyPowerLoadDataMaxCount", "number", title: "PowerLoad Graph Data Max Count", description: "0 is max", defaultValue:0, displayDuringSetup: true
		input "historyPowerConsumedDataMaxCount", "number", title: "Power Graph Data Max Count", description: "0 is max", defaultValue:0, displayDuringSetup: true   
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setExternalAddress(address){
	log.debug "External Address >> ${address}"
	state.externalAddress = address
}

def setInfo(String app_url, String id) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
}

def setStatus(params){
    log.debug "${params.key} >> ${params.data}"
 
 	switch(params.key){
    case "power":
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off"))
    	break;
    case "powerLoad":
    	sendEvent(name:"power", value: params.data.replace(" w", ""))
    	break;
    case "loadVoltage":
    	sendEvent(name:"powerVolt", value: params.data)
    	break;
    case "powerConsumed":
		def para = "${params.data}"
		String data = para
		def st = data.replace(" J","").replace(",","")
		def stf = Float.parseFloat(st)
		def powerc = Math.round(stf)/1000
    	sendEvent(name:"energyMeter", value: powerc)
    	break;
    }
    
    updateLastTime()
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

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
		log.debug jsonObj
        
		try{ sendEvent(name:"power", value: jsonObj.properties.powerLoad.value) }catch(err){}
    //    try{ sendEvent(name:"battery", value: jsonObj.properties.batteryLevel) }catch(err){}
        try{ sendEvent(name:"energyMeter", value: jsonObj.properties.powerConsumed.value/1000)  }catch(err){}
        
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {
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

def chartPower() {
	def url = makeURL("power", "Power")
    if(settings.historyPowerDataMaxCount > 0){
    	url.query.limit = settings.historyPowerDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "power")
    }
}

def chartPowerMeter(){
	def url = makeURL("powerLoad", "PowerLoad")
    if(settings.historyPowerLoadDataMaxCount > 0){
    	url.query.limit = settings.historyPowerLoadDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "powerLoad")
    }
}

def chartEnergyMeter(){
	def url = makeURL("powerConsumed", "PowerConsumed")
    if(settings.historyPowerConsumedDataMaxCount > 0){
    	url.query.limit = settings.historyPowerConsumedDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "powerConsumed")
    }
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
