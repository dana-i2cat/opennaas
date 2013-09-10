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

}
