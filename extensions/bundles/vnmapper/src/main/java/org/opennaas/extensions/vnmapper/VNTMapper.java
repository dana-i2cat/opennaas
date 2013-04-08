/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.vnmapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author ahammaa
 */

// /// the next class is resposible for matching and mapping///
public class VNTMapper {

	Log	log	= LogFactory.getLog(VNTMapper.class);

	// // the next method sorts the virtual nodes based on nodes degrees
	public ArrayList<Integer> vNodesSorting(VNTRequest v)
	{
		// cout<<"------------------------------sorting based on virtual nodes degrees---------------------------------"<<endl;
		ArrayList<Integer> degrees = new ArrayList<Integer>();
		ArrayList<Integer> res = new ArrayList<Integer>();

		for (int i = 0; i < v.getVnodeNum(); i++)
			degrees.add(0);
		;
		for (int i = 0; i < v.getVnodeNum(); i++) {
			for (int j = 0; j < v.getVnodeNum(); j++) {
				if ((v.getConnections().get(i).get(j).getId() == 1))
				{
					int d1 = Integer.valueOf(degrees.get(i).toString());
					int d2 = Integer.valueOf(degrees.get(j).toString());
					d1++;
					d2++;
					degrees.remove(i);
					degrees.add(i, d1);
					degrees.remove(j);
					degrees.add(j, d2);
				}
			}
		}

		res.add(0);
		int u;
		for (int i = 1; i < v.getVnodeNum(); i++) {
			u = 0;

			while (((Integer.valueOf((degrees.get(Integer.valueOf(res.get(u).toString()))).toString())) >= (Integer
					.valueOf(degrees.get(i).toString())) && (u < i)))
			{
				u++;
			}
			for (int e = i; e > u; e--) {
				int temp = Integer.valueOf(res.get(e - 1).toString());
				res.remove(e);
				res.add(e, temp);

			}
			res.remove(u);
			res.add(u, i);

		}

		return res;
	}

	// // the next method sorts the virtual nodes based on the number of candidate physical nodes
	public ArrayList<Integer> vNodesCandidatePNodesSorting(ArrayList<VNTNodeMappingCell> VNTNodeMappingArray)
	{
		// cout<<"------------------------------sorting based on candidate physical nodes number---------------------------------"<<endl;
		ArrayList<Integer> res = new ArrayList<Integer>();
		int j = 0;
		for (int y = 0; y < VNTNodeMappingArray.size(); y++)
		{
			res.add(0);
		}

		for (int i = 1; i < (int) VNTNodeMappingArray.size(); i++) {
			j = 0;
			while (((VNTNodeMappingArray.get(Integer.valueOf(res.get(j).toString())).getPossibleRealNodes().size()) <= (VNTNodeMappingArray.get(i)
					.getPossibleRealNodes().size())) && (j < i))
			{
				j++;
			}

			for (int k = i; k > j; k--) {
				int temp = Integer.valueOf(res.get(k - 1).toString());
				res.remove(k);
				res.add(k, temp);
			}
			res.remove(j);
			res.add(j, i);

		}

		return res;
	}

	// the next method maps the virtual link to a physical path//
	public Path mapVirtualLink(VNTRequest v, InPNetwork net, int v1id, int v2id, int v1RealNode, int v2RealNode)
	{

		int requiredB;
		int requiredD;
		Path p = new Path();
		if (v1id < v2id) {
			requiredB = v.getConnections().get(v1id).get(v2id).getCapacity();
			requiredD = v.getConnections().get(v1id).get(v2id).getDelay();

		} else {

			requiredB = v.getConnections().get(v2id).get(v1id).getCapacity();
			requiredD = v.getConnections().get(v2id).get(v1id).getDelay();

		}

		if (Global.pathChoice == 1) // // shortest path
			p = net.findPathBetweemTwoNodes(v1RealNode, v2RealNode, requiredB, requiredD, Global.maxPathLinksNum);
		else
			// / load balancing
			p = net.findPathBetweemTwoNodes2(v1RealNode, v2RealNode, requiredB, requiredD, Global.maxPathLinksNum);
		return p;
	}

