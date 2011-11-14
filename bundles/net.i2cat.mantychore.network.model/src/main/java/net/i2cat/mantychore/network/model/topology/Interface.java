package net.i2cat.mantychore.network.model.topology;

import java.util.List;

import net.i2cat.mantychore.network.model.predicates.ConnectedTo;
import net.i2cat.mantychore.network.model.predicates.LinkTo;
import net.i2cat.mantychore.network.model.predicates.SwitchedTo;

/**
 * A configured interface. Thus an entity that can transport data at a given layer. 
 * 
 * @author isart
 *
 */
public class Interface extends ConnectionPoint {
	
	List<ConnectedTo> connectedTo;
	
	List<LinkTo> linkTo;
	
	SwitchedTo switchedTo;

	public List<ConnectedTo> getConnectedTo() {
		return connectedTo;
	}

	public void setConnectedTo(List<ConnectedTo> connectedTo) {
		this.connectedTo = connectedTo;
	}

	public List<LinkTo> getLinkTo() {
		return linkTo;
	}

	public void setLinkTo(List<LinkTo> linkTo) {
		this.linkTo = linkTo;
	}

	public SwitchedTo getSwitchedTo() {
		return switchedTo;
	}

	public void setSwitchedTo(SwitchedTo switchedTo) {
		this.switchedTo = switchedTo;
	}
}
