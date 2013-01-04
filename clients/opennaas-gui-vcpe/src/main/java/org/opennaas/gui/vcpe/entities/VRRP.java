/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Jordi
 */
public class VRRP {

	@Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$", message = "{message.error.field.format.ip}")
	private String	virtualIPAddress;
	@NotNull(message = "{message.error.field.mandatory}")
	@DecimalMin(value = "0")
	@DecimalMax(value = "255")
	private Integer	group;
	@NotNull(message = "{message.error.field.mandatory}")
	@DecimalMin(value = "1")
	@DecimalMax(value = "255")
	private Integer	priorityMaster;
	@NotNull(message = "{message.error.field.mandatory}")
	@DecimalMin(value = "1")
	@DecimalMax(value = "255")
	private Integer	priorityBackup;

	/**
	 * @return the virtualIPAddress
	 */
	public String getVirtualIPAddress() {
		return virtualIPAddress;
	}

	/**
	 * @param virtualIPAddress
	 *            the virtualIPAddress to set
	 */
	public void setVirtualIPAddress(String virtualIPAddress) {
		this.virtualIPAddress = virtualIPAddress;
	}

	/**
	 * @return the group
	 */
	public Integer getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(Integer group) {
		this.group = group;
	}

	/**
	 * @return the priorityMaster
	 */
	public Integer getPriorityMaster() {
		return priorityMaster;
	}

	/**
	 * @param priorityMaster
	 *            the priorityMaster to set
	 */
	public void setPriorityMaster(Integer priorityMaster) {
		this.priorityMaster = priorityMaster;
	}

	/**
	 * @return the priorityBackup
	 */
	public Integer getPriorityBackup() {
		return priorityBackup;
	}

	/**
	 * @param priorityBackup
	 *            the priorityBackup to set
	 */
	public void setPriorityBackup(Integer priorityBackup) {
		this.priorityBackup = priorityBackup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VRRP [virtualIPAddress=" + virtualIPAddress + ", group=" + group + ", priorityMaster=" + priorityMaster + ", priorityBackup=" + priorityBackup + "]";
	}

}
