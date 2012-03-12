package net.i2cat.mantychore.actionsets.junos.actions.logicalrouters;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.JunosAction;
import net.i2cat.mantychore.commandsets.junos.commands.CommandNetconfConstants;
import net.i2cat.mantychore.commandsets.junos.commands.CommandNetconfConstants.TargetConfiguration;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.commands.GetNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.commands.JunosCommand;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.NetworkPort;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AddInterfaceToLogicalRouterAction extends JunosAction {

	private String	getSubInterfaceTemplate		= "/VM_files/getSubInterface.vm";
	private String	copyInterfaceToLRTemplate	= "/VM_files/mergeRawXML.vm";
	private String	deleteSubInterfaceTemplate	= "/VM_files/deletesubinterface.vm";

	public AddInterfaceToLogicalRouterAction() {
		this.setActionID(ActionConstants.ADDINTERFACETOLOGICALROUTER);
		this.protocolName = "netconf";
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (!(params instanceof ComputerSystem))
			return false;

		if (((ComputerSystem) params).getElementName() == null ||
				((ComputerSystem) params).getElementName().equals("")) {
			return false;
		}

		try {
			LogicalDevice interfaceToAdd = getOnlyElement(((ComputerSystem) params).getLogicalDevices(), null);
			return (interfaceToAdd instanceof NetworkPort);
		} catch (IllegalArgumentException e) {
			// There is more than one LogicalDevice in ComputerSystem params
			return false;
		}
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		try {

			// get interface
			JunosCommand getCommand = new GetNetconfCommand(prepareGetInterfaceMessage((ComputerSystem) params), TargetConfiguration.CANDIDATE);
			getCommand.initialize();
			Response getInterfaceResponse = sendCommandToProtocol(getCommand, protocol);
			actionResponse.addResponse(getInterfaceResponse);

			if (getInterfaceResponse.getStatus().equals(Response.Status.OK)) {

				// copy it to LR
				String interfaceConfiguration = filterInterfaceConfiguration(getInterfaceResponse);

				JunosCommand editCommand = new EditNetconfCommand(prepareCopyInterfaceToLRMessage((ComputerSystem) params, interfaceConfiguration));
				editCommand.initialize();
				Response copyInterfaceResponse = sendCommandToProtocol(editCommand, protocol);
				actionResponse.addResponse(copyInterfaceResponse);

				if (copyInterfaceResponse.getStatus().equals(Response.Status.OK)) {

					// remove it from parent
					JunosCommand deleteCommand = new EditNetconfCommand(prepareDeleteInterfaceMessage((ComputerSystem) params),
							CommandNetconfConstants.NONE_OPERATION);
					deleteCommand.initialize();
					actionResponse.addResponse(sendCommandToProtocol(deleteCommand, protocol));
				}
			}

			validateAction(actionResponse);

		} catch (ProtocolException e) {
			throw new ActionException(this.actionID, e);
		}
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// Nothing to parse
	}

	@Override
	public void prepareMessage() throws ActionException {
		// Nothing to prepare
		// Messages are prepared in executeListCommand
	}

	private String prepareGetInterfaceMessage(ComputerSystem model) throws ActionException {

		NetworkPort iface = (NetworkPort) getOnlyElement(((ComputerSystem) params).getLogicalDevices(), null);

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(((ComputerSystem) getModelToUpdate()).getElementName()));

		try {
			return prepareVelocityCommand(iface, getSubInterfaceTemplate, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private String prepareCopyInterfaceToLRMessage(ComputerSystem model, String interfaceConfiguration) throws ActionException {

		NetworkPort iface = (NetworkPort) getOnlyElement(((ComputerSystem) params).getLogicalDevices(), null);

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(model.getElementName()));
		extraParams.put("xmlToMerge", interfaceConfiguration);

		try {
			return prepareVelocityCommand(iface, copyInterfaceToLRTemplate, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private String prepareDeleteInterfaceMessage(ComputerSystem model) throws ActionException {

		NetworkPort iface = (NetworkPort) getOnlyElement(((ComputerSystem) params).getLogicalDevices(), null);

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(((ComputerSystem) getModelToUpdate()).getElementName()));

		try {
			return prepareVelocityCommand(iface, deleteSubInterfaceTemplate, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private String filterInterfaceConfiguration(Response getInterfaceResponse) throws ActionException {
		try {

			Document completeResponseDoc = stringToDOM(getInterfaceResponse.getInformation());

			Element interfaces = getInterfacesElement(completeResponseDoc);
			if (interfaces == null)
				throw new ActionException("Failed to get interfaces from configuration");

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();
			Document filteredDocument = db.newDocument();

			Node importedInterfaces = filteredDocument.importNode(interfaces, true);
			filteredDocument.appendChild(importedInterfaces);

			return domToString(filteredDocument);

		} catch (Exception e) {
			throw new ActionException("Failed to filter configuration", e);
		}
	}

	/**
	 * Gets configuration/interfaces element from given doc.
	 * 
	 * @param doc
	 *            containing configuration
	 * @return configuration/interfaces Element or null if it does not exist.
	 */
	private Element getInterfacesElement(Document doc) {
		Element configElement = doc.getDocumentElement();
		assert (configElement.getNodeName().equals("configuration"));

		NodeList configDirectChilds = configElement.getChildNodes();
		if (configDirectChilds.getLength() == 0) {
			return null;
		}

		for (int i = 0; i < configDirectChilds.getLength(); i++) {
			if (configDirectChilds.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (((Element) configDirectChilds.item(i)).getNodeName().equals("interfaces")) {
					return (Element) configDirectChilds.item(i);
				}
			}
		}
		return null;
	}

	private String domToString(Document doc) throws TransformerException, IOException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty("omit-xml-declaration", "yes");
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		Source source = new DOMSource(doc);
		transformer.transform(source, result);
		writer.close();
		return writer.toString();
	}

	private Document stringToDOM(String xmlString) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = factory.newDocumentBuilder();
		InputSource inStream = new InputSource();
		inStream.setCharacterStream(new StringReader(xmlString));
		return db.parse(inStream);
	}
}
