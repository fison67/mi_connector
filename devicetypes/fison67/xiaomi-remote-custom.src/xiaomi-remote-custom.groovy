/**
 *  Xiaomi Remote Custom (v.0.0.1)
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
	definition (name: "Xiaomi Remote Custom", namespace: "fison67", author: "fison67") {
        capability "Switch"
        capability "Configuration"
        
        command "playIRCmdByID", ["string"]
        command "playIR", ["string"]
        command "remoteCustom1"
        command "remoteCustom2"
        command "remoteCustom3"
        command "remoteCustom4"
        command "remoteCustom5"
        command "remoteCustom6"
        command "remoteCustom7"
        command "remoteCustom8"
        command "remoteCustom9"
        command "remoteCustom10"
        command "remoteCustom11"
        command "remoteCustom12"
        command "remoteCustom13"
        command "remoteCustom14"
        command "remoteCustom15"
        
	}


	simulator {
	}
    
	preferences {
	}

	tiles(scale: 2) {
		
        multiAttributeTile(name:"switch", type: "generic", width: 6, height: 2){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"remoteCustom2", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"off"
                attributeState "off", label:'${name}', action:"remoteCustom1", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"on"
			}
		}
        
        valueTile("remoteCustom1", "device.remoteCustom1", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom1"
        }
        valueTile("remoteCustom2", "device.remoteCustom2", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom2"
        }
        valueTile("remoteCustom3", "device.remoteCustom3", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom3"
        }
        valueTile("remoteCustom4", "device.remoteCustom4", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom4"
        }
        valueTile("remoteCustom5", "device.remoteCustom5", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom5"
        }
        valueTile("remoteCustom6", "device.remoteCustom6", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom6"
        }
        valueTile("remoteCustom7", "device.remoteCustom7", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom7"
        }
        valueTile("remoteCustom8", "device.remoteCustom8", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom8"
        }
        valueTile("remoteCustom9", "device.remoteCustom9", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom9"
        }
        valueTile("remoteCustom10", "device.remoteCustom10", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom10"
        }
        valueTile("remoteCustom11", "device.remoteCustom11", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom11"
        }
        valueTile("remoteCustom12", "device.remoteCustom12", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom12"
        }
        valueTile("remoteCustom13", "device.remoteCustom13", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom13"
        }
        valueTile("remoteCustom14", "device.remoteCustom14", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom14"
        }
        valueTile("remoteCustom15", "device.remoteCustom15", decoration: "flat", width: 2, height: 1 ) {
            state "default", label:'${currentValue}', action:"remoteCustom15"
        }
        
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

def setData(dataList){
	for(data in dataList){
        state[data.id] = data.code
        sendEvent(name:"remoteCustom" + data.id.substring(7, data.id.length()), value: data.title )
    }
}

def setStatus(params){
}

def remoteCustom1(){
	playIRCmd(state['custom-1'])
}

def remoteCustom2(){
	playIRCmd(state['custom-2'])
}

def remoteCustom3(){
	playIRCmd(state['custom-3'])
}

def remoteCustom4(){
	playIRCmd(state['custom-4'])
}

def remoteCustom5(){
	playIRCmd(state['custom-5'])
}

def remoteCustom6(){
	playIRCmd(state['custom-6'])
}

def remoteCustom7(){
	playIRCmd(state['custom-7'])
}

def remoteCustom8(){
	playIRCmd(state['custom-8'])
}

def remoteCustom9(){
	playIRCmd(state['custom-9'])
}

def remoteCustom10(){
	playIRCmd(state['custom-10'])
}

def remoteCustom11(){
	playIRCmd(state['custom-11'])
}

def remoteCustom12(){
	playIRCmd(state['custom-12'])
}

def remoteCustom13(){
	playIRCmd(state['custom-13'])
}

def remoteCustom14(){
	playIRCmd(state['custom-14'])
}

def remoteCustom15(){
	playIRCmd(state['custom-15'])
}


def playIRCmdByID(id){
	playIRCmd(state[id])
}

def playIRCmd(code){
	if(code == null || code == ""){
    	log.error("Non exist code")
    	return;
    }
    
    def body = [
        "id": state.id,
        "cmd": "playIRByCode",
        "data": code
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
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
