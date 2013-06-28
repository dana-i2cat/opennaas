package org.opennaas.extensions.gim.controller;

import java.util.List;

import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;

public interface IConsumerController {

	// Aggregated methods

	public Energy getAggregatedEnergy(String consumerId) throws Exception;

	public double getAggregatedEnergyPrice(String consumerId) throws Exception;

	public boolean getPowerStatus(String consumerId) throws Exception;

	public void powerOn(String consumerId) throws Exception;

	public void powerOff(String consumerId) throws Exception;

	public MeasuredLoad getCurrentPowerMetrics(String consumerId) throws Exception;

	// Per socket methods

	public Energy getReceptorEnergy(String consumerId, String socketId) throws Exception;

	public double getReceptorEnergyPrice(String consumerId, String socketId) throws Exception;

	public boolean getReceptorPowerStatus(String consumerId, String socketId) throws Exception;

	public void powerOnReceptor(String consumerId, String socketId) throws Exception;

	public void powerOffReceptor(String consumerId, String socketId) throws Exception;

	public MeasuredLoad getReceptorCurrentPowerMetrics(String consumerId, String socketId) throws Exception;

	public PowerReceptor getPowerReceptor(String consumerId, String socketId) throws Exception;

	public List<PowerReceptor> getPowerReceptors(String consumerId) throws Exception;

}
