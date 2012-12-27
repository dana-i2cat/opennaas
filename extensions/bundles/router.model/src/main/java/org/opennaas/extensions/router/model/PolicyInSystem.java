package org.opennaas.extensions.router.model;

/**
 * CIM_PolicyInSystem is a generic association used to establish dependency relationships between Policies and the Systems that host them. These
 * Systems may be ComputerSystems where Policies are 'running' or they may be Policy Repositories where Policies are stored. This relationship is
 * similar to the concept of CIM_Services being dependent on CIM_Systems as defined by the HostedService association.<br>
 * <br>
 * <br>
 * <br>
 * Cardinality is Max (1) for the Antecedent/System reference since Policies can only be hosted in at most one System context. Some subclasses of the
 * association will further refine this definition to make the Policies Weak to Systems. Other subclasses of PolicyInSystem will define an optional
 * hosting relationship. Examples of each of these are the PolicyRuleInSystem and PolicyConditionIn PolicyRepository associations, respectively.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class PolicyInSystem extends HostedDependency {

	/**
	 * Default constructor
	 */
	public PolicyInSystem() {
	}

	/**
	 * The hosting System.
	 */
	private System	antecedent;

	/**
	 * The hosted Policy.
	 */
	private Policy	dependent;

	public System getAntecedent() {
		return antecedent;
	}

	public void setAntecedent(System antecedent) {
		this.antecedent = antecedent;
	}

	public Policy getDependent() {
		return dependent;
	}

	public void setDependent(Policy dependent) {
		this.dependent = dependent;
	}

}
