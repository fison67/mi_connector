/**
 *  Xiaomi Remote TV (v.0.0.1)
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
	definition (name: "Xiaomi Remote TV", namespace: "fison67", author: "fison67") {
        capability "Switch"
        capability "Configuration"
        
        command "remoteCHUp"
        command "remoteExit"
        command "remoteCHDown"
        
        command "remoteVOLUp"
        command "remoteVOLMute"
        command "remoteVOLDown"
        
        command "remoteNum1"
        command "remoteNum2"
        command "remoteNum3"
        command "remoteNum4"
        command "remoteNum5"
        command "remoteNum6"
        command "remoteNum7"
        command "remoteNum8"
        command "remoteNum9"
		command "remoteHyphen"
        command "remoteNum0"
		command "remotePrv"
        
        command "remoteCustom1"
        command "remoteCustom2"
        command "remoteCustom3"
        command "remoteCustom4"
        command "remoteCustom5"
        command "remoteCustom6"
        command "remoteCustom7"
        command "remoteCustom8"
        command "remoteCustom9"
        
	}


	simulator {
	}
    
	preferences {
	}

	tiles(scale: 2) {
		
        multiAttributeTile(name:"switch", type: "generic", width: 6, height: 2){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"off", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"off"
                attributeState "off", label:'${name}', action:"on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"on"
			}
		}
        
        standardTile("remoteCHUp", "device.remoteCHUp", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCHUp", label: "CH UP"
        }

		standardTile("remoteExit", "device.remoteExit", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteExit", label: "EXIT"
        }
        
        standardTile("remoteVOLUp", "device.remoteVOLUp", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteVOLUp", label: "VOL UP"
        }
        
		standardTile("remoteCHDown", "device.remoteCHDown", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCHDown", label: "CH DOWN"
        }
        
        standardTile("remoteVOLMute", "device.remoteVOLMute", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteVOLMute", label: "VOL Mute"
        }

		standardTile("remoteVOLDown", "device.remoteVOLDown", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteVOLDown", label: "VOL DOWN"
        }
        
        standardTile("remoteNum1", "device.remoteNum1", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum1", label: "1"
        }

		standardTile("remoteNum2", "device.remoteNum2", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum2", label: "2"
        }
        
        standardTile("remoteNum3", "device.remoteNum3", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum3", label: "3"
        }
        
        standardTile("remoteNum4", "device.remoteNum4", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum4", label: "4"
        }

		standardTile("remoteNum5", "device.remoteNum5", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum5", label: "5"
        }
        
        standardTile("remoteNum6", "device.remoteNum6", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum6", label: "6"
        }   
        
        standardTile("remoteNum7", "device.remoteNum7", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum4", label: "7"
        }

		standardTile("remoteNum8", "device.remoteNum8", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum5", label: "8"
        }
        
        standardTile("remoteNum9", "device.remoteNum9", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum9", label: "9"
        }
        
        standardTile("remoteHyphen", "device.remoteHyphen", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteHyphen", label: "-"
        }

		standardTile("remoteNum0", "device.remoteNum0", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remoteNum0", label: "0"
        }
        
        standardTile("remotePrv", "device.remotePrv", decoration: "flat", width: 2, height: 2) {
            state "default", action:"remotePrv", label: "PRV"
        }        
        
        standardTile("remoteCustom1", "device.remoteCustom1", decoration: "flat", width: 1, height: 1) {
            state "default", action:"remoteCustom1", label: "1"
        }
        standardTile("remoteCustom2", "device.remoteCustom2", decoration: "flat", width: 1, height: 1) {
            state "default", action:"remoteCustom2", label: "2"
        }
        standardTile("remoteCustom3", "device.remoteCustom3", decoration: "flat", width: 1, height: 1) {
            state "default", action:"remoteCustom3", label: "3"
        }
        standardTile("remoteCustom4", "device.remoteCustom4", decoration: "flat", width: 1, height: 1) {
            state "default", action:"remoteCustom4", label: "4"
        }
        standardTile("remoteCustom5", "device.remoteCustom5", decoration: "flat", width: 1, height: 1) {
            state "default", action:"remoteCustom5", label: "5"
        }
        standardTile("remoteCustom6", "device.remoteCustom6", decoration: "flat", width: 1, height: 1) {
            state "default", action:"remoteCustom6", label: "6"
        }
        standardTile("remoteCustom7", "device.remoteCustom7", decoration: "flat", width: 1, height: 1) {
            state "default", action:"remoteCustom7", label: "7"
        }
        standardTile("remoteCustom8", "device.remoteCustom8", decoration: "flat", width: 1, height: 1) {
            state "default", action:"remoteCustom1", label: "8"
        }
        standardTile("remoteCustom9", "device.remoteCustom9", decoration: "flat", width: 1, height: 1) {
            state "default", action:"remoteCustom9", label: "9"
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
    }
}

def setStatus(params){
}

def playIR(code){
}

def on(){
	playIRCmd(state['tv-on'])
}

def off(){
	playIRCmd(state['tv-off'])
}

def remoteCHUp(){
	playIRCmd(state['tv-ch-up'])
}

def remoteCHDown(){
	playIRCmd(state['tv-ch-down'])
}

def remoteExit(){
	playIRCmd(state['tv-exit'])
}

def remoteVOLUp(){
	playIRCmd(state['tv-vol-up'])
}

def remoteVOLDown(){
	playIRCmd(state['tv-vol-down'])
}

def remoteVOLMute(){
	playIRCmd(state['tv-vol-mute'])
}

def remoteNum1(){
	playIRCmd(state['tv-1'])
}

def remoteNum2(){
	playIRCmd(state['tv-2'])
}

def remoteNum3(){
	playIRCmd(state['tv-3'])
}

def remoteNum4(){
	playIRCmd(state['tv-4'])
}

def remoteNum5(){
	playIRCmd(state['tv-5'])
}

def remoteNum6(){
	playIRCmd(state['tv-6'])
}

def remoteNum7(){
	playIRCmd(state['tv-7'])
}

def remoteNum8(){
	playIRCmd(state['tv-8'])
}

def remoteNum9(){
	playIRCmd(state['tv-9'])
}

def remoteHyphen(){
	playIRCmd(state['tv--'])
}

def remoteNum0(){
	playIRCmd(state['tv-0'])
}

def remotePrv(){
	playIRCmd(state['tv-*'])
}

def remoteCustom1(){
	playIRCmd(state['tv-custom-1'])
}

def remoteCustom2(){
	playIRCmd(state['tv-custom-2'])
}

def remoteCustom3(){
	playIRCmd(state['tv-custom-3'])
}

def remoteCustom4(){
	playIRCmd(state['tv-custom-4'])
}

def remoteCustom5(){
	playIRCmd(state['tv-custom-5'])
}

def remoteCustom6(){
	playIRCmd(state['tv-custom-6'])
}

def remoteCustom7(){
	playIRCmd(state['tv-custom-7'])
}

def remoteCustom8(){
	playIRCmd(state['tv-custom-8'])
}

def remoteCustom9(){
	playIRCmd(state['tv-custom-9'])
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
