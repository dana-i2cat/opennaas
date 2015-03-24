package org.opennaas.extensions.router.junos.actionssets.actions.linkaggregation;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.capability.linkaggregation.LinkAggregationActionSet;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.AggregatedLogicalPort;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.utils.ModelHelper;

/**
 * Implementation of LinkAggregationActionSet.CREATE_AGGRETATED_INTERFACE for junos 10.04 devices.
 * 
 * Configures a new aggregated interface specified by given AggregatedLogicalPort.
 * 
 * NOTICE: This action will fail if any interface in AggregatedLogicalPort.getInterfaces() contains subinterfaces (units). The router will launch
 * following error: "logical unit is not allowed on aggregated links : unit XX". The user is forced to manually delete these subinterfaces in order to
 * be able to put an interface as part of an aggregated.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 */
public class CreateAggregatedInterfaceAction extends JunosAction {

	/**
	 * The name of any aggregated interface in junos devices must start with this prefix
	 */
	public static final String		JUNOS_AGGREGATED_INTERFACES_PREFIX	= "ae";

	private static final String		CREATION_TEMPLATE					= "/VM_files/createAggregatedInterface.vm";
	private static final String		ADD_TO_AGGREGATED_TEMPLATE			= "/VM_files/addInterfaceToAggregated.vm";
	private static final String		REMOVE_UNIT_TEMPLATE				= "/VM_files/removeInterfaceUnit.vm";

	private AggregatedLogicalPort	aggregator;
	private boolean 				forceFlag 							= false;

	public CreateAggregatedInterfaceAction() {
		super();
		setActionID(LinkAggregationActionSet.CREATE_AGGREGATED_INTERFACE);
		this.protocolName = "netconf";
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		
		Object[] paramsArray = (Object[]) params;
		if (paramsArray.length != 2)
			throw new ActionException("Invalid parameter in action " + actionID + ". Two parameters expected");
		
		if (!(paramsArray[0] instanceof AggregatedLogicalPort)) {
			throw new ActionException("Invalid parameter in action " + actionID + ". Expected AggregatedLogicalPort.");
		}
		
		if (!(paramsArray[1] instanceof Boolean)) {
			throw new ActionException("Invalid parameter in action " + actionID + ". Expected Boolean.");
		}

		AggregatedLogicalPort aggregator = (AggregatedLogicalPort) paramsArray[0];
		if (StringUtils.isEmpty(aggregator.getElementName()))
			throw new ActionException("Invalid parameter in action " + actionID + "." +
					" Missing AggregatedLogicalPort name.");
		if (!aggregator.getElementName().startsWith(JUNOS_AGGREGATED_INTERFACES_PREFIX))
			throw new ActionException("Invalid parameter in action " + actionID + "." +
					" AggregatedLogicalPort name must begin with " + JUNOS_AGGREGATED_INTERFACES_PREFIX + " in junos.");
		if (aggregator.getInterfaces() == null || aggregator.getInterfaces().isEmpty())
			throw new ActionException("Invalid parameter in action " + actionID + "." +
					" Given AggregatedLogicalPort must contain at least one interface to be aggregated.");

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		checkParams(params);
		Object[] paramsArray = (Object[]) params;
		this.aggregator = (AggregatedLogicalPort) paramsArray[0];
		this.forceFlag = ((Boolean) paramsArray[1]).booleanValue();
		// Nothing to prepare
		// Messages are prepared in executeListCommand
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		
		actionResponse.addResponse(execEditCommand(prepareCreateAggregatedInterfaceMessage(aggregator), protocol));
		for (String interfaceName : aggregator.getInterfaces()) {
			if (forceFlag) {
				for (NetworkPort unitToClear : getInterfacesToRemove(interfaceName)) {
					actionResponse.addResponse(execRemovingEditCommand(
							prepareClearUnitMessage(interfaceName, unitToClear.getPortNumber()), protocol));
				}
			}
			actionResponse.addResponse(execEditCommand(
					prepareAddInterfaceToAggregatedMessage(interfaceName, aggregator.getElementName()), protocol));
		}
		validateAction(actionResponse);
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// nothing to parse
	}

