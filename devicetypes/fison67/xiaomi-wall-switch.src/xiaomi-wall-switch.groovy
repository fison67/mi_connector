/**
 *  Xiaomi Wall Switch (v.0.0.1)
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
	definition (name: "Xiaomi Wall Switch", namespace: "fison67", author: "fison67") {
        capability "Switch"						
         
        attribute "switch", "string"
        
        attribute "lastCheckin", "Date"
        
        command "on"
        command "off"
        command "refresh"

	}

	simulator { }

}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setInfo(String app_url, String id, String index) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
    state.deviceIndex = index
}

def setStatus(params){
    log.debug "${params.key} >> ${params.data}"
 
 	switch(params.key){
    case "power":
 		def power = params.data
    	sendEvent(name:"switch", value: (power == "true" ? "on" : "off") )
    	break;
    }
    
    updateLastTime()
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def on(){
    log.debug "On >> ${state.id} >> ${state.deviceIndex}"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on",
        "index": state.deviceIndex
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "Off >> ${state.id} >> ${state.deviceIndex}"
	def body = [
        "id": state.id,
        "cmd": "power",
        "data": "off",
        "index": state.deviceIndex
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def refresh(){
	log.debug "Refresh"
    def options = [
     	"method": "GET",
        "path": "/devices/get/${state.id}:${state.deviceIndex}",
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
     	sendEvent(name:"switch", value: (jsonObj.state.power == "true" ? "on" : "off") )
        
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
