#!/bin/bash
echo "Insert flows curl..."
response=$(curl -H "Content-Type: application/octet-stream" -H "Accept: text/plain" -X POST http://admin:123456@localhost:8888/opennaas/vrf/routing/insertRouteFromFile/vrf.json)
echo $response
echo "Insert done..."

#echo "Mapping openflow switches to OpenNaaS resource."
#curl -H "Content-Type: application/xml" -X GET http://admin:123456@localhost:8888/opennaas/sdnnetwork/sdn1/ofprovisionnet/mapDeviceResource/00:00:00:00:00:00:00:01
