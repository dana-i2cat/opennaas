package org.opennaas.extensions.vnmapper.capability.vnmapping;

import org.opennaas.extensions.vnmapper.MappingResult;
import org.opennaas.extensions.vnmapper.VNState;

public class VNMapperOutput {

	private MappingResult	result;
	private VNMapperInput	mapperInput;

	public VNMapperOutput(MappingResult result, VNMapperInput mapperInput) {
		this.result = result;
		this.mapperInput = mapperInput;
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
