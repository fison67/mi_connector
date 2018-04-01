/**
 *  Xiaomi Light (v.0.0.1)
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
	definition (name: "Xiaomi Light Mono", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch Level"
        capability "Health Check"
        capability "Light"

         
        attribute "switch", "string"
        attribute "color", "string"
        attribute "brightness", "string"
        
        attribute "lastCheckin", "Date"
         
        command "localOn"
        command "localOff"
        command "on"
        command "off"
        
        command "setColor"
        command "setBrightness"
        
	}


	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTU5/MDAxNTIyMTUzOTk3MTgy.j2vWDdDUen5w1lVKthaUgjRTk8EU0X1DTzLkIurRAyMg.Me30JNZPejyeC_GrQ1rffZvzaiUWYxHjLCyVkMjGCHYg.PNG.shin4299/Yeelight_mo_tile_on.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'\n${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfNTAg/MDAxNTIyMTUzOTk3MTcy.XnqpwsxugesLmLzedrSispSEYs8dm3M18Y_UAx5M1icg.XC0qikMbzjwWsl_gQnSoQUnFwzsT78q-9rihSuWvVGEg.PNG.shin4299/Yeelight_mo_tile_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'\n${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfNTAg/MDAxNTIyMTUzOTk3MTcy.XnqpwsxugesLmLzedrSispSEYs8dm3M18Y_UAx5M1icg.XC0qikMbzjwWsl_gQnSoQUnFwzsT78q-9rihSuWvVGEg.PNG.shin4299/Yeelight_mo_tile_off.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'\n${name}', action:"switch.ofn", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTU5/MDAxNTIyMTUzOTk3MTgy.j2vWDdDUen5w1lVKthaUgjRTk8EU0X1DTzLkIurRAyMg.Me30JNZPejyeC_GrQ1rffZvzaiUWYxHjLCyVkMjGCHYg.PNG.shin4299/Yeelight_mo_tile_on.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"

			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}')
            }
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"switch level.setLevel"
            }
		}
		multiAttributeTile(name:"switch2", type: "lighting"){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'ON', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTk4/MDAxNTIyMTUzOTk3MTY1.PJymTUZE8iMBPumZzoG_l0WgKTgqMJccUKBTy3-etrMg.2AdvU9rFVJH0_v6NogrZPCwv-NtqeB5oBqHSqaVoYfgg.PNG.shin4299/Yeelight_mo_main_on.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'OFF', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjU0/MDAxNTIyMTUzOTk3MTU1.ihvsjIGETKpfERltO0G-QgqHG7zls-jxzwrnCx3ivYog.uR77SPI-Dcs40NigVqpeYfMfL4HwJY9C1gcQwbbnmwYg.PNG.shin4299/Yeelight_mo_main_off.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMjU0/MDAxNTIyMTUzOTk3MTU1.ihvsjIGETKpfERltO0G-QgqHG7zls-jxzwrnCx3ivYog.uR77SPI-Dcs40NigVqpeYfMfL4HwJY9C1gcQwbbnmwYg.PNG.shin4299/Yeelight_mo_main_off.png?type=w580", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.ofn", icon:"https://postfiles.pstatic.net/MjAxODAzMjdfMTk4/MDAxNTIyMTUzOTk3MTY1.PJymTUZE8iMBPumZzoG_l0WgKTgqMJccUKBTy3-etrMg.2AdvU9rFVJH0_v6NogrZPCwv-NtqeB5oBqHSqaVoYfgg.PNG.shin4299/Yeelight_mo_main_on.png?type=w580", backgroundColor:"#ffffff", nextState:"turningOn"

			}
        }
   	main (["switch2"])
	details(["switch"])               
       
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
 	switch(params.key){
    case "power":
    	log.debug "MI >> power " + (params.data == "true" ? "on" : "off")
    	sendEvent(name:"switch", value: (params.data == "true" ? "on" : "off") )
    	break;
    case "brightness":
    	sendEvent(name:"level", value: params.data )
    	break;
    }
    
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def setLevel(brightness){
	log.debug "setBrightness >> ${state.id}, val=${brightness}"
    def body = [
        "id": state.id,
        "cmd": "brightness",
        "data": brightness
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setColor(color){
	log.debug "setColor >> ${state.id}"
    log.debug "${color.hex}"
    def body = [
        "id": state.id,
        "cmd": "color",
        "data": color.hex
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
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


def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
 //       setStatus(jsonObj)
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {}

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
