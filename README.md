# Mi-Connector
Connector for Xiaomi Devices with [SmartThings](https://www.smartthings.com/getting-started)

Simplify setup process for xiaomi devices to SmartThings.<br/>
If Mi-Connector is installed, virtual devices is registered automatically by Mi Connector Smartapp.<br/>
You don't have to do anything to add xiaomi device in Smartthings IDE.

<br/><br/>
## Donation
If this project help you, you can give me a cup of coffee<br/>
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://paypal.me/fison67)
<br/><br/>

#### Example Video<br/>
[![Example](https://img.youtube.com/vi/CtPce-KBVcY/0.jpg)](https://www.youtube.com/watch?v=CtPce-KBVcY)

<br/><br/>

## History

### Version: 0.0.3
```
Added a graph in DTH & Web. [ Important!!! You must install a DB. ]
Added support devices.
Added function [find childs] in a gateway DTH.
Fixed bug when Zigbee device count is over 25, network error occured
```

<img src="./imgs/v.0.0.3/xiaomi_weather_graph_temperature_total.png?raw=true" title="Button" width="300px"/>
<img src="./imgs/v.0.0.3/mi_connector_graph2.png?raw=true" title="Button" width="500px"/>
https://github.com/fison67/mi_connector/blob/master/imgs/v.0.0.3/README.md

<br/>

### Version: 0.0.2
```
Added Log page. You can check for an invalid token or see if Mi Connector can not get a token.
Fixed bugs.
Added refresh feature.
Added support devices.
```


<br/><br/>
## Management Web Desktop Version.<br/>
![web-dashboard-total](./imgs/web-dashboard-total.png) 


<br/><br/>

## Management Web Mobile Version.<br/>
![total](./imgs/total.png) 


<br/><br/>
## DTH Example<br/>
<a href="./imgs/dth/README.md">![total2](./imgs/dth/xiaomi-dth-exam.png) 


<br/><br/>

# Install
#### Preparing
```
You need a Raspbery pi or Synology Nas to install Mi Connector API Server
```
<br/><br/>

## Install API Server<br/>
#### Raspberry pi<br/>
> You must install docker first.
```
sudo mkdir /docker
sudo mkdir /docker/mi-connector
sudo chown -R pi:pi /docker
docker pull fison67/mi-connector-arm:latest
docker run -d --restart=always -v /docker/mi-connector:/config --name=mi-connector-arm --net=host fison67/mi-connector-arm:latest
```

###### Synology nas<br/>
> You must install docker first.<br/>
[See the Manual](doc/install/synology/README.md) file for details<br/>
Current Beta version is 'mi-connector:test'
```
make folder /docker/mi-connector
Run Docker
-> Registery 
-> Search fison67/mi-connector
-> Advanced Settings
-> Volume tab -> folder -> Select mi-connector & Mount path '/config'
-> Network tab -> Check 'use same network as Docker Host'
-> Complete
```

###### Linux x86 x64<br/>
> You must install docker first.
```
sudo mkdir /docker
sudo mkdir /docker/mi-connector
docker pull fison67/mi-connector:latest
docker run -d --restart=always -v /docker/mi-connector:/config --name=mi-connector --net=host fison67/mi-connector:latest
```

<br/><br/>
 
## Install DTH<br/>
```
Go to the Smartthings IDE
Click My Device Handlers
Click Create New Device Handlers
Copy content of file in the devicetypes/fison67 folder to the area
Click Create
Loop until all of file is registered
```
<br/><br/>

## Install Smartapps<br/>
See the [Manual](doc/install/smartapp/README.md) file for details
```
Connect to the Smartthings IDE
Click My Smartapps
Click New Smartapp
Click From Code 
Copy content of mi_connector.groovy & Paste
Click Create
Click My Smartapps & Edit properties (Mi-Connector)
Enable OAuth
Update Click
```

<br/><br/>


## Install DB<br/>
#### Raspberry pi<br/>
> You must install docker first.
```
docker pull jsurf/rpi-mariadb
docker run -d --name mariadb -e MYSQL_ROOT_PASSWORD=password1234 -e TZ=Asia/Seoul -p 33006:3306 -d jsurf/rpi-mariadb
```
###### Synology nas<br/>
> You must install docker first.<br/>
Run Docker
-> Registery 
-> Search mariadb
-> Advanced Settings
-> Port setup tab -> local port 33006, container post 3306
-> Enviroment tab -> MYSQL_ROOT_PASSWORD (password1234),  TZ (Asia/Seoul)
-> Complete

#### Linux x86 x64<br/>
> You must install docker first.
```
docker pull mariadb
docker run -d --name mariadb -e MYSQL_ROOT_PASSWORD=password1234 -e TZ=Asia/Seoul -p 33006:3306 -d mariadb
```

<br/>
Fill the blank [db_url, db_port, db_password] on the Mi-connector web menu setup
If you don't change value, it must be a [ localhost, 33006, password1234 ].
Restart a Mi-connector container.
<br/><br/>

## Problem solving
#### Some of xiaomi product is not registered
> Some of product is not getting token automatically like Xiaomi Vacuum. You have to get token yourself.<br/>
And go to the 'Manage Device' > 'Device List' >  Click the add button > Fill out the blank. (IP & Token) > Click OK Button
<br/><br/>

#### How to get token
> https://www.home-assistant.io/components/vacuum.xiaomi_miio/#retrieving-the-access-token

## These devices is not working auto mode.
### You must add device manually
#### Management Web -> Manage Device -> Device List -> Fill the address & token -> Add Button
- Yeelight Desk Lamp
- Yeelight Color Bulb
- Yeelight White Bulb
- Yeelight LED Strip
- Mi Robot Vacuum
- Mi Air Quality Monitor (PM2.5)
- Mi Smart Power Strip 1
- Mi Smart Power Strip 2

## Support devices<br/>
#### Wi-Fi Version
| Type  | Model | Tested |
| ------------- | ------------- | ------------- |
| Xiaomi Air Purifier  | zhimi.airpurifier.m1  |   O |
|   | zhimi.airpurifier.v1  |   X |
|   | zhimi.airpurifier.v2  |   X |
|   | zhimi.airpurifier.v3  |   X |
|   | zhimi.airpurifier.v6  |   X |
|   | zhimi.airpurifier.ma2  |   X |
| Xiaomi Humidifier  | zhimi.humidifier.v1  |   X |
|   | zhimi.humidifier.ca1  |   O |
| Xiaomi Vacuum  | rockrobo.vacuum.v1  |   O |
|   | roborock.vacuum.s5  |   X |
| Xiaomi Power Socket  | chuangmi.plug.v1  |   X |
|   | chuangmi.plug.v2  |   X |
|   | chuangmi.plug.m1  |   O |
| Xiaomi Power Strip  | qmi.powerstrip.v1  |   X |
|   | zimi.powerstrip.v2  |   O |
| Xiaomi Air Monitor  | zhimi.airmonitor.v1  |   O |
| Xiaomi Gateway  | lumi.gateway.v2  |   X |
|   | lumi.gateway.v3  |   O |
| Xiaomi Fan  | zhimi.fan.v2  |   O |
|   | zhimi.fan.v3  |   O |
| Yeelight Mono  | yeelink.light.lamp1  |   X |
|   | yeelink.light.mono1  |   O |
|   | yeelink.light.ct2  |   O |
| Yeelight Color  | yeelink.light.color1  |   O |
|   | yeelink.light.color2  |   O |
|   | yeelink.light.strip1  |   O |
|  Yeelight Ceiling | yeelink.light.ceiling1  |   O |
|  Philips Ceiling | philips.light.ceiling  |   O |


#### Zigbee Version
| Type  | Model | Tested |
| ------------- | ------------- | ------------- |
| Xiaomi Motion Sensor | lumi.motion  |  X  |
|  | lumi.motion.aq2  |  O  |
| Xiaomi Door/Window Sensor | lumi.magnet  |  X  |
|  | lumi.magnet.aq2  |  O  |
| Xiaomi Weather Sensor | lumi.weather  |  O  |
|  | lumi.sensor_ht  |  O  |
| Xiaomi Power Socket | lumi.plug  |  O  |
| Xiaomi Button | lumi.switch  |  O  |
|  | lumi.switch.v2  |  O  |
|  | lumi.86sw1  |  O  |
|  | lumi.86sw2  |  O  |
| Xiaomi Cube | lumi.cube  |  O  |
| Xiaomi Wall Switch | lumi.ctrl_neutral1  |  O  |
| Xiaomi Wall Switch | lumi.ctrl_neutral2  |  O  |
| Xiaomi Smoke Sensor | lumi.smoke  |  O  |
| Xiaomi Gas Sensor | lumi.gas  |  O  |
| Xiaomi Water Sensor | lumi.water  |  O  |
| Xiaomi Curtain Motor | lumi.curtain  |  O  |


<img src="./imgs/product/button.jpg" title="Button" width="200px"><img src="./imgs/product/button_aq.png" title="Button Aqara" width="200px"><img src="./imgs/product/cube.png" title="Cube" width="200px">
<img src="./imgs/product/door.jpg" title="Door" width="200px"><img src="./imgs/product/door_aq.png" title="Door Aqara" width="200px"><img src="./imgs/product/fire.jpg" title="Fire Sensor" width="200px">
<img src="./imgs/product/gateway.jpg" title="Gateway" width="200px"><img src="./imgs/product/humidifier.jpg" title="Humidifier #1" width="200px"><img src="./imgs/product/humidifier2.png" title="Humidifier #2" width="200px">
<img src="./imgs/product/motion.jpg" title="Motion Sensor" width="200px"><img src="./imgs/product/motion_aq.png" title="Motion Aqara Sensor" width="200px"><img src="./imgs/product/smoke.jpg" title="Smoke Sensor" width="200px">
<img src="./imgs/product/socket.png" title="Power Socket" width="200px"><img src="./imgs/product/wall_socket_1.png" title="Wall Socket #1" width="200px"><img src="./imgs/product/wall_socket_2.png" title="Wall Socket #2" width="200px">
<img src="./imgs/product/weather.jpeg" title="Weather Sensor" width="200px"><img src="./imgs/product/weather_aq.png" title="Weather Aqara Sensor" width="200px"><img src="./imgs/product/xiaomi_

.jpg" title="Fan" width="200px">
<img src="./imgs/product/wireless_1.png" title="Button" width="200px"><img src="./imgs/product/wireless_2.png" title="Button" width="200px"><img src="./imgs/product/yeelight_color.jpg" title="Yeelight Color" width="200px">
<img src="./imgs/product/yeelight_mono.jpg" title="Yeelight Mono" width="200px"><img src="./imgs/product/air_purifier.jpg" title="Air Purifier" width="200px">
<img src="./imgs/product/air-monitor.jpg?raw=true" title="Air Monitor" width="200px">
<img src="./imgs/product/water.jpg?raw=true" title="Water Sensor" width="200px">
<img src="./imgs/product/curtain.png?raw=true" title="Curtain" width="200px">
<img src="./imgs/product/yeelight-color-e27.jpg?raw=true" title="Yeelight Color E27" width="200px">
<img src="./imgs/product/yeelight-mono-e27.png?raw=true" title="Yeelight Mono E27" width="200px">
<img src="./imgs/product/ceiling.jpg?raw=true" title="Ceiling" width="200px">
<img src="./imgs/product/power-strip.png?raw=true" title="Power Strip" width="200px">
<img src="./imgs/product/philips-ceiling.jpg?raw=true" title="Philips Ceiling Light" width="200px">


<br/><br/>
## Library
- https://github.com/aholstenson/miio
- https://github.com/zlargon/google-tts

<br/><br/>
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

<br/><br/>

## Thanks
- Many thanks to [ShinJjang](https://github.com/shin4299) for testing, updating DTH.
