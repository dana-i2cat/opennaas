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
import org.opennaas.extensions.router.model.ManagedElement;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.utils.ModelHelper;

/**
 * Implementation of LinkAggregationActionSet.REMOVE_AGGRETATED_INTERFACE for junos 10.04 devices.<br/>
 * 
 * Removes an aggregated interface identified by given name.<br/>
 * 
 * NOTICE: There must be an AggregatedLogicalPort with given name in the model. Otherwise, executing this action will fail. This requirement is
 * related to the action implementation, not capability defined. There is need to retrieve the names of interfaces given aggregation includes, in
 * order to remove an association in the junos configuration.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class RemoveAggregatedInterfaceAction extends JunosAction {

	/**
	 * The name of any aggregated interface in junos devices must start with this prefix
	 */
	public static final String		JUNOS_AGGREGATED_INTERFACES_PREFIX	= "ae";

	private static final String		DELETION_TEMPLATE					= "/VM_files/deleteAggregatedInterface.vm";
	private static final String		REMOVE_FROM_AGGREGATED_TEMPLATE		= "/VM_files/removeInterfaceFromAggregated.vm";

	private AggregatedLogicalPort	aggregator;

	public RemoveAggregatedInterfaceAction() {
		super();
		setActionID(LinkAggregationActionSet.REMOVE_AGGREGATED_INTERFACE);
		this.protocolName = "netconf";
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (!(params instanceof String)) {
			throw new ActionException("Invalid parameter in action " + actionID + ". Expected interface name.");
		}
		if (StringUtils.isEmpty((String) params))
			throw new ActionException("Invalid parameter in action " + actionID + "." +
					" Missing AggregatedLogicalPort name.");

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		checkParams(params);
		this.aggregator = retrieveAggregatedInterfaceFromModel((String) params);
		// Nothing to prepare
		// Messages are prepared in executeListCommand
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		actionResponse.addResponse(execRemovingEditCommand(prepareDeleteAggregatedInterfaceMessage(aggregator), protocol));
		for (String interfaceName : aggregator.getInterfaces()) {
			actionResponse.addResponse(execRemovingEditCommand(
					prepareRemoveInterfaceFromAggregatedMessage(interfaceName, aggregator.getElementName()), protocol));
		}
		validateAction(actionResponse);
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// nothing to parse
	}

	/**
	 * Creates an xml message that removes given aggregated interface (aggregator). <br/>
	 * 
	 * @param aggregator
	 * @return
	 * @throws ActionException
	 */
	private String prepareDeleteAggregatedInterfaceMessage(AggregatedLogicalPort aggregator) throws ActionException {
		try {
			// FIXME passing unchecked values to junos (aggregator.getElementName()). May cause security issues.
			String xml = XmlHelper.formatXML(prepareVelocityCommand(aggregator.getElementName(), DELETION_TEMPLATE, null));
			return xml;
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
	private String prepareRemoveInterfaceFromAggregatedMessage(String interfaceName, String aggregatedInterfaceName) throws ActionException {
		Map<String, Object> extraParams = prepareRemoveInterfaceFromAggregatedExtraParams(interfaceName, aggregatedInterfaceName);
		try {
			// FIXME passing unchecked values to junos (interfaceName and inside extraParams). May cause security issues.
			String xml = XmlHelper.formatXML(prepareVelocityCommand(interfaceName, REMOVE_FROM_AGGREGATED_TEMPLATE, extraParams));
			return xml;
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * Prepares extraParams for REMOVE_FROM_AGGREGATED_TEMPLATE. <br/>
	 * 
	 * Sets following params: <br/>
	 * - String aggregatedIfaceName <br/>
	 * - boolean isLR <br/>
	 * - String lrName <br/>
	 * See the template for more information.
	 * 
	 * @param interfaceName
	 *            the interface to be de-aggregated
	 * @param aggregatedInterfaceName
	 *            the interface to de-aggregate from
	 * @return extraParams required for REMOVE_FROM_AGGREGATED_TEMPLATE
	 */
	private Map<String, Object> prepareRemoveInterfaceFromAggregatedExtraParams(String interfaceName, String aggregatedInterfaceName) {
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

	private AggregatedLogicalPort retrieveAggregatedInterfaceFromModel(String aggregatedInterfaceName) throws ActionException {

		List<AggregatedLogicalPort> allAggregated = ModelHelper.getAggregatedLogicalPorts((System) modelToUpdate);
		ManagedElement desired = ModelHelper.getManagedElementByElementName(allAggregated, aggregatedInterfaceName);
		if (desired == null)
			throw new ActionException("Could not retrieve aggregatedInterface " + aggregatedInterfaceName + " from the model.");

		return (AggregatedLogicalPort) desired;
	}
}
