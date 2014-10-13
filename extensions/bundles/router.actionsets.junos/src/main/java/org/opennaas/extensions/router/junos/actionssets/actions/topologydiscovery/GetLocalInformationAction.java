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
import org.opennaas.extensions.router.capability.topologydiscovery.model.LocalInformation;
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
public class GetLocalInformationAction extends JunosAction {

	private static final String	PROTOCOL_NAME			= "netconf";

	private static final String	LOCAL_ID_PATH			= "//lldp-local-info/lldp-local-chassis-id";
	private static final String	LOCAL_IFACE_INFO_PATH	= "//lldp-local-info/lldp-local-interface-info";

	private static final String	LOCAL_IFACE_NAME_TAG	= "lldp-local-interface-name";
	private static final String	LOCAL_IFACE_ID_TAG		= "lldp-local-interface-id";

	private static final String	REQUEST_XML				= "<get-lldp-local-info />";

	private Log					log						= LogFactory.getLog(GetLocalInformationAction.class);
	private LocalInformation	rpcResponse;

	public GetLocalInformationAction() {
		setActionID(ActionConstants.TOPOLOGY_DISCOVERY_GET_LOCAL_INFORMATION);
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

			GenericJunosCommand command = new GenericJunosCommand("get-lldp-local-info", REQUEST_XML);
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

				XPath xPath = XPathFactory.newInstance().newXPath();
				Document xmlDocument = builder.parse(new ByteArrayInputStream(message.getBytes()));

				String deviceId = (String) xPath.compile(LOCAL_ID_PATH).evaluate(xmlDocument, XPathConstants.STRING);
				NodeList interfaces = (NodeList) xPath.compile(LOCAL_IFACE_INFO_PATH).evaluate(xmlDocument, XPathConstants.NODESET);

				Map<String, String> ifacesMap = new HashMap<String, String>();

				for (int i = 0; i < interfaces.getLength(); i++) {

					Element element = (Element) interfaces.item(i);
					String ifaceName = element.getElementsByTagName(LOCAL_IFACE_NAME_TAG).item(0).getTextContent();
					String portId = element.getElementsByTagName(LOCAL_IFACE_ID_TAG).item(0).getTextContent();

					ifacesMap.put(portId, ifaceName);

				}

				rpcResponse = new LocalInformation();
				rpcResponse.setDeviceId(deviceId);
				rpcResponse.setInterfacesMap(ifacesMap);

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
