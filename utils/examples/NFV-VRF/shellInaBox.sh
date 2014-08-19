#!/bin/bash

ctrl="localhost:8080"
sudo /etc/init.d/shellinabox stop
killall shellinaboxd

h1_ip="10.0.1.1"
h2_ip="10.0.2.2"
s1_ip="10.0.1.254"
s2_ip="10.0.2.254"

sudo shellinaboxd --disable-ssl -s /h1/:SSH:$h1_ip -s /h2/:SSH:$h2_ip &

#!/bin/bash
sudo ifconfig s1 $s1_ip
sudo ifconfig s4 $s2_ip

sudo route add $h1_ip s1
sudo route add $h2_ip s4

curl -d '{"switch": "00:00:00:00:00:00:00:01", "name":"flow-mod-12", "cookie":"0", "priority":"32768", "ether-type":"0x0800", "dst-ip":"$h1_ip", "src-ip":"s1_ip","active":"true", "actions":"output=3"}' http://$ctrl/wm/staticflowentrypusher/json
curl -d '{"switch": "00:00:00:00:00:00:00:01", "name":"flow-mod-11", "cookie":"0", "priority":"32768", "ether-type":"0x0800", "src-ip":"$h1_ip", "dst-ip":"s1_ip","active":"true", "actions":"output=65534"}' http://$ctrl/wm/staticflowentrypusher/json

curl -d '{"switch": "00:00:00:00:00:00:00:04", "name":"flow-mod-42", "cookie":"0", "priority":"32768", "ether-type":"0x0800", "dst-ip":"$h2_ip", "src-ip":"s2_ip","active":"true", "actions":"output=3"}' http://$ctrl/wm/staticflowentrypusher/json
curl -d '{"switch": "00:00:00:00:00:00:00:04", "name":"flow-mod-41", "cookie":"0", "priority":"32768", "ether-type":"0x0800", "src-ip":"$h2_ip", "dst-ip":"$s2_ip","active":"true", "actions":"output=65534"}' http://$ctrl/wm/staticflowentrypusher/json

echo ""
