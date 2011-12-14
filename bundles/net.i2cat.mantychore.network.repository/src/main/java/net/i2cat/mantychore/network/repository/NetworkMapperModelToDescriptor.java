package net.i2cat.mantychore.network.repository;

import org.opennaas.core.resources.descriptor.network.NetworkTopology;

import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.domain.NetworkDomain;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

public class NetworkMapperModelToDescriptor {
	
	public NetworkTopology modelToDescriptor (NetworkModel networkModel) {
		NetworkTopology networkTopology = new NetworkTopology();
		
		for (NetworkElement networkElement: networkModel.getNetworkElements()) {
			if (networkElement instanceof NetworkDomain) {
				NetworkDomain networkDomain = (NetworkDomain) networkElement;
				
				
			}
			
		}
		
		
		return networkTopology;
	}

}
