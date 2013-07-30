package org.opennaas.extensions.gim.controller;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;

/**
 * BasicDeliveryController
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class BasicDeliveryController implements IDeliveryController {

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

	// @Override
	// public MeasuredLoad getCurrentPowerMetrics(String deliveryId) throws ModelElementNotFoundException, Exception {
	// PowerDelivery delivery = GIMController.getPowerDelivery(model, deliveryId);
	// List<MeasuredLoad> receptorMetrics = new ArrayList<MeasuredLoad>(delivery.getPowerReceptors().size());
	// for (PowerReceptor receptor : delivery.getPowerReceptors()) {
	// receptorMetrics.add(getReceptorCurrentPowerMetrics(deliveryId, receptor.getId()));
	// }
	// return calculateAggregaredPowerMetrics(receptorMetrics);
	// }

	@Override
	public Energy getReceptorEnergy(String deliveryId, String socketId) throws ModelElementNotFoundException, Exception {
		return getReceptorAttachedSource(deliveryId, socketId).getEnergy();
	}

	@Override
	public double getReceptorEnergyPrice(String deliveryId, String socketId) throws ModelElementNotFoundException, Exception {
		return getReceptorAttachedSource(deliveryId, socketId).getPricePerUnit();
	}

	@Override
	public boolean getSourcePowerStatus(String deliveryId, String socketId) throws ModelElementNotFoundException {
		return getPowerSource(deliveryId, socketId).getPowerState();
	}

	/**
	 * It does not really power on the source, but only marks it as powered on.
	 * 
	 * @throws ModelElementNotFoundException
	 *             if failed to find required PowerSource
	 */
	@Override
	public void powerOnSource(String deliveryId, String socketId) throws ModelElementNotFoundException {
		getPowerSource(deliveryId, socketId).setPowerState(true);
	}

	/**
	 * It does not really power if the source, but only marks it as powered off.
	 * 
	 * @throws ModelElementNotFoundException
	 *             if failed to find required PowerSource
	 */
	@Override
	public void powerOffSource(String deliveryId, String socketId) throws ModelElementNotFoundException {
		getPowerSource(deliveryId, socketId).setPowerState(false);
	}

	@Override
	public MeasuredLoad getSourceCurrentPowerMetrics(String deliveryId, String socketId) throws ModelElementNotFoundException {
		return GIMController.getLastMeasuredLoad(getPowerSource(deliveryId, socketId).getPowerMonitorLog());
	}

	@Override
	public Energy calculateSourceAggregatedEnergy(String deliveryId, String sourceId) throws ModelElementNotFoundException, Exception {
		return calculateAggregatedEnergy(deliveryId);
	}

	@Override
	public double calculateSourceAggregatedEnergyPrice(String deliveryId, String sourceId) throws ModelElementNotFoundException, Exception {
		return calculateAggregatedEnergyPrice(deliveryId);
	}

	@Override
	public PowerSource getPowerSource(String deliveryId, String sourceId) throws ModelElementNotFoundException {
		PowerDelivery delivery = GIMController.getPowerDelivery(model, deliveryId);
		return (PowerSource) GIMController.getSocketById(delivery.getPowerSources(), sourceId);
	}

	protected Energy calculateAggregatedEnergy(String deliveryId) throws ModelElementNotFoundException, Exception {

		PowerDelivery delivery = GIMController.getPowerDelivery(model, deliveryId);
		List<Energy> energies = new ArrayList<Energy>(delivery.getPowerReceptors().size());
		for (PowerReceptor receptor : delivery.getPowerReceptors()) {
			//ignore not attached receptors
			if (receptor.getAttachedTo() != null)
				energies.add(getReceptorEnergy(deliveryId, receptor.getId()));
		}

		return calculateAggregaredEnergy(energies);
	}

	protected double calculateAggregatedEnergyPrice(String deliveryId) throws ModelElementNotFoundException, Exception {
		PowerDelivery delivery = GIMController.getPowerDelivery(model, deliveryId);
		List<Double> prices = new ArrayList<Double>(delivery.getPowerReceptors().size());
		for (PowerReceptor receptor : delivery.getPowerReceptors()) {
			//ignore not attached receptors
			if (receptor.getAttachedTo() != null)
				prices.add(getReceptorEnergyPrice(deliveryId, receptor.getId()));
		}
		return calculateAggregaredEnergyPrice(prices);
	}

	protected Energy calculateAggregaredEnergy(List<Energy> energies) throws Exception {
		// FIXME proper implementation of this method
		// It now returns first not null energy
		for (Energy e : energies) {
			if (e != null)
				return e;
		}
		throw new Exception("Could not calculate energy");
	}

	protected double calculateAggregaredEnergyPrice(List<Double> prices) throws Exception {
		// FIXME proper implementation of this method
		// It now returns first not null price
		for (Double price : prices) {
			if (price != null)
				return price;
		}
		throw new Exception("Could not calculate price");
	}

	protected MeasuredLoad calculateAggregaredPowerMetrics(List<MeasuredLoad> receptorMetrics) throws Exception {
		// FIXME proper implementation of this method
		// It now returns first not null metrics
		for (MeasuredLoad load : receptorMetrics) {
			if (load != null)
				return load;
		}
		throw new Exception("Could not calculate PowerMetrics");
	}

	protected PowerSource getReceptorAttachedSource(String deliveryId, String receptorId) throws ModelElementNotFoundException, Exception {
		PowerDelivery delivery = GIMController.getPowerDelivery(model, deliveryId);
		PowerReceptor receptor = (PowerReceptor) GIMController.getSocketById(delivery.getPowerReceptors(), receptorId);

		if (receptor.getAttachedTo() == null) {
			throw new Exception("Failed to get receptor attached source, in receptor" + receptorId);
		}

		return receptor.getAttachedTo();
	}

}
