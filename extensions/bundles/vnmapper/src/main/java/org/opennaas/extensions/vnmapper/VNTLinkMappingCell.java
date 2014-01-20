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

/// this class and VNTNodeMappingCell one are used to store information during the mapping process

public class VNTLinkMappingCell {

	private int		isConnected;
	private int		isMapped;
	private Path	resultPath;

	public VNTLinkMappingCell()
	{
		isConnected = 0;
		isMapped = 0;
		resultPath = new Path();
	}

	public int getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(int isConnected) {
		this.isConnected = isConnected;
	}

	public int getIsMapped() {
		return isMapped;
	}

	public void setIsMapped(int isMapped) {
		this.isMapped = isMapped;
	}

	public Path getResultPath() {
		return resultPath;
	}

	public void setResultPath(Path resultPath) {
		this.resultPath = resultPath;
	}
}
