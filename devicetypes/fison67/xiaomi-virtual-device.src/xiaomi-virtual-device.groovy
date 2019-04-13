/**
 *  Xiaomi Virtual Device(v.0.0.1)
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
	definition (name: "Xiaomi Virtual Device", namespace: "fison67", author: "fison67") {
        capability "Sensor"
        capability "Presence Sensor"			//"present", "not present"
               
        attribute "lastCheckin", "Date"
        attribute "lastPresent", "Date"
        attribute "lastNotPresent", "Date"
        
	}

	simulator {
	}
    
    preferences {
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

def setExternalAddress(address){
	log.debug "External Address >> ${address}"
	state.externalAddress = address
}

def setStatus(params){
	log.debug "${params.key} : ${params.data}"
    
 	switch(params.key){
    case "presence":
        if(params.data == "true"){
            sendEvent(name:"presence", value: "present" )
            sendEvent(name:"lastPresent", value: now() )
        } else {
            sendEvent(name:"presence", value: "not present" )
            sendEvent(name:"lastNotPresent", value: now() )
        }		
    	break;
    }
    
    updateLastTime()
}

def updated() {
}

def updateLastTime(){
    sendEvent(name: "lastCheckin", value: now())
}

def now(){
	return new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
}
