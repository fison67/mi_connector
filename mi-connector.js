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
	cacheTime: 300 // 5 minutes. Default is 1800 seconds (30 minutes)
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
			}
		}.bind(this));
		server.listen(config.connector.port); //6 - listen for any incoming requests
	}catch(e){
		logger.error("Init Api Server Error >> " + e);
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
							logger.error(err);
						}
					});
				}, time);
				time += 3000;
			}catch(e){
			  logger.info(e);
			}
		});
	}catch(e){
		logger.error("Send Device To ST Error >> " + e);
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
					logger.debug("Sid >> " + sid + ", id >> " + id);
					if(id == sid){
						logger.debug(child);
						target = child;
					}
				}
			 
				switch(cmd){
				case "power":
					var val = (data == "on" ? true : false);
					target.power(val);
					break;
				case "color":
					if(target.matches('cap:colorable')) {
						target.color(data);
					}else{
						logger.warn(type + " is not supported to set color!!!! ID(" + id + ")");
					}
					break;
				case "brightness":
					if(target.matches('cap:colorable')) {
					  target.setBrightness(parseInt(data));
					}else{
						logger.warn(type + " is not supported to set brightness!!!! ID(" + id + ")");
					}
					break;
				}
				return;
			}
		}catch(e){
			logger.error("Control Device1 Error >> " + e);
		}
    
		// Non zigbee devices
		try{
			switch(cmd){
			case "power":
				var val = (data == "on" ? true : false);
				device.power(val);
				notifyEvent(type, id, "power", val.toString());
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
				device.mode("favorite");
				device.setFavoriteLevel(parseInt(data));
				notifyEvent(type, id, "power", "true");
				notifyEvent(type, id, "mode", "favorite");
				break;
			case "color":
				if(device.matches('cap:colorable')) {
					device.color(data);
				}else{
					logger.warn(type + " is not supported to set color!!!! ID(" + id + ")");
				}
				break;
			case "brightness":
				if(device.matches('cap:colorable')) {
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
			}	
		}catch(e){
			logger.error("Control Device2 Error >> " + e)
		}
	});
}

function init(){
	logger.info("################# Config ###################");
	logger.info("--------------------------------------------");
	logger.info(config);
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
        }
      }
    }catch(e){
		logger.error(e);
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

function initAirpurifier(ip){
	logger.info("Init Airpurifier\n");
	miio.device({
		address: ip,
	}).then(device => {

    try{
		var id = device.id.split(":")[1];
		var modes = device.modes();
		device.modes().then(list=>{
			var modes = [];
			for(var i=0; i<list.length; i++){
				modes.push(list[i].id);
			}

			deviceMap[id] = {'type': device.miioModel, 'mode':modes, 'ip':ip}
		});


		device.on('stateChanged', state=>{
			logger.info("Notify Airpurifier >> id(" + id + ") state=" + JSON.stringify(state) + " >> [" + state.value.toString() + "]\n");
			try{
			/*	var url = getNotifyURL();
				var data = {
					type: device.miioModel,
					id: id,
					cmd: "notify",
					key: state.key,
					data: state.value.toString()
				}
				if(state.key == "mode"){
					data['modes'] = deviceMap[id].mode.toString();
				}
				request.post({url:url, form: data}, function(err,httpResponse,body){ 
					if(err){
						logger.error(err);
					}
				});
			*/	
				notifyEvent(device.miioModel, id, state.key, state.value.toString());
				
			}catch(e){
				logger.error(e);
			}
		});
    }catch(e){
		logger.error(e);
    }
  });
}

function initHumidifier(ip){
    logger.info("Init Humidifier");
    miio.device({
		address: ip,
    }).then(device => {

		var id = device.id.split(":")[1];

		device.on('stateChanged', state=>{
			logger.info("Humidifier Notify >> id(" + id + ") state=" + JSON.stringify(state) + " >> [" + state.value.toString() + "]\n");

			try{
				notifyEvent(device.miioModel, id, state.key, state.value.toString()); 
			}catch(e){
				logger.error("Humidifier Notify Error >> " + e);
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
		logger.info("Gateway id >> " + id + ", type >> " + device.miioModel);
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
					
					logger.info("Notify Gateway ID(" + id + ") " + type  + " (" + JSON.stringify(data) + ") >> [" + data.action.toString() + "]\n");
				}catch(e){
					logger.error("Zigbee Action Notify Error >> " + e + "\n" + new Error().stack);
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

					logger.info("Notify Zigbee ID(" + id + ") " + type + " key >> " + state.key  + " (" + JSON.stringify(state) + ") >> [" + value+ "]\n");
				}catch(e){
					logger.error("Zigbee Notify Error >> " + e + "\n" + new Error().stack);
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






