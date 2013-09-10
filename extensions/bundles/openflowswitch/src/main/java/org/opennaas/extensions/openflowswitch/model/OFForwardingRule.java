package org.opennaas.extensions.openflowswitch.model;

import org.openflow.protocol.OFFlowMod;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class OFForwardingRule {

	private String		flowId;
	private OFFlowMod	ofFlowMod;

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public OFFlowMod getOfFlowMod() {
		return ofFlowMod;
	}

	public void setOfFlowMod(OFFlowMod ofFlowMod) {
		this.ofFlowMod = ofFlowMod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flowId == null) ? 0 : flowId.hashCode());
		result = prime * result + ((ofFlowMod == null) ? 0 : ofFlowMod.hashCode());
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
		OFForwardingRule other = (OFForwardingRule) obj;
		if (flowId == null) {
			if (other.flowId != null)
				return false;
		} else if (!flowId.equals(other.flowId))
			return false;
		if (ofFlowMod == null) {
			if (other.ofFlowMod != null)
				return false;
		} else if (!ofFlowMod.equals(other.ofFlowMod))
			return false;
		return true;
	}

}
