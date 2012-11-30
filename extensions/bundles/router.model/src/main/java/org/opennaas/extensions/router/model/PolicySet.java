package org.opennaas.extensions.router.model;

/**
 * PolicySet is an abstract class that represents a set of policies that form a coherent set. The set of contained policies has a common decision
 * strategy and a common set of policy roles (defined via the PolicySetInRole Collection association). Subclasses include PolicyGroup and PolicyRule.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class PolicySet extends Policy {

	/**
	 * Default constructor
	 */
	public PolicySet() {
	}

	/**
	 * PolicyDecisionStrategy defines the evaluation method used for policies contained in the PolicySet. There are two values currently defined:<br>
	 * <br>
	 * - 'First Matching' (1) executes the actions of the first rule whose conditions evaluate to TRUE. The concept of 'first' is determined by
	 * examining the priority of the rule within the policy set (i.e., by examining the property, PolicySetComponent.Priority). Note that this
	 * ordering property MUST be maintained when processing the PolicyDecisionStrategy.<br>
	 * <br>
	 * - 'All' (2) executes the actions of ALL rules whose conditions evaluate to TRUE, in the set. As noted above, the order of processing of the
	 * rules is defined by the property, PolicySetComponent.Priority (and within a rule, the ordering of the actions is defined by the property,
	 * PolicyActionStructure.ActionOrder). Note that when this strategy is defined, processing MUST be completed of ALL rules whose conditions
	 * evaluate to TRUE, regardless of errors in the execution of the rule actions.
	 */
	public enum policyDecisionStrategy_enum {

		First_Matching(1),
		All(2);

		private final int	localValue;

		policyDecisionStrategy_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private policyDecisionStrategy_enum	policyDecisionStrategy;

	/**
	 * The PolicyRoles property represents the roles associated with a PolicySet. All contained PolicySet instances inherit the values of the
	 * PolicyRoles of the aggregating PolicySet but the values are not copied. A contained PolicySet instance may, however, add additional PolicyRoles
	 * to those it inherits from its aggregating PolicySet(s). Each value in PolicyRoles multi-valued property represents a role for which the
	 * PolicySet applies, i.e., the PolicySet should be used by any enforcement point that assumes any of the listed PolicyRoles values.<br>
	 * <br>
	 * <br>
	 * <br>
	 * Although not officially designated as 'role combinations', multiple roles may be specified using the form:<br>
	 * <br>
	 * &lt;RoleName&gt;[&amp;&amp;&lt;RoleName&gt;]*<br>
	 * <br>
	 * where the individual role names appear in alphabetical order (according to the collating sequence for UCS-2). Implementations may treat
	 * PolicyRoles values that are specified as 'role combinations' as simple strings.<br>
	 * <br>
	 * <br>
	 * <br>
	 * This property is deprecated in lieu of the use of an association, CIM_PolicySetInRoleCollection. The latter is a more explicit and less
	 * error-prone approach to modeling that a PolicySet has one or more PolicyRoles.
	 * 
	 * @deprecated - Replaced by CIM_PolicySetInRoleCollection
	 */
	@Deprecated
	private String[]					policyRoles;

	/**
	 * Indicates whether this PolicySet is administratively enabled, administratively disabled, or enabled for debug. The "EnabledForDebug" property
	 * value is deprecated and, when it or any value not understood by the receiver is specified, the receiving enforcement point treats the PolicySet
	 * as "Disabled". To determine if a PolicySet is "Enabled", the containment hierarchy specified by the PolicySetComponent aggregation is examined
	 * and the Enabled property values of the hierarchy are ANDed together. Thus, for example, everything aggregated by a PolicyGroup may be disabled
	 * by setting the Enabled property in the PolicyGroup instance to "Disabled" without changing the Enabled property values of any of the aggregated
	 * instances. The default value is 1 ("Enabled").
	 */
	public enum enabled_enum {

		Enabled(1),
		Disabled(2),
		Enabled_For_Debug(3);

		private final int	localValue;

		enabled_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private enabled_enum	enabled	= enabled_enum.Enabled;

	public policyDecisionStrategy_enum getPolicyDecisionStrategy() {
		return policyDecisionStrategy;
	}

	public void setPolicyDecisionStrategy(policyDecisionStrategy_enum policyDecisionStrategy) {
		this.policyDecisionStrategy = policyDecisionStrategy;
	}

	@Deprecated
	public String[] getPolicyRoles() {
		return policyRoles;
	}

	@Deprecated
	public void setPolicyRoles(String[] policyRoles) {
		this.policyRoles = policyRoles;
	}

	public enabled_enum getEnabled() {
		return enabled;
	}

	public void setEnabled(enabled_enum enabled) {
		this.enabled = enabled;
	}

}
