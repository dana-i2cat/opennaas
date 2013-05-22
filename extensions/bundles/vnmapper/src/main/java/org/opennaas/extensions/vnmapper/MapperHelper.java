package org.opennaas.extensions.vnmapper;

import java.util.ArrayList;
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

	public static String matchingResultToString(ArrayList<ArrayList<Integer>> matchingResult)
	{
		String str = "";

		for (int i = 0; i < matchingResult.size(); i++)
		{
			str += i + ": ";
			for (int j = 0; j < matchingResult.get(i).size(); j++)
			{
				int t = Integer.valueOf(matchingResult.get(i).get(j).toString());
				str += t + "- ";
			}
			str += "\n";
		}

		return str;
	}

}
