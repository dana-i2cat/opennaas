package org.opennaas.extensions.vnmapper;

import java.util.ArrayList;

public class MappingResult {

	private ArrayList<Integer>							nodes;
	private ArrayList<Integer>							vnodes;
	private ArrayList<ArrayList<Integer>>				links;
	private ArrayList<ArrayList<VNTLinkMappingCell>>	VNTLinkMappingArray;
	private int											providerID;
	private int											cost;

	private VNState										mappingState;
	private VNState										matchingState;

	public MappingResult() {

		nodes = new ArrayList<Integer>();
		vnodes = new ArrayList<Integer>();
		links = new ArrayList<ArrayList<Integer>>();
		VNTLinkMappingArray = new ArrayList<ArrayList<VNTLinkMappingCell>>();
		mappingState = VNState.SKIPPED;
		matchingState = VNState.SKIPPED;
	}

	public VNState getMappingState() {
		return mappingState;
	}

	public void setMappingState(VNState mappingState) {
		this.mappingState = mappingState;
	}

	public VNState getMatchingState() {
		return matchingState;
	}

	public void setMatchingState(VNState matchingState) {
		this.matchingState = matchingState;
	}

	public ArrayList<Integer> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Integer> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<Integer> getVnodes() {
		return vnodes;
	}

	public void setVnodes(ArrayList<Integer> vnodes) {
		this.vnodes = vnodes;
	}

	public ArrayList<ArrayList<Integer>> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<ArrayList<Integer>> links) {
		this.links = links;
	}

	public ArrayList<ArrayList<VNTLinkMappingCell>> getVNTLinkMappingArray() {
		return VNTLinkMappingArray;
	}

	public void setVNTLinkMappingArray(ArrayList<ArrayList<VNTLinkMappingCell>> vNTLinkMappingArray) {
		VNTLinkMappingArray = vNTLinkMappingArray;
	}

	public int getProviderID() {
		return providerID;
	}

	public void setProviderID(int providerID) {
		this.providerID = providerID;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {

		String mappingString = "";
		mappingString += "Mapping Result: \n";
		mappingString += "vnodes: \n";

		for (int i = 0; i < vnodes.size(); i++)
		{
			mappingString += i + "--" + Integer.valueOf(vnodes.get(i).toString()) + "\n";
		}
		mappingString += "vlinks: \n";
		for (int i = 0; i < (int) VNTLinkMappingArray.size(); i++) {
			for (int j = 0; j < (int) VNTLinkMappingArray.get(i).size(); j++)
			{
				if (VNTLinkMappingArray.get(i).get(j).getIsMapped() == 1)
				{
					if (i < j) {
						mappingString += i + "--" + j + ":";
						VNTLinkMappingArray.get(i).get(j).getResultPath().setNode1Id(Integer.valueOf(vnodes.get(i).toString()));
						VNTLinkMappingArray.get(i).get(j).getResultPath().setNode2Id(Integer.valueOf(vnodes.get(j).toString()));
						mappingString += VNTLinkMappingArray.get(i).get(j).getResultPath().toString();
						mappingString += "\n";
					}
					else
					{
						mappingString += j + "--" + i + ":";
						VNTLinkMappingArray.get(j).get(i).getResultPath().setNode1Id(Integer.valueOf(vnodes.get(j).toString()));
						VNTLinkMappingArray.get(j).get(i).getResultPath().setNode2Id(Integer.valueOf(vnodes.get(i).toString()));
						mappingString += VNTLinkMappingArray.get(j).get(i).getResultPath().toString();
						mappingString += "\n";
					}
				}
			}
		}

		return mappingString;
	}
}
