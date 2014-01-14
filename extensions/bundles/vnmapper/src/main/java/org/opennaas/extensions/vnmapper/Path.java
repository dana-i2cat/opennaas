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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Path implements Serializable {

	private static final long	serialVersionUID	= -8058566845188905088L;

	private int					id;
	private int					node1Id;
	private int					node2Id;
	private ArrayList<PLink>	links;
	private int					capacity;
	private int					maxCapacity;
	private int					delay;

	// /// method to calculate the capacity and delay of a path based on the included links

	public Path() {
		links = new ArrayList<PLink>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNode1Id() {
		return node1Id;
	}

	public void setNode1Id(int node1Id) {
		this.node1Id = node1Id;
	}

	public int getNode2Id() {
		return node2Id;
	}

	public void setNode2Id(int node2Id) {
		this.node2Id = node2Id;
	}

	public ArrayList<PLink> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<PLink> links) {
		this.links = links;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public void findPathCapacityAndDelay(List<List<PLink>> connections)
	{
		int minCapacity, delay;
		minCapacity = 1000000;
		delay = 0;
		this.maxCapacity = 0;
		int n1, n2, n3;
		for (int i = 0; i < (int) this.links.size(); i++)
		{

			n1 = this.links.get(i).getNode1Id();
			n2 = this.links.get(i).getNode2Id();
			if (n2 < n1) {
				n3 = n2;
				n2 = n1;
				n1 = n3;
			}
			if (connections.get(n1).get(n2).getCapacity() < minCapacity)
				minCapacity = connections.get(n1).get(n2).getCapacity();

			if (connections.get(n1).get(n2).getCapacity() > this.maxCapacity)
				this.maxCapacity = connections.get(n1).get(n2).getCapacity();

			delay = delay + links.get(i).getDelay();
		}
		this.capacity = minCapacity;

		this.delay = delay;
	}

	// / method to add a link to a path
	public Path addLinkToPath(PLink l)
	{
		Path res = new Path();
		int size = this.links.size();

		for (int i = 0; i < (int) this.links.size(); i++)
			res.links.add(i, this.links.get(i));

		res.links.add(size, l);
		// res->findPathCapacityAndDelay();
		return res;
	}

	@Override
	public String toString() {

		String pathString = "";
		pathString += node1Id + "--";
		int previous = node1Id;
		int n1, n2;
		if ((node1Id == this.links.get(0).getNode1Id()) || (node1Id == this.links.get(0).getNode2Id()))
		{
			for (int i = 0; i < (int) this.links.size(); i++)
			{
				n1 = this.links.get(i).getNode1Id();
				n2 = this.links.get(i).getNode2Id();
				// System.out.println(n1+"--"+n2);
				if (n1 == previous)
				{
					pathString += n2 + "--";
					previous = n2;
				}
				else
				{
					pathString += n1 + "--";
					previous = n1;
				}
			}
		}
		else
		{
			for (int i = (int) this.links.size() - 1; i >= 0; i--)
			{
				n1 = this.links.get(i).getNode1Id();
				n2 = this.links.get(i).getNode2Id();
				// System.out.println(n1+"--"+n2);
				if (n1 == previous)
				{
					pathString += n2 + "--";
					previous = n2;
				}
				else
				{
					pathString += n1 + "--";
					previous = n1;
				}
			}
		}

		if (pathString.endsWith("--"))
			pathString = pathString.substring(0, pathString.length() - 2);

		return pathString;
	}
}