	// // the next method is the main method to map a VNT
	public int VNTMapping(VNTRequest v, InPNetwork net, int steps, ArrayList<ArrayList<Integer>> matchingResult, int option,
			MappingResult mappingResult)
	{
		int res = 1;
		ArrayList<ArrayList<Integer>> nodesMapping = new ArrayList<ArrayList<Integer>>();

		nodesMapping = matchingResult;

		for (int i = 0; i < (int) nodesMapping.size(); i++) {
			if ((int) nodesMapping.get(i).size() == 0) {
				res = 0;
			}
		}

		if (res != 0) {
			// / initilaizing the array to store the current status of each virtual node including:
			// / possible real node set
			// / the current chosen real node
			// / pointer to the previous virtual node in the way to map the virtual node
			ArrayList<VNTNodeMappingCell> VNTNodeMappingArray = new ArrayList<VNTNodeMappingCell>();
			ArrayList<Integer> sortedVNodesSet = new ArrayList<Integer>();
			for (int i = 0; i < (int) v.getVnodes().size(); i++) {
				VNTNodeMappingCell vNodeCell = new VNTNodeMappingCell();
				vNodeCell.setVid(i);
				vNodeCell.setPossibleRealNodes(nodesMapping.get(vNodeCell.getVid()));

				vNodeCell.setChosenRealNode(-1);
				vNodeCell.setPointer(-1);
				vNodeCell.setConnectionsNum(0);
				VNTNodeMappingArray.add(vNodeCell);
			}

			// // sort the virtual node starting from the virtual node that has the least number of physical nodes
			sortedVNodesSet = vNodesCandidatePNodesSorting(VNTNodeMappingArray);

			// / initializing the array to store the current status of each virtual link (if its been mapped or not yet)
			ArrayList<ArrayList<VNTLinkMappingCell>> VNTLinkMappingArray = new ArrayList<ArrayList<VNTLinkMappingCell>>();

			for (int i = 0; i < (int) v.getVnodeNum(); i++) {
				VNTLinkMappingArray.add(new ArrayList<VNTLinkMappingCell>());
				for (int j = 0; j < v.getVnodeNum(); j++)
				{
					VNTLinkMappingArray.get(i).add(new VNTLinkMappingCell());
				}
			}

			// // initializing the set to store the current selected physical nodes so that a physical node can be selected only once
			IntSet selectedRealNodes = new IntSet();

			// / initializing a set to store the successfully mapped virtual nodes
			IntSet mappedVNTNodes = new IntSet();

			// // call the method that is resposible for the recursive backtracking job of composing the VNT
			Global.stepsNum = 0;
			res = VNTMappingFunc(v, net, selectedRealNodes, VNTNodeMappingArray, VNTLinkMappingArray, sortedVNodesSet, mappedVNTNodes);

			// // calculating the mapping result and cost of it //
			if (res == 1)
			{

				// / initialize mappingResult///
				for (int u1 = 0; u1 < net.getNodeNum(); u1++)
				{
					mappingResult.getNodes().add(0);
					mappingResult.getLinks().add(new ArrayList<Integer>());
					for (int u2 = 0; u2 < net.getNodeNum(); u2++)
					{
						mappingResult.getLinks().get(u1).add(0);
					}
				}
				for (int u1 = 0; u1 < v.getVnodeNum(); u1++)
				{
					mappingResult.getVnodes().add(0);
				}
				int nodeCosts = 0;
				int linkCosts = 0;
				for (int i = 0; i < v.getVnodes().size(); i++)
				{
					nodeCosts = nodeCosts + v.getVnodes().get(i).getCapacity();
					int chosen = VNTNodeMappingArray.get(i).getChosenRealNode();
					if (chosen != -1)
					{
						mappingResult.getNodes().remove(chosen);
						mappingResult.getNodes().add(chosen, v.getVnodes().get(i).getCapacity());
						mappingResult.getVnodes().remove(i);
						mappingResult.getVnodes().add(i, chosen);
					}
				}
				mappingResult.setVNTLinkMappingArray(VNTLinkMappingArray);
				for (int i = 0; i < (int) VNTLinkMappingArray.size(); i++) {
					for (int j = 0; j < (int) VNTLinkMappingArray.get(i).size(); j++)
					{
						if (VNTLinkMappingArray.get(i).get(j).getIsMapped() == 1)
						{
							if (i < j) {
								linkCosts = linkCosts + v.getConnections().get(i).get(j).getCapacity() * VNTLinkMappingArray.get(i).get(j)
										.getResultPath()
										.getLinks()
										.size();

								for (int u = 0; u < VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().size(); u++)
								{
									int temp = Integer.valueOf(mappingResult.getLinks()
											.get(VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode1Id())
											.get(VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode2Id()).toString());
									temp += v.getConnections().get(i).get(j).getCapacity();
									mappingResult.getLinks().get(VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode1Id())
											.remove(
													VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode2Id());
									mappingResult.getLinks().get(VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode1Id())
											.add(
													VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode2Id(), temp);
								}
								// /
							} else
							{
								linkCosts = linkCosts + v.getConnections().get(j).get(i).getCapacity() * VNTLinkMappingArray.get(j).get(i)
										.getResultPath()
										.getLinks()
										.size();
								// /
								for (int u = 0; u < VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().size(); u++)
								{
									int temp = Integer.valueOf(mappingResult.getLinks()
											.get(VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode1Id())
											.get(VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode2Id()).toString());
									temp += v.getConnections().get(j).get(i).getCapacity();
									mappingResult.getLinks().get(VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode1Id())
											.remove(
													VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode2Id());
									mappingResult.getLinks().get(VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode1Id())
											.add(
													VNTLinkMappingArray.get(i).get(j).getResultPath().getLinks().get(u).getNode2Id(), temp);

									// mappingResult.links[VNTLinkMappingArray.get(i).get(j).resultPath.links[u].node1Id][VNTLinkMappingArray.get(i).get(j).resultPath.links[u].node2Id]
									// += v.connections.get(j).get(i).capacity;
								}
								// /
							}
						}
					}
				}

				mappingResult.setCost(nodeCosts + linkCosts);

			}

			// VNTLinkMappingArray.clear();
			VNTNodeMappingArray.clear();
			sortedVNodesSet.clear();
			selectedRealNodes.clear();

		}
		return res;
	}

