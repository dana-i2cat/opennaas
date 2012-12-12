package org.opennaas.extensions.router.model;

/**
 * PolicyActions may be aggregated into rules and into compound actions. PolicyActionStructure is the abstract aggregation class for the structuring
 * of policy actions.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class PolicyActionStructure extends PolicyComponent {

	/**
	 * Default constructor
	 */
	public PolicyActionStructure() {
	}

	/**
	 * PolicyAction instances may be aggregated into either PolicyRule instances or CompoundPolicyAction instances.
	 */
	private Policy			groupComponent;

	/**
	 * A PolicyAction aggregated by a PolicyRule or CompoundPolicyAction.
	 */
	private PolicyAction	partComponent;

	/**
	 * ActionOrder is an unsigned integer 'n' that indicates the relative position of a PolicyAction in the sequence of actions associated with a
	 * PolicyRule or CompoundPolicyAction. When 'n' is a positive integer, it indicates a place in the sequence of actions to be performed, with
	 * smaller integers indicating earlier positions in the sequence. The special value '0' indicates 'don't care'. If two or more PolicyActions have
	 * the same non-zero sequence number, they may be performed in any order, but they must all be performed at the appropriate place in the overall
	 * action sequence.<br>
	 * <br>
	 * <br>
	 * <br>
	 * A series of examples will make ordering of PolicyActions clearer:<br>
	 * <br>
	 * o If all actions have the same sequence number, regardless of whether it is '0' or non-zero, any order is acceptable.<br>
	 * <br>
	 * o The values:<br>
	 * <br>
	 * 1:ACTION A<br>
	 * <br>
	 * 2:ACTION B<br>
	 * <br>
	 * 1:ACTION C<br>
	 * <br>
	 * 3:ACTION D<br>
	 * <br>
	 * indicate two acceptable orders: A,C,B,D or C,A,B,D,<br>
	 * <br>
	 * since A and C can be performed in either order, but only at the '1' position.<br>
	 * <br>
	 * o The values:<br>
	 * <br>
	 * 0:ACTION A<br>
	 * <br>
	 * 2:ACTION B<br>
	 * <br>
	 * 3:ACTION C<br>
	 * <br>
	 * 3:ACTION D<br>
	 * <br>
	 * require that B,C, and D occur either as B,C,D or as B,D,C. Action A may appear at any point relative to B, C, and D. Thus the complete set of
	 * acceptable orders is: A,B,C,D; B,A,C,D; B,C,A,D; B,C,D,A; A,B,D,C; B,A,D,C; B,D,A,C; B,D,C,A.<br>
	 * <br>
	 * <br>
	 * <br>
	 * Note that the non-zero sequence numbers need not start with '1', and they need not be consecutive. All that matters is their relative
	 * magnitude.
	 */
	private int				actionOrder;

	public Policy getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(Policy groupComponent) {
		this.groupComponent = groupComponent;
	}

	public PolicyAction getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(PolicyAction partComponent) {
		this.partComponent = partComponent;
	}

	public int getActionOrder() {
		return actionOrder;
	}

	public void setActionOrder(int actionOrder) {
		this.actionOrder = actionOrder;
	}

}
