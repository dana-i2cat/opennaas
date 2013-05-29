package controller;

import gim.core.entities.pdu.PDU;
import gim.core.entities.pdu.PDUPort;
import gim.energy.Energy;
import gim.load.MeasuredLoad;
import gim.log.PowerMonitorLog;

import java.util.Calendar;
import java.util.Date;

import controller.capabilities.IPDUPowerManagementCapability;
import controller.capabilities.IPDUPowerMonitoringCapability;
import controller.capabilities.IPowerSupplyCapability;

/**
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
		return driver.getCurrentPowerMetrics(port);
	}

	public PowerMonitorLog getPowerMetricsByTimeRange(PDUPort port, Date from, Date to) throws Exception {
		// read current metrics if now is in requested period
		Date now = Calendar.getInstance().getTime();
		if (now.after(from) && now.before(to)) {
			driver.getCurrentPowerMetrics(port);
		}

		// TODO return a log filtered copy including only desired measures.
		return port.getPowerMonitorLog();
	}

	public boolean getPowerStatus(PDUPort port) throws Exception {
		return driver.getPowerStatus(port);
	}

	public boolean powerOn(PDUPort port) throws Exception {
		return driver.powerOn(port);
	}

	public boolean powerOff(PDUPort port) throws Exception {
		return driver.powerOff(port);
	}

	public Energy getAggregatedEnergy() {
		// FIXME assuming there is only one supply
		return pdu.getPowerSupplies().get(0).getEnergy();
	}

	public double getAggregatedPricePerEnergyUnit() {
		// FIXME assuming there is only one supply
		return pdu.getPowerSupplies().get(0).getPricePerUnit();
	}

}
