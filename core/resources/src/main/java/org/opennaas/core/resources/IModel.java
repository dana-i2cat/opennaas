package org.opennaas.core.resources;

import java.io.Serializable;
import java.util.List;

public interface IModel extends Serializable {
	// FIXME: This is disgraceful.
	List<String> getChildren();

	public String toXml() throws SerializationException;
}
