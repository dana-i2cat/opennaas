package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.ConfigureVLANAction;
import net.i2cat.mantychore.actionsets.junos.actions.CreateSubInterfaceAction;
import net.i2cat.mantychore.actionsets.junos.actions.DeleteSubInterfaceAction;
import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.nexus.resources.action.ActionSet;

@SuppressWarnings("serial")
public class ChassisActionSet extends ActionSet {

	public ChassisActionSet() {
		super.setActionSetId("chassisActionSet");

		// TODO create new actions
		// encapsulation
		// up down interfaces
		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);
		this.putAction(ActionConstants.DELETESUBINTERFACE, DeleteSubInterfaceAction.class);
		this.putAction(ActionConstants.CREATESUBINTERFACE, CreateSubInterfaceAction.class);
		this.putAction(ActionConstants.SETVLAN, ConfigureVLANAction.class);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(ActionConstants.DELETESUBINTERFACE);
		actionNames.add(ActionConstants.CREATESUBINTERFACE);
		actionNames.add(ActionConstants.SETVLAN);
		return actionNames;
	}
}
