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

public class RemoveInterfaceFromLogicalRouterAction extends JunosAction {

	private String	getSubInterfaceTemplate		= "/VM_files/getSubInterface.vm";
	private String	copyInterfaceToLRTemplate	= "/VM_files/mergeRawXML.vm";
	private String	deleteSubInterfaceTemplate	= "/VM_files/deletesubinterface.vm";

	public RemoveInterfaceFromLogicalRouterAction() {
		this.setActionID(ActionConstants.REMOVEINTERFACEFROMLOGICALROUTER);
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

			// get interface from candidate
			JunosCommand getCommand = new GetNetconfCommand(prepareGetInterfaceMessage((ComputerSystem) params), TargetConfiguration.CANDIDATE);
			getCommand.initialize();
			Response getInterfaceResponse = sendCommandToProtocol(getCommand, protocol);
			actionResponse.addResponse(getInterfaceResponse);

			if (getInterfaceResponse.getStatus().equals(Response.Status.OK)) {

				checkInterfaceExists(ifaceToAddName, logicalRouterName, getInterfaceResponse);

				// copy it to physical router
				String interfaceConfiguration = filterInterfaceConfiguration(getInterfaceResponse, ((ComputerSystem) params).getElementName());

				JunosCommand editCommand = new EditNetconfCommand(prepareCopyInterfacePhysicalRouterMessage((ComputerSystem) params,
						interfaceConfiguration));
				editCommand.initialize();
				Response copyInterfaceResponse = sendCommandToProtocol(editCommand, protocol);
				actionResponse.addResponse(copyInterfaceResponse);

				if (copyInterfaceResponse.getStatus().equals(Response.Status.OK)) {

					// remove it from LR
					JunosCommand deleteCommand = new EditNetconfCommand(prepareDeleteInterfaceFromLRMessage((ComputerSystem) params),
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

	private void checkInterfaceExists(String ifaceName, String logicalRouterName, Response getInterfaceResponse) throws ActionException {
		if (getInterfaceResponse.getInformation().equals("<configuration></configuration>")) {
			// an empty configuration means filter has failed
			throw new ActionException("Interface " + ifaceName + " not found in router " + logicalRouterName);
		}
	}

	private String prepareGetInterfaceMessage(ComputerSystem model) throws ActionException {

		NetworkPort iface = (NetworkPort) getOnlyElement(((ComputerSystem) params).getLogicalDevices(), null);

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(model.getElementName()));

		try {
			return prepareVelocityCommand(iface, getSubInterfaceTemplate, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private String prepareCopyInterfacePhysicalRouterMessage(ComputerSystem model, String interfaceConfiguration) throws ActionException {

		NetworkPort iface = (NetworkPort) getOnlyElement(((ComputerSystem) params).getLogicalDevices(), null);

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(((ComputerSystem) getModelToUpdate()).getElementName()));
		extraParams.put("xmlToMerge", interfaceConfiguration);

		try {
			return prepareVelocityCommand(iface, copyInterfaceToLRTemplate, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private String prepareDeleteInterfaceFromLRMessage(ComputerSystem model) throws ActionException {

		NetworkPort iface = (NetworkPort) getOnlyElement(((ComputerSystem) params).getLogicalDevices(), null);

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(model.getElementName()));

		try {
			return prepareVelocityCommand(iface, deleteSubInterfaceTemplate, extraParams);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private String filterInterfaceConfiguration(Response getInterfaceResponse, String logicalRouterName) throws ActionException {
		try {

			Document completeResponseDoc = stringToDOM(getInterfaceResponse.getInformation());

			Element interfaces = getInterfacesElement(completeResponseDoc, logicalRouterName);
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
	 * Gets /configuration/logical-systems/name[text()="elementName"]/../interfaces element from given doc.
	 * 
	 * @param doc
	 *            containing configuration
	 * @return configuration/logical-systems/name[text()="elementName"]/../interfaces Element or null if it does not exist.
	 * @throws XPathExpressionException
	 */
	private Element getInterfacesElement(Document doc, String logicalRouterName) throws XPathExpressionException {
		Element configElement = doc.getDocumentElement();
		assert (configElement.getNodeName().equals("configuration"));

		XPath xpath = XPathFactory.newInstance().newXPath();
		String xpathExpression = "/configuration/logical-systems/name[text()=\"" + logicalRouterName + "\"]/../interfaces";
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
