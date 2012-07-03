package org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSession.Status;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.EnableCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.IOSCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.ShowInterfacesStatusCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.ShowVLANCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.TerminalLengthCommand;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eduard Grasa
 */
public class RefreshAction extends Action {
	
	public static final String REFRESH_ACTION = "RefreshAction";
	
	/** CLI Session Log */
    static private Logger logger = LoggerFactory.getLogger(RefreshAction.class);

	/**
	 * 
	 */
	public RefreshAction() {
		super();
		this.setActionID(REFRESH_ACTION);
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		IOSCommand command = null;
		MACBridge macBridgeModel = (MACBridge) this.getModelToUpdate();
		
		try{
			logger.debug("Executing Refresh action");
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol("cli", false);
			
			logger.debug("Connecting to device");
			protocol.connect();
			
			//Login and enter enable mode
			logger.debug("Setting terminal length");
			command = new TerminalLengthCommand();
			protocol.sendReceive(command.getCommand());
			logger.debug("Retrieving interfaces status");
			command = new ShowInterfacesStatusCommand();
			CLIResponseMessage message = (CLIResponseMessage) protocol.sendReceive(command.getCommand());
			command.updateModel(message, macBridgeModel);
			command = new ShowVLANCommand();
			message = (CLIResponseMessage) protocol.sendReceive(command.getCommand());
			command.updateModel(message, macBridgeModel);
			
			protocol.disconnect();
		}catch(ProtocolException ex){
			throw new ActionException(ex);
		}
		
		return ActionResponse.okResponse(this.getActionID());
	}

}