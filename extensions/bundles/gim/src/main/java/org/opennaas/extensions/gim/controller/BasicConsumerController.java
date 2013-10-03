package org.opennaas.extensions.gim.controller;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;

/**
 * BasicConsumerController that delegates queries to power sources of specified consumer.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class BasicConsumerController implements IConsumerController {

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
	public Energy getAggregatedEnergy(String consumerId) throws ModelElementNotFoundException, Exception {

		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		List<Energy> energies = new ArrayList<Energy>(consumer.getPowerReceptors().size());
		for (PowerReceptor receptor : consumer.getPowerReceptors()) {
			// ignore not attached receptors computation will be done with attached ones
			if (receptor.getAttachedTo() != null) {
				energies.add(getReceptorEnergy(consumerId, receptor.getId()));
			}
		}
		return calculateAggregaredEnergy(energies);
	}

	@Override
	public double getAggregatedEnergyPrice(String consumerId) throws ModelElementNotFoundException, Exception {
		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		List<Double> prices = new ArrayList<Double>(consumer.getPowerReceptors().size());
		for (PowerReceptor receptor : consumer.getPowerReceptors()) {
			// ignore not attached receptors computation will be done with attached ones
			if (receptor.getAttachedTo() != null) {
				prices.add(getReceptorEnergyPrice(consumerId, receptor.getId()));
			}
		}
		return calculateAggregaredEnergyPrice(prices);
	}

	@Override
	public boolean getPowerStatus(String consumerId) throws ModelElementNotFoundException, Exception {
		// Returns true if any of its power receptors is powered on
		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		boolean powerStatus = false;
		for (PowerReceptor receptor : consumer.getPowerReceptors()) {
			// ignore not attached receptors computation will be done with attached ones
			if (receptor.getAttachedTo() != null) {
				if (getReceptorPowerStatus(consumerId, receptor.getId())) {
					powerStatus = true;
					break;
				}
			}
		}
		return powerStatus;
	}

	/**
	 * It does not really power on the resource, but only marks all power sources to be on.
	 */
	@Override
	public void powerOn(String consumerId) throws ModelElementNotFoundException, Exception {
		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		for (PowerReceptor receptor : consumer.getPowerReceptors()) {
			// ignore not attached receptors
			if (receptor.getAttachedTo() != null) {
				powerOnReceptor(consumerId, receptor.getId());
			}
		}
	}

	/**
	 * It does not really power off the resource, but only marks all power sources to be off.
	 */
	@Override
	public void powerOff(String consumerId) throws ModelElementNotFoundException, Exception {
		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		for (PowerReceptor receptor : consumer.getPowerReceptors()) {
			// ignore not attached receptors
			if (receptor.getAttachedTo() != null) {
				powerOffReceptor(consumerId, receptor.getId());
			}
		}
	}

	@Override
	public MeasuredLoad getCurrentPowerMetrics(String consumerId) throws ModelElementNotFoundException, Exception {
		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		List<MeasuredLoad> receptorMetrics = new ArrayList<MeasuredLoad>(consumer.getPowerReceptors().size());
		for (PowerReceptor receptor : consumer.getPowerReceptors()) {
			// ignore not attached receptors
			if (receptor.getAttachedTo() != null) {
				receptorMetrics.add(getReceptorCurrentPowerMetrics(consumerId, receptor.getId()));
			}
		}
		return calculateAggregaredPowerMetrics(receptorMetrics);
	}

	@Override
	public Energy getReceptorEnergy(String consumerId, String socketId) throws ModelElementNotFoundException, Exception {
		return getReceptorAttachedSource(consumerId, socketId).getEnergy();
	}

	@Override
	public double getReceptorEnergyPrice(String consumerId, String socketId) throws ModelElementNotFoundException, Exception {
		return getReceptorAttachedSource(consumerId, socketId).getPricePerUnit();
	}

	@Override
	public boolean getReceptorPowerStatus(String consumerId, String socketId) throws ModelElementNotFoundException, Exception {
		return getReceptorAttachedSource(consumerId, socketId).getPowerState();
	}

	/**
	 * It does not really power on the receptor, but only marks its power source to be on.
	 */
	@Override
	public void powerOnReceptor(String consumerId, String socketId) throws ModelElementNotFoundException, Exception {
		getReceptorAttachedSource(consumerId, socketId).setPowerState(true);
	}

	/**
	 * It does not really power off the receptor, but only marks its power source to be off.
	 */
	@Override
	public void powerOffReceptor(String consumerId, String socketId) throws ModelElementNotFoundException, Exception {
		getReceptorAttachedSource(consumerId, socketId).setPowerState(false);
	}

	@Override
	public MeasuredLoad getReceptorCurrentPowerMetrics(String consumerId, String socketId) throws ModelElementNotFoundException, Exception {
		return GIMController.getLastMeasuredLoad(getReceptorAttachedSource(consumerId, socketId).getPowerMonitorLog());
	}

	@Override
	public PowerReceptor getPowerReceptor(String consumerId, String socketId) throws Exception {
		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		return (PowerReceptor) GIMController.getSocketById(consumer.getPowerReceptors(), socketId);
	}

	@Override
	public List<PowerReceptor> getPowerReceptors(String consumerId) throws Exception {
		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		return consumer.getPowerReceptors();
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

	protected PowerSource getReceptorAttachedSource(String consumerId, String receptorId) throws ModelElementNotFoundException {
		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		PowerReceptor receptor = (PowerReceptor) GIMController.getSocketById(consumer.getPowerReceptors(), receptorId);

		if (receptor.getAttachedTo() == null) {
			throw new ModelElementNotFoundException("Failed to get receptor attached source, in receptor " + receptorId);
		}

		return receptor.getAttachedTo();
	}

}
