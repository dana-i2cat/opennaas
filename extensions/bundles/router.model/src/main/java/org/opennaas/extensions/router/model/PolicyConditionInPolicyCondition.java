package org.opennaas.extensions.router.model;

/**
 * A CompoundPolicyCondition aggregates zero or more instances of the PolicyCondition class, via the PolicyConditionInPolicyCondition association. A
 * CompoundPolicyCondition that aggregates zero Conditions is not valid; it may, however, be in the process of being defined. Note that a
 * CompoundPolicyCondition should have no effect until it is valid.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyConditionInPolicyCondition extends PolicyConditionStructure {

	/**
	 * Default constructor
	 */
	public PolicyConditionInPolicyCondition() {
	}

	/**
	 * This property represents the CompoundPolicyCondition that contains one or more PolicyConditions.
	 */
	private CompoundPolicyCondition	groupComponent;

	/**
	 * This property holds the name of a PolicyCondition contained by one or more PolicyRules.
	 */
	private PolicyCondition			partComponent;

	public CompoundPolicyCondition getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(CompoundPolicyCondition groupComponent) {
		this.groupComponent = groupComponent;
	}

	public PolicyCondition getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(PolicyCondition partComponent) {
		this.partComponent = partComponent;
	}

}
