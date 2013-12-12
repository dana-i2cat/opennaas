#!/bin/bash

#echo "Mapping openflow switches to OpenNaaS resource."
response=$(curl -H "Content-Type: application/xml" -X GET http://admin:123456@localhost:8888/opennaas/sdnnetwork/sdn1/ofprovisionnet/mapDeviceResource/$1/$2)
echo $response
