package net.i2cat.mantychore.model.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OpticalSwitchCardProfileLoader {

	Log						logger							= LogFactory.getLog(OpticalSwitchCardProfileLoader.class);

	Hashtable<String, Node>	cardsHashTable					= new Hashtable<String, Node>();

	private final String	DEFAULT_CARD_PROFILE_FILEPATH	= "/proteusCards.xml";

	// private final String DEFAULT_CARD_PROFILE_FILEPATH = System.getProperty("user.dir") + "\\proteusCards.xml";

	// NodeList listOfCards;

	public OpticalSwitchCardProfileLoader() throws IOException {

		InputStream is = getClass().getResourceAsStream(DEFAULT_CARD_PROFILE_FILEPATH);
		// logger.info(convertStreamToString(is));
		load(is);

		// load(DEFAULT_CARD_PROFILE_FILEPATH);
	}

	public OpticalSwitchCardProfileLoader(String configFile) throws IOException {
		load(configFile);
	}

	public void load(String configFile) throws IOException {

		FileInputStream s = new FileInputStream(new File(configFile));
		load(s);
	}

	private void load(InputStream configFileContent) throws IOException {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(configFileContent);

			doc.getDocumentElement().normalize();

			NodeList listOfCards = doc.getElementsByTagName("card");
			int totalCards = listOfCards.getLength();

			for (int s = 0; s < totalCards; s++) {
				Node cardNode = listOfCards.item(s);
				if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
					String type = cardNode.getAttributes().getNamedItem("type").getTextContent();

					String subtypes = ((cardNode.getAttributes().getNamedItem("subtype") != null) ? cardNode.getAttributes().getNamedItem("subtype")
							.getTextContent() : "");
					if (subtypes.length() > 0) {
						String[] dataSubtype = subtypes.split(",");
						for (int t = 0; t < dataSubtype.length; t++) {
							String subtypevalue = dataSubtype[t];
							this.cardsHashTable.put(type + subtypevalue, cardNode);
						}
					} else {
						this.cardsHashTable.put(type + "0", cardNode);
					}
				}
			}
		} catch (SAXException e) {
			throw new IOException("Error parsing card profiles file:", e);
		} catch (ParserConfigurationException e) {
			throw new IOException("Error parsing card profiles file:", e);
		}

	}

	public String getValue(int type, int subtype, String Param) {
		return getValue(String.valueOf(type), String.valueOf(subtype), Param, "");
	}

	public String getValue(String type, String subtype, String Param) {
		return getValue(String.valueOf(type), String.valueOf(subtype), Param, "");
	}

	public String getValue(int type, int subtype, String Param, String DefaultValue) {
		return getValue(String.valueOf(type), String.valueOf(subtype), Param, DefaultValue);
	}

	public String getValue(String type, String subtype, String Param, String DefaultValue) {
		String value = DefaultValue;
		Node cardNode = (Node) cardsHashTable.get(type + subtype);

		if (cardNode != null) {
			NodeList cardData = cardNode.getChildNodes();

			for (int i = 0; i < cardData.getLength(); i++) {
				Node nodeData = cardData.item(i);
				if (nodeData.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeData.getNodeName().trim().equals(Param)) {
						value = nodeData.getTextContent().trim();
						break;
					}
				}
			}
		}

		return value;
	}

	public String[] getParameters(int type, int subtype, String Param, String name) {
		return getParameters(String.valueOf(type), String.valueOf(subtype), Param, name);
	}

	public String[] getParameters(String type, String subtype, String Param, String name) {
		String[] values = null;
		Vector<String> vector = new Vector<String>();

		Node cardNode = (Node) cardsHashTable.get(type + subtype);

		if (cardNode != null) {
			NodeList cardData = cardNode.getChildNodes();
			for (int i = 0; i < cardData.getLength(); i++) {
				Node nodeData = cardData.item(i);
				if (nodeData.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeData.getNodeName().trim().equals(Param)) {
						NodeList paramsData = nodeData.getChildNodes();

						for (int j = 0; j < paramsData.getLength(); j++) {
							Node paramData = paramsData.item(j);
							if (paramData.getNodeType() == Node.ELEMENT_NODE) {
								if (paramData.getAttributes().getNamedItem("name").getTextContent().equals(name)) {
									vector.add(paramData.getTextContent().trim());
								}
							}
						}
					}
				}
			}
		}

		values = new String[vector.size()];
		int count = 0;
		for (int i = 0; i < vector.size(); i++) {
			values[count] = (String) vector.get(count);
			count += 1;
		}

		return values;
	}

	public String[] getPortsParameter(int type, int subtype, String paramName) {
		return getPortsParameter(String.valueOf(type), String.valueOf(subtype), paramName);
	}

	/**
	 *
	 * @param type
	 * @param subtype
	 * @param Param
	 * @param name
	 * @return
	 */
	public String[] getPortsParameter(String type, String subtype, String paramName) {

		Vector<String> vector = new Vector<String>();

		NodeList ports = getPorts(type, subtype);
		if (ports != null) {
			for (int j = 0; j < ports.getLength(); j++) {
				Node port = ports.item(j);
				if (port.getNodeType() == Node.ELEMENT_NODE) {
					if (port.getNodeName().trim().equals("port")) {
						// get desired param
						boolean added = false;
						NodeList portParams = port.getChildNodes();
						for (int k = 0; k < portParams.getLength(); k++) {
							Node paramData = portParams.item(k);
							if (paramData.getNodeType() == Node.ELEMENT_NODE) {
								if (paramData.getAttributes() != null) {
									if (paramData.getAttributes().getNamedItem("name") != null) {
										if (paramData.getAttributes().getNamedItem("name").getTextContent().equals(paramName)) {
											vector.add(paramData.getTextContent().trim());
											added = true;
										}
									}
								}
							}
						}
						// if param is not found add empty string
						if (!added) {
							vector.add("");
						}
					}
				}
			}
		}
		String[] values = new String[vector.size()];
		return vector.toArray(values);
	}

	public List<String> getPortInternalConnections(int type, int subtype, String portNum) {
		return getPortInternalConnections(String.valueOf(type), String.valueOf(subtype), portNum);
	}

	private List<String> getPortInternalConnections(String type, String subtype, String portNum) {

		Vector<String> vector = new Vector<String>();
		NodeList internalConnections = null;

		Node port = getPort(type, subtype, portNum);
		if (port != null) {
			NodeList portChilds = port.getChildNodes();
			for (int i = 0; i < portChilds.getLength(); i++) {
				Node child = portChilds.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if (child.getNodeName().trim().equals("internalConnections")) {
						internalConnections = child.getChildNodes();
						break;
					}
				}
			}
			if (internalConnections != null) {
				Node connection;
				for (int i = 0; i < internalConnections.getLength(); i++) {
					connection = internalConnections.item(i);
					if (connection.getNodeType() == Node.ELEMENT_NODE) {
						vector.add(connection.getTextContent().trim());
					}
				}
			}
		}
		return vector;
	}

	private NodeList getPorts(String type, String subtype) {

		Node cardNode = (Node) cardsHashTable.get(type + subtype);

		if (cardNode != null) {
			// get ports Node
			Node portsNode = null;
			NodeList cardData = cardNode.getChildNodes();
			for (int i = 0; i < cardData.getLength(); i++) {

				Node cardChild = cardData.item(i);
				if (cardChild.getNodeType() == Node.ELEMENT_NODE) {
					if (cardChild.getNodeName().trim().equals("ports")) {
						portsNode = cardChild;
						break;
					}
				}
			}
			if (portsNode != null)
				return portsNode.getChildNodes();
		}
		return null;
	}

	private Node getPort(String type, String subtype, String portNum) {
		NodeList ports = getPorts(type, subtype);

		if (ports != null) {
			for (int j = 0; j < ports.getLength(); j++) {
				Node port = ports.item(j);
				if (port.getNodeType() == Node.ELEMENT_NODE) {
					if (port.getNodeName().trim().equals("port")) {
						// check portNum
						NodeList portParams = port.getChildNodes();
						for (int k = 0; k < portParams.getLength(); k++) {
							Node paramData = portParams.item(k);
							if (paramData.getNodeType() == Node.ELEMENT_NODE) {
								if (paramData.getAttributes() != null) {
									if (paramData.getAttributes().getNamedItem("name") != null) {
										if (paramData.getAttributes().getNamedItem("name").getTextContent().equals("portNumber")) {
											if (paramData.getTextContent().trim().equals(portNum))
												return port;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	public boolean existsCardOfType(int type) {
		return existsCardOfType(String.valueOf(type));
	}

	public boolean existsCardOfType(String type) {
		return cardsHashTable.containsKey(type);
	}

	public String convertStreamToString(InputStream is)
			throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

}
