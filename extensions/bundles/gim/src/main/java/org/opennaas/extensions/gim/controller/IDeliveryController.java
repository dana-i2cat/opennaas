package org.opennaas.extensions.gim.controller;

import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;

public interface IDeliveryController {

	// Aggregated methods

	// public MeasuredLoad getCurrentPowerMetrics(String deliveryId) throws Exception;

	// Per socket methods

	public Energy getReceptorEnergy(String deliveryId, String socketId) throws Exception;

	public double getReceptorEnergyPrice(String deliveryId, String socketId) throws Exception;

	public boolean getSourcePowerStatus(String deliveryId, String socketId) throws Exception;

	public void powerOnSource(String deliveryId, String socketId) throws Exception;

	public void powerOffSource(String deliveryId, String socketId) throws Exception;

	public MeasuredLoad getSourceCurrentPowerMetrics(String deliveryId, String socketId) throws Exception;

	public Energy calculateSourceAggregatedEnergy(String deliveryId, String sourceId) throws Exception;

	public double calculateSourceAggregatedEnergyPrice(String deliveryId, String sourceId) throws Exception;

	public PowerSource getPowerSource(String deliveryId, String sourceId) throws Exception;

}
