package org.opennaas.extensions.gim.controller;

import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;

/**
 * BasicDeliveryController
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class BasicSupplyController implements ISupplyController {

	private GIModel	model;

	/**
	 * @return the model
	 */
	public GIModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(GIModel model) {
		this.model = model;
	}

	@Override
	public boolean getSourcePowerStatus(String supplyId, String socketId) throws ModelElementNotFoundException {
		PowerSupply supply = GIMController.getPowerSupply(model, supplyId);
		return GIMController.getSocketById(supply.getPowerSources(), socketId).getPowerState();
	}

	/**
	 * It does not really power on the source, but only marks it as powered on.
	 * 
	 * @throws ModelElementNotFoundException
	 *             if failed to find required PowerSource
	 */
	@Override
	public void powerOnSource(String supplyId, String socketId) throws ModelElementNotFoundException {
		PowerSupply supply = GIMController.getPowerSupply(model, supplyId);
		GIMController.getSocketById(supply.getPowerSources(), socketId).setPowerState(true);
	}

	/**
	 * It does not really power if the source, but only marks it as powered off.
	 * 
	 * @throws ModelElementNotFoundException
	 *             if failed to find required PowerSource
	 */
	@Override
	public void powerOffSource(String supplyId, String socketId) throws ModelElementNotFoundException {
		PowerSupply supply = GIMController.getPowerSupply(model, supplyId);
		GIMController.getSocketById(supply.getPowerSources(), socketId).setPowerState(false);
	}

	@Override
	public MeasuredLoad getSourceCurrentPowerMetrics(String supplyId, String socketId) throws ModelElementNotFoundException {
		PowerSupply supply = GIMController.getPowerSupply(model, supplyId);
		PowerSource source = (PowerSource) GIMController.getSocketById(supply.getPowerSources(), socketId);
		return GIMController.getLastMeasuredLoad(source.getPowerMonitorLog());
	}

	@Override
	public Energy getSourceEnergy(String supplyId, String socketId) throws ModelElementNotFoundException {
		PowerSupply supply = GIMController.getPowerSupply(model, supplyId);
		PowerSource source = (PowerSource) GIMController.getSocketById(supply.getPowerSources(), socketId);
		return source.getEnergy();
	}

	@Override
	public double getSourceEnergyPrice(String supplyId, String socketId) throws ModelElementNotFoundException {
		PowerSupply supply = GIMController.getPowerSupply(model, supplyId);
		PowerSource source = (PowerSource) GIMController.getSocketById(supply.getPowerSources(), socketId);
		return source.getPricePerUnit();
	}

}
