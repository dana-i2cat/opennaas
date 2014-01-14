package org.opennaas.extensions.vnmapper;

/*
 * #%L
 * OpenNaaS :: VNMapper Resource
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
