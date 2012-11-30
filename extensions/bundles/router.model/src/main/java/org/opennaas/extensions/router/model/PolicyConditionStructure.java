package org.opennaas.extensions.router.model;

/**
 * PolicyConditions may be aggregated into rules and into compound conditions. PolicyConditionStructure is the abstract aggregation class for the
 * structuring of policy conditions.<br>
 * <br>
 * <br>
 * <br>
 * The Conditions aggregated by a PolicyRule or CompoundPolicyCondition are grouped into two levels of lists: either an ORed set of ANDed sets of
 * conditions (DNF, the default) or an ANDed set of ORed sets of conditions (CNF). Individual PolicyConditions in these lists may be negated. The
 * property ConditionListType specifies which of these two grouping schemes applies to a particular PolicyRule or CompoundPolicyCondition instance.<br>
 * <br>
 * <br>
 * <br>
 * One or more PolicyTimePeriodConditions may be among the conditions associated with a PolicyRule or CompoundPolicyCondition via the
 * PolicyConditionStructure subclass association. In this case, the time periods are simply additional Conditions to be evaluated along with any
 * others that are specified.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class PolicyConditionStructure extends PolicyComponent {

	/**
	 * Default constructor
	 */
	public PolicyConditionStructure() {
	}

	/**
	 * This property represents the Policy that contains one or more PolicyConditions.
	 */
	private Policy			groupComponent;

	/**
	 * This property holds the name of a PolicyCondition contained by one or more PolicyRule or CompoundPolicyCondition instances.
	 */
	private PolicyCondition	partComponent;

	/**
	 * Unsigned integer indicating the group to which the contained PolicyCondition belongs. This integer segments the Conditions into the ANDed sets
	 * (when the ConditionListType is "DNF") or, similarly, into the ORed sets (when the ConditionListType is "CNF").
	 */
	private int				groupNumber;

	/**
	 * Indication of whether the contained PolicyCondition is negated. TRUE indicates that the PolicyCondition IS negated, FALSE indicates that it IS
	 * NOT negated.
	 */
	private boolean			conditionNegated;

	public Policy getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(Policy groupComponent) {
		this.groupComponent = groupComponent;
	}

	public PolicyCondition getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(PolicyCondition partComponent) {
		this.partComponent = partComponent;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public boolean isConditionNegated() {
		return conditionNegated;
	}

	public void setConditionNegated(boolean conditionNegated) {
		this.conditionNegated = conditionNegated;
	}

}
