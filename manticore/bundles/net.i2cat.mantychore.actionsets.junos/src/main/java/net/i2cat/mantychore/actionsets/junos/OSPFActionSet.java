package net.i2cat.mantychore.actionsets.junos;

import net.i2cat.mantychore.actionsets.junos.actions.ospf.ActivateOSPFAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.AddOSPFInterfaceInAreaAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.ClearOSPFAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.ConfigureOSPFAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.ConfigureOSPFAreaAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.ConfigureOSPFInterfaceStatusAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.DeactivateOSPFAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.GetOSPFConfigAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.RemoveOSPFAreaAction;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.RemoveOSPFInterfaceInAreaAction;

import org.opennaas.core.resources.action.ActionSet;

public class OSPFActionSet extends ActionSet {

	public OSPFActionSet() {
		super.setActionSetId("OSPFActionSet");

		this.putAction(ActionConstants.OSPF_GET_CONFIGURATION, GetOSPFConfigAction.class);
		this.putAction(ActionConstants.OSPF_CONFIGURE, ConfigureOSPFAction.class);
		this.putAction(ActionConstants.OSPF_CLEAR, ClearOSPFAction.class);
		this.putAction(ActionConstants.OSPF_ACTIVATE, ActivateOSPFAction.class);
		this.putAction(ActionConstants.OSPF_DEACTIVATE, DeactivateOSPFAction.class);
		this.putAction(ActionConstants.OSPF_ENABLE_INTERFACE, ConfigureOSPFInterfaceStatusAction.class);
		this.putAction(ActionConstants.OSPF_DISABLE_INTERFACE, ConfigureOSPFInterfaceStatusAction.class);
		this.putAction(ActionConstants.OSPF_CONFIGURE_AREA, ConfigureOSPFAreaAction.class);
		this.putAction(ActionConstants.OSPF_REMOVE_AREA, RemoveOSPFAreaAction.class);
		this.putAction(ActionConstants.OSPF_ADD_INTERFACE_IN_AREA, AddOSPFInterfaceInAreaAction.class);
		this.putAction(ActionConstants.OSPF_REMOVE_INTERFACE_IN_AREA, RemoveOSPFInterfaceInAreaAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.OSPF_GET_CONFIGURATION);
	}
}
