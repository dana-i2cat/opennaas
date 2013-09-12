package org.opennaas.extensions.openflowswitch.capability;

import java.util.List;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.openflowswitch.model.OFForwardingRule;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public interface IOpenflowForwardingCapability extends ICapability {

	public void createOpenflowForwardingRule(OFForwardingRule forwardingRule);

	public void removeOpenflowForwardingRule(String flowId);

	public List<OFForwardingRule> getOpenflowForwardingRules();

}
