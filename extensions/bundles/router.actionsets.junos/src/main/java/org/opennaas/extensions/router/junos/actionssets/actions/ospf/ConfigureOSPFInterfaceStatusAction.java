package org.opennaas.extensions.router.junos.actionssets.actions.ospf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFProtocolEndpointBase;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.Service;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;

/**
 * @author Jordi Puig
 */
public class ConfigureOSPFInterfaceStatusAction extends JunosAction {

	private static final String	VELOCITY_TEMPLATE	= "/VM_files/ospfConfigureInterfaceStatus.vm";

	private static final String	PROTOCOL_NAME		= "netconf";

	/**
	 * 
	 */
	public ConfigureOSPFInterfaceStatusAction() {
		setActionID(ActionConstants.OSPF_ENABLE_INTERFACE + "/" + ActionConstants.OSPF_DISABLE_INTERFACE);
		setTemplate(VELOCITY_TEMPLATE);
		this.protocolName = PROTOCOL_NAME;
	}

	/**
	 * Send the command to the protocol session
	 * 
	 * @param actionResponse
	 * @param protocol
	 * @throws ActionException
	 */
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

	/**
	 * Create the velocity template to send info to the Junos device
	 * 
	 * @throws ActionException
	 */
	@Override
	public void prepareMessage() throws ActionException {

		// validate input parameters
		validate();

		try {
			String elementName = "";
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				elementName = ((ComputerSystem) modelToUpdate).getElementName();
			}

			OSPFService velocityParam = prepareVelocityParams((List<OSPFProtocolEndpoint>) params, (ComputerSystem) modelToUpdate);

			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("disabledState", EnabledState.DISABLED.toString());
			extraParams.put("enabledState", EnabledState.ENABLED.toString());
			extraParams.put("ipUtilsHelper", IPUtilsHelper.class);
			extraParams.put("elementName", elementName);

			setVelocityMessage(prepareVelocityCommand(velocityParam, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * We do not have to do anything with the response
	 * 
	 * @param responseMessage
	 * @param model
	 * @throws ActionException
	 */
	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// Nothing to do
	}

	/**
	 * Params must be a List<OSPFProtocolEndpoint> with values
	 * 
	 * @param params
	 *            it should be a List<OSPFProtocolEndpoint> with values
	 * @return false if params is null, is empty or is not a List<OSPFProtocolEndpoint>
	 */
	@Override
	public boolean checkParams(Object params) throws ActionException {
		boolean paramsOK = true;

		if (params == null || !(params instanceof List<?>)
				|| ((List<?>) params).size() <= 0) {
			paramsOK = false;
		} else {
			for (Object param : (List<?>) params) {
				if (!(param instanceof OSPFProtocolEndpoint)) {
					paramsOK = false;
					break;
				}
			}
		}

		for (OSPFProtocolEndpoint pep : ((List<OSPFProtocolEndpoint>) params)) {
			if (pep.getName() == null)
				throw new ActionException("Invalid parameters for action " + getActionID() + ": Interface has no name");

			// if (pep.getOSPFArea() == null) {
			// throw new ActionException(
			// "Invalid parameters for action " + getActionID() + ": Could not get OSPF area for interface " + pep.getName());
			// }
		}

		return paramsOK;
	}

	/**
	 * @param template
	 * @throws ActionException
	 *             if template is null or empty
	 */
	private boolean checkTemplate(String template) throws ActionException {

		boolean templateOK = true;
		// The template can not be null or empty
		if (template == null || template.equals("")) {
			templateOK = false;
		}

		return templateOK;
	}

	/**
	 * @throws ActionException
	 */
	private void validate() throws ActionException {

		if (!checkTemplate(template)) {
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		}

		// Check the params
		if (!checkParams(params)) {
			throw new ActionException("Invalid parameters for action " + getActionID());
		}
	}

	/**
	 * Returns an OSPFService containing a copy of each pepToConfig with its OSPFArea.
	 * 
	 * OSPFArea information is gathered from model_ro.
	 * 
	 * @param pepsToConfig
	 *            unchanged list of OSPFProtocolEndpoints to configure
	 * @param model_ro
	 *            unchanged model to extract data from.
	 * @return OSPFService containing a copy of each pepToConfig with its OSPFArea.
	 * @throws ActionException
	 *             if could not get required information from the model
	 */
	private OSPFService prepareVelocityParams(final List<OSPFProtocolEndpoint> pepsToConfig, final ComputerSystem model_ro) throws ActionException {

		// getOSPFService
		OSPFService ospfService = getOSPFService((ComputerSystem) modelToUpdate);
		if (ospfService == null)
			throw new ActionException("Could not get required information from the model. OSPFService missing");

		OSPFService toReturn = new OSPFService();

		// get OSPF area id for each pep to configure
		OSPFArea tmpArea;
		OSPFArea areaWithPep;
		for (OSPFProtocolEndpoint pepToConfig : pepsToConfig) {
			areaWithPep = getOSPFAreaContainingPEPWithName(ospfService, pepToConfig.getName());
			if (areaWithPep == null)
				throw new ActionException(
						"Could not get required information from the model. OSPFAreaID missing for interface " + pepToConfig.getName());

			tmpArea = getOSPFAreaFromID(toReturn, areaWithPep.getAreaID());
			if (tmpArea == null) {
				// create OSPFArea with desired id and add it to toReturn
				tmpArea = new OSPFArea();
				tmpArea.setAreaID(areaWithPep.getAreaID());
				OSPFAreaConfiguration tmpConfig = new OSPFAreaConfiguration();
				toReturn.addOSPFAreaConfiguration(tmpConfig);
				tmpConfig.setOSPFArea(tmpArea);
			}
			// create a copy of pepToConfig with required information and link it to tmpArea
			OSPFProtocolEndpoint tmpPep = new OSPFProtocolEndpoint();
			tmpPep.setName(pepToConfig.getName());
			tmpPep.setEnabledState(pepToConfig.getEnabledState());
			tmpPep.setOSPFArea(tmpArea);
		}

		return toReturn;
	}

	private OSPFArea getOSPFAreaFromID(OSPFService ospfService, long areaId) {
		for (OSPFAreaConfiguration configArea : ospfService.getOSPFAreaConfiguration()) {
			if (configArea.getOSPFArea() != null) {
				if (configArea.getOSPFArea().getAreaID() == areaId)
					return configArea.getOSPFArea();
			}
		}
		return null;
	}

	private OSPFArea getOSPFAreaContainingPEPWithName(OSPFService ospfService, String pepName) {
		for (OSPFAreaConfiguration configArea : ospfService.getOSPFAreaConfiguration()) {
			if (configArea.getOSPFArea() != null) {
				for (OSPFProtocolEndpointBase pep : configArea.getOSPFArea().getEndpointsInArea()) {
					if (pep.getName().equals(pepName)) {
						return configArea.getOSPFArea();
					}
				}
			}
		}
		return null;
	}

	private OSPFService getOSPFService(ComputerSystem system) {
		for (Service service : system.getHostedService()) {
			if (service instanceof OSPFService) {
				return (OSPFService) service;
			}
		}
		return null;
	}

}