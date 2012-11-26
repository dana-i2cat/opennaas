package org.opennaas.extensions.router.model;

/**
 * A PolicyRule aggregates zero or more instances of the PolicyCondition class, via the PolicyConditionInPolicyRule association. A Rule that
 * aggregates zero Conditions is not valid; it may, however, be in the process of being defined. Note that a PolicyRule should have no effect until it
 * is valid.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyConditionInPolicyRule extends PolicyConditionStructure {

	/**
	 * Default constructor
	 */
	public PolicyConditionInPolicyRule() {
	}

	/**
	 * This property represents the PolicyRule that contains one or more PolicyConditions.
	 */
	private PolicyRule		groupComponent;

	/**
	 * This property holds the name of a PolicyCondition contained by one or more PolicyRules.
	 */
	private PolicyCondition	partComponent;

	public PolicyRule getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(PolicyRule groupComponent) {
		this.groupComponent = groupComponent;
	}

	public PolicyCondition getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(PolicyCondition partComponent) {
		this.partComponent = partComponent;
	}

	public static PolicyConditionInPolicyRule link(PolicyRule groupComponent, PolicyCondition partComponent) {
		PolicyConditionInPolicyRule assoc = (PolicyConditionInPolicyRule) Association.link(PolicyConditionInPolicyRule.class, groupComponent,
				partComponent);

		assoc.setGroupComponent(groupComponent);
		assoc.setPartComponent(partComponent);

		return assoc;
	}

}
