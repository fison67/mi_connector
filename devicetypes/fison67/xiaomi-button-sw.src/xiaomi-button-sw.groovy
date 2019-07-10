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
	definition (name: "Xiaomi Button SW", namespace: "fison67", author: "fison67", mnmn:"SmartThings", vid: "generic-switch", ocfDeviceType: "oic.d.switch") {
        capability "Sensor"						
        capability "Button"
        capability "Battery"
        
        attribute "lastCheckin", "Date"
        
        command "Lclick"
        command "Rclick"
        command "both_click"
	}


	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"button", type: "generic", width: 6, height: 4){
			tileAttribute ("device.button", key: "PRIMARY_CONTROL") {
                attributeState "click", label:'\nButton', icon:"http://postfiles9.naver.net/MjAxODA0MDJfOSAg/MDAxNTIyNjcwOTc2MTcx.Eq3RLdNXT6nbshuDgjG4qbfMjCob8eTjYv6fltmg7Zcg.1W8CkaPojCBp07iCYi5JYkJl5YTWxQL5aDG-TQ0XF_kg.PNG.shin4299/buttonSW_main.png?type=w3", backgroundColor:"#8CB8C9"
			}
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
        valueTile("btn0-click", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Button#1_Core \n Left_click', action:"Lclick"
        }
        valueTile("btn1-click", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#2_Core \n Right_click", action:"Rclick"
        }
        valueTile("both_click", "device.button", decoration: "flat", width: 2, height: 2) {
            state "default", label:"Button#3_Core \n Both_click", action:"both_click"
        }

        valueTile("battery", "device.battery", width: 2, height: 2) {
            state "val", label:'${currentValue}%', defaultState: true
        }
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
	}
}


def Lclick() {buttonEvent(1, "pushed")}
def Rclick() {buttonEvent(2, "pushed")}
def both_click() {buttonEvent(3, "pushed")}


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
    	if(params.data == "btn0-click") {
        	buttonEvent(1, "pushed")
        } else if(params.data == "btn1-click") {
       	 	buttonEvent(2, "pushed")
        } else if(params.data == "both_click") {
        	buttonEvent(3, "pushed")
        } else if(params.data == "btn0-long_click") {
        	buttonEvent(4, "pushed")
        } else if(params.data == "btn1-long_click") {
        	buttonEvent(5, "pushed")
        } else if(params.data == "btn0-double_click") {
        	buttonEvent(6, "pushed")
        } else if(params.data == "btn1-double_click") {
        	buttonEvent(7, "pushed")
        } else if(params.data == "long_both_click") {
        	buttonEvent(8, "pushed")
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

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def updated() {}
