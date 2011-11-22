package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.mantychore.actionsets.junos.actions.SetIPv4Action;
import net.i2cat.mantychore.actionsets.junos.actions.SetInterfaceDescriptionAction;

import org.opennaas.core.resources.action.ActionSet;

public class IPActionSet extends ActionSet {
	public IPActionSet() {
		super.setActionSetId("ipActionSet");
		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);
		this.putAction(ActionConstants.SETIPv4, SetIPv4Action.class);
		this.putAction(ActionConstants.SETINTERFACEDESCRIPTION, SetInterfaceDescriptionAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(ActionConstants.SETIPv4);
		actionNames.add(ActionConstants.SETINTERFACEDESCRIPTION);
		return actionNames;
	}

}
