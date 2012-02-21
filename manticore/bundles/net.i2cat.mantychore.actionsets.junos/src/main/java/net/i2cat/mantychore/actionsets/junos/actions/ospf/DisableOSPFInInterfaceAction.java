package net.i2cat.mantychore.actionsets.junos.actions.ospf;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;

public class DisableOSPFInInterfaceAction extends OSPFInInterfaceAction {

	/**
	 * 
	 */
	public DisableOSPFInInterfaceAction() {
		super();
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {

		setActionID(ActionConstants.OSPF_DISABLE_INTERFACE);
		setTemplate("/VM_files/ospfEnableDisableInterface.vm");
		this.protocolName = "netconf";
	}

}
