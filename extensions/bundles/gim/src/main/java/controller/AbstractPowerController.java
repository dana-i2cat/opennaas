package controller;

import controller.capabilities.IPowerManagementCapability;
import controller.capabilities.IPowerMonitoringCapability;
import controller.capabilities.IPowerSupplyCapability;

public abstract class AbstractPowerController implements IPowerMonitoringCapability, IPowerManagementCapability, IPowerSupplyCapability {

}
