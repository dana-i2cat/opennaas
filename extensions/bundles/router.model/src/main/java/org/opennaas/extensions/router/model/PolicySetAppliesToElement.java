package org.opennaas.extensions.router.model;

/**
 * PolicySetAppliesToElement makes explicit which PolicySets (i.e., policy rules and groups of rules) ARE CURRENTLY applied to a particular Element.
 * This association indicates that the PolicySets that are appropriate for a ManagedElement (specified using the PolicyRoleCollection aggregation)
 * have actually been deployed in the policy management infrastructure. Note that if the named Element refers to a Collection, then the PolicySet is
 * assumed to be applied to all the members of the Collection.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicySetAppliesToElement extends Association {

	/**
	 * Default constructor
	 */
	public PolicySetAppliesToElement() {
	}

	/**
	 * The PolicyRules and/or groups of rules that are currently applied to an Element.
	 */
	private PolicySet		policySet;

	/**
	 * The ManagedElement to which the PolicySet applies.
	 */
	private ManagedElement	managedElement;

	public PolicySet getPolicySet() {
		return policySet;
	}

	public void setPolicySet(PolicySet policySet) {
		this.policySet = policySet;
	}

	public ManagedElement getManagedElement() {
		return managedElement;
	}

	public void setManagedElement(ManagedElement managedElement) {
		this.managedElement = managedElement;
	}

	public static PolicySetAppliesToElement link(PolicySet from, ManagedElement to) {

		PolicySetAppliesToElement assoc = (PolicySetAppliesToElement) Association.link(PolicySetAppliesToElement.class, from, to);
		assoc.setPolicySet(from);
		assoc.setManagedElement(to);

		return assoc;
	}

}
