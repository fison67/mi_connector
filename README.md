# Mi-Connector
Connector for Xiaomi Devices with ST

This makes you easy to setup xiaomi devices to Smartthings.<br/>
If Mi-Connector is installed, virtual devices is registered automatically by Mi Connector Smartapp.<br/>
You don't have to do something to add xiaomi device in Smartthings IDE.

<br/><br/>
Example Video<br/>
[![Example](https://img.youtube.com/vi/CtPce-KBVcY/0.jpg)](https://www.youtube.com/watch?v=CtPce-KBVcY)

<br/><br/>



<br/><br/>
## Management Web Desktop Version.<br/>
#### Dashboard
![web-dashboard](./imgs/web-dashboard.png) 
#### Params of Device
![web-dashboard-click](./imgs/web-dashboard-click.png) 
#### Search Xiaomi Devices manually
![web-search-result](./imgs/web-search-result.png) 
#### SoundPack setup of Xiaomi Vacuum
![web-dashboard-vacuum](./imgs/web-dashboard-vacuum.png) 

![web-dashboard-vacuum-custom](./imgs/web-dashboard-vacuum-custom.png) 
#### Mi Connector System Setup
![web-setup](./imgs/web-setup.png) 

<br/><br/>

## Management Web Mobile Version.<br/>
![web-setup](./imgs/main.jpg) 
![app-add-manual2](./imgs/app-add-manual2.jpg) 


<br/><br/>
# DTH Example<br/>
Xiaomi Vacuum<br/>
![vacuum_](./imgs/vacuum_.png) 

Xiaomi Air Purifier<br/>
![air_pir](./imgs/air_pir.png) 

Xiaomi Gateway<br/>
![gateway](./imgs/gateway.jpg) 



## Install API Server<br/>
a. raspberry pi<br/>
```
sudo mkdir /docker
sudo mkdir /docker/mi-connector
sudo chown -R pi:pi /docker
docker pull fison67/mi-connector-arm:0.0.1
docker run -d -v /docker/mi-connector:/config --net=host fison67/mi-connector-arm:0.0.1
```

b. synology nas<br/>
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
<br/><br/>
 
## Install DTH<br/>
```
Go to the Smartthings IDE
Click My Device Handlers
Click Create New Device Handlers
Copy content of file in the dth folder to the area
Click Create
Loop until all of file is registered
```
<br/><br/>

## Install Smartapps<br/>
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

## Support devices<br/>
- xiaomi air purifier<br/>
- xiaomi humidifier<br/>
- xiaomi vacuum<br/>
- xiaomi air monitor<br/>
- xiaomi gateway<br/>
&nbsp;&nbsp;- xiaomi motion sensor<br/>
&nbsp;&nbsp;- xiaomi door/window sensor<br/>
&nbsp;&nbsp;- xiaomi button<br/>
&nbsp;&nbsp;- xiaomi cube<br/>
&nbsp;&nbsp;- xiaomi socket<br/>
&nbsp;&nbsp;- xiaomi weather<br/>

<br/><br/>
## Library
- https://github.com/aholstenson/miio
- https://github.com/zlargon/google-tts

<br/><br/>
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

<br/><br/>
## Donation Button

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://paypal.me/fison67)


