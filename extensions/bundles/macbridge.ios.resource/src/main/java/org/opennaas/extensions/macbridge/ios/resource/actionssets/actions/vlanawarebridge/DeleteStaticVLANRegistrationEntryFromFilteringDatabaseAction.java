package org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
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
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.SwitchportAccessVLANCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.SwitchportModeAccessCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.SwitchportTrunkAllowedVLANRemoveCommand;

/**
 * @author Eduard Grasa
 */
public class DeleteStaticVLANRegistrationEntryFromFilteringDatabaseAction extends Action {

	/**
	 * 
	 */
	public DeleteStaticVLANRegistrationEntryFromFilteringDatabaseAction() {
		super();
		this.setActionID(VLANAwareBridgeActionSet.DELETE_STATIC_VLAN_REGISTRATION_ENTRY_FROM_FILTERING_DATABASE);
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || ! (params instanceof Integer)){
			return false;
		}
		
		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		IOSCommand command = null;
		Integer vlanID = (Integer) this.getParams();
		MACBridge macBridgeModel = (MACBridge) this.getModelToUpdate();
		StaticVLANRegistrationEntry entry = macBridgeModel.getFilteringDatabase().getStaticVLANRegistrations().get(vlanID);
		if (entry == null){
			throw new ActionException("Could not find any static VLAN registration entry in the filtering database related to vlan "+vlanID);
		}
		
		try{
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol("cli", false);
			protocol.connect();
			
			//Login and enter enable mode
			command = new EnableCommand();
			protocol.sendReceive(command.getCommand());
			command = new EnablePasswordCommand((String)protocol.getSessionContext().getSessionParameters().get("protocol.enablepassword"));
			protocol.sendReceive(command.getCommand());
			
			//Remove the VLANs from the ports
			command = new ConfigureTerminalCommand();
			protocol.sendReceive(command.getCommand());
			PortConfiguration portConfiguration = null;
			for(int i=0; i<entry.getPortConfigurations().size(); i++){
				portConfiguration = entry.getPortConfigurations().get(i);
				command = new InterfaceCommand(portConfiguration.getPortInterfaceId());
				protocol.sendReceive(command.getCommand());
				if (portConfiguration.isTagged()){
					command = new SwitchportTrunkAllowedVLANRemoveCommand(entry.getVlanID());
					protocol.sendReceive(command.getCommand());
				}else{
					command = new SwitchportAccessVLANCommand(entry.getVlanID(), true);
					protocol.sendReceive(command.getCommand());
					command = new SwitchportModeAccessCommand(true);
					protocol.sendReceive(command.getCommand());
					//Don't send the shutdown command for now
					/*command = new ShutdownCommand(false);
					protocol.sendReceive(command.getCommand());*/
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
		
		macBridgeModel.getFilteringDatabase().getStaticVLANRegistrations().remove(vlanID);
		
		return ActionResponse.okResponse(this.getActionID());
	}

}