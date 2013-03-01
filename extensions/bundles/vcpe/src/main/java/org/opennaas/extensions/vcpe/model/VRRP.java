/**
 * 
 */
package org.opennaas.extensions.vcpe.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Jordi
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VRRP {

	private String		virtualIPAddress;
	private Integer		group;
	private Integer		priorityMaster;
	private Integer		priorityBackup;
	@XmlIDREF
	private Router		masterRouter;
	@XmlIDREF
	private Interface	masterInterface;
	@XmlIDREF
	private Router		backupRouter;
	@XmlIDREF
	private Interface	backupInterface;

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

	/**
	 * 
	 * @return the master router
	 */
	public Router getMasterRouter() {
		return masterRouter;
	}

	/**
	 * 
	 * @param masterRouter
	 *            the master router to set
	 */
	public void setMasterRouter(Router masterRouter) {
		this.masterRouter = masterRouter;
	}

	/**
	 * 
	 * @return the master interface
	 */
	public Interface getMasterInterface() {
		return masterInterface;
	}

	/**
	 * 
	 * @param masterInterface
	 *            the master interface to set
	 */
	public void setMasterInterface(Interface masterInterface) {
		this.masterInterface = masterInterface;
	}

	/**
	 * 
	 * @return the backup router
	 */
	public Router getBackupRouter() {
		return backupRouter;
	}

	/**
	 * 
	 * @param backupRouter
	 *            the backup router to set
	 */
	public void setBackupRouter(Router backupRouter) {
		this.backupRouter = backupRouter;
	}

	/**
	 * 
	 * @return the backup interface
	 */
	public Interface getBackupInterface() {
		return backupInterface;
	}

	/**
	 * 
	 * @param backupInterface
	 *            the master interface to set
	 */
	public void setBackupInterface(Interface backupInterface) {
		this.backupInterface = backupInterface;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((backupInterface == null) ? 0 : backupInterface.hashCode());
		result = prime * result + ((backupRouter == null) ? 0 : backupRouter.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((masterInterface == null) ? 0 : masterInterface.hashCode());
		result = prime * result + ((masterRouter == null) ? 0 : masterRouter.hashCode());
		result = prime * result + ((priorityBackup == null) ? 0 : priorityBackup.hashCode());
		result = prime * result + ((priorityMaster == null) ? 0 : priorityMaster.hashCode());
		result = prime * result + ((virtualIPAddress == null) ? 0 : virtualIPAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VRRP other = (VRRP) obj;
		if (backupInterface == null) {
			if (other.backupInterface != null)
				return false;
		} else if (!backupInterface.equals(other.backupInterface))
			return false;
		if (backupRouter == null) {
			if (other.backupRouter != null)
				return false;
		} else if (!backupRouter.equals(other.backupRouter))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (masterInterface == null) {
			if (other.masterInterface != null)
				return false;
		} else if (!masterInterface.equals(other.masterInterface))
			return false;
		if (masterRouter == null) {
			if (other.masterRouter != null)
				return false;
		} else if (!masterRouter.equals(other.masterRouter))
			return false;
		if (priorityBackup == null) {
			if (other.priorityBackup != null)
				return false;
		} else if (!priorityBackup.equals(other.priorityBackup))
			return false;
		if (priorityMaster == null) {
			if (other.priorityMaster != null)
				return false;
		} else if (!priorityMaster.equals(other.priorityMaster))
			return false;
		if (virtualIPAddress == null) {
			if (other.virtualIPAddress != null)
				return false;
		} else if (!virtualIPAddress.equals(other.virtualIPAddress))
			return false;
		return true;
	}

}
