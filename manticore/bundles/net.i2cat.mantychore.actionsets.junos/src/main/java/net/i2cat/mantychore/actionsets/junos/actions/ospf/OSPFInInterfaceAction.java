package net.i2cat.mantychore.actionsets.junos.actions.ospf;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.actionsets.junos.actions.JunosAction;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import net.i2cat.mantychore.model.OSPFProtocolEndpoint;
import net.i2cat.mantychore.model.ProtocolEndpoint;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class OSPFInInterfaceAction extends JunosAction {

	/**
	 * 
	 */
	public OSPFInInterfaceAction() {
		super();
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		try {
			EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage());
			command.initialize();
			Response response = sendCommandToProtocol(command, protocol);
			actionResponse.addResponse(response);
		} catch (Exception e) {
			throw new ActionException(this.actionID, e);
		}
		validateAction(actionResponse);
	}

	@Override
	public void prepareMessage() throws ActionException {

		// Check the template
		checkTemplate(template);

		// Check the params
		checkParams(params);

		try {
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
			} else if (params != null && params instanceof ManagedElement && ((ManagedElement) params).getElementName() == null) {
				((ManagedElement) params).setElementName("");
			}

			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("areaId", getAreaId((LogicalPort) params));
			extraParams.put("statusDown", OperationalStatus.STOPPED.toString());
			extraParams.put("statusUp", OperationalStatus.OK.toString());

			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * Get the area from LogicalPort. A LogicalPort has an unic OSPFProtocolEndpoint
	 * 
	 * @param logicalPort
	 * @return area id
	 * @throws IOException
	 */
	private String getAreaId(LogicalPort logicalPort) throws IOException {

		String areaId = null;

		List<ProtocolEndpoint> lProtocolEndPoint = logicalPort.getProtocolEndpoint();
		// Search in the list to find the OSPFProtocolEndpoint
		for (ProtocolEndpoint protocolEndpoint : lProtocolEndPoint) {
			if (protocolEndpoint instanceof OSPFProtocolEndpoint) {
				long ip = ((OSPFProtocolEndpoint) protocolEndpoint).getOSPFArea().getAreaID();
				areaId = IPUtilsHelper.ipv4LongToString(ip);
			}
		}

		return areaId;
	}

	/**
	 * Params must be a LogicalPort.
	 * 
	 * @param params
	 *            LogicalPort whose description to set
	 * @throws ActionException
	 *             if params is null or is not a LogicalPort
	 */
	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params == null || !(params instanceof LogicalPort)) {
			throw new ActionException("Invalid parameters for action " + getActionID());
		}

		return true;
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// Nothing to do
	}

	/**
	 * @param template
	 * @throws ActionException
	 *             if templete is null or empty
	 */
	private boolean checkTemplate(String template) throws ActionException {

		// The template can not be null or empty
		if (template == null || template.equals("")) {
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		}

		return true;
	}

}
