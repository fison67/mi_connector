const miio = require('miio');
var yaml = require('js-yaml');
var fs   = require('fs');
var request = require('request');
var http = require('http');
var url = require('url');
var qs  = require('querystring');
var path = require('path');

var config;
var deviceMap = {};

var log4js = require('log4js');
log4js.configure({
	appenders: { 
		cheese: { 
			type: 'file', 
			filename: 'connector.log' ,
			maxLogSize: 1000000,
			backups: 5
		},
	},
	categories: { default: { appenders: ['cheese'], level: 'error' } }
});

var logger = log4js.getLogger();
logger.level = 'debug';


const devices = miio.devices({
	cacheTime: 1 // 5 minutes. Default is 1800 seconds (30 minutes)
});

function initAPIServier(){
	try{
		var server = http.createServer(function (req, res) {   //create web server
			var urlObj = url.parse(req.url, true);
			var path = urlObj.pathname;
			if (path == '/requestDevice') { //check the URL of the current request
				res.writeHead(200, { 'Content-Type': 'application/json' });
				res.write( JSON.stringify({"result":"ok"}) )
				res.end();

				sendAllDevices();
			}else if(path == '/control'){
				var body = "";
					req.on('data', function (chunk) {
						body += chunk;
					});
				req.on('end', function () {
					var jsonObj = JSON.parse(body);
					res.writeHead(200, { 'Content-Type': 'application/json' });
					res.write( JSON.stringify({"result":"ok"}) )
					res.end();

					controlDevice(jsonObj);
				});
			}else if(path == '/get'){
                        	res.writeHead(200, { 'Content-Type': 'application/json' });
                                res.write( JSON.stringify({"result":"ok"}) )
                                res.end();

                                getDeviceStatus(urlObj.query.id);
			}
		}.bind(this));
		server.listen(config.connector.port); //6 - listen for any incoming requests
	}catch(e){
		logger.error("Init Api Server Error >> " + e + "\n" + new Error().stack);
	}
}

/**
* Send All Devices to ST
*/
function sendAllDevices(){
	logger.info("Send Device List to ST......");
	logger.info("------------------------");
	logger.info(deviceMap);
	logger.info("------------------------");
	try{
		var url = config.st.app_url + config.st.app_id + "/add?access_token=" + config.st.access_token;
		var time = 0;
		Object.keys(deviceMap).forEach(function(id) {
			var data = deviceMap[id];
			data['id'] = id;
			try{
				setTimeout(function(){
					request.post({url:url, form: data}, function(err,httpResponse,body){
						if(err){
							logger.error("Send Devices To ST Request Error >> " + e + "\n" + new Error().stack);
						}
					});
				}, time);
				time += 3000;
			}catch(e){
			  logger.info(e);
			}
		});
	}catch(e){
		logger.error("Send Devices To ST Error >> " + e + "\n" + new Error().stack);
	}
}

/**
* Control Device
*/
function controlDevice(jsonObj){
	var id = jsonObj.id;
	var cmd = jsonObj.cmd;
	var data = jsonObj.data;

	logger.info("Requested to control by ST [" + deviceMap[id].type + "] >> " + JSON.stringify(jsonObj) + "\n");
	
	var target = deviceMap[id];
	if(target == null){
		return;
	}

	miio.device({
		address: target.ip,
	}).then(device => {
//		const id = device.id.split(":")[1];
		var type = device.miioModel;

		try{
			// Gateway
			if(type.indexOf("lumi") > -1){
				var target = null;
				const children = device.children();
				for(const child of children) {
					var sid = child.id.split(":")[1];
					if(id == sid){
						target = child;
					}
				}
			 
				switch(cmd){
				case "power":
					if(target.matches('cap:power')) {
						var val = (data == "on" ? true : false);
						target.power(val);
					}
					break;
				case "color":
					if(target.matches('cap:colorable')) {
						target.color(data);
					}else{
						logger.warn(type + " is not supported to set color!!!! ID(" + id + ")");
					}
					break;
				case "brightness":
					if(target.matches('cap:brightness') || target.matches('cap:dimmable')) {
					  target.setBrightness(parseInt(data));
					}else{
						logger.warn(type + " is not supported to set brightness!!!! ID(" + id + ")");
					}
					break;
				}
				return;
			}
		}catch(e){
			logger.error("Control Device1 Error " + type + " >> " + e + "\n" + new Error().stack);
		}
    
		// Non zigbee devices
		try{
			switch(cmd){
			case "power":
				if(device.matches('cap:power')) {
					var val = (data == "on" ? true : false);
					device.power(val);
					notifyEvent(type, id, "power", val.toString());
				}
				break;
			case "buzzer":
				var val = (data == "on" ? true : false);
				device.buzzer(val);
				notifyEvent(type, id, "buzzer", val.toString());
				break;
			case "led":
				var val = (data == "on" ? true : false);
				device.led(val);
				notifyEvent(type, id, "led", val.toString());
				break;
			case "ledBrightness":
				device.ledBrightness(data);
				notifyEvent(type, id, "ledBrightness", data);
				break;
			case "mode":
				device.mode(data);
				notifyEvent(type, id, "mode", data);
				if(data == "favorite"){
					device.favoriteLevel().then(speed => {
						notifyEvent(type, id, "speed", speed.toString());
					});
				}
				break;
			case "speed":
				if(device.miioModel == "zhimi.airpurifier.m1" || device.miioModel == "zhimi.airpurifier.v1" || device.miioModel == "zhimi.airpurifier.v2" || device.miioModel == "zhimi.airpurifier.v3" || device.miioModel == "zhimi.airpurifier.v6"){
					device.mode("favorite");
					device.setFavoriteLevel(parseInt(data));
					notifyEvent(type, id, "mode", "favorite");
					notifyEvent(type, id, "power", "true");
				}else{
					device.speed(parseInt(data));
					notifyEvent(type, id, "speed", data);
				}
				break;
			case "color":
				if(device.matches('cap:colorable')) {
					device.color(data);
				}else{
					logger.warn(type + " is not supported to set color!!!! ID(" + id + ")");
				}
				break;
			case "brightness":
				if(device.matches('cap:brightness') || device.matches('cap:dimmable')) {
					device.setBrightness(parseInt(data));
				}else{
					logger.warn(type + " is not supported to set brightness!!!! ID(" + id + ")");
				}
				break;
			case "fanSpeed":
				device.fanSpeed(parseInt(data));
				break;
			case "spotClean":
				device.spotClean();
				break;
			case "charge":
				device.charge();
				break;
			case "clean":
				device.clean();
				break;
			case "stop":
				device.stop();
				break;
			case "start":
				device.start();
				break;
			case "pause":
				device.pause();
				break;
			}	
		}catch(e){
			logger.error("Control Device2 Error >> " + e + "\n" + new Error().stack);
		}
	});
}

