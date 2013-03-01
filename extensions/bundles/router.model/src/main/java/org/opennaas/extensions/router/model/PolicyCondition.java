package org.opennaas.extensions.router.model;

/**
 * A class representing a rule-specific or reusable policy condition to be evaluated in conjunction with a Policy Rule. Since all operational details
 * of a PolicyCondition are provided in subclasses of this object, this class is abstract.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class PolicyCondition extends Policy {

	/**
	 * Default constructor
	 */
	public PolicyCondition() {
	}

	/**
	 * The name of the class or the subclass used in the creation of the System object in whose scope this PolicyCondition is defined.<br>
	 * <br>
	 * <br>
	 * <br>
	 * This property helps to identify the System object in whose scope this instance of PolicyCondition exists. For a rule-specific PolicyCondition,
	 * this is the System in whose context the PolicyRule is defined. For a reusable PolicyCondition, this is the instance of PolicyRepository (which
	 * is a subclass of System) that holds the Condition.<br>
	 * <br>
	 * <br>
	 * <br>
	 * Note that this property, and the analogous property SystemName, do not represent propagated keys from an instance of the class System. Instead,
	 * they are properties defined in the context of this class, which repeat the values from the instance of System to which this PolicyCondition is
	 * related, either directly via the PolicyConditionInPolicyRepository association or indirectly via the PolicyConditionInPolicyRule aggregation.
	 */
	private String	systemCreationClassName;

	/**
	 * The name of the System object in whose scope this PolicyCondition is defined.<br>
	 * <br>
	 * <br>
	 * <br>
	 * This property completes the identification of the System object in whose scope this instance of PolicyCondition exists. For a rule-specific
	 * PolicyCondition, this is the System in whose context the PolicyRule is defined. For a reusable PolicyCondition, this is the instance of
	 * PolicyRepository (which is a subclass of System) that holds the Condition.
	 */
	private String	systemName;

	/**
	 * For a rule-specific PolicyCondition, the CreationClassName of the PolicyRule object with which this Condition is associated. For a reusable
	 * Policy Condition, a special value, 'NO RULE', should be used to indicate that this Condition is reusable and not associated with a single
	 * PolicyRule.
	 */
	private String	policyRuleCreationClassName;

	/**
	 * For a rule-specific PolicyCondition, the name of the PolicyRule object with which this Condition is associated. For a reusable PolicyCondition,
	 * a special value, 'NO RULE', should be used to indicate that this Condition is reusable and not associated with a single PolicyRule.
	 */
	private String	policyRuleName;

	/**
	 * CreationClassName indicates the name of the class or the subclass used in the creation of an instance. When used with the other key properties
	 * of this class, this property allows all instances of this class and its subclasses to be uniquely identified.
	 */
	private String	creationClassName;

	/**
	 * A user-friendly name of this PolicyCondition.
	 */
	private String	policyConditionName;

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

	public String getPolicyRuleCreationClassName() {
		return policyRuleCreationClassName;
	}

	public void setPolicyRuleCreationClassName(String policyRuleCreationClassName) {
		this.policyRuleCreationClassName = policyRuleCreationClassName;
	}

	public String getPolicyRuleName() {
		return policyRuleName;
	}

	public void setPolicyRuleName(String policyRuleName) {
		this.policyRuleName = policyRuleName;
	}

	public String getCreationClassName() {
		return creationClassName;
	}

	public void setCreationClassName(String creationClassName) {
		this.creationClassName = creationClassName;
	}

	public String getPolicyConditionName() {
		return policyConditionName;
	}

	public void setPolicyConditionName(String policyConditionName) {
		this.policyConditionName = policyConditionName;
	}

}
