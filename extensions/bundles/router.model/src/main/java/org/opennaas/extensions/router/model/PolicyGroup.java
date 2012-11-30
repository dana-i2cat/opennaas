package org.opennaas.extensions.router.model;

/**
 * An aggregation of PolicySet instances (PolicyGroups and/or PolicyRules) that have the same decision strategy and inherit policy roles. PolicyGroup
 * instances are defined and named relative to the CIM_System that provides their context.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyGroup extends PolicySet {

	/**
	 * Default constructor
	 */
	public PolicyGroup() {
	}

	/**
	 * The scoping System's CreationClassName.
	 */
	private String	systemCreationClassName;

	/**
	 * The scoping System's Name.
	 */
	private String	systemName;

	/**
	 * CreationClassName indicates the name of the class or the subclass used in the creation of an instance. When used with the other key properties
	 * of this class, this property allows all instances of this class and its subclasses to be uniquely identified.
	 */
	private String	creationClassName;

	/**
	 * A user-friendly name of this PolicyGroup.
	 */
	private String	policyGroupName;

	public String getSystemCreationClassName() {
		return systemCreationClassName;
	}

	public void setSystemCreationClassName(String systemCreationClassName) {
		this.systemCreationClassName = systemCreationClassName;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getCreationClassName() {
		return creationClassName;
	}

	public void setCreationClassName(String creationClassName) {
		this.creationClassName = creationClassName;
	}

	public String getPolicyGroupName() {
		return policyGroupName;
	}

	public void setPolicyGroupName(String policyGroupName) {
		this.policyGroupName = policyGroupName;
	}
}
