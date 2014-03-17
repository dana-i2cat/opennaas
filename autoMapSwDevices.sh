#!/bin/bash
#requires the dpid and resourceId {$1}/{$2}
echo "Mapping openflow switches to OpenNaaS resource."
response=$(curl -X GET http://admin:123456@localhost:8888/opennaas/vrf/routing/switchMapping)
echo $response
