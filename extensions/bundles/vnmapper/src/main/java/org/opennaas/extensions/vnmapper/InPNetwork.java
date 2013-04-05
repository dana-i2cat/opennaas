/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.vnmapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author ahammaa
 */

public class InPNetwork implements Serializable {

	private static final long			serialVersionUID	= -6318751318197152995L;

	private ArrayList<ArrayList<PLink>>	connections;
	private ArrayList<PNode>			nodes;
	private ArrayList<PLink>			links;

	private ArrayList<ArrayList<Paths>>	allPaths;
	private ArrayList<ArrayList>		locations;
	private int							nodeNum;

	public InPNetwork() {
		connections = new ArrayList<ArrayList<PLink>>();
		nodes = new ArrayList<PNode>();
		allPaths = new ArrayList<ArrayList<Paths>>();
		locations = new ArrayList<ArrayList>();
		links = new ArrayList<PLink>();
	}

	public ArrayList<ArrayList<PLink>> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<ArrayList<PLink>> connections) {
		this.connections = connections;
	}

	public ArrayList<PNode> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<PNode> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<PLink> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<PLink> links) {
		this.links = links;
	}

	public ArrayList<ArrayList<Paths>> getAllPaths() {
		return allPaths;
	}

	public void setAllPaths(ArrayList<ArrayList<Paths>> allPaths) {
		this.allPaths = allPaths;
	}

	public ArrayList<ArrayList> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<ArrayList> locations) {
		this.locations = locations;
	}

	public int getNodeNum() {
		return nodeNum;
	}

	public void setNodeNum(int nodeNum) {
		this.nodeNum = nodeNum;
	}

	@Override
	public String toString()
	{
		String netString = "";
		netString += "Physical Nodes:\n";
		// System.out.println(nodeNum);
		for (int i = 0; i < nodeNum; i++)
		{
			// System.out.println("node " + nodes.get(i).id + "--" + nodes.get(i).pnodeID+ "--" + nodes.get(i).capacity );
			netString += "node " + nodes.get(i).getId() + "--" + nodes.get(i).getPnodeID() + "\n";

		}
		// System.out.println("------------------------------------------------------------------------------------");
		// System.out.println("links" + links.size());
		netString += "Physical links:\n";
		for (int i = 0; i < links.size(); i++)
		{
			// System.out.println("link : " + links.get(i).node1Id + "--" + links.get(i).node2Id + " : " + links.get(i).capacity + " , " +
			// links.get(i).delay);
			netString += "link : " + links.get(i).getNode1Id() + "--" + links.get(i).getNode2Id() + "\n";
		}

		return netString;
	}

	// /// get the adjacent nodes to a node
	public ArrayList<Integer> getAdjacentNodes(int nodeId)
	{
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < (int) this.links.size(); i++)
		{
			if (links.get(i).getNode1Id() == nodeId)
			{
				res.add(links.get(i).getNode2Id());
			}
			if (links.get(i).getNode2Id() == nodeId)
			{
				res.add(links.get(i).getNode1Id());
			}
		}
		return res;
	}

	// // get the index of a physical link
	public int getLinkIndex(int node1, int node2)
	{
		int res = -1;
		int n1, n2;
		if (node1 < node2) {
			n1 = node1;
			n2 = node2;
		}
		else {
			n1 = node2;
			n2 = node1;
		}

		for (int i = 0; i < (int) this.links.size(); i++)
		{
			if ((this.links.get(i).getNode1Id() == n1) && (this.links.get(i).getNode2Id() == n2))
			{
				res = i;
			}
		}
		return res;
	}

	// / find the shortest path that satisfy the virtual link requirement
	public Path findPathBetweemTwoNodes(int src, int dst, int requiredCapacity, int requiredDelay, int maxLinkNum)
	{
		ArrayList<PathCell> passedNodes = new ArrayList<PathCell>();
		PathCell p = new PathCell();
		p.setNodeId(src);
		p.setPrev(new PathCell());
		p.getPrev().setNodeId(-1);
		Path res = new Path();
		res.setId(-1);
		passedNodes.add(p);
		int currentPassedNodeIndex = 0;
		int stop = 0;
		int n, n1, n2;
		int max = 100;
		int t = 0;
		while ((stop != 1) && (t < max))
		{
			t++;
			if (passedNodes.get(currentPassedNodeIndex).getLinkNum() < maxLinkNum)
			{
				ArrayList adjacentNodes = this.getAdjacentNodes(passedNodes.get(currentPassedNodeIndex).getNodeId());

				for (int i = 0; i < (int) adjacentNodes.size(); i++)
				{
					// / choosing next adjacent node to continue
					n = Integer.valueOf(adjacentNodes.get(i).toString());

					if ((n != passedNodes.get(currentPassedNodeIndex).getPrev().getNodeId()) && (n != src))
					{
						if (passedNodes.get(currentPassedNodeIndex).getNodeId() < n) {
							n1 = passedNodes.get(currentPassedNodeIndex).getNodeId();
							n2 = n;
						}

						else {
							n2 = passedNodes.get(currentPassedNodeIndex).getNodeId();
							n1 = n;
						}

						if ((this.connections.get(n1).get(n2).getCapacity() >= requiredCapacity) && (this.connections.get(n1).get(n2).getDelay() + passedNodes
								.get(currentPassedNodeIndex).getDelay() <= requiredDelay))
						{

							if (n == dst)
							{

								stop = 1;
								res.setId(1);
								res.setNode1Id(src);
								res.setNode2Id(dst);
								res.setCapacity(requiredCapacity);

								int delay = passedNodes.get(currentPassedNodeIndex).getDelay() + this.connections.get(n1).get(n2).getDelay();
								res.setDelay(delay);
								// // now construcing the resulted links
								int current;
								int prev;
								current = n;
								PathCell prevCell = new PathCell();
								prevCell = passedNodes.get(currentPassedNodeIndex);
								prev = passedNodes.get(currentPassedNodeIndex).getNodeId();

								for (int j = passedNodes.get(currentPassedNodeIndex).getLinkNum(); j >= 0; j--)
								{
									if (current < prev)
									{
										res.getLinks().add(this.connections.get(current).get(prev));
									}
									else
									{
										res.getLinks().add(this.connections.get(prev).get(current));
									}

									if (j != 0)
									{
										current = prev;
										prevCell = prevCell.getPrev();
										prev = prevCell.getNodeId();
									}
								}
							}
							else
							{
								if ((passedNodes.get(currentPassedNodeIndex).getLinkNum() + 1 < maxLinkNum) && (stop != 1))
								{
									// / now check if is passed before//
									PathCell c = new PathCell();
									c.setNodeId(n);
									int linkNum = passedNodes.get(currentPassedNodeIndex).getLinkNum() + 1;
									c.setLinkNum(linkNum);
									int delay = this.connections.get(n1).get(n2).getDelay() + passedNodes.get(currentPassedNodeIndex).getDelay();
									c.setDelay(delay);
									c.setPrev(passedNodes.get(currentPassedNodeIndex));
									passedNodes.add(c);
								}
							}
						}
					}
				}
			}
			currentPassedNodeIndex++;
			if (currentPassedNodeIndex == (int) passedNodes.size())
				stop = 1;
		}

		passedNodes.clear();
		return res;
	}

	// / find the path that satisfy the virtual link requirement considering load balancing
	public Path findPathBetweemTwoNodes2(int src, int dst, int requiredCapacity, int requiredDelay, int maxLinkNum)
	{
		Path res = new Path();
		res.setId(-1);
		ArrayList<PathCell2> cells = new ArrayList<PathCell2>();
		for (int i = 0; i < this.nodeNum; i++)
		{
			cells.add(new PathCell2());
		}
		ArrayList adjacentNodes = new ArrayList();
		int curNode = src;
		int n, n1, n2;
		cells.get(curNode).setPassed(1);
		cells.get(curNode).setLinkNum(0);
		int stop = 0;
		while ((curNode != dst) && (stop != 1))
		{
			adjacentNodes = this.getAdjacentNodes(curNode);
			cells.get(curNode).setPassed(1);
			for (int i = 0; i < (int) adjacentNodes.size(); i++)
			{
				n = Integer.valueOf(adjacentNodes.get(i).toString());

				if (curNode < n) {
					n1 = curNode;
					n2 = n;
				}

				else {
					n2 = curNode;
					n1 = n;
				}
				if ((cells.get(curNode).getLinkNum() + 1 < maxLinkNum) || (n == dst))
					if ((this.connections.get(n1).get(n2).getCapacity() >= requiredCapacity) && (this.connections.get(n1).get(n2).getDelay() + cells
							.get(curNode).getDelay() <= requiredDelay))
					{
						if ((cells.get(n).getPassed() != 1) && (cells.get(n).getRemaining() < this.connections.get(n1).get(n2).getCapacity() - requiredCapacity))
						{
							int remaining = this.connections.get(n1).get(n2).getCapacity() - requiredCapacity;
							cells.get(n).setRemaining(remaining);
							int delay = this.connections.get(n1).get(n2).getDelay() + cells.get(curNode).getDelay();
							cells.get(n).setDelay(delay);
							cells.get(n).setLinkNum(cells.get(curNode).getLinkNum() + 1);
							cells.get(n).setPrev(curNode);
						}
					}
			}
			// // get current node with the maximum remaining
			int max = -1;
			for (int i = 0; i < cells.size(); i++)
			{
				if ((cells.get(i).getRemaining() > max) && (cells.get(i).getPassed() == 0))
				{
					max = cells.get(i).getRemaining();
					curNode = i;
				}
			}
			if (max == -1)
				stop = 1;
		}
		// //////////
		if (stop != 1)
		{
			res.setNode1Id(src);
			res.setId(1);
			res.setNode2Id(dst);
			res.setCapacity(requiredCapacity);
			res.setDelay(cells.get(dst).getDelay());
			int current = dst;
			int prev = cells.get(dst).getPrev();
			for (int j = cells.get(dst).getLinkNum() - 1; j >= 0; j--)
			{
				if (current < prev)
					res.getLinks().add(this.connections.get(current).get(prev));
				else
					res.getLinks().add(this.connections.get(prev).get(current));

				if (j != 0)
				{
					current = prev;

					prev = cells.get(current).getPrev();
				}
			}
		}
		return res;
	}

	public InPNetwork readPNetworkFromXMLFile(String fileName) throws ParserConfigurationException, SAXException, IOException
	{
		InPNetwork res = new InPNetwork();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		File file = new File(fileName);
		if (file.exists()) {
			Document doc = db.parse(file);
			Element docEle = doc.getDocumentElement();

			NodeList theNodes = docEle.getElementsByTagName("Node");

			if (theNodes != null && theNodes.getLength() > 0) {
				for (int i = 0; i < theNodes.getLength(); i++) {

					Node node = theNodes.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {

						PNode n = new PNode();
						Element e = (Element) node;
						NodeList nodeList = e.getElementsByTagName("id");
						n.setId(Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue()));

						nodeList = e.getElementsByTagName("pnodeID");
						n.setPnodeID(nodeList.item(0).getChildNodes().item(0).getNodeValue());

						nodeList = e.getElementsByTagName("capacity");
						n.setCapacity(Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue()));

						n.setPathNum(0);
						res.nodes.add(n);
						res.nodeNum++;

					}
				}
			} else {
				System.exit(1);
			}

			for (int i = 0; i < res.nodeNum; i++) {
				res.connections.add(new ArrayList<PLink>());
				for (int j = 0; j < res.nodeNum; j++) {
					res.connections.get(i).add(new PLink());
				}
			}

			NodeList theLinks = docEle.getElementsByTagName("Link");

			if (theLinks != null && theLinks.getLength() > 0) {
				for (int i = 0; i < theLinks.getLength(); i++) {

					Node link = theLinks.item(i);

					if (link.getNodeType() == Node.ELEMENT_NODE) {

						Element e = (Element) link;

						NodeList nodeList = e.getElementsByTagName("node1");
						int node1 = Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
						nodeList = e.getElementsByTagName("node2");
						int node2 = Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());

						res.connections.get(node1).get(node2).setId(1);

						res.connections.get(node1).get(node2).setNode1Id(node1);
						res.connections.get(node1).get(node2).setNode2Id(node2);

						nodeList = e.getElementsByTagName("capacity");
						res.connections.get(node1).get(node2).setCapacity(Integer
								.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue()));

						nodeList = e.getElementsByTagName("delay");
						res.connections.get(node1).get(node2).setDelay(Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue()));

						res.links.add(res.connections.get(node1).get(node2));

					}
				}
			} else {
				System.exit(1);
			}

		}

		return res;
	}
}
