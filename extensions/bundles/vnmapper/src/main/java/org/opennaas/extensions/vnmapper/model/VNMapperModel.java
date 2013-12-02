package org.opennaas.extensions.vnmapper.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.SerializationException;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class VNMapperModel implements IModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4002472167559948067L;

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}

	@Override
	public String toXml() throws SerializationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
