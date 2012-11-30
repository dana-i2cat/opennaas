/**
 * 
 */
package org.opennaas.extensions.vcpe.model;

/**
 * @author Jordi
 */
public class VRRP {

	private String		virtualIPAddress;
	private Integer		group;
	private Integer		priorityMaster;
	private Integer		priorityBackup;
	private Router		masterRouter;
	private Interface	masterInterface;
	private Router		backupRouter;
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

}
