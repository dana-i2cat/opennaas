package controller;

import gim.core.entities.pdu.PDUPort;
import gim.load.MeasuredLoad;
import gim.log.PowerMonitorLog;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import snmp.APCDriver_SNMP;

public class APCPDUPowerControllerDriver extends PDUPowerControllerDriver {

	private APCDriver_SNMP			driver;

	/**
	 * A Map used to translate from ports in the model to targetOutletIndexes known for the driver
	 * <p/>
	 * Key: Ports, Value: port targetOutletIndex known for the driver
	 */
	private Map<PDUPort, Integer>	outletIndexes	= new HashMap<PDUPort, Integer>();

	public String getDeviceName() throws Exception {
		return driver.getDeviceName();
	}

	public String getPortName(PDUPort port) throws Exception {
		return driver.getOutletName(getOutletIndex(port));
	}

	public boolean getPowerStatus(PDUPort port) throws Exception {
		return driver.getOutletStatus(getOutletIndex(port));
	}

	public boolean powerOn(PDUPort port) throws Exception {
		return driver.powerOnPort(getOutletIndex(port));
	}

	public boolean powerOff(PDUPort port) throws Exception {
		return driver.powerOffPort(getOutletIndex(port));
	}

	public MeasuredLoad getCurrentPowerMetrics(PDUPort port) throws Exception {
		MeasuredLoad ml = getCurrentPowerMetrics(getOutletIndex(port));
		port.getPowerMonitorLog().add(ml);
		return ml;
	}

	public PowerMonitorLog getPowerMetricsByTimeRange(PDUPort port, Date from, Date to) throws Exception {
		// TODO return a log filtered copy including only desired measures.
		return port.getPowerMonitorLog();
	}

	/**
	 * @return the driver
	 */
	public APCDriver_SNMP getDriver() {
		return driver;
	}

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(APCDriver_SNMP driver) {
		this.driver = driver;
	}

	/**
	 * @return the outletIndexes
	 */
	public Map<PDUPort, Integer> getOutletIndexes() {
		return outletIndexes;
	}

	/**
	 * @param outletIndexes
	 *            the outletIndexes to set
	 */
	public void setOutletIndexes(Map<PDUPort, Integer> outletIndexes) {
		this.outletIndexes = outletIndexes;
	}

	private int getOutletIndex(PDUPort port) throws Exception {
		if (!outletIndexes.containsKey(port))
			throw new Exception("Unknown port " + port);
		return outletIndexes.get(port);
	}

	private MeasuredLoad getCurrentPowerMetrics(int targetOutletIndex) throws Exception {

		Double outletCurrent, outletVoltage, outletPower, outletEnergy;

		MeasuredLoad ml;
		Date currentTime;

		try {
			outletCurrent = driver.getCurrentCurrent(targetOutletIndex);
			outletVoltage = driver.getCurrentVoltage(targetOutletIndex);
			outletPower = driver.getCurrentPower(targetOutletIndex);
			outletEnergy = driver.getCurrentEnergy(targetOutletIndex);
		} catch (IOException ioe) {
			throw new Exception("Failed to read currentPowerMetrics. Error from APC Driver:", ioe);
		}

		ml = new MeasuredLoad();
		ml.setCurrent(outletCurrent);
		ml.setPower(outletPower);
		ml.setVoltage(outletVoltage);
		ml.setEnergy(outletEnergy);

		currentTime = Calendar.getInstance().getTime();
		ml.setReadingTime(currentTime);

		return ml;
	}

}
