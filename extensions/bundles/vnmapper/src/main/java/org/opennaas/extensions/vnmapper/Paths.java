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

public class Paths {

	// / the class is used to store a list of paths between two nodes

	private int				node1Id;
	private int				node2Id;
	private ArrayList<Path>	paths;

	public Paths() {
		paths = new ArrayList<Path>();
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

	public ArrayList<Path> getPaths() {
		return paths;
	}

	public void setPaths(ArrayList<Path> paths) {
		this.paths = paths;
	}

}
