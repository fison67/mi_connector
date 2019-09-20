/**
 *  Xiaomi Cube (v.0.0.2)
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
	definition (name: "Xiaomi Cube", namespace: "fison67", author: "fison67") {
        capability "Sensor"						
        capability "Button"
        capability "Battery"
        capability "Configuration"
		capability "Refresh"
         
        attribute "lastCheckin", "Date"
        
        command "alert"
        command "flip90"
        command "flip180"
        command "move"
        command "tap_twice"
        command "shake_air"
        command "free_fall"
        command "rotate"
	}


	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"button", type: "generic", width: 6, height: 4){
			tileAttribute ("device.button", key: "PRIMARY_CONTROL") {
                attributeState "alert", label:'\nButton', icon:"https://github.com/fison67/mi_connector/blob/master/icons/xiaomi-cube.png?raw=true", backgroundColor:"#8CB8C9"                
			}
            tileAttribute("device.battery", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Battery: ${currentValue}%\n')
            }		
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'\nLast Update: ${currentValue}')
            }
		}
        valueTile("alert", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Button#1_Core \n alert', action:"alert"
        }
        valueTile("flip90", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#2_Core \n flip90", action:"flip90"
        }
        valueTile("flip180", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#3_Core \n flip180", action:"flip180"
        }   
        valueTile("move", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#4_Core \n move", action:"move"
        }    
        valueTile("tap_twice", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#5_Core \n tap_twice", action:"tap_twice"
        }    
        valueTile("shake_air", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#6_Core \n shake_air", action:"shake_air"
        }    
        valueTile("free_fall", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#7_Core \n free_fall", action:"free_fall"
        }    
        valueTile("rotate", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#7_Core \n rotate", action:"rotate"
        }   
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
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
	log.debug "${params.key} >> ${params.data}" + (params.subData != "" ? " (" + params.subData + ")" : "")
    
 	switch(params.key){
    case "action":
        if(params.data == "alert") {
        	buttonEvent(1, "pushed", "Alert")
        } else if(params.data == "flip90") {
        	buttonEvent(2, "pushed", "Flip 90")
        } else if(params.data == "flip180") {
        	buttonEvent(3, "pushed", "Flip 180")
        } else if(params.data == "move") {
        	buttonEvent(4, "pushed", "Move")
        } else if(params.data == "tap_twice") {
        	buttonEvent(5, "pushed", "Tap Twice")
        } else if(params.data == "shake_air") {
        	buttonEvent(6, "pushed", "Shake Air")
        } else if(params.data == "free_fall") {
        	buttonEvent(7, "pushed", "Free Fall")
        }  else if(params.data == "rotate") {
        	buttonEvent(8, "pushed", "Rotate")
            sendEvent(name:"rotate", value: params.subData as int, descriptionText: "Cube is rotated " + params.subData + " degrees." )
        }
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data)
    	break;
    }
    
    updateLastTime()
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
        	"HOST": parent._getServerURL(),
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

def buttonEvent(Integer button, String action, String realAction) {
    sendEvent(name: "button", value: action, data: [buttonNumber: button], descriptionText: "$device.displayName button $button was $action ($realAction)", isStateChange: true)
}

def alert() {buttonEvent(1, "pushed", "Alert By Click")}
def flip90() {buttonEvent(2, "pushed", "Flip 90 By Click")}
def flip180() {buttonEvent(3, "pushed", "Flip 180 By Click")}
def move() {buttonEvent(4, "pushed", "Move By Click")}
def tap_twice() {buttonEvent(5, "pushed", "Tap Twice By Click")}
def shake_air() {buttonEvent(6, "pushed", "Shake Air By Click")}
def free_fall() {buttonEvent(7, "pushed", "Free Fall By Click")}
def rotate() {buttonEvent(8, "pushed", "Rotate By Click")}
