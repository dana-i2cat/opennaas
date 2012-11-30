package org.opennaas.extensions.router.model;

/**
 * CIM_PolicyComponent is a generic association used to establish 'part of' relationships between the subclasses of CIM_Policy. For example, the
 * PolicyConditionInPolicyRule association defines that PolicyConditions are part of a PolicyRule.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class PolicyComponent extends Component {

	/**
	 * Default constructor
	 */
	public PolicyComponent() {
	}

	/**
	 * The parent Policy in the association.
	 */
	private Policy	groupComponent;

	/**
	 * The child/part Policy in the association.
	 */
	private Policy	partComponent;

	public Policy getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(Policy groupComponent) {
		this.groupComponent = groupComponent;
	}

	public Policy getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(Policy partComponent) {
		this.partComponent = partComponent;
	}

}