	// // the next method is resposible for the backtracking job of composing the VNT by mapping the vnodes and vlinks and building the subgraph until
	// it is equal to the VNT graph
	public int VNTMappingFunc(VNTRequest v, InPNetwork net, IntSet selectedRealNodes, ArrayList<VNTNodeMappingCell> VNTNodeMappingArray,
			ArrayList<ArrayList<VNTLinkMappingCell>> VNTLinkMappingArray, ArrayList<Integer> sortedVNodesSet, IntSet mappedVNTNodes)
	{

		int mappingFinish = 0;

		// // select the current virtual node
		int currentVNodeId = -1;

		// currentVNodeId=chooseNextVNode(VNTNodeMappingArray,sortedVNodesSet);
		currentVNodeId = chooseNextVNodeMod(VNTNodeMappingArray);

		if (currentVNodeId != -1) {

			int currentRealNode;
			InPNetwork originalNet = new InPNetwork();
			try {
				originalNet = (InPNetwork) ObjectCopier.deepCopy(net);
			} catch (Exception e) {
				log.error("Exception 33 /// - ", e);
				log.warn("Ignoring error.");
			}

			Path resultedPath = new Path();

			// // sort the possible candidate physical nodes
			if (Global.PNodeChoice == 1) { // / cost reduction
				ArrayList<Integer> node = sortRealNode1(v, net, currentVNodeId,
						VNTNodeMappingArray.get(currentVNodeId).getPossibleRealNodes(), selectedRealNodes);
				VNTNodeMappingArray.get(currentVNodeId).setPossibleRealNodes(node);
			}
			if (Global.PNodeChoice == 2) { // / load balancing
				ArrayList<Integer> node = sortRealNode2(v, net, currentVNodeId,
						VNTNodeMappingArray.get(currentVNodeId).getPossibleRealNodes(), selectedRealNodes);
				VNTNodeMappingArray.get(currentVNodeId).setPossibleRealNodes(node);
			}
			for (int i = 0; i < VNTNodeMappingArray.get(currentVNodeId).getPossibleRealNodes().size() && (mappingFinish == 0) && (Global.stepsNum <= Global.stepsMax); i++)
			{
				// / get next candidate pnode
				currentRealNode = Integer.valueOf(VNTNodeMappingArray.get(currentVNodeId).getPossibleRealNodes().get(i).toString());

				try { // / at each step we have to go back to the initial net
					net = (InPNetwork) ObjectCopier.deepCopy(originalNet);
				} catch (Exception e) {
					log.error("Exception 44 /// - ", e);
					log.warn("Ignoring error.");
				}

				// / if currentRealNode has not been selected before
				if (!selectedRealNodes.contains(currentRealNode))
				{
					int linksMappingRes = 1;
					// / start mapping the vlinks connecting the current vnode
					for (int k = 0; (k < (int) VNTNodeMappingArray.size() && (linksMappingRes == 1)); k++)
					{
						if (VNTNodeMappingArray.get(k).getChosenRealNode() != -1)
						{
							// / check if there is virtual link between VNTNodeMappingArray[i].vid and currentVNode
							if ((v.getConnections().get(VNTNodeMappingArray.get(k).getVid()).get(currentVNodeId).getId() != -1) || (v
									.getConnections()
									.get(currentVNodeId).get(VNTNodeMappingArray.get(k).getVid()).getId() != -1))
							// / now try to map the virtual link
							{
								// mapping a virtual link affects the availabilty of resources in the network
								// so after successfully mapping each virtual link, availability has to be updated before.
								// cout<<"matching virtual link : "<<currentVNodeId<<",,"<<VNTNodeMappingArray[i].vid<<endl;
								resultedPath = mapVirtualLink(v, net, VNTNodeMappingArray.get(k).getVid(), currentVNodeId,
										VNTNodeMappingArray.get(k).getChosenRealNode(), currentRealNode);
								if (resultedPath.getId() == -1) {
									linksMappingRes = -1; // / stop the loop because the chosen real node is not successfull
								}
								else
								{
									// / here we change the availability of physical links included in the resultedPath

									linksMappingRes = 1;
									int requiredLinkCapacity;
									if (v.getConnections().get(VNTNodeMappingArray.get(k).getVid()).get(currentVNodeId).getId() != -1) {
										requiredLinkCapacity = v.getConnections().get(VNTNodeMappingArray.get(k).getVid()).get(currentVNodeId)
												.getCapacity();
										VNTLinkMappingArray.get(VNTNodeMappingArray.get(k).getVid()).get(currentVNodeId).setResultPath(resultedPath);
										VNTLinkMappingArray.get(VNTNodeMappingArray.get(k).getVid()).get(currentVNodeId).setIsMapped(1);
									} else {
										requiredLinkCapacity = v.getConnections().get(currentVNodeId).get(VNTNodeMappingArray.get(k).getVid())
												.getCapacity();
										VNTLinkMappingArray.get(currentVNodeId).get(VNTNodeMappingArray.get(k).getVid()).setResultPath(resultedPath);
										VNTLinkMappingArray.get(currentVNodeId).get(VNTNodeMappingArray.get(k).getVid()).setIsMapped(1);
									}
									for (int u = 0; u < (int) resultedPath.getLinks().size(); u++)
									{
										int n1, n2, n3;
										n1 = resultedPath.getLinks().get(u).getNode1Id();
										n2 = resultedPath.getLinks().get(u).getNode2Id();
										if (n2 < n1) {
											n3 = n2;
											n2 = n1;
											n1 = n3;
										}
										net.getConnections().get(n1).get(n2)
												.setCapacity(net.getConnections().get(n1).get(n2).getCapacity() - requiredLinkCapacity);
									}

								}
							}
						}
					}

					if (linksMappingRes == 1) // /mapping of virtual links is done successfully
					{
						// / changing the availability of the selected pnode
						net.getNodes().get(currentRealNode).setCapacity(net.getNodes().get(currentRealNode).getCapacity() - v.getVnodes()
								.get(currentVNodeId)
								.getCapacity());
						// / add the current vnode to the mapped vnodes
						mappedVNTNodes.add(currentVNodeId);

						// / record the selected pnode
						VNTNodeMappingArray.get(currentVNodeId).setChosenRealNode(currentRealNode);
						// / add the selected pnode to the set of all selected pnodes
						selectedRealNodes.add(currentRealNode);

						// / check if the whole VNT is mapped after this operation
						mappingFinish = 1;
						if (mappedVNTNodes.size() < v.getVnodeNum())
							mappingFinish = 0;

						// / updated the number of connections to the already mapped vnodes
						for (int u = 0; u < v.getVnodeNum(); u++)
						{
							if ((u < currentVNodeId) && (v.getConnections().get(u).get(currentVNodeId).getId() != -1))
							{
								VNTNodeMappingArray.get(u).incrementConnectionsNum();
							} else if ((u > currentVNodeId) && (v.getConnections().get(currentVNodeId).get(u).getId() != -1))
							{
								VNTNodeMappingArray.get(u).incrementConnectionsNum();
							}
						}

						if ((mappingFinish == 0) && (Global.stepsNum < Global.stepsMax)) // / need to continue mapping
						{
							// / recursive call
							mappingFinish = VNTMappingFunc(v, net, selectedRealNodes, VNTNodeMappingArray, VNTLinkMappingArray, sortedVNodesSet,
									mappedVNTNodes);
							if (mappingFinish == 0) {
								// / remove the selected pnode
								VNTNodeMappingArray.get(currentVNodeId).setChosenRealNode(-1);
								selectedRealNodes.remove(currentRealNode);
								Global.stepsNum++;
								// System.out.println("steps = "+Global.stepsNum);
								for (int u = 0; u < v.getVnodeNum(); u++)
								{
									if ((u < currentVNodeId) && (v.getConnections().get(u).get(currentVNodeId).getId() != -1))
									{
										VNTNodeMappingArray.get(u).decrementConnectionsNum();
									}
									else if ((u > currentVNodeId) && (v.getConnections().get(currentVNodeId).get(u).getId() != -1))
									{
										VNTNodeMappingArray.get(u).decrementConnectionsNum();
									}

								}

							}

						}
					}
				}
			} // / end for loop of possible pnodes ///
			if (mappingFinish == 0) {
				try {
					net = (InPNetwork) ObjectCopier.deepCopy(originalNet);
				} catch (Exception e) {
					log.error("Exception 555 /// - ", e);
					log.warn("Ignoring error.");
				}
			}
		}

		if (currentVNodeId == -1)
			mappingFinish = 1;

		return mappingFinish;
	}

