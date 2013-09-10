package org.opennaas.extensions.openflowswitch.model;

import java.util.List;

import org.openflow.protocol.OFFlowMod;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class OFTable {

	private String			tableId;
	private List<OFFlowMod>	ofMatches;

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public List<OFFlowMod> getOfMatches() {
		return ofMatches;
	}

	public void setOfMatches(List<OFFlowMod> ofMatches) {
		this.ofMatches = ofMatches;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ofMatches == null) ? 0 : ofMatches.hashCode());
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
		OFTable other = (OFTable) obj;
		if (ofMatches == null) {
			if (other.ofMatches != null)
				return false;
		} else if (!ofMatches.equals(other.ofMatches))
			return false;
		if (tableId == null) {
			if (other.tableId != null)
				return false;
		} else if (!tableId.equals(other.tableId))
			return false;
		return true;
	}

}
