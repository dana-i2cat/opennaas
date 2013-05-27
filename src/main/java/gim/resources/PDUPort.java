package gim.resources;

import gim.IPowerDelivery;
import gim.IPowerSupply;
import gim.load.MeasuredLoad;

import java.util.List;

public class PDUPort implements IPowerDelivery {

	private PowerDeliveryResource	parentPDU;
	private int						id;

	public MeasuredLoad getCurrentPowerMetrics() throws Exception {
		return parentPDU.getPortCurrentPowerMetrics(this);
	}

	public boolean getPowerStatus() throws Exception {
		return parentPDU.getPortPowerStatus(this);
	}

	public boolean powerOn() throws Exception {
		return parentPDU.powerOnPort(this);
	}

	public boolean powerOff() throws Exception {
		return parentPDU.powerOffPort(this);
	}

	public List<IPowerSupply> getPowerSupplies() {
		return parentPDU.getPowerSupplies();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public PowerDeliveryResource getParentPDU() {
		return parentPDU;
	}

	public void setParentPDU(PowerDeliveryResource parentPDU) {
		this.parentPDU = parentPDU;
	}

}
