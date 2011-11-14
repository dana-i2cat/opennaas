package net.i2cat.mantychore.network.model;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.network.model.topology.NetworkElement;

import org.opennaas.core.resources.IModel;

public class NetworkModel implements IModel {

	private static final long serialVersionUID = -8240103035074766194L;
	
	List<NetworkElement> networkElements;
	
	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}
	
	public List<NetworkElement> getNetworkElements(){
		return networkElements;
	}
}

	