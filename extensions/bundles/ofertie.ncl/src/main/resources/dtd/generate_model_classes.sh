#!/bin/bash
xjc -dtd -d ../../java/ -p org.opennaas.extensions.ofertie.ncl.provisioner.api.model qos_policy_request.dtd
xjc -dtd -d ../../java/ -p org.opennaas.extensions.ofertie.ncl.monitoring.api.model qos_monitoring.dtd
