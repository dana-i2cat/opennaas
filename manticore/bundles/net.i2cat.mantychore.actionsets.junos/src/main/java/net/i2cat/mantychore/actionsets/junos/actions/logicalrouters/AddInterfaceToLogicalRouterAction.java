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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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

		NetworkPort ifaceToAdd = (NetworkPort) getOnlyElement(((ComputerSystem) params).getLogicalDevices(), null);
		String ifaceToAddName = ifaceToAdd.getName() + "." + ifaceToAdd.getPortNumber();
		String logicalRouterName = nullToEmpty(((ComputerSystem) params).getElementName());

		try {

			// TODO Check LR is in opennaas resource manager and it is not active

			Response getInterfaceResponse = getInterfaceFromCandidate(ifaceToAdd, protocol);
			actionResponse.addResponse(getInterfaceResponse);

			if (getInterfaceResponse.getStatus().equals(Response.Status.OK)) {

				checkInterfaceExists(ifaceToAddName, getInterfaceResponse);

				Response copyInterfaceToLRResponse = copyInterfaceToLR(ifaceToAdd, getInterfaceResponse, logicalRouterName, protocol);
				actionResponse.addResponse(copyInterfaceToLRResponse);

				if (copyInterfaceToLRResponse.getStatus().equals(Response.Status.OK)) {

					// remove it from parent
					Response deleteInterfaceResponse = deleteInterface(ifaceToAdd, protocol);
					actionResponse.addResponse(deleteInterfaceResponse);
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

	private void checkInterfaceExists(String ifaceName, Response getInterfaceResponse) throws ActionException {

		if (getInterfaceResponse.getInformation().equals("<configuration></configuration>")) {
			// an empty configuration means filter has failed
			throw new ActionException("Interface " + ifaceName + " not found in this router");
		}
	}

	private Response getInterfaceFromCandidate(NetworkPort iface, IProtocolSession protocol) throws ActionException, ProtocolException {

		String getInterfaceFilter = prepareGetInterfaceMessage(iface);

		JunosCommand getCommand = new GetNetconfCommand(getInterfaceFilter, TargetConfiguration.CANDIDATE);
		getCommand.initialize();
		return sendCommandToProtocol(getCommand, protocol);
	}

	private Response copyInterfaceToLR(NetworkPort ifaceToAdd, Response getInterfaceResponse, String logicalRouterName, IProtocolSession protocol)
			throws ActionException, ProtocolException {

		String interfacesConfiguration = filterInterfaceConfiguration(getInterfaceResponse);

		JunosCommand editCommand = new EditNetconfCommand(
				prepareCopyInterfaceToLRMessage(interfacesConfiguration, logicalRouterName));
		editCommand.initialize();
		return sendCommandToProtocol(editCommand, protocol);
	}

	private Response deleteInterface(NetworkPort ifaceToAdd, IProtocolSession protocol) throws ActionException, ProtocolException {

		JunosCommand deleteCommand = new EditNetconfCommand(prepareDeleteInterfaceMessage(ifaceToAdd),
				CommandNetconfConstants.NONE_OPERATION);
		deleteCommand.initialize();
		return sendCommandToProtocol(deleteCommand, protocol);
	}

	private String prepareGetInterfaceMessage(NetworkPort ifaceToAdd) throws ActionException {

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(((ComputerSystem) getModelToUpdate()).getElementName()));

		try {
			return prepareVelocityCommand(ifaceToAdd, getSubInterfaceTemplate, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private String prepareCopyInterfaceToLRMessage(String interfaceConfiguration, String logicalRouterName) throws ActionException {

		NetworkPort iface = (NetworkPort) getOnlyElement(((ComputerSystem) params).getLogicalDevices(), null);

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", logicalRouterName);
		extraParams.put("xmlToMerge", interfaceConfiguration);

		try {
			return prepareVelocityCommand(iface, copyInterfaceToLRTemplate, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private String prepareDeleteInterfaceMessage(NetworkPort ifaceToDelete) throws ActionException {

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(((ComputerSystem) getModelToUpdate()).getElementName()));

		try {
			return prepareVelocityCommand(ifaceToDelete, deleteSubInterfaceTemplate, extraParams);
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
	 * @throws XPathExpressionException
	 */
	private Element getInterfacesElement(Document doc) throws XPathExpressionException {
		Element configElement = doc.getDocumentElement();
		assert (configElement.getNodeName().equals("configuration"));

		XPath xpath = XPathFactory.newInstance().newXPath();
		String xpathExpression = "/configuration/interfaces";
		Element interfaces = (Element) xpath.evaluate(xpathExpression, configElement, XPathConstants.NODE);
		return interfaces;
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
