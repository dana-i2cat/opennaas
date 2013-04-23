package org.opennaas.extensions.vnmapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MapperHelper {

	public static int getRandInt(int min, int max, Random r)
	{
		int res = 0;
		// return r.Next(min, max+1);
		if (max == min)
			res = max;
		else
		{
			int t = r.nextInt(max - min + 1);
			res = min + t;
		}
		return res;

	}

	public static int getNodeNum(int nodeMin, int nodeMax, Random r)
	{
		return getRandInt(nodeMin, nodeMax, r);
	}

	public static void printMatchingResult(ArrayList<ArrayList<Integer>> matchingResult)
	{
		for (int i = 0; i < matchingResult.size(); i++)
		{
			System.out.print(i + ":");
			for (int j = 0; j < matchingResult.get(i).size(); j++)
			{
				int t = Integer.valueOf(matchingResult.get(i).get(j).toString());
				System.out.print(t + "-");
			}
			System.out.println();
		}
	}

	public static void printMappingResult(MappingResult mres)
	{
		System.out.println("Mapping Result: ");
		System.out.println("Nodes: ");
		for (int i = 0; i < mres.getNodes().size(); i++)
		{
			int t = Integer.valueOf(mres.getNodes().get(i).toString());
			if (t > 0)
				System.out.println(i + " : " + t);
		}
		System.out.println("Links: ");

		for (int i = 0; i < mres.getLinks().size(); i++)
			for (int j = 0; j < mres.getLinks().get(i).size(); j++)
			{
				int t = Integer.valueOf(mres.getLinks().get(i).get(j).toString());
				if (t > 0)
					System.out.println(i + "-" + j + " : " + t);
			}

		System.out.println("------------------");
	}

	public static void printSet(IntSet s)
	{
		Iterator<Integer> it = s.iterator();

		while (it.hasNext())
		{
			System.out.println("set : " + it.next());
		}
	}

}
