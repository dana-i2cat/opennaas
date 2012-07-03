package org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge;

import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.ConfigureTerminalCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.EnableCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.EnablePasswordCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.ExitCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.IOSCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.VLANCommand;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

/**
 * @author Eduard Grasa
 */
public class DeleteVLANConfigurationAction extends Action {

	/**
	 * 
	 */
	public DeleteVLANConfigurationAction() {
		super();
		this.setActionID(VLANAwareBridgeActionSet.DELETE_VLAN_CONFIGURATION);
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
		
		try{
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol("cli", false);
			protocol.connect();
			
			//Login and enter enable mode
			command = new EnableCommand();
			protocol.sendReceive(command.getCommand());
			command = new EnablePasswordCommand((String)protocol.getSessionContext().getSessionParameters().get("protocol.enablepassword"));
			protocol.sendReceive(command.getCommand());
			
			//Remove the VLAN from the VLAN database
			command = new ConfigureTerminalCommand();
			protocol.sendReceive(command.getCommand());
			command = new VLANCommand(vlanID.intValue(), true);
			protocol.sendReceive(command.getCommand());
			command = new ExitCommand();
			protocol.sendReceive(command.getCommand());
			
			protocol.disconnect();
		}catch(ProtocolException ex){
			throw new ActionException(ex);
		}
		
		MACBridge macBridgeModel = (MACBridge) this.getModelToUpdate();
		macBridgeModel.getVLANDatabase().remove(vlanID);
		
		return ActionResponse.okResponse(this.getActionID());
	}

}