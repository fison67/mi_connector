/**
 *  Xiaomi Switch (v.0.0.2)
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
	definition (name: "Xiaomi Button AQ", namespace: "fison67", author: "fison67") {
        capability "Sensor"						//"on", "off"
        capability "PushableButton"
        capability "DoubleTapableButton"
		capability "HoldableButton"
		capability "ReleasableButton"
        capability "Battery"
		capability "Refresh"
               
        attribute "lastCheckin", "Date"
        
        command "click"
        command "double_click"
        command "long_click"
        command "long_click_release"
		
	}

}

def click() {
	sendEvent(name:"pushed", value:1, isStateChange: true, descriptionText: "Click")
}

def double_click() {
	sendEvent(name:"doubleTapped", value:1, isStateChange: true, descriptionText: "Double Click")
}

def long_click(){
	sendEvent(name:"held", value:1, isStateChange: true, descriptionText: "Long Click")
}

def long_click_release(){
	sendEvent(name:"released", value:1, isStateChange: true, descriptionText: "Release")
}



// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setInfo(String app_url, String id) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
    
    sendEvent(name:"numberOfButtons", value: 1)
}

def setStatus(params){
	log.debug "Mi Connector >> ${params.key} : ${params.data}"
 	switch(params.key){
    case "action":
    	if(params.data == "click") {
            click()
        } else if(params.data == "double_click") {
            double_click()
        } else if(params.data == "long_click_press") {
        	long_click()
        } else if(params.data == "long_release_press") {
        	long_click_release()
        }
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data ) 
    	break;
    }
    
    updateLastTime()
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
        
        sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, )
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
