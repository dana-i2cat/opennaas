package org.opennaas.extensions.gim.controller;

import java.util.Calendar;
import java.util.Date;

import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerManagementCapability;
import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerMonitoringCapability;
import org.opennaas.extensions.gim.controller.capabilities.IPowerSupplyCapability;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

/**
 * PDUPowerController that delegates to a PDUPowerControllerDriver.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUPowerController implements IPDUPowerManagementCapability, IPDUPowerMonitoringCapability, IPowerSupplyCapability {

	private PDUPowerControllerDriver	driver;
	private PDU							pdu;

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

	public MeasuredLoad getCurrentPowerMetrics(PDUPort port) throws Exception {
		return getDriver().getCurrentPowerMetrics(port);
	}

	public PowerMonitorLog getPowerMetricsByTimeRange(PDUPort port, Date from, Date to) throws Exception {
		// read current metrics if now is in requested period
		Date now = Calendar.getInstance().getTime();
		if (now.after(from) && now.before(to)) {
			getDriver().getCurrentPowerMetrics(port);
		}

		// TODO return a log filtered copy including only desired measures.
		return port.getPowerMonitorLog();
	}

	public boolean getPowerStatus(PDUPort port) throws Exception {
		return getDriver().getPowerStatus(port);
	}

	public boolean powerOn(PDUPort port) throws Exception {
		return getDriver().powerOn(port);
	}

	public boolean powerOff(PDUPort port) throws Exception {
		return getDriver().powerOff(port);
	}

	public Energy getAggregatedEnergy() {
		// FIXME assuming there is only one supply
		return getPdu().getPowerSupplies().get(0).getEnergy();
	}

	public double getAggregatedPricePerEnergyUnit() {
		// FIXME assuming there is only one supply
		return getPdu().getPowerSupplies().get(0).getPricePerUnit();
	}

}
