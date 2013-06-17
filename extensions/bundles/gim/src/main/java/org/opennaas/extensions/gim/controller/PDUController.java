package org.opennaas.extensions.gim.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerManagementCapability;
import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerMonitoringCapability;
import org.opennaas.extensions.gim.controller.capabilities.IPowerSupplyCapability;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class PDUController implements IPDUPowerManagementCapability, IPDUPowerMonitoringCapability, IPowerSupplyCapability {

	private PDUPowerControllerDriver	driver;
	private PDU							pdu;
	private IDeliveryController			deliveryController;

	public PDUPowerControllerDriver getDriver() {
		return driver;
	}

	public void setDriver(PDUPowerControllerDriver driver) {
		this.driver = driver;
	}

	public PDU getPdu() {
		return pdu;
	}

	public void setPdu(PDU pdu) {
		this.pdu = pdu;
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

	@Override
	public MeasuredLoad getCurrentPowerMetrics(String portId) throws Exception {
		MeasuredLoad load = getDriver().getCurrentPowerMetrics(portId);
		// update model
		deliveryController.getPowerSource(pdu.getId(), portId).getPowerMonitorLog().add(load);
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
		return deliveryController.getPowerSource(pdu.getId(), portId).getPowerMonitorLog();
	}

	@Override
	public boolean getPowerStatus(String portId) throws Exception {
		boolean status = getDriver().getPowerStatus(portId);
		// update model
		deliveryController.getPowerSource(pdu.getId(), portId).setPowerState(status);
		return status;
	}

	@Override
	public boolean powerOn(String portId) throws Exception {
		boolean isOk = getDriver().powerOn(portId);
		if (isOk) {
			// update model
			deliveryController.getPowerSource(pdu.getId(), portId).setPowerState(true);
		}
		return isOk;
	}

	@Override
	public boolean powerOff(String portId) throws Exception {
		boolean isOk = getDriver().powerOff(portId);
		if (isOk) {
			// update model
			deliveryController.getPowerSource(pdu.getId(), portId).setPowerState(false);
		}
		return isOk;
	}

	@Override
	public Energy getAggregatedEnergy() throws Exception {
		return deliveryController.calculateSourceAggregatedEnergy(pdu.getId(), null);
	}

	@Override
	public double getAggregatedPricePerEnergyUnit() throws Exception {
		return deliveryController.calculateSourceAggregatedEnergyPrice(pdu.getId(), null);
	}

	@Override
	public List<PDUPort> listPorts() throws Exception {
		return getDriver().listPorts();
	}

}
