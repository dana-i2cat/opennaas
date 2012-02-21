package net.i2cat.mantychore.actionsets.junos.actions.ospf;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;

public class EnableOSPFInInterfaceAction extends OSPFInInterfaceAction {

	/**
	 * 
	 */
	public EnableOSPFInInterfaceAction() {
		super();
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {

		setActionID(ActionConstants.OSPF_ENABLE_INTERFACE);
		setTemplate("/VM_files/ospfEnableDisableInterface.vm");
		this.protocolName = "netconf";
	}

}
