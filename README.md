# Mi-Connector
Connector for Xiaomi with ST

This makes you easy to setup xiaomi devices to Smartthings
<br/><br/>
Example Video<br/>
[![Example](https://img.youtube.com/vi/CtPce-KBVcY/0.jpg)](https://www.youtube.com/watch?v=CtPce-KBVcY)

<br/><br/>



<br/><br/>
Management Web Desktop Version.<br/>
![web-dashboard](./imgs/web-dashboard.png) 

![web-dashboard-click](./imgs/web-dashboard-click.png) 

![web-search-result](./imgs/web-search-result.png) 

![web-dashboard-vacuum](./imgs/web-dashboard-vacuum.png) 

![web-dashboard-vacuum-custom](./imgs/web-dashboard-vacuum-custom.png) 

![web-setup](./imgs/web-setup.png) 

<br/><br/>

Management Web Mobile Version.<br/>
![web-setup](./imgs/main.jpg) 
![app-add-manual2](./imgs/app-add-manual2.jpg) 


<br/><br/>
DTH<br/>
Xiaomi Vacuum<br/>
![vacuum_](./imgs/vacuum_.png) 

Xiaomi Air Purifier<br/>
![air_pir](./imgs/air_pir.png) 

Xiaomi Gateway<br/>
![gateway](./imgs/gateway.jpg) 



# Install program. <br/>
a. raspberry pi<br/>
&nbsp;&nbsp;sudo mkdir /docker<br/>
&nbsp;&nbsp;sudo mkdir /docker/mi-connector<br/>
&nbsp;&nbsp;sudo chown -R pi:pi /docker<br/>
&nbsp;&nbsp;docker pull fison67/mi-connector-arm:0.0.1<br/>
&nbsp;&nbsp;docker run -d -v /docker/mi-connector:/config --net=host fison67/mi-connector-arm:0.0.1<br/>

b. synology nas<br/>
&nbsp;&nbsp;make folder /docker/mi-connector<br/>
&nbsp;&nbsp;Run Docker<br/>
&nbsp;&nbsp;&nbsp;-> Registery <br/>
&nbsp;&nbsp;&nbsp;-> Search fison67/mi-connector<br/>
&nbsp;&nbsp;&nbsp;-> Advanced Settings<br/>
&nbsp;&nbsp;&nbsp;-> Volume tab -> folder -> Select mi-connector & Mount path '/config'<br/>
&nbsp;&nbsp;&nbsp;-> Network tab -> Check 'use same network as Docker Host'<br/>
&nbsp;&nbsp;&nbsp;-> Complete<br/>

<br/><br/>
 
# Install DTH<br/>
&nbsp;a. Connect to the Smartthings IDE<br/>
&nbsp;b. Click My Device Handlers<br/>
&nbsp;c. Click Create New Device Handlers<br/>
&nbsp;d. Copy content of file in the dth folder to the area<br/>
&nbsp;e. Click Create<br/>
&nbsp;f. Loop<br/>

<br/><br/>

# Install Smartapps<br/>
&nbsp;a. Connect to the Smartthings IDE<br/>
&nbsp;b. Click My Smartapps<br/>
&nbsp;c. Click New Smartapp<br/>
&nbsp;d. Click From Code <br/>
&nbsp;e. Copy content of mi_connector.groovy to area<br/>
&nbsp;f. Click Create<br/>
&nbsp;e. Click My Smartapps & Edit properties (Mi-Connector)<br/>
&nbsp;g. Enable OAuth<br/>
&nbsp;h. Update Click<br/>

<br/><br/>

Support devices<br/>
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
# Library
- https://github.com/aholstenson/miio
- https://github.com/zlargon/google-tts


<br/><br/>
# Donation Button

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](paypal.me/fison67)


