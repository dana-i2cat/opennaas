package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.ConfigureEncapsulationAction;
import net.i2cat.mantychore.actionsets.junos.actions.ConfigureStatusAction;
import net.i2cat.mantychore.actionsets.junos.actions.CreateLogicalRouterAction;
import net.i2cat.mantychore.actionsets.junos.actions.CreateSubInterfaceAction;
import net.i2cat.mantychore.actionsets.junos.actions.DeleteLogicalRouterAction;
import net.i2cat.mantychore.actionsets.junos.actions.DeleteSubInterfaceAction;
import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.mantychore.actionsets.junos.actions.SetInterfaceDescriptionAction;

import org.opennaas.core.resources.action.ActionSet;

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
		this.putAction(ActionConstants.SETENCAPSULATION, ConfigureEncapsulationAction.class);
		this.putAction(ActionConstants.CONFIGURESTATUS, ConfigureStatusAction.class);
		//this.putAction(ActionConstants.SETINTERFACEDESCRIPTION, SetInterfaceDescriptionAction.class);
		this.putAction(ActionConstants.CREATELOGICALROUTER, CreateLogicalRouterAction.class);
		this.putAction(ActionConstants.DELETELOGICALROUTER, DeleteLogicalRouterAction.class);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(ActionConstants.DELETESUBINTERFACE);
		actionNames.add(ActionConstants.CREATESUBINTERFACE);
		actionNames.add(ActionConstants.SETENCAPSULATION);
		actionNames.add(ActionConstants.CONFIGURESTATUS);
		//actionNames.add(ActionConstants.SETINTERFACEDESCRIPTION);
		actionNames.add(ActionConstants.CREATELOGICALROUTER);
		actionNames.add(ActionConstants.DELETELOGICALROUTER);

		return actionNames;
	}
}
