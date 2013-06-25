package org.opennaas.extensions.gim.controller;

import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;

public interface ISupplyController {

	// Per socket methods

	public Energy getSourceEnergy(String supplyId, String socketId) throws Exception;

	public double getSourceEnergyPrice(String supplyId, String socketId) throws Exception;

	public boolean getSourcePowerStatus(String supplyId, String socketId) throws Exception;

	public void powerOnSource(String supplyId, String socketId) throws Exception;

	public void powerOffSource(String supplyId, String socketId) throws Exception;

	public MeasuredLoad getSourceCurrentPowerMetrics(String supplyId, String socketId) throws Exception;

}
