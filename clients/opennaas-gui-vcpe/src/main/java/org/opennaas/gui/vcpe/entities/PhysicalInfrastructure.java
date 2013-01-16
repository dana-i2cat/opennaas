package org.opennaas.gui.vcpe.entities;

/**
 * @author Jordi
 */
import javax.validation.Valid;

public class PhysicalInfrastructure {

	private String			templateType;
	@Valid
	private PhysicalRouter	physicalRouterMaster;
	@Valid
	private PhysicalRouter	physicalRouterBackup;
	@Valid
	private PhysicalRouter	physicalRouterCore;

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
	 * @return the physicalRouterMaster
	 */
	public PhysicalRouter getPhysicalRouterMaster() {
		return physicalRouterMaster;
	}

	/**
	 * @param physicalRouterMaster
	 *            the physicalRouterMaster to set
	 */
	public void setPhysicalRouterMaster(PhysicalRouter physicalRouterMaster) {
		this.physicalRouterMaster = physicalRouterMaster;
	}

	/**
	 * @return the physicalRouterBackup
	 */
	public PhysicalRouter getPhysicalRouterBackup() {
		return physicalRouterBackup;
	}

	/**
	 * @param physicalRouterBackup
	 *            the physicalRouterBackup to set
	 */
	public void setPhysicalRouterBackup(PhysicalRouter physicalRouterBackup) {
		this.physicalRouterBackup = physicalRouterBackup;
	}

	/**
	 * @return the physicalRouterCore
	 */
	public PhysicalRouter getPhysicalRouterCore() {
		return physicalRouterCore;
	}

	/**
	 * @param physicalRouterCore
	 *            the physicalRouterCore to set
	 */
	public void setPhysicalRouterCore(PhysicalRouter physicalRouterCore) {
		this.physicalRouterCore = physicalRouterCore;
	}

}
