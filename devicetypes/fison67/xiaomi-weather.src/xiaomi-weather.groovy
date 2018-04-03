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
import groovy.transform.Field

@Field 
LANGUAGE_MAP = [
	"atmospheric_pressure":[
    	"Korean": "기압",
        "English": "Atmospheric Pressure"
    ],
    "temperature": [
        "Korean": "온도",
        "English": "Temperature"
    ],
    "humidity": [
        "Korean": "습도",
        "English": "Humidity"
    ],
    "battery": [
    	"Korean": "배터리",
        "English": "Battery"
    ],
    "todays": [
    	"Korean": "오늘",
        "English": "Today's"
    ],
    "high": [
    	"Korean": "최고",
        "English": "High"
    ],
    "low": [
    	"Korean": "최저",
        "English": "Low"
    ],
    "mon": [
    	"Korean": "월",
        "English": "Mon"
    ],
    "tue": [
    	"Korean": "화",
        "English": "Tue"
    ],
    "wed": [
    	"Korean": "수",
        "English": "Wed"
    ],
    "thu": [
    	"Korean": "목",
        "English": "Thu"
    ],
    "fri": [
    	"Korean": "금",
        "English": "Fri"
    ],
    "sat": [
    	"Korean": "토",
        "English": "Sat"
    ],
    "sun": [
    	"Korean": "일",
        "English": "Sun"
    ]
]

metadata {
	definition (name: "xiaomi weather", namespace: "fison67", author: "fison67") {
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

        attribute "1l", "string"
        attribute "1t", "string"
        attribute "1h", "string"
        attribute "2l", "string"
        attribute "2t", "string"
        attribute "2h", "string"
        attribute "3l", "string"
        attribute "3t", "string"
        attribute "3h", "string"
        attribute "4l", "string"
        attribute "4t", "string"
        attribute "4h", "string"
        attribute "5l", "string"
        attribute "5t", "string"
        attribute "5h", "string"
        attribute "6l", "string"
        attribute "6t", "string"
        attribute "6h", "string"
        attribute "lastCheckin", "Date"
		attribute "lastCheckinDate", "String"

        command "setLanguage" 
        command "refresh"
	}


	simulator {}
	preferences {
		input name: "displayTempHighLow", type: "bool", title: "Display high/low temperature?"
		input name: "displayHumidHighLow", type: "bool", title: "Display high/low humidity?"
		input name: "selectedLang", title:"Select a language" , type: "enum", required: true, options: ["English", "Korean"], defaultValue: "English", description:"Language for DTH"
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
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Updated: ${currentValue}\n')
            }
            tileAttribute("device.multiAttributesReport", key: "SECONDARY_CONTROL") {
                attributeState("multiAttributesReport", label:'\n${currentValue}' //icon:"st.Weather.weather12",
                ) }
        }        
        valueTile("temperature2", "device.temperature", inactiveLabel: false) {
            state "temperature", label:'${currentValue}°', icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNzkg/MDAxNTIyNjcwOTc4NTIy.9VGDZZ4ieBY5jCJ0tvO8L5HFKbkvnms3ymk62HL4rzMg.HYTGtieTVMLE421M8lF8WE1THRgdyFfb1GG39OhtrU4g.PNG.shin4299/temp.png?type=w3",
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
        
        
        valueTile("pressure", "device.pressure", width: 2, height: 2, unit: "") {
            state("val", label:'${currentValue} kpa', defaultState: true, 
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
        valueTile("pre", "device.pre", decoration: "flat", inactiveLabel: false, width: 2, height: 1) {
            state("val", label:'${currentValue}', defaultState: true)
        }
        valueTile("humi", "device.humi", decoration: "flat", inactiveLabel: false, width: 2, height: 1) {
            state("val", label:'${currentValue}', defaultState: true)
        }
        
        
        valueTile("1l", "device.1l") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("1t", "device.1t") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("1h", "device.1h") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("2l", "device.2l") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("2t", "device.2t") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("2h", "device.2h") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("3l", "device.3l") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("3t", "device.3t") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("3h", "device.3h") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("4l", "device.4l") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("4t", "device.4t") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("4h", "device.4h") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("5l", "device.5l") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("5t", "device.5t") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("5h", "device.5h") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("6l", "device.6l") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("6t", "device.6t") {
            state "val", label:'${currentValue}', defaultState: true
        }		
        valueTile("6h", "device.6h") {
            state "val", label:'${currentValue}', defaultState: true
        }		
//        valueTile("lastcheckin", "device.lastCheckin", inactiveLabel: false, decoration:"flat", width: 4, height: 1) {
//        state "lastcheckin", label:'Last Event:\n ${currentValue}'
//        }
		valueTile("bat", "device.bat", decoration: "flat", inactiveLabel: false, width: 2, height: 1) {
            state("val", label:'${currentValue}', defaultState: true)
        }
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }

        main("temperature2")
        details(["temperature", "humi", "pre", "bat", "humidity", "pressure", "battery",
            "1l", "2l", "3l", "4l", "5l", "6l", 
            "1t", "2t", "3t", "4t", "5t", "6t", 
            "1h", "2h", "3h", "4h", "5h", "6h", 
            "refresh"
		])
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
 //    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
//    sendEvent(name: "lastCheckin", value: now)
//    def now = formatDate()
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)

	// Any report - temp, humidity, pressure, & battery - results in a lastCheckin event and update to Last Checkin tile
	// However, only a non-parseable report results in lastCheckin being displayed in events log
    sendEvent(name: "lastCheckin", value: now, displayed: false)

	// Check if the min/max temp and min/max humidity should be reset
    
 	switch(params.key){
    case "relativeHumidity":
		def para = "${params.data}"
		String data = para
		def stf = Float.parseFloat(data)
		def humidity = Math.round(stf)
    	sendEvent(name:"humidity", value: humidity )
        updateMinMaxHumidity(humidity)
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
    case "atmosphericPressure":
    	sendEvent(name:"pressure", value: params.data.replace(" Pa","").replace(",","").toInteger()/1000 )
    	break;
    case "batteryLevel":
    	sendEvent(name:"battery", value: params.data )
    	break;		
    checkNewDay()
		
    }
}


