package org.opennaas.extensions.gim.controller;

import org.opennaas.extensions.gim.controller.capabilities.IPowerManagementController;
import org.opennaas.extensions.gim.controller.capabilities.IPowerMonitoringController;
import org.opennaas.extensions.gim.controller.capabilities.IPowerSupplyController;

public abstract class AbstractPowerController implements IPowerMonitoringController, IPowerManagementController, IPowerSupplyController {

}
