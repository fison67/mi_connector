# mi_connector
Connector for Xiaomi with ST


Install
- wget https://raw.githubusercontent.com/fison67/mi_connector/master/install.sh

- chmod 755 install.sh
- sudo ./install.sh

- cd /app/mi_connector
- sudo pm2 start mi_connector.js


logging
- tail -500f /app/mi_connector/connector.log
