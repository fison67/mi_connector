/**
 *  Xiaomi Light Ceiling Moon (v.0.0.2)
 *
 * MIT License
 *
 * Copyright (c) 2019 fison67@nate.com
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

import java.util.ArrayList

metadata {
	definition (name: "Xiaomi Light Ceiling Moon", namespace: "fison67", author: "fison67", vid: "generic-switch", ocfDeviceType: "oic.d.switch") {
        capability "Switch"	
        capability "Light"
	}
    
	preferences { }
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setStatus(key, data){
	log.debug "${key} >> ${data}"
    sendEvent(name:key, value: data )
}

def on(){
    def body = [
        "id": parent._getID(),
        "cmd": "scene",
        "data": 5
    ]
    def options = parent.makeCommand(body)
    parent.sendCommand(options, null)
}

def off(){
    def body = [
        "id": parent._getID(),
        "cmd": "scene",
        "data": 1
    ]
    def options = parent.makeCommand(body)
    parent.sendCommand(options, null)
}

def updated() {}
