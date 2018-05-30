/**
 *  Xiaomi Switch (v.0.0.1)
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
	definition (name: "Xiaomi Switch", namespace: "fison67", author: "fison67") {
        capability "Sensor"						//"on", "off"
         
        attribute "status", "string"
        attribute "battery", "string"
        
        attribute "lastCheckin", "Date"
         
	}


	simulator {
	}

	tiles {
		multiAttributeTile(name:"status", type: "generic", width: 6, height: 4, icon:"st.Home.home30", canChangeIcon: true){
			tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
                attributeState "click", label:'Click', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
            	attributeState "double_click", label:'Double', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
                attributeState "long_click_press", label:'Long', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
            	attributeState "long_click_release", label:'Long End', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
                
                attributeState "alert", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
            	attributeState "flip90", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
                attributeState "flip180", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
            	attributeState "move", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
                attributeState "tap_twice", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
            	attributeState "shake_air", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
                attributeState "free_fall", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
            	attributeState "rotate", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
                
                attributeState "btn0-click", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
            	attributeState "btn0-double_click", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
                attributeState "btn1-click", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
            	attributeState "btn1-double_click", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
            	attributeState "both_click", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
			}
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state "val", label:'${currentValue}', defaultState: true
        }
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
	log.debug "Mi Connector >> ${params.key} : ${params.data}"
 	switch(params.key){
    case "action":
    	sendEvent(name:"status", value: params.data )
    	sendEvent(name:"switch", value: params.data )
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data + "%" )
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        setStatus(jsonObj.state)
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
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
