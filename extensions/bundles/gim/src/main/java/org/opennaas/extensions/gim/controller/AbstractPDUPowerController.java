package org.opennaas.extensions.gim.controller;

import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerManagementController;
import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerMonitoringController;
import org.opennaas.extensions.gim.controller.capabilities.IPowerSupplyController;

public abstract class AbstractPDUPowerController implements IPDUPowerMonitoringController, IPDUPowerManagementController, IPowerSupplyController {

}
