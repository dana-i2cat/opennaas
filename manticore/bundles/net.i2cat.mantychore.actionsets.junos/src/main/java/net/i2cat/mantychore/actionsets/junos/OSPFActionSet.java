package net.i2cat.mantychore.actionsets.junos;

import net.i2cat.mantychore.actionsets.junos.actions.ospf.ActivateOSPFAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.ConfigureOSPFAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.DeactivateOSPFAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.DisableOSPFInInterfaceAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.EnableOSPFInInterfaceAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.GetOSPFConfigAction;

import org.opennaas.core.resources.action.ActionSet;

public class OSPFActionSet extends ActionSet {

	public OSPFActionSet() {
		super.setActionSetId("OSPFActionSet");

		this.putAction(ActionConstants.OSPF_GET_CONFIGURATION, GetOSPFConfigAction.class);
		this.putAction(ActionConstants.OSPF_CONFIGURE, ConfigureOSPFAction.class);
		this.putAction(ActionConstants.OSPF_ACTIVATE, ActivateOSPFAction.class);
		this.putAction(ActionConstants.OSPF_DEACTIVATE, DeactivateOSPFAction.class);
		this.putAction(ActionConstants.OSPF_ENABLE_INTERFACE, EnableOSPFInInterfaceAction.class);
		this.putAction(ActionConstants.OSPF_DISABLE_INTERFACE, DisableOSPFInInterfaceAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.OSPF_GET_CONFIGURATION);
	}
}
