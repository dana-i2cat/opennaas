package gim.resources;

import gim.IPowerSupply;
import gim.energy.Energy;
import gim.energy.energyClass;
import gim.energy.energyType;
import gim.load.DeliveryRatedLoad;
import gim.load.MeasuredLoad;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import snmp.APCDriver_SNMP;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:41:56
 */
public class PowerDeliveryResource extends ONS_Resource {

	private PowerSupplyResource		primaryPowerSupply;
	private List<IPowerSupply>		powerSupplies;
	private DeliveryRatedLoad		deliveryRatedLoad;

	private APCDriver_SNMP			driver;
	private String					myIP;

	/**
	 * Ports belonging to this PDU
	 */
	private List<PDUPort>			ports;
	/**
	 * A Map used to translate from ports in the model to targetOutletIndexes known for the driver
	 * <p/>
	 * Key: Port ids, Value: port targetOutletIndex known for the driver
	 */
	private Map<Integer, Integer>	portOutletIndexes;

	// PowerSupply data, statically set here but should be read dynamically in reality

	private energyClass				energyclass	= energyClass.Green;
	private energyType				energytype	= energyType.Wind;

	private double					pricePerKW	= 0.11;
	private Energy					psEnergy	= new Energy(energyclass, energytype, pricePerKW);

	public PowerDeliveryResource(String SNMPIP) {
		myIP = SNMPIP;
		primaryPowerSupply = new PowerSupplyResource(psEnergy, pricePerKW);

		try {
			driver = new APCDriver_SNMP(myIP);
		} catch (IOException ioe) {
			System.out.println("Error from APC Driver:" + ioe.getMessage());
		}

	}

	public String getDeviceName() {
		String name;

		try {
			name = driver.getDeviceName();

		} catch (IOException ioe) {
			System.out.println("Error from APC Driver:" + ioe.getMessage());
			return null;
		}

		return name;
	}

	public MeasuredLoad getPortCurrentPowerMetrics(PDUPort port) throws Exception {
		return getCurrentPowerMetrics(getPortOutletIndex(port));
	}

	public MeasuredLoad getCurrentPowerMetrics(int targetOutletIndex) {

		Double outletCurrent, outletVoltage, outletPower, outletEnergy;

		MeasuredLoad ml;
		Date currentTime;

		try {
			outletCurrent = driver.getCurrentCurrent(targetOutletIndex);
			outletVoltage = driver.getCurrentVoltage(targetOutletIndex);
			outletPower = driver.getCurrentPower(targetOutletIndex);
			outletEnergy = driver.getCurrentEnergy(targetOutletIndex);

		} catch (IOException ioe) {
			System.out.println("Error from APC Driver:" + ioe.getMessage());
			return null;
		}

		ml = new MeasuredLoad();
		ml.setCurrent(outletCurrent);
		ml.setPower(outletPower);
		ml.setVoltage(outletVoltage);
		ml.setEnergy(outletEnergy);

		currentTime = new Date();
		ml.setReadingTime(currentTime);

		return ml;
	}

	public boolean powerOnPort(PDUPort port) throws Exception {
		return powerOnPort(getPortOutletIndex(port));
	}

	public boolean powerOnPort(int targetOutletIndex) {
		try {

			return driver.powerOnPort(targetOutletIndex);

		} catch (IOException ioe) {
			System.out.println("Error from APC Driver:" + ioe.getMessage());
			return false;
		}

	}

	public boolean powerOffPort(PDUPort port) throws Exception {
		return powerOffPort(getPortOutletIndex(port));
	}

	public boolean powerOffPort(int targetOutletIndex) {
		try {
			return driver.powerOffPort(targetOutletIndex);
		} catch (IOException ioe) {
			System.out.println("Error from APC Driver:" + ioe.getMessage());
			return false;
		}
	}

	public boolean getPortPowerStatus(PDUPort port) throws Exception {
		return getPortPowerStatus(getPortOutletIndex(port));
	}

	public boolean getPortPowerStatus(int targetOutletIndex) {
		try {
			return driver.getOutletStatus(targetOutletIndex);
		} catch (IOException ioe) {
			System.out.println("Error from APC Driver:" + ioe.getMessage());
			return false;
		}

	}

	/**
	 * @return the powerSupplies
	 */
	public List<IPowerSupply> getPowerSupplies() {
		return powerSupplies;
	}

	/**
	 * @param powerSupplies the powerSupplies to set
	 */
	public void setPowerSupplies(List<IPowerSupply> powerSupplies) {
		this.powerSupplies = powerSupplies;
	}

	public String getEnergyType() {
		return primaryPowerSupply.getEnergy().getEnergyType().toString();
	}

	public String getEnergyClass() {
		return primaryPowerSupply.getEnergy().getEnergyClass().toString();
	}

	public double getCO2perKW() {
		return primaryPowerSupply.getEnergy().getCO2perKw();
	}

	private int getPortOutletIndex(PDUPort port) throws Exception {
		if (!portOutletIndexes.containsKey(port.getId()))
			throw new Exception("Unknow port " + port.getId());
		return portOutletIndexes.get(port.getId());
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}