	// /// the next method is used to choose the next unmapped vnode
	public int chooseNextVNode(ArrayList<VNTNodeMappingCell> VNTNodeMappingArray, ArrayList<Integer> sortedVNodesSet)
	{
		int currentVNodeId = -1;
		for (int i = 0; (i < (int) sortedVNodesSet.size()) && (currentVNodeId == -1); i++)
		{
			int vid = Integer.valueOf(sortedVNodesSet.get(i).toString());
			if (VNTNodeMappingArray.get(vid).getChosenRealNode() == -1)
				currentVNodeId = vid;
		}

		return currentVNodeId;
	}

	// /// the next method is modified from the above method to select the next virtual node who has the maximum number of connection to the already
	// mapped vnodes
	public int chooseNextVNodeMod(ArrayList<VNTNodeMappingCell> VNTNodeMappingArray)
	{
		int currentVNodeId = -1;
		int temp = -1;
		for (int i = 0; (i < (int) VNTNodeMappingArray.size()); i++)
		{
			// int vid = Integer.valueOf(VNTNodeMappingArray.get(i).toString());
			if ((VNTNodeMappingArray.get(i).getConnectionsNum() > temp) && (VNTNodeMappingArray.get(i).getChosenRealNode() == -1))
			{
				temp = VNTNodeMappingArray.get(i).getConnectionsNum();
				currentVNodeId = VNTNodeMappingArray.get(i).getVid();
			}
		}

		return currentVNodeId;
	}

