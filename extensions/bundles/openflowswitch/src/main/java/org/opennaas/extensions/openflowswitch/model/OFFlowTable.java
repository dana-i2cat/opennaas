package org.opennaas.extensions.openflowswitch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class OFFlowTable {

	private String					tableId;
	private List<FloodlightOFFlow>	ofForwardingRules;

	public OFFlowTable() {
		tableId = new String();
		ofForwardingRules = new ArrayList<FloodlightOFFlow>();
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public List<FloodlightOFFlow> getOfForwardingRules() {
		return ofForwardingRules;
	}

	public void setOfForwardingRules(List<FloodlightOFFlow> ofForwardingRules) {
		this.ofForwardingRules = ofForwardingRules;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ofForwardingRules == null) ? 0 : ofForwardingRules.hashCode());
		result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
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
		OFFlowTable other = (OFFlowTable) obj;
		if (ofForwardingRules == null) {
			if (other.ofForwardingRules != null)
				return false;
		} else if (!ofForwardingRules.equals(other.ofForwardingRules))
			return false;
		if (tableId == null) {
			if (other.tableId != null)
				return false;
		} else if (!tableId.equals(other.tableId))
			return false;
		return true;
	}

}
