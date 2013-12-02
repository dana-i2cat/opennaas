package org.opennaas.extensions.powernet.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.gim.model.core.entities.GIModel;

public class PowerNetModel extends GIModel implements IModel {

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}

	@Override
	public String toXml() throws SerializationException {
		throw new UnsupportedOperationException();
	}

}