	// /// the next method is used to sort the possible real nodes based on the connections number with the selected real nodes.
	ArrayList<Integer> sortRealNode1(VNTRequest v, InPNetwork net, int currentVNodeId, ArrayList<Integer> possibleRealNodes, IntSet selectedRealNodes)
	{
		int realNode;
		ArrayList<Integer> connectionNum = new ArrayList<Integer>();
		for (int y = 0; y < net.getNodeNum(); y++)
		{
			connectionNum.add(0);
		}

		Iterator<Integer> it = selectedRealNodes.iterator();
		while (it.hasNext())
		{
			realNode = Integer.valueOf(it.next().toString());
			for (int i = 0; i < possibleRealNodes.size(); i++)
			{
				if ((realNode < Integer.valueOf(possibleRealNodes.get(i).toString())) && (net.getConnections().get(realNode).get(
						Integer.valueOf(possibleRealNodes.get(i).toString())).getId() != -1))
				{
					int temp = Integer.valueOf(connectionNum.get(Integer.valueOf(possibleRealNodes.get(i).toString())).toString());
					temp++;
					connectionNum.remove(Integer.valueOf(possibleRealNodes.get(i).toString()));
					connectionNum.add(Integer.valueOf(possibleRealNodes.get(i).toString()), temp);
				}
				if ((realNode > Integer.valueOf(possibleRealNodes.get(i).toString())) && (net.getConnections().get(
						Integer.valueOf(possibleRealNodes.get(i).toString())).get(realNode).getId() != -1))
				{
					int temp = Integer.valueOf(connectionNum.get(Integer.valueOf(possibleRealNodes.get(i).toString())).toString());
					temp++;
					connectionNum.remove(Integer.valueOf(possibleRealNodes.get(i).toString()));
					connectionNum.add(Integer.valueOf(possibleRealNodes.get(i).toString()), temp);
				}
			}
		}

		int j = 0;
		int tmp;
		for (int i = 1; i < (int) possibleRealNodes.size(); i++) {
			j = i;
			tmp = Integer.valueOf(possibleRealNodes.get(i).toString());
			while (j > 0 && Integer.valueOf(connectionNum.get(tmp).toString()) > Integer.valueOf(connectionNum.get(
					Integer.valueOf(possibleRealNodes.get(j - 1).toString())).toString()))
			{
				int t = Integer.valueOf(possibleRealNodes.get(j - 1).toString());
				possibleRealNodes.remove(j);
				possibleRealNodes.add(j, t);
				j--;
			}
			possibleRealNodes.remove(j);
			possibleRealNodes.add(j, tmp);
		}

		connectionNum.clear();
		return possibleRealNodes;
	}

