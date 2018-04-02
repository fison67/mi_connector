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

	tiles {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"off", icon:"http://postfiles11.naver.net/MjAxODA0MDJfNzEg/MDAxNTIyNjcwODgzNDMy.gQ9ebEInEePLIPq2X0CrHRORPktdXqKEgKYid5ziXRcg.zZ_5pLqlGdjOl8U4u6DqOTlw-FVlxwyPrzNMSNX5axQg.PNG.shin4299/ceilinglight_tile_on.png?type=w3", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"on", icon:"http://postfiles4.naver.net/MjAxODA0MDJfMjg2/MDAxNTIyNjcwODgzMjc4.zVguk8EhV__yamYSG9j21VDxB0TdDyFMM3DJ_h_QVdAg.D7ZIhOABgQ9Mosi3VYq_NG4Tp3mkWzhTG1RW-DjWnm0g.PNG.shin4299/ceilinglight_tile_off.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"off", icon:"http://postfiles4.naver.net/MjAxODA0MDJfMjg2/MDAxNTIyNjcwODgzMjc4.zVguk8EhV__yamYSG9j21VDxB0TdDyFMM3DJ_h_QVdAg.D7ZIhOABgQ9Mosi3VYq_NG4Tp3mkWzhTG1RW-DjWnm0g.PNG.shin4299/ceilinglight_tile_off.png?type=w3", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"on", icon:"http://postfiles11.naver.net/MjAxODA0MDJfNzEg/MDAxNTIyNjcwODgzNDMy.gQ9ebEInEePLIPq2X0CrHRORPktdXqKEgKYid5ziXRcg.zZ_5pLqlGdjOl8U4u6DqOTlw-FVlxwyPrzNMSNX5axQg.PNG.shin4299/ceilinglight_tile_on.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        standardTile("switch2", "device.switch", width: 2, height: 2) {
                state "on", label:'${name}', action:"off", icon:"http://postfiles3.naver.net/MjAxODA0MDJfMTYg/MDAxNTIyNjcwODgzMTQw.lWo8f6HJS-sRwzAVlYadM1ZSLjbt2Ck7AxIzvMyejTwg.MujTFyozBZpuVfAWYv70fL6D5Nlsx-7CTcktMj303SMg.PNG.shin4299/ceilinglight_main_on.png?type=w3", backgroundColor:"#00a0dc", nextState:"turningOff"
                state "off", label:'${name}', action:"on", icon:"http://postfiles7.naver.net/MjAxODA0MDJfMjc0/MDAxNTIyNjcwODgyODc5.E6Q9geVbLPxJJ8EFopCzvWRgFVOSvQNZwf52ypZgJmMg.pDNVSHWK9WOwjVhXsrNLZc1RW3jfIT9yB21OgCUHAVIg.PNG.shin4299/ceilinglight_main_off.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn"
                
                state "turningOn", label:'${name}', action:"off", icon:"http://postfiles7.naver.net/MjAxODA0MDJfMjc0/MDAxNTIyNjcwODgyODc5.E6Q9geVbLPxJJ8EFopCzvWRgFVOSvQNZwf52ypZgJmMg.pDNVSHWK9WOwjVhXsrNLZc1RW3jfIT9yB21OgCUHAVIg.PNG.shin4299/ceilinglight_main_off.png?type=w3", backgroundColor:"#00a0dc", nextState:"turningOff"
                state "turningOff", label:'${name}', action:"on", icon:"http://postfiles3.naver.net/MjAxODA0MDJfMTYg/MDAxNTIyNjcwODgzMTQw.lWo8f6HJS-sRwzAVlYadM1ZSLjbt2Ck7AxIzvMyejTwg.MujTFyozBZpuVfAWYv70fL6D5Nlsx-7CTcktMj303SMg.PNG.shin4299/ceilinglight_main_on.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn"
	}
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
   	main (["switch2"])
	details(["switch", "refresh"])
		
	}
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
        
     	sendEvent(name:"switch", value: jsonObj.state.power ? "on" : "off")
        
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
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}
