package org.opennaas.extensions.router.junos.actionssets.actions.topologydiscovery;

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

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.GenericJunosCommand;
import org.w3c.dom.Document;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GetInterfaceNeighbourAction extends JunosAction {

	private static final String	PROTOCOL_NAME		= "netconf";
	private static final String	TEMPLATE_URL		= "/VM_files/topologydiscovery/getInterfaceNeighbour.vm";
	private static final String	REMOTE_PORT_ID_TAG	= "//lldp-neighbor-information/lldp-remote-port-id";

	private Log					log					= LogFactory.getLog(GetInterfaceNeighbourAction.class);
	private String				rpcResponse;

	public GetInterfaceNeighbourAction() {
		setActionID(ActionConstants.TOPOLOGY_DISCOVERY_GET_INTERFACE_NEIGHBOUR);
		this.protocolName = PROTOCOL_NAME;
		setTemplate(TEMPLATE_URL); // ask for the whole configuration

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

			GenericJunosCommand command = new GenericJunosCommand("get-lldp-interface-neighbour", getVelocityMessage());
			command.initialize();
			Response response = sendCommandToProtocol(command, protocol);
			actionResponse.addResponse(response);
			actionResponse.setResult(rpcResponse);
		} catch (Exception e) {
			throw new ActionException(this.actionID, e);
		}
		validateAction(actionResponse);
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {

		log.info("Parsing netconf response");

		if (!(responseMessage instanceof Reply))
			throw new CommandException("Error parsing response: the response is not a Reply message");

		Reply rpcReply = (Reply) responseMessage;
		String message = rpcReply.getContain();

		if (message != null) {
			try {

				DocumentBuilderFactory builderFactory =
						DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				Document xmlDocument = builder.parse(new ByteArrayInputStream(message.getBytes()));

				XPath xPath = XPathFactory.newInstance().newXPath();

				rpcResponse = (String) xPath.compile(REMOTE_PORT_ID_TAG).evaluate(xmlDocument, XPathConstants.STRING);

			} catch (Exception e) {
				log.error("Error parsing netconf message: ", e);
				throw new ActionException(e);
			}

		}

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null)
			throw new ActionException("Params should not be null for action " + this.actionID);
		if (!(params instanceof String))
			throw new ActionException("Param should be a string for action " + this.actionID);
		if (((String) params).isEmpty())
			throw new ActionException("Param should be an interface name for action " + this.actionID);

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		checkParams(params);

		try {
			setVelocityMessage(prepareVelocityCommand(params, template));
		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

}
