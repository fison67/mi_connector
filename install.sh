mkdir /app
mkdir /app/mi_connector
chown -R pi:pi /app/mi_connector
cd /app/mi_connector

wget https://raw.githubusercontent.com/fison67/mi_connector/master/mi-connector.js
wget https://raw.githubusercontent.com/fison67/mi_connector/master/config.yaml
wget https://raw.githubusercontent.com/fison67/mi_connector/master/log.sh

chmod 755 /app/mi_connector/log.sh

npm install pm2 -g
npm install miio yaml request http log4js js-yaml node-schedule

