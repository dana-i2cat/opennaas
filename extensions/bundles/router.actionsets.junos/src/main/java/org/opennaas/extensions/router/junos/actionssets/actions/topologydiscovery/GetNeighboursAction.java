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
import java.util.HashMap;
import java.util.Map;

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
import org.opennaas.extensions.router.capability.topologydiscovery.model.Neighbours;
import org.opennaas.extensions.router.capability.topologydiscovery.model.Port;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.GenericJunosCommand;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GetNeighboursAction extends JunosAction {

	private static final String	PROTOCOL_NAME			= "netconf";
	private static final String	REQUEST_XML				= "<get-lldp-neighbors-information />";

	private static final String	NEIGHBOUR_PATH			= "//lldp-neighbors-information/lldp-neighbor-information";
	private static final String	LOCAL_PORT_ID_TAG		= "lldp-local-interface";
	private static final String	REMOTE_CHASSIS_ID_TAG	= "lldp-remote-chassis-id";

	private Log					log						= LogFactory.getLog(GetLocalInformationAction.class);
	private Neighbours			rpcResponse;

	public GetNeighboursAction() {
		setActionID(ActionConstants.TOPOLOGY_DISCOVERY_GET_NEIGHBOURS);
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
			GenericJunosCommand command = new GenericJunosCommand("get-lldp-neighbors-information", REQUEST_XML);
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

				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = builderFactory.newDocumentBuilder();

				XPath xPath = XPathFactory.newInstance().newXPath();
				Document xmlDocument = builder.parse(new ByteArrayInputStream(message.getBytes()));

				NodeList neighboursList = (NodeList) xPath.compile(NEIGHBOUR_PATH).evaluate(xmlDocument, XPathConstants.NODESET);

				Map<String, Port> connectionMap = new HashMap<String, Port>();

				for (int i = 0; i < neighboursList.getLength(); i++) {
					Element element = (Element) neighboursList.item(i);

					String localPortId = element.getElementsByTagName(LOCAL_PORT_ID_TAG).item(0).getTextContent();
					String remoteDeviceId = element.getElementsByTagName(REMOTE_CHASSIS_ID_TAG).item(0).getTextContent();

					Port port = new Port();
					port.setDeviceId(remoteDeviceId);
					connectionMap.put(localPortId, port);

				}

				rpcResponse = new Neighbours();
				rpcResponse.setDevicePortMap(connectionMap);

			} catch (Exception e) {
				log.error("Error parsing netconf message: ", e);
				throw new ActionException(e);
			}
		}

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		log.debug("Ignoring params in " + this.actionID + " action");

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {

	}

}
