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

b. synology nas
  make folder /docker/mi-connector
  Run Docker
  -> Registery 
  -> Search fison67/mi-connector
  -> Advanced Settings
  -> Volume tab -> folder -> Select mi-connector & Mount path '/config'
  -> Network tab -> Check 'use same network as Docker Host'
  -> Complete
 

Library
- https://github.com/aholstenson/miio
- https://github.com/zlargon/google-tts
- 
