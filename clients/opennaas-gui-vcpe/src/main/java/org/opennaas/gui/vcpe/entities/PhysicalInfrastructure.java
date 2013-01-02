/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

/**
 * @author Jordi
 */
public class PhysicalInfrastructure {

	private PhysicalRouter	coreRouter;
	private PhysicalRouter	phyRouterMaster;
	private PhysicalRouter	phyRouterBackup;

	/**
	 * @return the coreRouter
	 */
	public PhysicalRouter getCoreRouter() {
		return coreRouter;
	}

	/**
	 * @param coreRouter
	 *            the coreRouter to set
	 */
	public void setCoreRouter(PhysicalRouter coreRouter) {
		this.coreRouter = coreRouter;
	}

	/**
	 * @return the phyRouterMaster
	 */
	public PhysicalRouter getPhyRouterMaster() {
		return phyRouterMaster;
	}

	/**
	 * @param phyRouterMaster
	 *            the phyRouterMaster to set
	 */
	public void setPhyRouterMaster(PhysicalRouter phyRouterMaster) {
		this.phyRouterMaster = phyRouterMaster;
	}

	/**
	 * @return the phyRouterBackup
	 */
	public PhysicalRouter getPhyRouterBackup() {
		return phyRouterBackup;
	}

	/**
	 * @param phyRouterBackup
	 *            the phyRouterBackup to set
	 */
	public void setPhyRouterBackup(PhysicalRouter phyRouterBackup) {
		this.phyRouterBackup = phyRouterBackup;
	}

}