function getDeviceStatus(id){
	var target = deviceMap[id];
	if(target == null){
		return;
	}

	miio.device({
		address: target.ip,
	}).then(device => {
		logger.info("Type >> " + device.miioModel);
		if(device.matches('type:air-purifier')) {

		}
	});
}

function init(){
//	logger.info("################# Config ###################");
//	logger.info("--------------------------------------------");
//	logger.info(config);
	logger.info("--------------------------------------------\n\n");
	logger.info("Init Program...................");
	devices.on('available', device => {
		try{
			var addr  = device.address;
			var model = device.device.miioModel;
			if(addr != undefined){
				logger.info("Model >> " + model + "(" + addr + ")");
				switch(model){
				case "lumi.gateway.v3":
					initGatewayV3(addr);
					break;
				case "zhimi.airpurifier.m1":
					initAirpurifier(addr);
					break;
				case "zhimi.airpurifier.v1":
					initAirpurifier(addr);
					break;
				case "zhimi.airpurifier.v2":
					initAirpurifier(addr);
					break;
				case "zhimi.airpurifier.v3":
					initAirpurifier(addr);
					break;
				case "zhimi.airpurifier.v6":
					initAirpurifier(addr);
					break;
				case "zhimi.humidifier.v1":
					initHumidifier(addr);
					break;
				case "yeelink.light.lamp1":
					initLight(addr);
					break;
				case "yeelink.light.mono1":
					initLight(addr);
					break;
				case "yeelink.light.color1":
					initLight(addr);
					break;
				case "yeelink.light.strip1":
					initLight(addr);
					break;
				case "philips.light.sread1":
					initLight(addr);
					break;
				case "philips.light.bulb":
					initLight(addr);
					break;
				case "rockrobo.vacuum.v1":
					initVacuum(addr);
					break;
				}
			}
		}catch(e){
			logger.error("Init Error >> " + e + "\n" + new Error().stack);
		}
	});
}

function loadConfig(){
	try {
		var doc = yaml.safeLoad(fs.readFileSync('./config.yaml', 'utf8'));
		config = doc;
	} catch (e) {
		logger.error("Load Config Error >> " + e);
	}
}

function initVacuum(ip){
	logger.info("Init Vacuum\n");
	miio.device({
		address: ip,
	}).then(device => {
		try{
			var id = device.id.split(":")[1];
			var type = device.miioModel;
			device.on('stateChanged', state=>{
				logger.info("Notify Vacuum >> id(" + id + "):type(" + type + ") state=" + JSON.stringify(state) + " >> [" + state.value.toString() + "]\n");
				notifyEvent(type, id, state.key, state.value.toString());
			});
		}catch(e){
			logger.error("Init Vacuum Error " + device.miioModel + " >> " + e + "\n" + new Error().stack);
		}
	});
}

function initLight(ip){
	logger.info("Init initLight\n");
	miio.device({
		address: ip,
	}).then(device => {
		try{
			var id = device.id.split(":")[1];
			var type = device.miioModel;
			device.on('stateChanged', state=>{
				logger.info("Notify Light >> id(" + id + "):type(" + type + ") state=" + JSON.stringify(state) + " >> [" + state.value.toString() + "]\n");
				notifyEvent(type, id, state.key, state.value.toString());
			});
		}catch(e){
			logger.error("Init Light Error " + device.miioModel + " >> " + e + "\n" + new Error().stack);
		}
	});
}

