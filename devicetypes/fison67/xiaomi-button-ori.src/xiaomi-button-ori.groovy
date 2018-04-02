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
	definition (name: "Xiaomi Button Ori", namespace: "fison67", author: "fison67") {
        capability "Sensor"						//"on", "off"
        capability "Button"
        capability "Configuration"
         
        attribute "status", "string"
        attribute "battery", "string"
        
        attribute "lastCheckin", "Date"
        
        command "click"
        command "double_click"
        command "long_click_release"
        command "refresh"
         
	}


	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"button", type: "generic", width: 6, height: 4){
			tileAttribute ("device.button", key: "PRIMARY_CONTROL") {
                attributeState "click", label:'\nButton', icon:"http://postfiles1.naver.net/MjAxODA0MDJfMjQ3/MDAxNTIyNjcwOTc1OTA0.g_GeJwDzpJhau4j0OOi2LzKoT8Qtnlq4sHnGVBnQYHwg.DpHVKVGEZfmefd-tfuz4VnAg5vknwkfA7XDo-_Cow88g.PNG.shin4299/buttonOr_main.png?type=w3", backgroundColor:"#8CB8C9"                
			}
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        valueTile("click", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Button#1_Core \n one_click', action:"click"
        }
        valueTile("double_click", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#2_Core \n double_click", action:"double_click"
        }

        valueTile("long_click_press", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#3_Core \n long_click", action:"long_click_release"
        }
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state "val", label:'${currentValue}%', defaultState: true
        }
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
	}
}


def click() {buttonEvent(1, "pushed")}
def double_click() {buttonEvent(2, "pushed")}
def long_click_press() {buttonEvent(3, "pushed")}

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
    	if(params.data == "click") {
        	buttonEvent(1, "pushed")
        } else if(params.data == "double_click") {
        	buttonEvent(2, "pushed")
        } else if(params.data == "long_click_press") {
        	buttonEvent(3, "pushed")
        }
        else if(params.data == "long_click_release") {
        } else { 
        }
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data)
    	break;
    }
    
    updateLastTime()
}

def buttonEvent(Integer button, String action) {
    sendEvent(name: "button", value: action, data: [buttonNumber: button], descriptionText: "$device.displayName button $button was $action", isStateChange: true)
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

def callback(physicalgraph.device.HubResponse hubResponse){
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

def updated() {
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
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
