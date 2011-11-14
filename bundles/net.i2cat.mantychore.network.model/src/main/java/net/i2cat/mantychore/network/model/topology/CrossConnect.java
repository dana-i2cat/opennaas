package net.i2cat.mantychore.network.model.topology;

import java.util.List;

import net.i2cat.mantychore.network.model.predicates.SwitchedTo;

/**
 * A (collection of) network element(s) that can be represented as a subnetwork connection (ITU-T G.805 terminology). A cross connect is an internal data transport within a device or domain, unlike Links which transport data between two devices or domains.
 * 
 * @author isart
 *
 */
public class CrossConnect extends NetworkConnection {

	List<SwitchedTo> switchedTo;

	public List<SwitchedTo> getSwitchedTo() {
		return switchedTo;
	}

	public void setSwitchedTo(List<SwitchedTo> switchedTo) {
		this.switchedTo = switchedTo;
	}
}
