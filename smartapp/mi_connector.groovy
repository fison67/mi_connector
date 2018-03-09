/**
 *  Mi Connector (v.0.0.1)
 *
 *  Authors
 *   - fison67@nate.com
 *  Copyright 2018
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
 
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field



definition(
    name: "Mi Connector",
    namespace: "fison67",
    author: "fison67",
    description: "A Connector between Xiaomi and ST",
    category: "My Apps",
    iconUrl: "https://github.com/fison67/mi_connector/raw/master/icon.png",
    iconX2Url: "https://github.com/fison67/mi_connector/raw/master/icon.png",
    iconX3Url: "https://github.com/fison67/mi_connector/raw/master/icon.png",
    oauth: true
)

preferences {
   page(name: "mainPage")
   page(name: "requestPage")
}


def mainPage() {
    dynamicPage(name: "mainPage", title: "Home Assistant Manage", nextPage: null, uninstall: true, install: true) {
   		section("Request New Devices"){
        	input "address", "string", title: "Server address", required: true
        	href "requestPage", title: "Request New Devices", description:""
        }
       	section() {
            paragraph "View this SmartApp's configuration to use it in other places."
            href url:"${apiServerUrl("/api/smartapps/installations/${app.id}/config?access_token=${state.accessToken}")}", style:"embedded", required:false, title:"Config", description:"Tap, select, copy, then click \"Done\""
       }
    }
}

def requestPage(){
	log.debug "Executing requestPage"
    getDataList()
    
    dynamicPage(name: "requestPage", title:"Get Xiaomi Devices", nextPage: null) {
        section("Please wait for the API to answer, this might take a couple of seconds.") {
           paragraph "Quit after 5 seconds...."
        }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"

    initialize()
    
    if (!state.accessToken) {
        createAccessToken()
    }
}

def updated() {
    log.debug "Updated with settings: ${settings}"

    // Unsubscribe from all events
    unsubscribe()
    // Subscribe to stuff
    initialize()
}

def initialize() {
	log.debug "initialize"
}

def dataCallback(physicalgraph.device.HubResponse hubResponse) {
    def msg, json, status
    try {
        msg = parseLanMessage(hubResponse.description)
        status = msg.status
        json = msg.json
        log.debug "${json}"
        state.latestHttpResponse = status
    } catch (e) {
        logger('warn', "Exception caught while parsing data: "+e);
    }
}

def getDataList(){
    def options = [
     	"method": "GET",
        "path": "/requestDevice",
        "headers": [
        	"HOST": settings.address,
            "Content-Type": "application/json"
        ]
    ]
    def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: dataCallback])
    sendHubCommand(myhubAction)
}

def addDevice(){
	def id = params.id
    def type = params.type
    
    log.debug("Type >> ${type}");
	
    def dni = "mi-connector-" + id.toLowerCase()
    
    def chlid = getChildDevice(dni)
    if(!child){
        def dth = null
        def name = null

        if(params.type == "zhimi.airpurifier.m1" || params.type == "zhimi.airpurifier.v1" || params.type == "zhimi.airpurifier.v2" || params.type ==  "zhimi.airpurifier.v3" || params.type ==  "zhimi.airpurifier.v6"){
            dth = "Xiaomi Air Purifier";
            name = "Xiaomi Air Purifier";
        }else if(params.type == "lumi.gateway.v2"){
        	dth = "Xiaomi Gateway";
            name = "Xiaomi Gateway V2";
        }else if(params.type == "lumi.gateway.v3"){
        	dth = "Xiaomi Gateway";
            name = "Xiaomi Gateway V3";
        }else if(params.type == "lumi.magnet" || params.type == "lumi.magnet.aq2"){
        	dth = "Xiaomi Door";
            name = "Xiaomi Door";
        }else if(params.type == "lumi.motion" || params.type == "lumi.motion.aq2"){
        	dth = "Xiaomi Motion";
            name = "Xiaomi Motion";
        }else if(params.type == "lumi.switch" || params.type == "lumi.switch.v2" || params.type == "lumi.86sw1" || params.type == "lumi.86sw2"){
        	dth = "Xiaomi Switch";
            name = "Xiaomi Switch";
        }else if(params.type == "zhimi.humidifier.v1"){
        	dth = "Xiaomi Humidifier";
            name = "Xiaomi Humidifier";
        }else if(params.type == "yeelink.light.lamp1" || params.type == "yeelink.light.mono1" || params.type == "yeelink.light.color1" || params.type == "yeelink.light.strip1"){
        	dth = "Xiaomi Light";
            name = "Xiaomi Light";
        }else if(params.type == "philips.light.sread1" || params.type == "philips.light.bulb"){
        	dth = "Xiaomi Light";
            name = "Philips Light";
        }else if(params.type == "rockrobo.vacuum.v1"){
        	dth = "Xiaomi Vacuums";
            name = "Xiaomi Vacuums";
        }else if(params.type == "qmi.powerstrip.v1" || params.type == "zimi.powerstrip.v2"){
        	dth = "Xiaomi Power Strip";
            name = "Xiaomi Power Strip";
        }else if(params.type == "chuangmi.plug.v1" || params.type == "chuangmi.plug.v2" || params.type == "chuangmi.plug.m1" || params.type == "lumi.plug"){
        	dth = "Xiaomi Power Plug";
            name = "Xiaomi Power Plug";
        }else if(params.type == "lumi.ctrl_neutral1" || params.type == "lumi.ctrl_ln1" ){
     //   	dth = "Xiaomi Wall Switch1";
     //       name = "Xiaomi Wall Switch1";
        }else if(params.type == "lumi.ctrl_neutral2" || params.type == "lumi.ctrl_ln2"){
     //   	dth = "Xiaomi Wall Switch2";
    //        name = "Xiaomi Wall Switch2";
        }else if(params.type == "lumi.sensor_ht"){
        	dth = "Xiaomi Sensor HT";
            name = "Xiaomi Sensor HT";
        }
        
        if(dth == null){
        	return;
        }
        
        try{
            def childDevice = addChildDevice("fison67", dth, dni, location.hubs[0].id, [
                "label": name
            ])    
            childDevice.setInfo(settings.address, id)
            log.debug "ADD Device >> ${type} DNI=${dni}"
        }catch(e){
        	console.log("ADD Device Error >> " + e);
        }
    }

}

def updateDevice(){
	log.debug "Mi >> ${params.type} (${params.key}) >> ${params.cmd}"
    def id = params.id
    def dni = "mi-connector-" + id.toLowerCase()
    def chlid = getChildDevice(dni)
    if(chlid){
		chlid.setStatus(params)
    }
}

def authError() {
    [error: "Permission denied"]
}

def renderConfig() {
    def configJson = new groovy.json.JsonOutput().toJson([
        description: "Mi Connector API",
        platforms: [
            [
                platform: "SmartThings Mi Connector",
                name: "Mi Connector",
                app_url: apiServerUrl("/api/smartapps/installations/"),
                app_id: app.id,
                access_token:  state.accessToken
            ]
        ],
    ])

    def configString = new groovy.json.JsonOutput().prettyPrint(configJson)
    render contentType: "text/plain", data: configString
}

mappings {
    if (!params.access_token || (params.access_token && params.access_token != state.accessToken)) {
        path("/config")                         { action: [GET: "authError"] }
        path("/update")                         { action: [POST: "authError"]  }
        path("/add")                         	{ action: [POST: "authError"]  }

    } else {
        path("/config")                         { action: [GET: "renderConfig"]  }
        path("/update")                         { action: [POST: "updateDevice"]  }
        path("/add")                         	{ action: [POST: "addDevice"]  }
    }
}
