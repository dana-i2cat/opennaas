package org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge;

import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.capability.macbridge.model.PortConfiguration;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.ConfigureTerminalCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.EnableCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.EnablePasswordCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.ExitCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.IOSCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.InterfaceCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.ShutdownCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.SwitchportAccessVLANCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.SwitchportCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.SwitchportModeAccessCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.SwitchportTrunkAllowedVLANAddCommand;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

/**
 * @author Eduard Grasa
 */
public class AddStaticVLANRegistrationEntryToFilteringDatabaseAction extends Action {

	/**
	 * 
	 */
	public AddStaticVLANRegistrationEntryToFilteringDatabaseAction() {
		super();
		this.setActionID(VLANAwareBridgeActionSet.ADD_STATIC_VLAN_REGISTRATION_ENTRY_TO_FILTERING_DATABASE);
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || ! (params instanceof StaticVLANRegistrationEntry)){
			return false;
		}
		
		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		IOSCommand command = null;
		StaticVLANRegistrationEntry entry = (StaticVLANRegistrationEntry) this.getParams();
		
		try{
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol("cli", false);
			protocol.connect();
			
			//Login and enter enable mode
			command = new EnableCommand();
			protocol.sendReceive(command.getCommand());
			command = new EnablePasswordCommand((String)protocol.getSessionContext().getSessionParameters().get("protocol.enablepassword"));
			protocol.sendReceive(command.getCommand());
			
			//Add the VLANs to the ports
			command = new ConfigureTerminalCommand();
			protocol.sendReceive(command.getCommand());
			PortConfiguration portConfiguration = null;
			for(int i=0; i<entry.getPortConfigurations().size(); i++){
				portConfiguration = entry.getPortConfigurations().get(i);
				command = new InterfaceCommand(portConfiguration.getPortInterfaceId());
				protocol.sendReceive(command.getCommand());
				if (portConfiguration.isTagged()){
					command = new SwitchportTrunkAllowedVLANAddCommand(entry.getVlanID());
					protocol.sendReceive(command.getCommand());
				}else{
					command = new SwitchportCommand(true);
					protocol.sendReceive(command.getCommand());
					command = new SwitchportAccessVLANCommand(entry.getVlanID(), false);
					protocol.sendReceive(command.getCommand());
					command = new SwitchportModeAccessCommand(false);
					protocol.sendReceive(command.getCommand());
					command = new ShutdownCommand(true);
					protocol.sendReceive(command.getCommand());
					command = new ExitCommand();
					protocol.sendReceive(command.getCommand());
				}
			}
			command = new ExitCommand();
			protocol.sendReceive(command.getCommand());
			
			protocol.disconnect();
		}catch(ProtocolException ex){
			throw new ActionException(ex);
		}
		
		MACBridge macBridgeModel = (MACBridge) this.getModelToUpdate();
		macBridgeModel.getFilteringDatabase().getStaticVLANRegistrations().put(new Integer(entry.getVlanID()), entry);
		
		return ActionResponse.okResponse(this.getActionID());
	}

}