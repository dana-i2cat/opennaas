#!/bin/bash
xjc -dtd -d ../../java/ -p org.opennaas.extensions.ofertie.ncl.provisioner.model.qos qos_policy_request.dtd
xjc -dtd -d ../../java/ -p org.opennaas.extensions.ofertie.ncl.provisioner.model.monitoring qos_monitoring.dtd
