/**
 *  Xiaomi Sensor Temperature & Humidity (v.0.0.1)
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
	definition (name: "Xiaomi Sensor HT", namespace: "fison67", author: "fison67") {
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Sensor"
        capability "Battery"
         
        attribute "pressure", "string"
		attribute "maxTemp", "number"
		attribute "minTemp", "number"
		attribute "maxHumidity", "number"
		attribute "minHumidity", "number"
		attribute "multiAttributesReport", "String"
		attribute "currentDay", "String"

        attribute "lastCheckin", "Date"
		attribute "lastCheckinDate", "String"

		command "refresh"
        
	}


	simulator {}
	preferences {
		input name: "displayTempHighLow", type: "bool", title: "Display high/low temperature?"
		input name: "displayHumidHighLow", type: "bool", title: "Display high/low humidity?"
	}


	tiles(scale: 2) {
        multiAttributeTile(name:"temperature", type:"generic", width:6, height:4) {
            tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState("temperature", label:'${currentValue}°',
                    backgroundColors:[
                        // Fahrenheit color set
                        [value: 0, color: "#153591"],
                        [value: 5, color: "#1e9cbb"],
                        [value: 10, color: "#90d2a7"],
                        [value: 15, color: "#44b621"],
                        [value: 20, color: "#f1d801"],
                        [value: 25, color: "#d04e00"],
                        [value: 30, color: "#bc2323"],
                        [value: 44, color: "#1e9cbb"],
                        [value: 59, color: "#90d2a7"],
                        [value: 74, color: "#44b621"],
                        [value: 84, color: "#f1d801"],
                        [value: 95, color: "#d04e00"],
                        [value: 96, color: "#bc2323"]
						// Celsius color set (to switch, delete the 13 lines above anmd remove the two slashes at the beginning of the line below)
                        //[value: 0, color: "#153591"], [value: 7, color: "#1e9cbb"], [value: 15, color: "#90d2a7"], [value: 23, color: "#44b621"], [value: 28, color: "#f1d801"], [value: 35, color: "#d04e00"], [value: 37, color: "#bc2323"]
                    ]
                )
            }
//            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
//    			attributeState("default", label:'Updated: ${currentValue}',icon: "st.Health & Wellness.health9")
//            }
            tileAttribute("device.multiAttributesReport", key: "SECONDARY_CONTROL") {
                attributeState("multiAttributesReport", label:'${currentValue}' //icon:"st.Weather.weather12",
                ) }
        }        
        valueTile("temperature2", "device.temperature", inactiveLabel: false) {
            state "temperature", label:'${currentValue}°', icon:"st.Weather.weather2",
            backgroundColors:[
                // Fahrenheit color set
                [value: 0, color: "#153591"],
                [value: 5, color: "#1e9cbb"],
                [value: 10, color: "#90d2a7"],
                [value: 15, color: "#44b621"],
                [value: 20, color: "#f1d801"],
                [value: 25, color: "#d04e00"],
                [value: 30, color: "#bc2323"],
                [value: 44, color: "#1e9cbb"],
                [value: 59, color: "#90d2a7"],
                [value: 74, color: "#44b621"],
                [value: 84, color: "#f1d801"],
                [value: 95, color: "#d04e00"],
                [value: 96, color: "#bc2323"]
                // Celsius color set (to switch, delete the 13 lines above anmd remove the two slashes at the beginning of the line below)
                //[value: 0, color: "#153591"], [value: 7, color: "#1e9cbb"], [value: 15, color: "#90d2a7"], [value: 23, color: "#44b621"], [value: 28, color: "#f1d801"], [value: 35, color: "#d04e00"], [value: 37, color: "#bc2323"]
            ]
        }
        
        valueTile("humidity", "device.humidity", width: 2, height: 2, unit: "%") {
            state("val", label:'${currentValue}%', defaultState: true, 
            	backgroundColors:[
                    [value: 10, color: "#153591"],
                    [value: 30, color: "#1e9cbb"],
                    [value: 40, color: "#90d2a7"],
                    [value: 50, color: "#44b621"],
                    [value: 60, color: "#f1d801"],
                    [value: 80, color: "#d04e00"],
                    [value: 90, color: "#bc2323"]
                ]
            )
        }
                
        valueTile("battery", "device.battery", width: 2, height: 2) {
            state "val", label:'${currentValue}%', defaultState: true
        }		
        valueTile("humi", "title", decoration: "flat", inactiveLabel: false, width: 2, height: 1) {
	    	state "default", label:'Humidity'
        }		
        valueTile("bat", "title", decoration: "flat", inactiveLabel: false, width: 2, height: 1) {
	    	state "default", label:'Battery'
        }		
        valueTile("lastcheckin", "device.lastCheckin", inactiveLabel: false, decoration:"flat", width: 2, height: 3) {
        	state "lastcheckin", label:'Last Event:\n ${currentValue}'
        }
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }

        main("temperature2")
        details(["temperature", "humi", "bat", "lastcheckin", "humidity",  "battery", "refresh"])
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
    log.debug "${params.key} : ${params.data}"
 
 	switch(params.key){
    case "relativeHumidity":
		def para = "${params.data}"
		String data = para
		def stf = Float.parseFloat(data)
		def hum = Math.round(stf)
    	sendEvent(name:"humidity", value: hum )
        updateMinMaxHumidity(hum)
    	break;
    case "temperature":
		def para = "${params.data}"
		String data = para
		def st = data.replace("C","");
		def stf = Float.parseFloat(st)
		def tem = Math.round(stf*10)/10
        sendEvent(name:"temperature", value: tem )
        updateMinMaxTemps(tem)
//        log.debug "${st}"
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data )
    	break;		
    }
    
//    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
//    sendEvent(name: "lastCheckin", value: now)
    def now = formatDate()    
    def nowDate = new Date(now).getTime()

	// Any report - temp, humidity, pressure, & battery - results in a lastCheckin event and update to Last Checkin tile
	// However, only a non-parseable report results in lastCheckin being displayed in events log
    sendEvent(name: "lastCheckin", value: now, displayed: false)
    sendEvent(name: "lastCheckinDate", value: nowDate, displayed: false)

	// Check if the min/max temp and min/max humidity should be reset
    checkNewDay(now)
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
        
     	sendEvent(name:"temperature", value: jsonObj.state.temperature)
     	sendEvent(name:"humidity", value: jsonObj.state.humidity)        
        sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}


def updated() {}

def checkNewDay(now) {
	def oldDay = ((device.currentValue("currentDay")) == null) ? "32" : (device.currentValue("currentDay"))
	def newDay = new Date(now).format("dd")
	if (newDay != oldDay) {
		resetMinMax()
		sendEvent(name: "currentDay", value: newDay, displayed: false)
	}
}

def resetMinMax() {
	def currentTemp = device.currentValue('temperature')
	def currentHumidity = device.currentValue('humidity')
    currentTemp = currentTemp ? (int) currentTemp : currentTemp
	log.debug "${device.displayName}: Resetting daily min/max values to current temperature of ${currentTemp}° and humidity of ${currentHumidity}%"
    sendEvent(name: "maxTemp", value: currentTemp, displayed: false)
    sendEvent(name: "minTemp", value: currentTemp, displayed: false)
    sendEvent(name: "maxHumidity", value: currentHumidity, displayed: false)
    sendEvent(name: "minHumidity", value: currentHumidity, displayed: false)
    refreshMultiAttributes()
}

// Check new min or max temp for the day
def updateMinMaxTemps(temp) {
	temp = temp ? (int) temp : temp
	if ((temp > device.currentValue('maxTemp')) || (device.currentValue('maxTemp') == null))
		sendEvent(name: "maxTemp", value: temp, displayed: false)	
	if ((temp < device.currentValue('minTemp')) || (device.currentValue('minTemp') == null))
		sendEvent(name: "minTemp", value: temp, displayed: false)
	refreshMultiAttributes()
}

// Check new min or max humidity for the day
def updateMinMaxHumidity(humidity) {
	if ((humidity > device.currentValue('maxHumidity')) || (device.currentValue('maxHumidity') == null))
		sendEvent(name: "maxHumidity", value: humidity, displayed: false)
	if ((humidity < device.currentValue('minHumidity')) || (device.currentValue('minHumidity') == null))
		sendEvent(name: "minHumidity", value: humidity, displayed: false)
	refreshMultiAttributes()
}

// Update display of multiattributes in main tile
def refreshMultiAttributes() {
	def temphiloAttributes = displayTempHighLow ? (displayHumidHighLow ? "Today's High/Low:  ${device.currentState('maxTemp')?.value}° / ${device.currentState('minTemp')?.value}°" : "Today's High: ${device.currentState('maxTemp')?.value}°  /  Low: ${device.currentState('minTemp')?.value}°") : ""
	def humidhiloAttributes = displayHumidHighLow ? (displayTempHighLow ? "    ${device.currentState('maxHumidity')?.value}% / ${device.currentState('minHumidity')?.value}%" : "Today's High: ${device.currentState('maxHumidity')?.value}%  /  Low: ${device.currentState('minHumidity')?.value}%") : ""
	sendEvent(name: "multiAttributesReport", value: "${temphiloAttributes}${humidhiloAttributes}", displayed: false)
}
