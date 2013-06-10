package org.opennaas.extensions.gim.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.extensions.gim.controller.snmp.APCDriver_SNMP;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class APCPDUPowerControllerDriver extends PDUPowerControllerDriver {

	private APCDriver_SNMP			driver;

	/**
	 * A Map used to translate from ports in the model to targetOutletIndexes known for the driver
	 * <p/>
	 * Key: Portid, Value: port targetOutletIndex known for the driver
	 */
	private Map<String, Integer>	outletIndexes	= new HashMap<String, Integer>();

	public String getDeviceName() throws Exception {
		return driver.getDeviceName();
	}

	public String getPortName(PDUPort port) throws Exception {
		return driver.getOutletName(getOutletIndex(port));
	}

	public List<PDUPort> getPorts() throws Exception {

		// read outlets
		List<Integer> outlets = driver.getOutletIndexes();

		// build ports
		List<PDUPort> ports = new ArrayList<PDUPort>(outlets.size());
		PDUPort port;
		for (int i = 0; i < outlets.size(); i++) {
			port = new PDUPort();
			port.setId(outlets.get(i).toString());
			port.setPowerMonitorLog(new PowerMonitorLog(1, 10));
			ports.add(port);
		}

		// populate outletIndexes map
		Map<String, Integer> newOutletIndexes = new HashMap<String, Integer>();
		for (int i = 0; i < outlets.size(); i++) {
			newOutletIndexes.put(ports.get(i).getId(), outlets.get(i));
		}
		setOutletIndexes(newOutletIndexes);
		return ports;
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
		// FIXME return a log filtered copy including only desired measures.
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
	public Map<String, Integer> getOutletIndexes() {
		return outletIndexes;
	}

	/**
	 * @param outletIndexes
	 *            the outletIndexes to set
	 */
	public void setOutletIndexes(Map<String, Integer> outletIndexes) {
		this.outletIndexes = outletIndexes;
	}

	private int getOutletIndex(String portId) throws Exception {
		if (!outletIndexes.containsKey(portId))
			throw new Exception("Unknown port " + portId);
		return outletIndexes.get(portId);
	}

	private int getOutletIndex(PDUPort port) throws Exception {
		return getOutletIndex(port.getId());
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

	@Override
	public List<PDUPort> listPorts() throws Exception {
		return getPorts();
	}

}
