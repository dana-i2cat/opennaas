package org.opennaas.extensions.router.model;

/**
 * CompoundPolicyCondition is used to represent compound conditions formed by aggregating simpler policy conditions. Compound conditions are
 * constructed by associating subordinate condition terms together using the PolicyConditionInPolicyCondition aggregation.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class CompoundPolicyCondition extends PolicyCondition {

	/**
	 * Default constructor
	 */
	public CompoundPolicyCondition() {
	}

	/**
	 * Indicates whether the list of CompoundPolicyConditions associated with this PolicyRule is in disjunctive normal form (DNF) or conjunctive
	 * normal form (CNF). The default value is 1 ("DNF").
	 */
	public enum conditionListType_enum {

		DNF(1),
		CNF(2);

		private final int	localValue;

		conditionListType_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private conditionListType_enum	conditionListType;

	public conditionListType_enum getConditionListType() {
		return conditionListType;
	}

	public void setConditionListType(conditionListType_enum conditionListType) {
		this.conditionListType = conditionListType;
	}
}
