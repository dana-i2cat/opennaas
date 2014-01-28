#!/bin/bash
echo "Insert flows curl..."
#response=$(curl -H "Content-Type: application/octet-stream" -H "Accept: text/plain" -X POST http://admin:123456@localhost:8888/opennaas/vrf/routing/insertRouteFromFile/vrf.json)
response=$(curl -H "Content-Type: application/json"  -H "Accept: text/plain" -d @vrf.json http://admin:123456@localhost:8888/opennaas/vrf/routing/insertRouteFromFile/vrf)
echo $response
echo "Insert done..."
