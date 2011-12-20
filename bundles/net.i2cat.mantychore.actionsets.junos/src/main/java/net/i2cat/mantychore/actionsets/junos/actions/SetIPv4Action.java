package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.ManagedElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class SetIPv4Action extends JunosAction {
	Log	log	= LogFactory.getLog(SetIPv4Action.class);

	public SetIPv4Action() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.SETIPv4);
		setTemplate("/VM_files/configureIPv4.vm");
		this.protocolName = "netconf";

	}

	@Override
	public void parseResponse(Object responseMessage, Object model) {
		// TODO it is not necessary to do response

	}

	@Override
	public boolean checkParams(Object params) {
		// TODO CHECK PARAMS
		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		// TODO implements
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		try {
			IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("ipUtilsHelper", ipUtilsHelper);

			if (((ComputerSystem)modelToUpdate).getElementName() != null) { 
				//is logicalRouter, add LRName param
				((ManagedElement)params).setElementName(((ComputerSystem)modelToUpdate).getElementName()); 
				//TODO If we don't have a ManagedElement initialized
				} else if (params!= null && params instanceof ManagedElement && ((ManagedElement)params).getElementName()==null){
					((ManagedElement)params).setElementName(""); 
					
				}
			
			
			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage());
			command.initialize();
			actionResponse.addResponse(sendCommandToProtocol(command, protocol));
		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
		validateAction(actionResponse);
	}
}
