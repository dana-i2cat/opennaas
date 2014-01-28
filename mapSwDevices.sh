#!/bin/bash
#requires the dpid and resourceId {$1}/{$2}
dpid=$1
resourceId=$2
echo "Mapping openflow switches to OpenNaaS resource."
response=$(curl -H "Content-Type: application/xml" -X GET http://admin:123456@localhost:8888/opennaas/sdnnetwork/sdn1/ofprovisionnet/mapDeviceResource/$dpid/$resourceId)
echo $response