def updated() {
    setLanguage(settings.selectedLang)
    
    if(state.sunMaxTemp == null) state.sunMaxTemp = 0
    if(state.sunMinTemp == null) state.sunMinTemp = 0
    if(state.sunMaxHumi == null) state.sunMaxHumi = 0
    if(state.sunMinHumi == null) state.sunMinHumi = 0
    
    if(state.monMaxTemp == null) state.monMaxTemp = 0
    if(state.monMinTemp == null) state.monMinTemp = 0
    if(state.monMaxHumi == null) state.monMaxHumi = 0
    if(state.monMinHumi == null) state.monMinHumi = 0
	
    if(state.tueMaxTemp == null) state.tueMaxTemp = 0
    if(state.tueMinTemp == null) state.tueMinTemp = 0
    if(state.tueMaxHumi == null) state.tueMaxHumi = 0
    if(state.tueMinHumi == null) state.tueMinHumi = 0
    
    if(state.wedMaxTemp == null) state.wedMaxTemp = 0
    if(state.wedMinTemp == null) state.wedMinTemp = 0
    if(state.wedMaxHumi == null) state.wedMaxHumi = 0
    if(state.wedMinHumi == null) state.wedMinHumi = 0
    
    if(state.thuMaxTemp == null) state.thuMaxTemp = 0
    if(state.thuMinTemp == null) state.thuMinTemp = 0
    if(state.thuMaxHumi == null) state.thuMaxHumi = 0
    if(state.thuMinHumi == null) state.thuMinHumi = 0

    if(state.firMaxTemp == null) state.firMaxTemp = 0
    if(state.firMinTemp == null) state.firMinTemp = 0
    if(state.firMaxHumi == null) state.firMaxHumi = 0
    if(state.firMinHumi == null) state.firMinHumi = 0
    
    if(state.satMaxTemp == null) state.satMaxTemp = 0
    if(state.satMinTemp == null) state.satMinTemp = 0
    if(state.satMaxHumi == null) state.satMaxHumi = 0
    if(state.satMinHumi == null) state.satMinHumi = 0
}

def setLanguage(language){
    log.debug "Languge >> ${language}"
	state.language = language
    
    sendEvent(name:"pre", value: LANGUAGE_MAP["atmospheric_pressure"][language] )
    sendEvent(name:"humi", value: LANGUAGE_MAP["humidity"][language] )
    sendEvent(name:"bat", value: LANGUAGE_MAP["battery"][language] )
    
    refreshMultiAttributes()
}