	ArrayList<Integer> sortRealNode2(VNTRequest v, InPNetwork net, int currentVNodeId, ArrayList<Integer> possibleRealNodes, IntSet selectedRealNodes)
	{
		// // sort the possible real nodes based on the connections number with the selected real nodes.
		ArrayList<Integer> capacityDifference = new ArrayList<Integer>();
		// capacityDifference.Capacity=net.nodeNum;
		int requiredCapacity = v.getVnodes().get(currentVNodeId).getCapacity();
		for (int k = 0; k < net.getNodeNum(); k++)
		{
			capacityDifference.add(-1);
		}
		for (int i = 0; i < possibleRealNodes.size(); i++)
		{
			capacityDifference.remove(Integer.valueOf(possibleRealNodes.get(i).toString()));
			capacityDifference.add(Integer.valueOf(possibleRealNodes.get(i).toString()),
					net.getNodes().get(Integer.valueOf(possibleRealNodes.get(i).toString())).getCapacity() - requiredCapacity);

		}

		// /

		int j = 0;
		int tmp;
		for (int i = 1; i < (int) possibleRealNodes.size(); i++) {
			j = i;
			tmp = Integer.valueOf(possibleRealNodes.get(i).toString());
			int t1 = Integer.valueOf(capacityDifference.get(tmp).toString());
			int t2 = Integer.valueOf(possibleRealNodes.get(j - 1).toString());
			int t3 = Integer.valueOf(capacityDifference.get(t2).toString());
			while (j > 0 && t1 > t3)
			{

				int t = Integer.valueOf(possibleRealNodes.get(j - 1).toString());
				possibleRealNodes.remove(j);
				possibleRealNodes.add(j, t);

				j--;
			}
			possibleRealNodes.remove(j);
			possibleRealNodes.add(j, tmp);
		}

		// /

		capacityDifference.clear();
		return possibleRealNodes;
	}

	// // the next method is used to match the capacity and location of a virtual node

