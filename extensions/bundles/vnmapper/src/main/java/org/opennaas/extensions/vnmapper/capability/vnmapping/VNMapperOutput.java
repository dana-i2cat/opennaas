package org.opennaas.extensions.vnmapper.capability.vnmapping;

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

import org.opennaas.core.resources.IResource;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.vnmapper.MappingResult;
import org.opennaas.extensions.vnmapper.VNState;

public class VNMapperOutput {

	private MappingResult	result;
	private VNMapperInput	mapperInput;

	private IResource		inputNetwork;
	private NetworkModel	networkModel;

	public VNMapperOutput(MappingResult result, VNMapperInput mapperInput) {
		this.result = result;
		this.mapperInput = mapperInput;
	}

	public VNMapperOutput(MappingResult result, VNMapperInput mapperInput, IResource networkResource, NetworkModel networkModel) {
		this.result = result;
		this.mapperInput = mapperInput;
		this.networkModel = networkModel;
		this.inputNetwork = networkResource;
	}

	public MappingResult getResult() {
		return result;
	}

	public void setResult(MappingResult result) {
		this.result = result;
	}

	public VNMapperInput getMapperInput() {
		return mapperInput;
	}

	public void setMapperInput(VNMapperInput mapperInput) {
		this.mapperInput = mapperInput;
	}

	public NetworkModel getNetworkModel() {
		return networkModel;
	}

	public void setNetworkModel(NetworkModel networkModel) {
		this.networkModel = networkModel;
	}

	public IResource getInputNetwork() {
		return inputNetwork;
	}

	public void setInputNetwork(IResource inputNetwork) {
		this.inputNetwork = inputNetwork;
	}

	@Override
	public String toString() {

		String outputString = mapperInput.toString();

		VNState matchingState = result.getMatchingState();
		VNState mappingState = result.getMappingState();

		if (!matchingState.equals(VNState.SUCCESSFUL))
			outputString += "Unsuccessful Matching\n";
		else if (matchingState.equals(VNState.SUCCESSFUL) && !mappingState.equals(VNState.SUCCESSFUL)) {
			outputString += "Successful Matching\n";
			outputString += "Unsuccessful Mapping\n";
		}
		else {
			outputString += "Successful Matching\n";
			outputString += "Successful Mapping\n";
			outputString += result.toString();
		}

		return outputString;
	}

}
