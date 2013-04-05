package org.opennaas.extensions.vnmapper.capability.example;

import org.opennaas.extensions.vnmapper.MappingResult;

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

}
