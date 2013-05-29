package org.opennaas.extensions.gim.controller;

import org.opennaas.extensions.gim.controller.capabilities.IPowerManagementCapability;
import org.opennaas.extensions.gim.controller.capabilities.IPowerMonitoringCapability;
import org.opennaas.extensions.gim.controller.capabilities.IPowerSupplyCapability;

public abstract class AbstractPowerController implements IPowerMonitoringCapability, IPowerManagementCapability, IPowerSupplyCapability {

}
