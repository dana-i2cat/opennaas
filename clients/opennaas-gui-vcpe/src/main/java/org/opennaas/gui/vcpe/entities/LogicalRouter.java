/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

/**
 * @author Jordi
 */
public class LogicalRouter extends Router {

	private PhysicalRouter	physicalRouter;

	/**
	 * @return the physicalRouter
	 */
	public PhysicalRouter getPhysicalRouter() {
		return physicalRouter;
	}

	/**
	 * @param physicalRouter
	 *            the physicalRouter to set
	 */
	public void setPhysicalRouter(PhysicalRouter physicalRouter) {
		this.physicalRouter = physicalRouter;
	}

}
