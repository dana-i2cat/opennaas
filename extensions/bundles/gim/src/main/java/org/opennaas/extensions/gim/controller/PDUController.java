package org.opennaas.extensions.gim.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerManagementCapability;
import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerMonitoringCapability;
import org.opennaas.extensions.gim.controller.capabilities.IPowerSupplyCapability;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class PDUController extends AbstractPDUPowerController {

	private PDUPowerControllerDriver	driver;
	private IDeliveryController			deliveryController;
	private String						deliveryId;

	public PDUPowerControllerDriver getDriver() {
		return driver;
	}

	public void setDriver(PDUPowerControllerDriver driver) {
		this.driver = driver;
	}

	/**
	 * @return the deliveryController
	 */
	public IDeliveryController getDeliveryController() {
		return deliveryController;
	}

	/**
	 * @param deliveryController
	 *            the deliveryController to set
	 */
	public void setDeliveryController(IDeliveryController deliveryController) {
		this.deliveryController = deliveryController;
	}

	/**
	 * @return the deliveryId
	 */
	public String getDeliveryId() {
		return deliveryId;
	}

	/**
	 * @param deliveryId
	 *            the deliveryId to set
	 */
	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}

	@Override
	public MeasuredLoad getCurrentPowerMetrics(String portId) throws Exception {
		MeasuredLoad load = getDriver().getCurrentPowerMetrics(portId);
		// update model
		getPort(portId).getPowerMonitorLog().add(load);
		return load;
	}

	@Override
	public PowerMonitorLog getPowerMetricsByTimeRange(String portId, Date from, Date to) throws Exception {
		// read current metrics if now is in requested period
		Date now = Calendar.getInstance().getTime();
		if (now.after(from) && now.before(to)) {
			getCurrentPowerMetrics(portId);
		}

		// TODO return a log filtered copy including only desired measures.
		return getPort(portId).getPowerMonitorLog();
	}

	@Override
	public boolean getPowerStatus(String portId) throws Exception {
		boolean status = getDriver().getPowerStatus(portId);
		// update model
		getPort(portId).setPowerState(status);
		return status;
	}

	@Override
	public boolean powerOn(String portId) throws Exception {
		boolean isOk = getDriver().powerOn(portId);
		if (isOk) {
			// update model
			getPort(portId).setPowerState(true);
		}
		return isOk;
	}

	@Override
	public boolean powerOff(String portId) throws Exception {
		boolean isOk = getDriver().powerOff(portId);
		if (isOk) {
			// update model
			getPort(portId).setPowerState(false);
		}
		return isOk;
	}

	@Override
	public Energy getAggregatedEnergy() throws Exception {
		return deliveryController.calculateSourceAggregatedEnergy(deliveryId, null);
	}

	@Override
	public double getAggregatedPricePerEnergyUnit() throws Exception {
		return deliveryController.calculateSourceAggregatedEnergyPrice(deliveryId, null);
	}

	@Override
	public List<PDUPort> listPorts() throws Exception {
		return getDriver().listPorts();
	}

	private PowerSource getPort(String portId) throws Exception {
		return deliveryController.getPowerSource(deliveryId, portId);
	}

}