	/**
	 * Creates an xml message that configures given aggregated interface (aggregator). <br/>
	 * If given interface already exists, the operation will fail.
	 * 
	 * @param aggregator
	 * @return
	 * @throws ActionException
	 */
	private String prepareCreateAggregatedInterfaceMessage(AggregatedLogicalPort aggregator) throws ActionException {
		Map<String, Object> extraParams = prepareCreateAggregatedInterfaceExtraParams(aggregator);
		try {
			// FIXME passing unchecked values to junos (inside aggregator). May cause security issues.
			return XmlHelper.formatXML(prepareVelocityCommand(aggregator, CREATION_TEMPLATE, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * Creates an xml message that configures given interfaceName to be part of given aggregatedInterfaceName.<br/>
	 * This operation replaces the aggregatedInterfaceName interfaceName is part of, despite the fact that ifaceName may be part of another
	 * aggregatedInterface before sending these message.
	 * 
	 * @param interfaceName
	 * @param aggregatedInterfaceName
	 * @return
	 * @throws ActionException
	 */
	private String prepareAddInterfaceToAggregatedMessage(String interfaceName, String aggregatedInterfaceName) throws ActionException {
		Map<String, Object> extraParams = prepareAddInterfaceToAggregatedExtraParams(interfaceName, aggregatedInterfaceName);
		try {
			// FIXME passing unchecked values to junos (interfaceName and inside extraParams). May cause security issues.
			return XmlHelper.formatXML(prepareVelocityCommand(interfaceName, ADD_TO_AGGREGATED_TEMPLATE, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}
	
	private String prepareClearUnitMessage(String interfaceName, int portNumber) throws ActionException {
		Map<String, Object> extraParams = prepareLRVelocityExtraParams();
		extraParams.put("ifaceName", interfaceName);
		extraParams.put("unitName", portNumber);
		
		try {
			// FIXME passing unchecked values to junos (interfaceName and inside extraParams). May cause security issues.
			return XmlHelper.formatXML(prepareVelocityCommand(interfaceName, REMOVE_UNIT_TEMPLATE, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * Prepares extraParams for CREATION_TEMPLATE.<br/>
	 * 
	 * Sets following params: <br/>
	 * - boolean setMinimumLinks <br/>
	 * - boolean setLinkSpeed <br/>
	 * - boolean isLR <br/>
	 * - String lrName <br/>
	 * 
	 * See the template for more information.
	 * 
	 * @param aggregator
	 * @return extraParams required for CREATION_TEMPLATE
	 */
	private Map<String, Object> prepareCreateAggregatedInterfaceExtraParams(AggregatedLogicalPort aggregator) {
		Map<String, Object> extraParams = prepareLRVelocityExtraParams();

		boolean setMinimumLinks = true;
		if (StringUtils.isEmpty(aggregator.getAggregatedOptions().getMinimumLinks()))
			setMinimumLinks = false;

		boolean setLinkSpeed = true;
		if (StringUtils.isEmpty(aggregator.getAggregatedOptions().getLinkSpeed()))
			setLinkSpeed = false;

		extraParams.put("setMinimumLinks", setMinimumLinks);
		extraParams.put("setLinkSpeed", setLinkSpeed);

		return extraParams;
	}

	/**
	 * Prepares extraParams for ADD_TO_AGGREGATED_TEMPLATE. <br/>
	 * 
	 * Sets following params: <br/>
	 * - String aggregatedIfaceName <br/>
	 * - boolean isLR <br/>
	 * - String lrName <br/>
	 * See the template for more information.
	 * 
	 * @param interfaceName
	 *            the interface to be aggregated
	 * @param aggregatedInterfaceName
	 *            the interface to aggregate to
	 * @return extraParams required for ADD_TO_AGGREGATED_TEMPLATE
	 */
	private Map<String, Object> prepareAddInterfaceToAggregatedExtraParams(String interfaceName, String aggregatedInterfaceName) {
		Map<String, Object> extraParams = prepareLRVelocityExtraParams();
		extraParams.put("ifaceName", interfaceName);
		extraParams.put("aggregatedIfaceName", aggregatedInterfaceName);

		return extraParams;
	}

	private Map<String, Object> prepareLRVelocityExtraParams() {
		Map<String, Object> extraParams = new HashMap<String, Object>();
		boolean isLR = isLogicalRouter((ComputerSystem) modelToUpdate);
		extraParams.put("isLR", isLR);
		if (isLR)
			extraParams.put("lrName", getLogicalRouterName((ComputerSystem) modelToUpdate));

		return extraParams;
	}

	private boolean isLogicalRouter(ComputerSystem system) {
		if (system.getElementName() != null) {
			return true;
		}
		return false;
	}

	private String getLogicalRouterName(ComputerSystem system) {
		if (!isLogicalRouter(system))
			return null;

		return system.getElementName();
	}

	private Response execEditCommand(String netconfMsg, IProtocolSession protocol) throws ActionException {
		try {
			EditNetconfCommand command = new EditNetconfCommand(netconfMsg);
			command.initialize();
			return sendCommandToProtocol(command, protocol);
		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
	}
	
	private Response execRemovingEditCommand(String netconfMsg, IProtocolSession protocol) throws ActionException {
		try {
			// when removing tags, none operation should be used as default
			EditNetconfCommand command = new EditNetconfCommand(netconfMsg, CommandNetconfConstants.NONE_OPERATION);
			command.initialize();
			return sendCommandToProtocol(command, protocol);
		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
	}
	
	private List<NetworkPort> getInterfacesToRemove(String interfaceName) {
		List<NetworkPort> allPorts = ModelHelper.getInterfaces((ComputerSystem) modelToUpdate);
		List<NetworkPort> filtered = new ArrayList<NetworkPort>();
		
		for (NetworkPort port : allPorts) {
			if (port.getName().equals(interfaceName))
				filtered.add(port);
		}
		return filtered;
	}

}
