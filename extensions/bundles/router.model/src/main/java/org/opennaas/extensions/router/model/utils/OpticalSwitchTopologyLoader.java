package org.opennaas.extensions.router.model.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OpticalSwitchTopologyLoader {

	Log						logger						= LogFactory.getLog(OpticalSwitchTopologyLoader.class);

	Hashtable<String, Node>	portsHashTable				= new Hashtable<String, Node>();
	List<String[]>			connections					= new ArrayList<String[]>();

	private final String	DEFAULT_TOPOLOGY_FILEPATH	= "/proteusTopology.xml";

	public OpticalSwitchTopologyLoader() throws IOException {
		InputStream is = getClass().getResourceAsStream(DEFAULT_TOPOLOGY_FILEPATH);
		load(is);
	}

	public OpticalSwitchTopologyLoader(String configFile) throws IOException {
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

			// load ports
			NodeList listOfPorts = doc.getElementsByTagName("port");
			for (int s = 0; s < listOfPorts.getLength(); s++) {
				Node portNode = listOfPorts.item(s);
				if (portNode.getNodeType() == Node.ELEMENT_NODE) {
					String portId = portNode.getAttributes().getNamedItem("id").getTextContent();
					this.portsHashTable.put(portId, portNode);
				}
			}

			// load connections
			NodeList listOfConnections = doc.getElementsByTagName("connection");
			for (int s = 0; s < listOfConnections.getLength(); s++) {
				Node connectionNode = listOfConnections.item(s);
				if (connectionNode.getNodeType() == Node.ELEMENT_NODE) {
					String srcPortId = connectionNode.getAttributes().getNamedItem("srcPort").getTextContent();
					String dstPortId = connectionNode.getAttributes().getNamedItem("dstPort").getTextContent();
					connections.add(new String[] { srcPortId, dstPortId });
				}
			}

		} catch (SAXException e) {
			throw new IOException("Error parsing card profiles file:", e);
		} catch (ParserConfigurationException e) {
			throw new IOException("Error parsing card profiles file:", e);
		}

	}

	public List<String[]> getConnections(String nodeName) {
		List<String[]> result = new ArrayList<String[]>();
		for (String[] connection : connections) {
			if (nodeName.equals(getPortNodeName(connection[0]))) {
				result.add(connection);
			}
		}
		return result;
	}

	public String getPortNodeName(String srcPortID) {
		return getPortNodeName(portsHashTable.get(srcPortID));
	}

	private String getPortNodeName(Node port) {
		if (port != null) {
			// port -> ports -> slot -> chassis -> node
			Node portNode = port.getParentNode().getParentNode().getParentNode().getParentNode();
			if (portNode.getNodeType() == Node.ELEMENT_NODE) {
				return portNode.getAttributes().getNamedItem("id").getTextContent();
			}
		}
		return "";
	}

	public int getPortChassis(String srcPortID) {
		// port -> ports -> slot -> chassis
		Node portChasis = portsHashTable.get(srcPortID).getParentNode().getParentNode().getParentNode();
		if (portChasis.getNodeType() == Node.ELEMENT_NODE) {
			return Integer.parseInt(portChasis.getAttributes().getNamedItem("number").getTextContent());
		}
		return -1;
	}

	public int getPortSlot(String srcPortID) {
		// port -> ports -> slot
		Node portSlot = portsHashTable.get(srcPortID).getParentNode().getParentNode();
		if (portSlot.getNodeType() == Node.ELEMENT_NODE) {
			return Integer.parseInt(portSlot.getAttributes().getNamedItem("number").getTextContent());
		}
		return -1;
	}

	public int getPortNumber(String srcPortID) {
		Node port = portsHashTable.get(srcPortID);
		if (port.getNodeType() == Node.ELEMENT_NODE) {
			return Integer.parseInt(port.getTextContent());
		}
		return -1;
	}

	public String convertStreamToString(InputStream is) throws IOException {
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
