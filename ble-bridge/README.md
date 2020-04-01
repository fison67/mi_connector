# ESP32 Bluetooth Bridge

This is a Bluetooth Bridge for Xiaomi Bluetooth Devices.<br/>
If you use a this, your Nas no needs a bluetooth dongle.<br/>
You can use a multiple ESP32 boards.<br/>
It extends a bluetooth range.<br/><br/><br/>


## Supported Device
a. Temperature-Humidity<br/>
b. Flora<br/>
c. E-ink<br/>

## Prerequisites
a. ESP32<br/>
b. esphome-flasher (https://github.com/esphome/esphome-flasher/releases)<br/>
c. ESP32 Firmware [file](/ble-bridge/mi-esp32.bin)<br/>


## Installation
Run a esphome-flasher & Select a firmware file then press a Flash ESP Button
<img src="./1.png?raw=true"><br/><br/><br/>
<img src="./2.png?raw=true"><br/><br/><br/>
If flashing is completed, it comes out as Run AP Mode, as in the log below:<br/>
IP is 192.168.4.1.<br/>
<img src="./3.png?raw=true"><br/><br/><br/>
Access Wi-Fi starting with MI-AP-XXXX; (Password 12341234)<br/>
<img src="./4.png?raw=true"><br/><br/><br/>
Open a browser & Go to 192.168.4.1 & Press a Load Button<br/>
<img src="./5.png?raw=true"><br/><br/><br/>
Select an your Wi-Fi<br/>
<img src="./6.png?raw=true"><br/><br/><br/>
Fill out the Wi-Fi password & Press a Save Button<br/>
<img src="./7.png?raw=true"><br/><br/><br/>
Then the ESP32 is rebooted, and the broadcast data is received and sent.<br/>
<img src="./8.png?raw=true"><br/><br/><br/>
This is mi connector dashboard page.<br/>
<img src="./9.png?raw=true"><br/><br/><br/>
If you use a ESP32 board, you must disable a bluetooth enable as below<br/>
<img src="./10.png?raw=true">
