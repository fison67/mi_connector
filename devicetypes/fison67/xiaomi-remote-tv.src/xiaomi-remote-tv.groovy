/**
 *  Xiaomi Remote TV (v.0.0.3)
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
        
        command "setStatus"
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
        
        command "setTimeRemaining"
        command "stop"
	}

	simulator {
	}
    
	preferences {
        input name: "syncByDevice", title:"Sync By Device" , type: "bool", required: true, defaultValue:true, description:"" 
	}
}


def isIRRemoteDevice(){
	return true
}

def msToTime(duration) {
    def seconds = (duration%60).intValue()
    def minutes = ((duration/60).intValue() % 60).intValue()
    def hours = ( (duration/(60*60)).intValue() %24).intValue()

    hours = (hours < 10) ? "0" + hours : hours
    minutes = (minutes < 10) ? "0" + minutes : minutes
    seconds = (seconds < 10) ? "0" + seconds : seconds

    return hours + ":" + minutes + ":" + seconds
}

def stop() { 
	log.debug "stop"
	unschedule()
	state.timerCount = 0
	updateTimer()
}

def timer(){
	if(state.timerCount > 0){
    	state.timerCount = state.timerCount - 30;
        if(state.timerCount <= 0){
        	if(device.currentValue("switch") == "on"){
        		off()
            }
        }else{
        	runIn(30, timer)
        }
        updateTimer()
    }
}

def updateTimer(){
    def timeStr = msToTime(state.timerCount)
    sendEvent(name:"leftTime", value: "${timeStr}")
    sendEvent(name:"timeRemaining", value: Math.round(state.timerCount/60))
}

def processTimer(second){
	if(state.timerCount == null){
    	state.timerCount = second;
    	runIn(30, timer)
    }else if(state.timerCount == 0){
		state.timerCount = second;
    	runIn(30, timer)
    }else{
    	state.timerCount = second
    }
    updateTimer()
}

def setTimeRemaining(time) { 
	if(time > 0){
        log.debug "Set a Timer ${time}Mins"
        processTimer(time * 60)
        setPowerByStatus(true)
    }
}

def setPowerByStatus(turnOn){
	if(device.currentValue("switch") == (turnOn ? "off" : "on")){
        if(turnOn){
        	on()
        }else{
        	off()
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

def setStatus(data){
	sendEvent(name:"switch", value: data )
}

def playIR(code){
}

def on(){
	playIRCmd(state['tv-on'])
    if(!syncByDevice){
		sendEvent(name:"switch", value: "on" )
    }
}

def off(){
	playIRCmd(state['tv-off'])
    if(!syncByDevice){
		sendEvent(name:"switch", value: "off" )
	}
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
