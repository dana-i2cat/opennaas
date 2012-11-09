package org.opennaas.extensions.router.model;

/**
 * PolicySetInSystem is an abstract association class that represents a relationship between a System and a PolicySet used in the administrative scope
 * of that system (e.g., AdminDomain, ComputerSystem). The Priority property is used to assign a relative priority to a PolicySet within the
 * administrative scope in contexts where it is not a component of another PolicySet.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class PolicySetInSystem extends PolicyInSystem {

	/**
	 * Default constructor
	 */
	public PolicySetInSystem() {
	}

	/**
	 * The System in whose scope a PolicySet is defined.
	 */
	private System		antecedent;

	/**
	 * A PolicySet named within the scope of a System.
	 */
	private PolicySet	dependent;

	/**
	 * The Priority property is used to specify the relative priority of the referenced PolicySet when there are more than one PolicySet instances
	 * applied to a managed resource that are not PolicySetComponents and, therefore, have no other relative priority defined. The priority is a
	 * non-negative integer; a larger value indicates a higher priority.
	 */
	private int			priority;

	public System getAntecedent() {
		return antecedent;
	}

	public void setAntecedent(System antecedent) {
		this.antecedent = antecedent;
	}

	public PolicySet getDependent() {
		return dependent;
	}

	public void setDependent(PolicySet dependent) {
		this.dependent = dependent;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