	public ArrayList<Integer> matchVirtualNode(VNTRequest v, int vid, InPNetwork net)
	{
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < (int) net.getNodes().size(); i++)
		{

			if (((v.getVnodes().get(vid).getPnodeID() != "") && (v.getVnodes().get(vid).getPnodeID().equals(net.getNodes().get(i).getPnodeID()))) || (v
					.getVnodes()
					.get(vid).getPnodeID().equals("-")))
			{
				if ((net.getNodes().get(i).getCapacity() >= v.getVnodes().get(vid).getCapacity()))
				{
					res.add(i);
				}
			}
		}
		return res;
	}

	public int matchVirtualNetwork(VNTRequest v, InPNetwork net, ArrayList<ArrayList<Integer>> res)
	{
		int t = 1;
		// // create a set to store the vnodes that have one matched pnodes///
		IntSet oneMatchedPnodes = new IntSet();
		for (int i = 0; (i < (int) v.getVnodes().size()) && (t == 1); i++) {
			ArrayList<Integer> s;
			s = matchVirtualNode(v, i, net);
			if (s.size() == 0)
				t = -1;

			if (s.size() == 1)
			{
				if (oneMatchedPnodes.contains(Integer.parseInt(s.get(0).toString())))
				{
					t = -1;
				}
				else
					oneMatchedPnodes.add(Integer.parseInt(s.get(0).toString()));
			}

			res.add(s);
		}

		return t;
	}

	// // the next method is used to release the resources allocated for a VNT
	public static InPNetwork freeVNT(InPNetwork net, MappingResult mappingResult)
	{
		for (int k = 0; k < mappingResult.getNodes().size(); k++)
		{
			net.getNodes().get(k).increaseCapacity(Integer.valueOf(mappingResult.getNodes().get(k).toString()));
		}
		for (int k = 0; k < mappingResult.getLinks().size(); k++)
		{
			for (int k2 = 0; k2 < mappingResult.getLinks().get(k).size(); k2++)
			{
				net.getConnections().get(k).get(k2).increaseCapacity(Integer.valueOf(mappingResult.getLinks().get(k).get(k2).toString()));
			}
		}
		return net;
	}

	// // the next method is used to allocate the resources for a VNT based on the mapping result
	public InPNetwork allocateVNT(InPNetwork net, MappingResult mappingResult)
	{

		for (int k = 0; k < mappingResult.getNodes().size(); k++)
		{
			net.getNodes().get(k).decreaseCapacity(Integer.valueOf(mappingResult.getNodes().get(k).toString()));
		}
		for (int k = 0; k < mappingResult.getLinks().size(); k++)
		{
			for (int k2 = 0; k2 < mappingResult.getLinks().get(k).size(); k2++)
			{
				net.getConnections().get(k).get(k2).decreaseCapacity(Integer.valueOf(mappingResult.getLinks().get(k).get(k2).toString()));
			}
		}

		return net;
	}

	public ArrayList<InPNetwork> releaseAllVNsBeforeCurrentTime(ArrayList<InPNetwork> nets, int currentTime, ArrayList<VNLifetime> VNLifetimes)
	{

		for (int i = 0; i < VNLifetimes.size(); i++)
		{
			if (VNLifetimes.get(i).getWaitingForReleasing() == 1)
			{
				if (VNLifetimes.get(i).getReleasingTime() <= currentTime)
				{
					InPNetwork net = new InPNetwork();
					int providerID = VNLifetimes.get(i).getProviderID();
					net = VNTMapper.freeVNT(nets.get(providerID), VNLifetimes.get(i).getMres());
					nets.remove(providerID);
					nets.add(providerID, net);
					VNLifetimes.get(i).setWaitingForReleasing(1);
					log.info("VNT Released: " + i + "  from provider " + providerID);
				}
			}
		}

		return nets;
	}

	public ArrayList<InPNetwork> releaseAllVNs(ArrayList<InPNetwork> nets, List<VNLifetime> VNLifetimes)
	{

		for (int i = 0; i < VNLifetimes.size(); i++)
		{
			if (VNLifetimes.get(i).getWaitingForReleasing() == 1)
			{
				InPNetwork net = new InPNetwork();
				int providerID = VNLifetimes.get(i).getProviderID();
				net = VNTMapper.freeVNT(nets.get(providerID), VNLifetimes.get(i).getMres());
				nets.remove(providerID);
				nets.add(providerID, net);

				VNLifetimes.get(i).setWaitingForReleasing(-1);

				log.info("VNT Released: " + i + "  from provider " + providerID);
			}
		}

		return nets;
	}

}
