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
	definition (name: "Xiaomi Remote TV", namespace: "fison67", author: "fison67", ocfDeviceType: "oic.d.tv") {
        capability "Switch"
        capability "Configuration"
        capability "Tv Channel"
        capability "Refresh"
        
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
        
        input name: "number_1", title:"Number #1" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "number_2", title:"Number #2" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "number_3", title:"Number #3" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "number_4", title:"Number #4" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "number_5", title:"Number #5" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "number_6", title:"Number #6" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "number_7", title:"Number #7" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "number_8", title:"Number #8" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "number_9", title:"Number #9" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "number_0", title:"Number #0" , type: "string", required: false, defaultValue:"", description:""
        input name: "ch_up", title:"Channel UP" , type: "string", required: false, defaultValue:"", description:""
        input name: "ch_down", title:"Channel DOWN" , type: "string", required: false, defaultValue:"", description:""
        input name: "vol_up", title:"Volume UP" , type: "string", required: false, defaultValue:"", description:""
        input name: "vol_down", title:"Volume DOWN" , type: "string", required: false, defaultValue:"", description:""
        input name: "vol_mute", title:"Volume Mute" , type: "string", required: false, defaultValue:"", description:""
        input name: "custom_1", title:"Custom #1" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "custom_2", title:"Custom #2" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "custom_3", title:"Custom #3" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "custom_4", title:"Custom #4" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "custom_5", title:"Custom #5" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "custom_6", title:"Custom #6" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "custom_7", title:"Custom #7" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "custom_8", title:"Custom #8" , type: "string", required: false, defaultValue:"", description:"" 
        input name: "custom_9", title:"Custom #9" , type: "string", required: false, defaultValue:"", description:"" 
	}

	tiles(scale: 2) {
		
        multiAttributeTile(name:"switch", type: "generic", width: 6, height: 2){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"off", icon:"https://github.com/fison67/mi_connector/blob/master/icons/tv-100.png?raw=true", backgroundColor:"#00a0dc", nextState:"off"
                attributeState "off", label:'${name}', action:"on", icon:"https://github.com/fison67/mi_connector/blob/master/icons/tv-100.png?raw=true", backgroundColor:"#ffffff", nextState:"on"
			}
		}
        
        standardTile("remoteCHUp", "device.remoteCHUp", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCHUp", label: "", icon: "https://github.com/fison67/mi_connector/blob/master/icons/ch-up.png?raw=true"
        }

		standardTile("remoteExit", "device.remoteExit", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteExit", label: "EXIT"
        }
        
        standardTile("remoteVOLUp", "device.remoteVOLUp", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteVOLUp", label: "", icon: "https://github.com/fison67/mi_connector/blob/master/icons/vol-up.png?raw=true"
        }
        
		standardTile("remoteCHDown", "device.remoteCHDown", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCHDown", label: "", icon: "https://github.com/fison67/mi_connector/blob/master/icons/ch-down.png?raw=true"
        }
        
        standardTile("remoteVOLMute", "device.remoteVOLMute", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteVOLMute", label: "VOL Mute"
        }

		standardTile("remoteVOLDown", "device.remoteVOLDown", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteVOLDown", label: "", icon: "https://github.com/fison67/mi_connector/blob/master/icons/vol-down.png?raw=true"
        }
        
        standardTile("remoteNum1", "device.remoteNum1", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum1", label: "1"
        }

		standardTile("remoteNum2", "device.remoteNum2", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum2", label: "2"
        }
        
        standardTile("remoteNum3", "device.remoteNum3", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum3", label: "3"
        }
        
        standardTile("remoteNum4", "device.remoteNum4", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum4", label: "4"
        }

		standardTile("remoteNum5", "device.remoteNum5", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum5", label: "5"
        }
        
        standardTile("remoteNum6", "device.remoteNum6", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum6", label: "6"
        }   
        
        standardTile("remoteNum7", "device.remoteNum7", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum7", label: "7"
        }

		standardTile("remoteNum8", "device.remoteNum8", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum8", label: "8"
        }
        
        standardTile("remoteNum9", "device.remoteNum9", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum9", label: "9"
        }
        
        standardTile("remoteHyphen", "device.remoteHyphen", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteHyphen", label: "-"
        }

		standardTile("remoteNum0", "device.remoteNum0", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteNum0", label: "0"
        }
        
        standardTile("remotePrv", "device.remotePrv", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remotePrv", label: "PRV"
        }        
        
        standardTile("remoteCustom1", "device.remoteCustom1", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCustom1", label: "#1"
        }
        standardTile("remoteCustom2", "device.remoteCustom2", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCustom2", label: "#2"
        }
        standardTile("remoteCustom3", "device.remoteCustom3", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCustom3", label: "#3"
        }
        standardTile("remoteCustom4", "device.remoteCustom4", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCustom4", label: "#4"
        }
        standardTile("remoteCustom5", "device.remoteCustom5", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCustom5", label: "#5"
        }
        standardTile("remoteCustom6", "device.remoteCustom6", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCustom6", label: "#6"
        }
        standardTile("remoteCustom7", "device.remoteCustom7", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCustom7", label: "#7"
        }
        standardTile("remoteCustom8", "device.remoteCustom8", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCustom1", label: "#8"
        }
        standardTile("remoteCustom9", "device.remoteCustom9", decoration: "flat", width: 2, height: 1) {
            state "default", action:"remoteCustom9", label: "#9"
        }
        
        valueTile("timer_label", "device.leftTime", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Set Timer\n${currentValue}'
        }
        
        controlTile("time", "device.timeRemaining", "slider", height: 1, width: 1, range:"(0..120)") {
	    	state "time", action:"setTimeRemaining"
		}
        
        standardTile("tiemr0", "device.timeRemaining") {
			state "default", label: "OFF", action: "stop", icon:"st.Health & Wellness.health7", backgroundColor:"#c7bbc9"
		}
	}
}