function initAirpurifier(ip){
	logger.info("Init Airpurifier\n");
	miio.device({
		address: ip,
	}).then(device => {

		try{
			var id = device.id.split(":")[1];
			var type = device.miioModel;
			var modes = device.modes();
			device.modes().then(list=>{
				var modes = [];
				for(var i=0; i<list.length; i++){
					modes.push(list[i].id);
				}

				deviceMap[id] = {'type': device.miioModel, 'mode':modes, 'ip':ip}
			});


			device.on('stateChanged', state=>{
				logger.info("Notify Airpurifier >> id(" + id + "):type(" + type + ") state=" + JSON.stringify(state) + " >> [" + state.value.toString() + "]\n");
				try{
					notifyEvent(type, id, state.key, state.value.toString());
				}catch(e){
					logger.error("Air Pirifier Notify Error " + device.miioModel + " >> " + e + "\n" + new Error().stack);
				}
			});
		}catch(e){
			logger.error("Air Pirifier Init Error " + device.miioModel + " >> " + e + "\n" + new Error().stack);
		}
	});
}

function initHumidifier(ip){
    logger.info("Init Humidifier (" + ip + ")\n");
    miio.device({
		address: ip,
    }).then(device => {

		var id = device.id.split(":")[1];
		var type = device.miioModel;

		device.on('stateChanged', state=>{
			logger.info("Humidifier Notify >> id(" + id + "):type(" + type + ") state=" + JSON.stringify(state) + " >> [" + state.value.toString() + "]\n");

			try{
				notifyEvent(device.miioModel, id, state.key, state.value.toString()); 
			}catch(e){
				logger.error("Humidifier Notify Error " + device.miioModel + " >> " + e + "\n" + new Error().stack);
			}
		});
	});
}

function initGatewayV3(ip){
	logger.info("Init Gateway V3 (" + ip + ")\n");
	miio.device({
		address: ip,
	}).then(device => {
		var id = device.id.split(":")[1];
		var type = device.miioModel;
		logger.info("Gateway id >> " + id + ", type >> " + type);
		deviceMap[id] = {'type': device.miioModel, 'ip':ip}

		const children = device.children();
		for(const child of children) {
			var sid = child.id.split(":")[1];
			if(child.miioModel != null){
				deviceMap[sid] = {'type': child.miioModel, 'ip':ip}
			}

			child.on('action', data=>{
				try{
					var id = child.id.split(":")[1];
					var type = child.miioModel;
					logger.info("Action Data >> " + JSON.stringify(data));
					notifyEvent2(type, id, "action", data.action.toString(), data.data.toString()); 
					
					logger.info("Notify Gateway ID(" + id + "):type(" + type  + ") (" + JSON.stringify(data) + ") >> [" + data.action.toString() + "]\n");
				}catch(e){
					logger.error("Zigbee Action Notify Error " + type + " >> " + e + "\n" + new Error().stack);
				}
			});

			child.on('stateChanged', state=>{
				try{
					var id = child.id.split(":")[1];
					var type = child.miioModel;
					if(type == null){
						type = device.miioModel;
					}

					var value = state.value.toString();
					if(state.key == "color"){
						value = state.value.values.toString();
					}
					notifyEvent(type, id, state.key, value);

					logger.info("Notify Zigbee ID(" + id + "):type(" + type + ") key >> " + state.key  + " (" + JSON.stringify(state) + ") >> [" + value+ "]\n");
				}catch(e){
					logger.error("Zigbee Notify Error " + child.miioModel + " >> " + e + "\n" + new Error().stack);
				}

				// Set Force Motion Off
				try{
					if(child.miioModel == "lumi.motion.aq2" || child.miioModel == "lumi.motion"){
						setTimeout(function(){
							child.updateMotion(false);
						}, 4900);
					}
				}catch(e){}
			});
		}
	}).catch(err => logger.info('Error occurred:', err));
}

function getNotifyURL(){
	return config.st.app_url + config.st.app_id + "/update?access_token=" + config.st.access_token
}

function notifyEvent(type, id, key, data){
	var data = makeNotifyData(type, id, key, data);
	requestNotify(data);
}

function notifyEvent2(type, id, key, data, subData){
	var data = makeNotifyData(type, id, key, data);
	data['subData'] = subData;
	requestNotify(data);
}

function makeNotifyData(type, id, key, data){
	var data = {
		type: type,
		id: id,
		cmd: "notify",
		key: key,
		data: data
	}
	return data;
}

function requestNotify(data){
	request.post({url:getNotifyURL(), form: data}, function(err,httpResponse,body){ 
		if(err){
			logger.info(err);
		}
	});
}

try{
	loadConfig();
	
	init();
	
	initAPIServier();
}catch(e){
	logger.fatal("Run Program Error!!! >> " + e);
}
