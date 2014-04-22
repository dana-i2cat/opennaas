#!/bin/bash
echo "Insert flows curl..."
#response=$(curl -H "Content-Type: application/octet-stream" -H "Accept: text/plain" -X POST http://admin:123456@localhost:8888/opennaas/vrf/routing/insertRouteFromFile/vrf.json)
#file=vrf.json
file="demo_odl.json"
if [ ! -z "$1" ]
	then
		file="$1"
fi
echo $file
response=$(curl -H "Content-Type: application/json"  -H "Accept: text/plain" -d @$file http://admin:123456@localhost:8888/opennaas/vrf/staticrouting/insertRouteFromFile/vrf)
echo $response
echo "Insert done..."
