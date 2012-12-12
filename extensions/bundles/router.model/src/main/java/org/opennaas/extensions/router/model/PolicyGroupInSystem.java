package org.opennaas.extensions.router.model;

/**
 * An association that links a PolicyGroup to the System in whose scope the Group is defined.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyGroupInSystem extends PolicySetInSystem {

	/**
	 * Default constructor
	 */
	public PolicyGroupInSystem() {
	}

	// /**
	// * The System in whose scope a PolicyGroup is defined.
	// */
	// private System antecedent;
	//
	// /**
	// * A PolicyGroup named within the scope of a System.
	// */
	// private PolicyGroup dependent;

	public System getAntecedent() {
		return super.getAntecedent();
	}

	public void setAntecedent(System antecedent) {
		super.setAntecedent(antecedent);
	}

	public PolicyGroup getDependent() {
		return (PolicyGroup) super.getDependent();
	}

	public void setDependent(PolicyGroup dependent) {
		super.setDependent(dependent);
	}

	public static PolicyGroupInSystem link(System antecedent, PolicyGroup dependent, int priority) {

		PolicyGroupInSystem assoc = (PolicyGroupInSystem) Association.link(PolicyGroupInSystem.class, antecedent, dependent);
		assoc.setAntecedent(antecedent);
		assoc.setDependent(dependent);
		assoc.setPriority(priority);

		return assoc;
	}

}
