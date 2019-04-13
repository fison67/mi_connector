/**
 *  Xiaomi Motion (v.0.0.3)
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
	definition (name: "Xiaomi Motion", namespace: "fison67", author: "fison67") {
        capability "Motion Sensor"
        capability "Illuminance Measurement"
        capability "Configuration"
        capability "Sensor"
        capability "Refresh"
         
        attribute "battery", "string"
        attribute "lastMotion", "Date"

        attribute "lastCheckin", "Date"
        
		command "reset"	
        command "chartMotion"
        command "chartIlluminance"
	}


	simulator {
	}
	preferences {
		input "motionReset", "number", title: "Motion Reset Time", description: "", defaultValue:0, displayDuringSetup: true
		input "historyDayCount", "number", title: "Day for History Graph", description: "", defaultValue:1, displayDuringSetup: true
		input "motionHistoryDataMaxCount", "number", title: "Motion Graph Data Max Count", description: "0 is max", defaultValue:100, displayDuringSetup: true
		input "illuminanceHistoryDataMaxCount", "number", title: "Illuminance Graph Data Max Count", description: "0 is max", defaultValue:0, displayDuringSetup: true
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
	log.debug params.data
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
 	switch(params.key){
    case "motion":
        sendEvent(name:"motion", value: (params.data == "true" ? "active" : "inactive") )
        if (settings.motionReset == null || settings.motionReset == "" ) settings.motionReset = 120
        if (params.data == "true" && settings.motionReset > 0) runIn(settings.motionReset, stopMotion)
		if (params.data == "true") sendEvent(name: "lastMotion", value: now, displayed:false)
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data)
    	break;
    case "illuminance":
    	sendEvent(name:"illuminance", value: params.data.replace("lx","").replace(",","") as int )
    	break;
    }
    
    updateLastTime()
}

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
        sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        sendEvent(name:"motion", value: jsonObj.properties.motion == true ? "active" : "inactive")
        
        if(jsonObj.properties.illuminance != null && jsonObj.properties.illuminance != ""){
        	sendEvent(name:"illuminance", value: jsonObj.properties.illuminance.value )
        }
      
        updateLastTime()

    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed:false)
}

def stopMotion() {
   sendEvent(name:"motion", value:"inactive")
}

def reset() {
   sendEvent(name:"motion", value:"inactive")
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

def chartMotion() {
	def url = makeURL("motion", "Motion")
    if(settings.motionHistoryDataMaxCount > 0){
    	url.query.limit = settings.motionHistoryDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "motion")
    }
}

def chartIlluminance() {
	def url = makeURL("illuminance", "Illuminance")
    if(settings.illuminanceHistoryDataMaxCount > 0){
    	url.query.limit = settings.illuminanceHistoryDataMaxCount
    }
    httpGet(url) { response ->
    	processImage(response, "illuminance")
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
