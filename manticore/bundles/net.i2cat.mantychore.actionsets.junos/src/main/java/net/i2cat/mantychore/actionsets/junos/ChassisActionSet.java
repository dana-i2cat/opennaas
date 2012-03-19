package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;
import net.i2cat.mantychore.actionsets.junos.actions.chassis.ConfigureEncapsulationAction;
import net.i2cat.mantychore.actionsets.junos.actions.chassis.ConfigureStatusAction;
import net.i2cat.mantychore.actionsets.junos.actions.chassis.ConfigureSubInterfaceAction;
import net.i2cat.mantychore.actionsets.junos.actions.chassis.DeleteSubInterfaceAction;
import net.i2cat.mantychore.actionsets.junos.actions.logicalrouters.AddInterfaceToLogicalRouterAction;
import net.i2cat.mantychore.actionsets.junos.actions.logicalrouters.CreateLogicalRouterAction;
import net.i2cat.mantychore.actionsets.junos.actions.logicalrouters.DeleteLogicalRouterAction;
import net.i2cat.mantychore.actionsets.junos.actions.logicalrouters.RemoveInterfaceFromLogicalRouterAction;

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
		this.putAction(ActionConstants.CONFIGURESUBINTERFACE, ConfigureSubInterfaceAction.class);
		this.putAction(ActionConstants.SETENCAPSULATION, ConfigureEncapsulationAction.class);
		this.putAction(ActionConstants.CONFIGURESTATUS, ConfigureStatusAction.class);
		// this.putAction(ActionConstants.SETINTERFACEDESCRIPTION, SetInterfaceDescriptionAction.class);
		this.putAction(ActionConstants.CREATELOGICALROUTER, CreateLogicalRouterAction.class);
		this.putAction(ActionConstants.DELETELOGICALROUTER, DeleteLogicalRouterAction.class);
		this.putAction(ActionConstants.ADDINTERFACETOLOGICALROUTER, AddInterfaceToLogicalRouterAction.class);
		this.putAction(ActionConstants.REMOVEINTERFACEFROMLOGICALROUTER, RemoveInterfaceFromLogicalRouterAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(ActionConstants.DELETESUBINTERFACE);
		actionNames.add(ActionConstants.SETENCAPSULATION);
		actionNames.add(ActionConstants.CONFIGURESTATUS);
		actionNames.add(ActionConstants.CONFIGURESUBINTERFACE);
		// actionNames.add(ActionConstants.SETINTERFACEDESCRIPTION);
		actionNames.add(ActionConstants.CREATELOGICALROUTER);
		actionNames.add(ActionConstants.DELETELOGICALROUTER);
		actionNames.add(ActionConstants.ADDINTERFACETOLOGICALROUTER);
		actionNames.add(ActionConstants.REMOVEINTERFACEFROMLOGICALROUTER);

		return actionNames;
	}
}