def channelUp(){

}

def channelDown(){

}

def setTvChannel(channel){

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
	if(settings.number_1 != "" && settings.number_1 != null){
    	state['tv-1'] = settings.number_1
    }
    if(settings.number_2 != "" && settings.number_2 != null){
    	state['tv-2'] = settings.number_2
    }    
    if(settings.number_3 != "" && settings.number_3 != null){
    	state['tv-3'] = settings.number_3
    }
    if(settings.number_4 != "" && settings.number_4 != null){
    	state['tv-4'] = settings.number_4
    }
    if(settings.number_5 != "" && settings.number_5 != null){
    	state['tv-5'] = settings.number_5
    }
    if(settings.number_6 != "" && settings.number_6 != null){
    	state['tv-6'] = settings.number_6
    }
    if(settings.number_7 != "" && settings.number_7 != null){
    	state['tv-7'] = settings.number_7
    }
    if(settings.number_8 != "" && settings.number_8 != null){
    	state['tv-8'] = settings.number_8
    }
    if(settings.number_9 != "" && settings.number_9 != null){
    	state['tv-9'] = settings.number_9
    }
    if(settings.number_0 != "" && settings.number_0 != null){
    	state['tv-0'] = settings.number_0
    }
    if(settings.ch_up != "" && settings.ch_up != null){
    	state['tv-ch-up'] = settings.ch_up
    }
    if(settings.ch_down != "" && settings.ch_down != null){
    	state['tv-ch-down'] = settings.ch_down
    }
    if(settings.vol_up != "" && settings.vol_up != null){
    	state['tv-vol-up'] = settings.vol_up
    }
    if(settings.vol_down != "" && settings.vol_down != null){
    	state['tv-vol-down'] = settings.vol_down
    }
    if(settings.vol_mute != "" && settings.vol_mute != null){
    	state['tv-vol-mute'] = settings.vol_mute
    }
    
    if(settings.custom_1 != "" && settings.custom_1 != null){
        state['custom-1'] = settings.custom_1
    }
    if(settings.custom_2 != "" && settings.custom_2 != null){
        state['custom-2'] = settings.custom_2
    }    
    if(settings.custom_3 != "" && settings.custom_3 != null){
        state['custom-3'] = settings.custom_3
    }
    if(settings.custom_4 != "" && settings.custom_4 != null){
        state['custom-4'] = settings.custom_4
    }
    if(settings.custom_5 != "" && settings.custom_5 != null){
        state['custom-5'] = settings.custom_5
    }
    if(settings.custom_6 != "" && settings.custom_6 != null){
        state['custom-6'] = settings.custom_6
    }
    if(settings.custom_7 != "" && settings.custom_7 != null){
        state['custom-7'] = settings.custom_7
    }
    if(settings.custom_8 != "" && settings.custom_8 != null){
        state['custom-8'] = settings.custom_8
    }
    if(settings.custom_9 != "" && settings.custom_9 != null){
        state['custom-9'] = settings.custom_9
    }
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
