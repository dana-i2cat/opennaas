/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.vnmapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class VNTRequest
{

	private ArrayList<ArrayList<VLink>>	connections;
	private ArrayList<VNode>			vnodes;
	private ArrayList<VLink>			vlinks;

	private int							mappingCost;
	private int							vnodeNum;

	Log									log	= LogFactory.getLog(VNTRequest.class);

	public VNTRequest() {
		connections = new ArrayList<ArrayList<VLink>>();
		vnodes = new ArrayList<VNode>();
		vlinks = new ArrayList<VLink>();
	}

	public ArrayList<ArrayList<VLink>> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<ArrayList<VLink>> connections) {
		this.connections = connections;
	}

	public ArrayList<VNode> getVnodes() {
		return vnodes;
	}

	public void setVnodes(ArrayList<VNode> vnodes) {
		this.vnodes = vnodes;
	}

	public ArrayList<VLink> getVlinks() {
		return vlinks;
	}

	public void setVlinks(ArrayList<VLink> vlinks) {
		this.vlinks = vlinks;
	}

	public int getMappingCost() {
		return mappingCost;
	}

	public void setMappingCost(int mappingCost) {
		this.mappingCost = mappingCost;
	}

	public int getVnodeNum() {
		return vnodeNum;
	}

	public void setVnodeNum(int vnodeNum) {
		this.vnodeNum = vnodeNum;
	}

	// / get the adjacent vnodes to a vnode
	public ArrayList getAdjacentVNodes(int nodeId)
	{
		ArrayList res = new ArrayList();
		for (int i = 0; i < (int) this.vlinks.size(); i++)
		{
			if (vlinks.get(i).getNode1Id() == nodeId)
			{
				res.add(vlinks.get(i).getNode2Id());
			}
			if (vlinks.get(i).getNode2Id() == nodeId)
			{
				res.add(vlinks.get(i).getNode1Id());
			}
		}
		return res;
	}

	public VNTRequest readVNTRequestFromXMLFile(String fileName) throws ParserConfigurationException, SAXException, IOException
	{

		log.info("Reading VNT Request from file : " + fileName);

		VNTRequest res = new VNTRequest();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		File file = new File(fileName);
		if (file.exists()) {
			Document doc = db.parse(file);
			Element element = doc.getDocumentElement();

			NodeList theVNodes = element.getElementsByTagName("VNode");

			if (theVNodes != null && theVNodes.getLength() > 0) {
				for (int i = 0; i < theVNodes.getLength(); i++) {

					Node node = theVNodes.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {

						VNode n = new VNode();
						Element e = (Element) node;
						NodeList nodeList = e.getElementsByTagName("id");
						n.setId(Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
						nodeList = e.getElementsByTagName("pnodeID");
						n.setPnodeID(nodeList.item(0).getChildNodes().item(0).getNodeValue());
						n.setCapacity(1);
						res.vnodes.add(n);
						res.vnodeNum++;

					}
				}
			}

			for (int i = 0; i < res.vnodeNum; i++) {
				res.connections.add(new ArrayList<VLink>());
				for (int j = 0; j < res.vnodeNum; j++) {
					res.connections.get(i).add(new VLink());
				}
			}

			NodeList theVLinks = element.getElementsByTagName("VLink");

			if (theVLinks != null && theVLinks.getLength() > 0) {
				for (int i = 0; i < theVLinks.getLength(); i++) {

					Node link = theVLinks.item(i);

					if (link.getNodeType() == Node.ELEMENT_NODE) {

						Element e = (Element) link;

						NodeList nodeList = e.getElementsByTagName("node1");
						int node1 = Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
						nodeList = e.getElementsByTagName("node2");
						int node2 = Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());

						res.connections.get(node1).get(node2).setId(1);

						res.connections.get(node1).get(node2).setNode1Id(node1);

						res.connections.get(node1).get(node2).setNode2Id(node2);

						// nodeList = e.getElementsByTagName("capacity");
						// res.connections.get(node1).get(node2).capacity=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
						res.connections.get(node1).get(node2).setCapacity(1);
						// nodeList = e.getElementsByTagName("delay");
						// res.connections.get(node1).get(node2).delay=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());;
						res.connections.get(node1).get(node2).setDelay(10000);

						res.vlinks.add(res.connections.get(node1).get(node2));

					}
				}
			}

		}

		log.info("VNT Request read : \n" + res.toString());

		return res;
	}

	@Override
	public String toString()
	{

		String vntString = "";
		vntString += "Required Virtual Nodes:\n";

		// System.out.println(vnodeNum);
		for (int i = 0; i < vnodeNum; i++)
		{
			vntString += "node " + vnodes.get(i).getId() + "\n";
			// System.out.println("node " + vnodes.get(i).id + "--" + vnodes.get(i).pnodeID );

		}
		// System.out.println("Required Virtual links" + vlinks.size());
		vntString += "Required Virtual links\n";
		for (int i = 0; i < vlinks.size(); i++)
		{
			vntString += "link : " + vlinks.get(i).getNode1Id() + "--" + vlinks.get(i).getNode2Id() + "\n";
		}

		return vntString;
	}
}
