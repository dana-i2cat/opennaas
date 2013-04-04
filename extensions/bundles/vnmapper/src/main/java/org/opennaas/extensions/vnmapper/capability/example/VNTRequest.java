/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.vnmapper.capability.example;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class VLink
{
	public int	id;
	public int	node1Id;
	public int	node2Id;
	public int	capacity;
	public int	delay;

	public VLink()
	{
		id = -1;
	}
}

class VNode
{
	public int		id;
	public int		capacity;
	public String	pnodeID	= "";
}

class VNLifetime
{
	public int				vnID;
	public int				arrivalTime;
	public int				releasingTime;
	public int				providerID;
	public MappingResult	mres				= new MappingResult();
	public int				waitingForReleasing	= -1;

	public VNLifetime(int id, int arrival, int life)
	{
		vnID = id;
		arrivalTime = arrival;
		releasingTime = life;
		waitingForReleasing = -1;
	}
}

public class VNTRequest
{
	public ArrayList<ArrayList<VLink>>	connections	= new ArrayList<ArrayList<VLink>>();
	public ArrayList<VNode>				vnodes		= new ArrayList<VNode>();
	public ArrayList<VLink>				vlinks		= new ArrayList<VLink>();

	public int							mappingCost;

	public int							vnodeNum;

	// / get the adjacent vnodes to a vnode
	public ArrayList getAdjacentVNodes(int nodeId)
	{
		ArrayList res = new ArrayList();
		for (int i = 0; i < (int) this.vlinks.size(); i++)
		{
			if (vlinks.get(i).node1Id == nodeId)
			{
				res.add(vlinks.get(i).node2Id);
			}
			if (vlinks.get(i).node2Id == nodeId)
			{
				res.add(vlinks.get(i).node1Id);
			}
		}
		return res;
	}

	public VNTRequest readVNTRequestFromXMLFile(String fileName)
	{
		VNTRequest res = new VNTRequest();

		try {

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
							n.id = Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
							nodeList = e.getElementsByTagName("pnodeID");
							n.pnodeID = nodeList.item(0).getChildNodes().item(0).getNodeValue();
							n.capacity = 1;
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

							res.connections.get(node1).get(node2).id = 1;

							res.connections.get(node1).get(node2).node1Id = node1;

							res.connections.get(node1).get(node2).node2Id = node2;

							// nodeList = e.getElementsByTagName("capacity");
							// res.connections.get(node1).get(node2).capacity=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
							res.connections.get(node1).get(node2).capacity = 1;
							// nodeList = e.getElementsByTagName("delay");
							// res.connections.get(node1).get(node2).delay=Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());;
							res.connections.get(node1).get(node2).delay = 10000;

							res.vlinks.add(res.connections.get(node1).get(node2));

						}
					}
				}

			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return res;
	}

	@Override
	public String toString()
	{

		String vntString = "";
		vntString += "Required Virtual Nodes:\n";

		// LOG System.out.println("Required Virtual Nodes:");
		// System.out.println(vnodeNum);
		for (int i = 0; i < vnodeNum; i++)
		{
			vntString += "node " + vnodes.get(i).id + "\n";
			// System.out.println("node " + vnodes.get(i).id + "--" + vnodes.get(i).pnodeID );
			// LOG System.out.println("node " + vnodes.get(i).id);

		}
		// System.out.println("Required Virtual links" + vlinks.size());
		vntString += "Required Virtual links\n";
		// LOG System.out.println("Required Virtual links");
		for (int i = 0; i < vlinks.size(); i++)
		{
			vntString += "link : " + vlinks.get(i).node1Id + "--" + vlinks.get(i).node2Id + "\n";
			// System.out.println("link : " + vlinks.get(i).node1Id + "--" + vlinks.get(i).node2Id + " : " + vlinks.get(i).capacity );
			// LOG System.out.println("link : " + vlinks.get(i).node1Id + "--" + vlinks.get(i).node2Id);
		}

		return vntString;
	}
}
