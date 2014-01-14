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

public class VNTNodeMappingCell {

	private int					vid;
	private ArrayList<Integer>	possibleRealNodes;
	private int					chosenRealNode;
	private int					connectionsNum;	// number of connection to the already mapped vnodes
	private int					pointer;

	public VNTNodeMappingCell() {
		possibleRealNodes = new ArrayList<Integer>();
	}

	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
	}

	public ArrayList<Integer> getPossibleRealNodes() {
		return possibleRealNodes;
	}

	public void setPossibleRealNodes(ArrayList<Integer> possibleRealNodes) {
		this.possibleRealNodes = possibleRealNodes;
	}

	public int getChosenRealNode() {
		return chosenRealNode;
	}

	public void setChosenRealNode(int chosenRealNode) {
		this.chosenRealNode = chosenRealNode;
	}

	public int getConnectionsNum() {
		return connectionsNum;
	}

	public void setConnectionsNum(int connectionsNum) {
		this.connectionsNum = connectionsNum;
	}

	public int getPointer() {
		return pointer;
	}

	public void setPointer(int pointer) {
		this.pointer = pointer;
	}

	public void incrementConnectionsNum() {
		this.connectionsNum++;
	}

	public void decrementConnectionsNum() {
		this.connectionsNum--;
	}
}
