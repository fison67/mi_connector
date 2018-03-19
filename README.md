# mi_connector
Connector for Xiaomi with ST

This makes you easy to setup xiaomi devices to Smartthings



Install. <br/>
a. raspberry pi<br/>
  sudo mkdir /docker<br/>
  sudo mkdir /docker/mi-connector<br/>
  sudo chown -R pi:pi /docker<br/>
  docker pull fison67/mi-connector-arm:0.0.1<br/>
  docker run -d -v /docker/mi-connector:/config --net=host fison67/mi-connector-arm:0.0.1<br/>

b. synology nas<br/>
  make folder /docker/mi-connector<br/>
  Run Docker<br/>
  -> Registery <br/>
  -> Search fison67/mi-connector<br/>
  -> Advanced Settings<br/>
  -> Volume tab -> folder -> Select mi-connector & Mount path '/config'<br/>
  -> Network tab -> Check 'use same network as Docker Host'<br/>
  -> Complete<br/>
 

Library
- https://github.com/aholstenson/miio
- https://github.com/zlargon/google-tts
- 
