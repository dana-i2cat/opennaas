package org.opennaas.extensions.openflowswitch.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class OpenflowSwitchModel implements IModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1377911113047348716L;

	private String				switchId;
	private List<OFTable>		ofTables;

	public String getSwitchId() {
		return switchId;
	}

	public void setSwitchId(String switchId) {
		this.switchId = switchId;
	}

	public List<OFTable> getOfTables() {
		return ofTables;
	}

	public void setOfTables(List<OFTable> ofTables) {
		this.ofTables = ofTables;
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}

	@Override
	public String toXml() throws SerializationException {
		return ObjectSerializer.toXml(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ofTables == null) ? 0 : ofTables.hashCode());
		result = prime * result + ((switchId == null) ? 0 : switchId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OpenflowSwitchModel other = (OpenflowSwitchModel) obj;
		if (ofTables == null) {
			if (other.ofTables != null)
				return false;
		} else if (!ofTables.equals(other.ofTables))
			return false;
		if (switchId == null) {
			if (other.switchId != null)
				return false;
		} else if (!switchId.equals(other.switchId))
			return false;
		return true;
	}

}
