# mi_connector
Connector for Xiaomi with ST

This makes you easy to setup xiaomi devices to Smartthings



Install.
a. raspberry pi
__  sudo mkdir /docker
__  sudo mkdir /docker/mi-connector
__  sudo chown -R pi:pi /docker
__  docker pull fison67/mi-connector-arm:0.0.1
__  docker run -d -v /docker/mi-connector:/config --net=host fison67/mi-connector-arm:0.0.1

b. synology nas
__  make folder /docker/mi-connector
__  Run Docker
__  -> Registery 
__  -> Search fison67/mi-connector
__  -> Advanced Settings
__  -> Volume tab -> folder -> Select mi-connector & Mount path '/config'
__  -> Network tab -> Check 'use same network as Docker Host'
__  -> Complete
 

Library
- https://github.com/aholstenson/miio
- https://github.com/zlargon/google-tts
- 
