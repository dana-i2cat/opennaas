package org.opennaas.extensions.router.model;

/**
 * PolicyActionInPolicyAction is used to represent the compounding of policy actions into a higher-level policy action.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyActionInPolicyAction extends PolicyActionStructure {

	/**
	 * Default constructor
	 */
	public PolicyActionInPolicyAction() {
	}

	/**
	 * This property represents the CompoundPolicyAction that contains one or more PolicyActions.
	 */
	private CompoundPolicyAction	groupComponent;

	/**
	 * This property holds the name of a PolicyAction contained by one or more CompoundPolicyActions.
	 */
	private PolicyAction			partComponent;

	public CompoundPolicyAction getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(CompoundPolicyAction groupComponent) {
		this.groupComponent = groupComponent;
	}

	public PolicyAction getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(PolicyAction partComponent) {
		this.partComponent = partComponent;
	}
}
