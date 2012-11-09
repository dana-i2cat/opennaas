package org.opennaas.extensions.router.model;

/**
 * The central class used for representing the 'If Condition then Action' semantics of a policy rule. A PolicyRule condition, in the most general
 * sense, is represented as either an ORed set of ANDed conditions (Disjunctive Normal Form, or DNF) or an ANDed set of ORed conditions (Conjunctive
 * Normal Form, or CNF). Individual conditions may either be negated (NOT C) or unnegated (C). The actions specified by a PolicyRule are to be
 * performed if and only if the PolicyRule condition (whether it is represented in DNF or CNF) evaluates to TRUE.<br>
 * <br>
 * <br>
 * <br>
 * The conditions and actions associated with a PolicyRule are modeled, respectively, with subclasses of PolicyCondition and PolicyAction. These
 * condition and action objects are tied to instances of PolicyRule by the PolicyConditionInPolicyRule and PolicyActionInPolicyRule aggregations.<br>
 * <br>
 * <br>
 * <br>
 * A PolicyRule may also be associated with one or more policy time periods, indicating the schedule according to which the policy rule is active and
 * inactive. In this case it is the PolicySetValidityPeriod aggregation that provides this linkage.<br>
 * <br>
 * <br>
 * <br>
 * The PolicyRule class uses the property ConditionListType, to indicate whether the conditions for the rule are in DNF (disjunctive normal form), CNF
 * (conjunctive normal form) or, in the case of a rule with no conditions, as an UnconditionalRule. The PolicyConditionInPolicyRule aggregation
 * contains two additional properties to complete the representation of the Rule's conditional expression. The first of these properties is an integer
 * to partition the referenced PolicyConditions into one or more groups, and the second is a Boolean to indicate whether a referenced Condition is
 * negated. An example shows how ConditionListType and these two additional properties provide a unique representation of a set of PolicyConditions in
 * either DNF or CNF.<br>
 * <br>
 * <br>
 * <br>
 * Suppose we have a PolicyRule that aggregates five PolicyConditions C1 through C5, with the following values in the properties of the five
 * PolicyConditionInPolicyRule associations:<br>
 * <br>
 * C1: GroupNumber = 1, ConditionNegated = FALSE<br>
 * <br>
 * C2: GroupNumber = 1, ConditionNegated = TRUE<br>
 * <br>
 * C3: GroupNumber = 1, ConditionNegated = FALSE<br>
 * <br>
 * C4: GroupNumber = 2, ConditionNegated = FALSE<br>
 * <br>
 * C5: GroupNumber = 2, ConditionNegated = FALSE<br>
 * <br>
 * <br>
 * <br>
 * If ConditionListType = DNF, then the overall condition for the PolicyRule is:<br>
 * <br>
 * (C1 AND (NOT C2) AND C3) OR (C4 AND C5)<br>
 * <br>
 * <br>
 * <br>
 * On the other hand, if ConditionListType = CNF, then the overall condition for the PolicyRule is:<br>
 * <br>
 * (C1 OR (NOT C2) OR C3) AND (C4 OR C5)<br>
 * <br>
 * <br>
 * <br>
 * In both cases, there is an unambiguous specification of the overall condition that is tested to determine whether to perform the PolicyActions
 * associated with the PolicyRule.<br>
 * <br>
 * <br>
 * <br>
 * PolicyRule instances may also be used to aggregate other PolicyRules and/or PolicyGroups. When used in this way to implement nested rules, the
 * conditions of the aggregating rule apply to the subordinate rules as well. However, any side effects of condition evaluation or the execution of
 * actions MUST NOT affect the result of the evaluation of other conditions evaluated by the rule engine in the same evaluation pass. That is, an
 * implementation of a rule engine MAY evaluate all conditions in any order before applying the priority and determining which actions are to be
 * executed.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyRule extends PolicySet {

	/**
	 * Default constructor
	 */
	public PolicyRule() {
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
	 * A user-friendly name of this PolicyRule.
	 */
	private String	policyRuleName;

	/**
	 * Indicates whether the list of PolicyConditions associated with this PolicyRule is in disjunctive normal form (DNF), conjunctive normal form
	 * (CNF), or has no conditions (i.e., is an UnconditionalRule) and is automatically evaluated to "True." The default value is 1 ("DNF").
	 */
	public enum conditionListType_enum {

		Unconditional_Rule(0),
		DNF(1),
		CNF(2);

		private final int	localValue;

		conditionListType_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private conditionListType_enum	conditionListType	= conditionListType_enum.DNF;

	/**
	 * A free-form string that can be used to provide guidelines on how this PolicyRule should be used.
	 */
	private String					ruleUsage;

	/**
	 * PolicyRule.Priority is deprecated and replaced by providing the priority for a rule (and a group) in the context of the aggregating PolicySet
	 * instead of the priority being used for all aggregating PolicySet instances. Thus, the assignment of priority values is much simpler.<br>
	 * <br>
	 * <br>
	 * <br>
	 * A non-negative integer for prioritizing this Policy Rule relative to other Rules. A larger value indicates a higher priority. The default value
	 * is 0.
	 * 
	 * @deprecated - Replaced by CIM_PolicySetComponent.Priority
	 */
	@Deprecated
	private int						priority			= 0;

	/**
	 * A flag indicating that the evaluation of the Policy Conditions and execution of PolicyActions (if the Conditions evaluate to TRUE) is required.
	 * The evaluation of a PolicyRule MUST be attempted if the Mandatory property value is TRUE. If the Mandatory property is FALSE, then the
	 * evaluation of the Rule is 'best effort' and MAY be ignored.
	 * 
	 * @deprecated - Replaced by No Value
	 */
	@Deprecated
	private boolean					mandatory;

	/**
	 * This property gives a policy administrator a way of specifying how the ordering of the PolicyActions associated with this PolicyRule is to be
	 * interpreted. Three values are supported:<br>
	 * <br>
	 * o mandatory(1): Do the actions in the indicated order, or don't do them at all.<br>
	 * <br>
	 * o recommended(2): Do the actions in the indicated order if you can, but if you can't do them in this order, do them in another order if you
	 * can.<br>
	 * <br>
	 * o dontCare(3): Do them -- I don't care about the order.<br>
	 * <br>
	 * The default value is 3 ("DontCare").
	 */
	public enum sequencedActions_enum {

		Mandatory(1),
		Recommended(2),
		Dont_Care(3);

		private final int	localValue;

		sequencedActions_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private sequencedActions_enum	sequencedActions	= sequencedActions_enum.Dont_Care;

	/**
	 * ExecutionStrategy defines the strategy to be used in executing the sequenced actions aggregated by this PolicyRule. There are three execution
	 * strategies:<br>
	 * <br>
	 * <br>
	 * <br>
	 * Do Until Success - execute actions according to predefined order, until successful execution of a single action.<br>
	 * <br>
	 * Do All - execute ALL actions which are part of the modeled set, according to their predefined order. Continue doing this, even if one or more
	 * of the actions fails.<br>
	 * <br>
	 * Do Until Failure - execute actions according to predefined order, until the first failure in execution of an action instance.
	 */
	public enum executionStrategy_enum {

		Do_Until_Success(1),
		Do_All(2),
		Do_Until_Failure(3);

		private final int	localValue;

		executionStrategy_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private executionStrategy_enum	executionStrategy;

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

	public String getPolicyRuleName() {
		return policyRuleName;
	}

	public void setPolicyRuleName(String policyRuleName) {
		this.policyRuleName = policyRuleName;
	}

	public conditionListType_enum getConditionListType() {
		return conditionListType;
	}

	public void setConditionListType(conditionListType_enum conditionListType) {
		this.conditionListType = conditionListType;
	}

	public String getRuleUsage() {
		return ruleUsage;
	}

	public void setRuleUsage(String ruleUsage) {
		this.ruleUsage = ruleUsage;
	}

	@Deprecated
	public int getPriority() {
		return priority;
	}

	@Deprecated
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Deprecated
	public boolean isMandatory() {
		return mandatory;
	}

	@Deprecated
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public sequencedActions_enum getSequencedActions() {
		return sequencedActions;
	}

	public void setSequencedActions(sequencedActions_enum sequencedActions) {
		this.sequencedActions = sequencedActions;
	}

	public executionStrategy_enum getExecutionStrategy() {
		return executionStrategy;
	}

	public void setExecutionStrategy(executionStrategy_enum executionStrategy) {
		this.executionStrategy = executionStrategy;
	}

}
