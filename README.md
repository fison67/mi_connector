# mi_connector
Connector for Xiaomi with ST

This makes you easy to setup xiaomi devices to Smartthings



Install. <br/>
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
 

Library
- https://github.com/aholstenson/miio
- https://github.com/zlargon/google-tts
- 
