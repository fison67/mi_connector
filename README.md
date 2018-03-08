# mi_connector
Connector for Xiaomi with ST


Install
1. Make folder
sudo mkdir /app
sudo mkdir /app/mi_connector

2. pm2 install
sudo npm install pm2 -g

3. library install
cd /app/mi_connector
npm install miio yaml request http log4js

4. Donwload mi_connector.js
cd /app/mi_connector
wget ....

5. Make config.yaml
cd /app/mi_connector
sudo nano config.yaml
ctrl+x -> y -> Enter
---------------------------------------------
st:
  app_url: https://.......
  app_id: 2..........
  access_token: ...........

connector:
  port: 11111
---------------------------------------------

6. Run Program
cd /app/mi_connector
pm2 start mi_connector
