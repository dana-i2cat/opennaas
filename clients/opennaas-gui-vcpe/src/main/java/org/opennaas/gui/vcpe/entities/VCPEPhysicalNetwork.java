package org.opennaas.gui.vcpe.entities;

/**
 * @author Jordi
 */
import javax.validation.Valid;

public class VCPEPhysicalNetwork {

	private String			templateType;
	@Valid
	private PhysicalRouter	routerMaster;
	@Valid
	private PhysicalRouter	routerBackup;
	@Valid
	private PhysicalRouter	routerCore;

	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * @param templateType
	 *            the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	/**
	 * @return the routerMaster
	 */
	public PhysicalRouter getRouterMaster() {
		return routerMaster;
	}

	/**
	 * @param routerMaster
	 *            the routerMaster to set
	 */
	public void setRouterMaster(PhysicalRouter routerMaster) {
		this.routerMaster = routerMaster;
	}

	/**
	 * @return the routerBackup
	 */
	public PhysicalRouter getRouterBackup() {
		return routerBackup;
	}

	/**
	 * @param routerBackup
	 *            the routerBackup to set
	 */
	public void setRouterBackup(PhysicalRouter routerBackup) {
		this.routerBackup = routerBackup;
	}

	/**
	 * @return the routerCore
	 */
	public PhysicalRouter getRouterCore() {
		return routerCore;
	}

	/**
	 * @param routerCore
	 *            the routerCore to set
	 */
	public void setRouterCore(PhysicalRouter routerCore) {
		this.routerCore = routerCore;
	}

}