def checkNewDay() {
	def now = new Date().format("yyyy-MM-dd", location.timeZone)
	if(state.prvDate == null){
		state.prvDate = now
	}else{
		if(state.prvDate != now){
			state.prvDate = now
			    log.debug "checkNewDay _ ${state.prvDate}"
			resetMinMax()            
		}
	}
}

def resetMinMax() {
	def day = new Date().format("EEE", location.timeZone)
	log.debug "resetMinMax day_ ${day}"
	def currentMaxTemp = device.currentValue('maxTemp')
	def currentMinTemp = device.currentValue('minTemp')
	def currentMaxHumi = device.currentValue('maxHumidity')
	def currentMinHumi = device.currentValue('minHumidity')	
	log.debug "resetMinMax day_ ${day}_xT${currentMaxTemp}_nT${currentMinTemp}_xH${currentMaxHumi}_nH${currentMinHumi}"
	
	if (day == "Mon") {
		 state.sunMaxTemp = currentMaxTemp
		 state.sunMinTemp = currentMinTemp
		 state.sunMaxHumi = currentMaxHumi
		 state.sunMinHumi = currentMinHumi
	} else if (day == "Tue") {
		 state.monMaxTemp = currentMaxTemp
		 state.monMinTemp = currentMinTemp
		 state.monMaxHumi = currentMaxHumi
		 state.monMinHumi = currentMinHumi
	} else if (day == "Wed") {
		 state.tueMaxTemp = currentMaxTemp
		 state.tueMinTemp = currentMinTemp
		 state.tueMaxHumi = currentMaxHumi
		 state.tueMinHumi = currentMinHumi
	} else if (day == "Thu") {
		 state.wedMaxTemp = currentMaxTemp
		 state.wedMinTemp = currentMinTemp
		 state.wedMaxHumi = currentMaxHumi
		 state.wedMinHumi = currentMinHumi
	} else if (day == "Fri") {
		 state.thuMaxTemp = currentMaxTemp
		 state.thuMinTemp = currentMinTemp
		 state.thuMaxHumi = currentMaxHumi
		 state.thuMinHumi = currentMinHumi
	} else if (day == "Sat") {
		 state.firMaxTemp = currentMaxTemp
		 state.firMinTemp = currentMinTemp
		 state.firMaxHumi = currentMaxHumi
		 state.firMinHumi = currentMinHumi
	} else if (day == "Sun") {
		 state.satMaxTemp = currentMaxTemp
		 state.satMinTemp = currentMinTemp
		 state.satMaxHumi = currentMaxHumi
		 state.satMinHumi = currentMinHumi
	}
	
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
	def day = new Date().format("EEE", location.timeZone)		
    
	def temphiloAttributes = displayTempHighLow ? (displayHumidHighLow ? getWordByLang("todays") + " " + getWordByLang("high") + "/" + getWordByLang("low") + ":  ${device.currentState('maxTemp')?.value}° / ${device.currentState('minTemp')?.value}°" : getWordByLang("todays") + " " + getWordByLang("high") + ": ${device.currentState('maxTemp')?.value}°  /  " + getWordByLang("low") + ": ${device.currentState('minTemp')?.value}°") : ""
	def humidhiloAttributes = displayHumidHighLow ? (displayTempHighLow ? "    ${device.currentState('maxHumidity')?.value}% / ${device.currentState('minHumidity')?.value}%" : getWordByLang("todays") + " " + getWordByLang("high") + ": ${device.currentState('maxHumidity')?.value}%  /  " + getWordByLang("low") + ": ${device.currentState('minHumidity')?.value}%") : ""
	sendEvent(name: "multiAttributesReport", value: "${temphiloAttributes}${humidhiloAttributes}", displayed: false)
	if (day == "Mon") {
		sendEvent(name: "1l", value: getWordByLang("tue"))
		sendEvent(name: "2l", value: getWordByLang("wed"))
		sendEvent(name: "3l", value: getWordByLang("thu"))
		sendEvent(name: "4l", value: getWordByLang("fri"))
		sendEvent(name: "5l", value: getWordByLang("sat"))
		sendEvent(name: "6l", value: getWordByLang("sun"))
		sendEvent(name: "1t", value: state.tueMaxTemp + "°\n" + state.tueMinTemp + "°")
		sendEvent(name: "2t", value: state.wedMaxTemp + "°\n" + state.wedMinTemp + "°")
		sendEvent(name: "3t", value: state.thuMaxTemp + "°\n" + state.thuMinTemp + "°")
		sendEvent(name: "4t", value: state.friMaxTemp + "°\n" + state.friMinTemp + "°")
		sendEvent(name: "5t", value: state.satMaxTemp + "°\n" + state.satMinTemp + "°")
		sendEvent(name: "6t", value: state.sunMaxTemp + "°\n" + state.sunMinTemp + "°")
		sendEvent(name: "1h", value: state.tueMaxHumi + "%\n" + state.tueMinHumi + "%")
		sendEvent(name: "2h", value: state.wedMaxHumi + "%\n" + state.wedMinHumi + "%")
		sendEvent(name: "3h", value: state.thuMaxHumi + "%\n" + state.thuMinHumi + "%")
		sendEvent(name: "4h", value: state.friMaxHumi + "%\n" + state.friMinHumi + "%")
		sendEvent(name: "5h", value: state.satMaxHumi + "%\n" + state.satMinHumi + "%")
		sendEvent(name: "6h", value: state.sunMaxHumi + "%\n" + state.sunMinHumi + "%")
	} else if (day == "Tue") {
		sendEvent(name: "6l", value: getWordByLang("mon"))
		sendEvent(name: "1l", value: getWordByLang("wed"))
		sendEvent(name: "2l", value: getWordByLang("thu"))
		sendEvent(name: "3l", value: getWordByLang("fri"))
		sendEvent(name: "4l", value: getWordByLang("sat"))
		sendEvent(name: "5l", value: getWordByLang("sun"))
		sendEvent(name: "6t", value: state.monMaxTemp + "°\n" + state.monMinTemp + "°")
		sendEvent(name: "1t", value: state.wedMaxTemp + "°\n" + state.wedMinTemp + "°")
		sendEvent(name: "2t", value: state.thuMaxTemp + "°\n" + state.thuMinTemp + "°")
		sendEvent(name: "3t", value: state.friMaxTemp + "°\n" + state.friMinTemp + "°")
		sendEvent(name: "4t", value: state.satMaxTemp + "\n" + state.satMinTemp + "°")
		sendEvent(name: "5t", value: state.sunMaxTemp + "\n" + state.sunMinTemp + "°")
		sendEvent(name: "6h", value: state.tueMaxHumi + "\n" + state.tueMinHumi + "%")
		sendEvent(name: "1h", value: state.wedMaxHumi + "\n" + state.wedMinHumi + "%")
		sendEvent(name: "2h", value: state.thuMaxHumi + "\n" + state.thuMinHumi + "%")
		sendEvent(name: "3h", value: state.friMaxHumi + "\n" + state.friMinHumi + "%")
		sendEvent(name: "4h", value: state.satMaxHumi + "\n" + state.satMinHumi + "%")
		sendEvent(name: "5h", value: state.sunMaxHumi + "\n" + state.sunMinHumi + "%")
	} else if (day == "Wed") {
		sendEvent(name: "6l", value: getWordByLang("tue"))
		sendEvent(name: "5l", value: getWordByLang("mon"))
		sendEvent(name: "l",  value: getWordByLang("tue"))
		sendEvent(name: "2l", value: getWordByLang("fri"))
		sendEvent(name: "3l", value: getWordByLang("sat"))
		sendEvent(name: "4l", value: getWordByLang("sun"))
		sendEvent(name: "6t", value: state.tueMaxTemp + "°\n" + state.tueMinTemp + "°")
		sendEvent(name: "5t", value: state.monMaxTemp + "°\n" + state.monMinTemp + "°")
		sendEvent(name: "1t", value: state.thuMaxTemp + "°\n" + state.thuMinTemp + "°")
		sendEvent(name: "2t", value: state.friMaxTemp + "°\n" + state.friMinTemp + "°")
		sendEvent(name: "3t", value: state.satMaxTemp + "°\n" + state.satMinTemp + "°")
		sendEvent(name: "4t", value: state.sunMaxTemp + "%°\n" + state.sunMinTemp + "°")
		sendEvent(name: "6h", value: state.tueMaxHumi + "%\n" + state.tueMinHumi + "%")
		sendEvent(name: "5h", value: state.monMaxHumi + "%\n" + state.monMinHumi + "%")
		sendEvent(name: "1h", value: state.thuMaxHumi + "%\n" + state.thuMinHumi + "%")
		sendEvent(name: "2h", value: state.friMaxHumi + "%\n" + state.friMinHumi + "%")
		sendEvent(name: "3h", value: state.satMaxHumi + "%\n" + state.satMinHumi + "%")
		sendEvent(name: "4h", value: state.sunMaxHumi + "%\n" + state.sunMinHumi + "%")
	} else if (day == "Thu") {
		sendEvent(name: "5l", value: getWordByLang("tue"))
		sendEvent(name: "6l", value: getWordByLang("wed"))
		sendEvent(name: "4l", value: getWordByLang("mon"))
		sendEvent(name: "1l", value: getWordByLang("fri"))
		sendEvent(name: "2l", value: getWordByLang("sat"))
		sendEvent(name: "3l", value: getWordByLang("sun"))
		sendEvent(name: "5t", value: state.tueMaxTemp + "°\n" + state.tueMinTemp + "°")
		sendEvent(name: "6t", value: state.wedMaxTemp + "°\n" + state.wedMinTemp + "°")
		sendEvent(name: "4t", value: state.monMaxTemp + "°\n" + state.monMinTemp + "°")
		sendEvent(name: "1t", value: state.friMaxTemp + "°\n" + state.friMinTemp + "°")
		sendEvent(name: "2t", value: state.satMaxTemp + "°\n" + state.satMinTemp + "°")
		sendEvent(name: "3t", value: state.sunMaxTemp + "°\n" + state.sunMinTemp + "°")
		sendEvent(name: "5h", value: state.tueMaxHumi + "%\n" + state.tueMinHumi + "%")
		sendEvent(name: "6h", value: state.wedMaxHumi + "%\n" + state.wedMinHumi + "%")
		sendEvent(name: "4h", value: state.monMaxHumi + "%\n" + state.monMinHumi + "%")
		sendEvent(name: "1h", value: state.friMaxHumi + "%\n" + state.friMinHumi + "%")
		sendEvent(name: "2h", value: state.satMaxHumi + "%\n" + state.satMinHumi + "%")
		sendEvent(name: "3h", value: state.sunMaxHumi + "%\n" + state.sunMinHumi + "%")
	} else if (day == "Fri") {
		sendEvent(name: "4l", value: getWordByLang("tue"))
		sendEvent(name: "5l", value: getWordByLang("wed"))
		sendEvent(name: "6l", value: getWordByLang("thu"))
		sendEvent(name: "3l", value: getWordByLang("mon"))
		sendEvent(name: "1l", value: getWordByLang("sat"))
		sendEvent(name: "2l", value: getWordByLang("sun"))
		sendEvent(name: "4t", value: state.tueMaxTemp + "°\n" + state.tueMinTemp + "°")
		sendEvent(name: "5t", value: state.wedMaxTemp + "°\n" + state.wedMinTemp + "°")
		sendEvent(name: "6t", value: state.thuMaxTemp + "°\n" + state.thuMinTemp + "°")
		sendEvent(name: "3t", value: state.monMaxTemp + "°\n" + state.monMinTemp + "°")
		sendEvent(name: "1t", value: state.satMaxTemp + "°\n" + state.satMinTemp + "°")
		sendEvent(name: "2t", value: state.sunMaxTemp + "°\n" + state.sunMinTemp + "°")
		sendEvent(name: "4h", value: state.tueMaxHumi + "%\n" + state.tueMinHumi + "%")
		sendEvent(name: "5h", value: state.wedMaxHumi + "%\n" + state.wedMinHumi + "%")
		sendEvent(name: "6h", value: state.thuMaxHumi + "%\n" + state.thuMinHumi + "%")
		sendEvent(name: "3h", value: state.monMaxHumi + "%\n" + state.monMinHumi + "%")
		sendEvent(name: "1h", value: state.satMaxHumi + "%\n" + state.satMinHumi + "%")
		sendEvent(name: "2h", value: state.sunMaxHumi + "%\n" + state.sunMinHumi + "%")
	} else if (day == "Sat") {
		sendEvent(name: "3l", value: getWordByLang("tue"))
		sendEvent(name: "4l", value: getWordByLang("wed"))
		sendEvent(name: "5l", value: getWordByLang("thu"))
		sendEvent(name: "6l", value: getWordByLang("fri"))
		sendEvent(name: "2l", value: getWordByLang("mon"))
		sendEvent(name: "1l", value: getWordByLang("sun"))
		sendEvent(name: "3t", value: state.tueMaxTemp + "°\n" + state.tueMinTemp + "°")
		sendEvent(name: "4t", value: state.wedMaxTemp + "°\n" + state.wedMinTemp + "°")
		sendEvent(name: "5t", value: state.thuMaxTemp + "°\n" + state.thuMinTemp + "°")
		sendEvent(name: "6t", value: state.friMaxTemp + "°\n" + state.friMinTemp + "°")
		sendEvent(name: "2t", value: state.monMaxTemp + "°\n" + state.monMinTemp + "°")
		sendEvent(name: "1t", value: state.sunMaxTemp + "°\n" + state.sunMinTemp + "°")
		sendEvent(name: "3h", value: state.tueMaxHumi + "%\n" + state.tueMinHumi + "%")
		sendEvent(name: "4h", value: state.wedMaxHumi + "%\n" + state.wedMinHumi + "%")
		sendEvent(name: "5h", value: state.thuMaxHumi + "%\n" + state.thuMinHumi + "%")
		sendEvent(name: "6h", value: state.friMaxHumi + "%\n" + state.friMinHumi + "%")
		sendEvent(name: "2h", value: state.monMaxHumi + "%\n" + state.monMinHumi + "%")
		sendEvent(name: "1h", value: state.sunMaxHumi + "%\n" + state.sunMinHumi + "%")
	} else if (day == "Sun") {
		sendEvent(name: "2l", value: getWordByLang("tue"))
		sendEvent(name: "3l", value: getWordByLang("wed"))
		sendEvent(name: "4l", value: getWordByLang("thu"))
		sendEvent(name: "5l", value: getWordByLang("fri"))
		sendEvent(name: "6l", value: getWordByLang("sat"))
		sendEvent(name: "1l", value: getWordByLang("mon"))
		sendEvent(name: "2t", value: state.tueMaxTemp + "°\n" + state.tueMinTemp + "°")
		sendEvent(name: "3t", value: state.wedMaxTemp + "°\n" + state.wedMinTemp + "°")
		sendEvent(name: "4t", value: state.thuMaxTemp + "°\n" + state.thuMinTemp + "°")
		sendEvent(name: "5t", value: state.friMaxTemp + "°\n" + state.friMinTemp + "°")
		sendEvent(name: "6t", value: state.satMaxTemp + "°\n" + state.satMinTemp + "°")
		sendEvent(name: "1t", value: state.monMaxTemp + "°\n" + state.monMinTemp + "°")
		sendEvent(name: "2h", value: state.tueMaxHumi + "%\n" + state.tueMinHumi + "%")
		sendEvent(name: "3h", value: state.wedMaxHumi + "%\n" + state.wedMinHumi + "%")
		sendEvent(name: "4h", value: state.thuMaxHumi + "%\n" + state.thuMinHumi + "%")
		sendEvent(name: "5h", value: state.friMaxHumi + "%\n" + state.friMinHumi + "%")
		sendEvent(name: "6h", value: state.satMaxHumi + "%\n" + state.satMinHumi + "%")
		sendEvent(name: "1h", value: state.monMaxHumi + "%\n" + state.monMinHumi + "%")
	} 
		
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

def sendCommand(options, _callback){
	def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: _callback])
    sendHubCommand(myhubAction)
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug jsonObj
        
 		sendEvent(name:"battery", value: jsonObj.properties.batteryLevel)
        sendEvent(name:"temperature", value: jsonObj.properties.temperature.value)
        sendEvent(name:"humidity", value: jsonObj.properties.relativeHumidity)
        sendEvent(name:"pressure", value: jsonObj.properties.atmosphericPressure.value / 1000)
        
        updateLastTime()
        checkNewDay()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
}

def getWordByLang(id){
	return LANGUAGE_MAP[id][state.language]
}
