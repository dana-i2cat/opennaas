package org.opennaas.extensions.vnmapper.capability.example;

import java.util.ArrayList;

public class MappingResult {
	public ArrayList								nodes				= new ArrayList();
	public ArrayList								vnodes				= new ArrayList();
	public ArrayList<ArrayList>						links				= new ArrayList<ArrayList>();
	public ArrayList<ArrayList<VNTLinkMappingCell>>	VNTLinkMappingArray	= new ArrayList<ArrayList<VNTLinkMappingCell>>();
	public int										providerID;
	public int										cost;

	@Override
	public String toString() {

		String mappingString = "";
		mappingString += "Mapping Result: \n";
		mappingString += "vnodes: \n";
		// LOG System.out.println("Mapping Result: ");
		// LOG System.out.println("vnodes: ");
		for (int i = 0; i < vnodes.size(); i++)
		{
			// LOG System.out.println(i + "--" + Integer.valueOf(vnodes.get(i).toString()));
			mappingString += i + "--" + Integer.valueOf(vnodes.get(i).toString()) + "\n";
		}
		// LOG System.out.println("vlinks: ");
		mappingString += "vlinks: \n";
		for (int i = 0; i < (int) VNTLinkMappingArray.size(); i++) {
			for (int j = 0; j < (int) VNTLinkMappingArray.get(i).size(); j++)
			{
				if (VNTLinkMappingArray.get(i).get(j).isMapped == 1)
				{
					if (i < j) {
						// LOG System.out.print(i + "--" + j + ":");
						mappingString += i + "--" + j + ":";
						VNTLinkMappingArray.get(i).get(j).resultPath.node1Id = Integer.valueOf(vnodes.get(i).toString());
						VNTLinkMappingArray.get(i).get(j).resultPath.node2Id = Integer.valueOf(vnodes.get(j).toString());
						mappingString += VNTLinkMappingArray.get(i).get(j).resultPath.toString();
						// LOG System.out.println();
						mappingString += "\n";
					}
					else
					{
						mappingString += j + "--" + i + ":";
						// System.out.print(j + "--" + i + ":");
						VNTLinkMappingArray.get(j).get(i).resultPath.node1Id = Integer.valueOf(vnodes.get(j).toString());
						VNTLinkMappingArray.get(j).get(i).resultPath.node2Id = Integer.valueOf(vnodes.get(i).toString());
						mappingString += VNTLinkMappingArray.get(j).get(i).resultPath.toString();
						mappingString += "\n";
						// LOG System.out.println();
					}
				}
			}
		}

		return mappingString;
	}
}
