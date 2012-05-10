package org.opennaas.extensions.network.repository.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.extensions.network.mock.MockNetworkDescriptor;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.domain.NetworkDomain;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.repository.NetworkMapperDescriptorToModel;

public class NetworkMapperModelToDescriptorTest {

	@Test
	public void testMockMapperNetworkElems() {
		try {

			NetworkTopology topology = MockNetworkDescriptor.newNetworkDescriptorWithNetworkDomain();

			int topologyDeviceCount = topology.getDevices().size();
			int topologyInterfacesCount = topology.getInterfaces().size();
			int topologyDomainsCount = topology.getNetworkDomains().size();
			int topologyLinksCount = 3; // has 3 links (2 of them bidi)
			topologyInterfacesCount += 1; // has a link to an external interface

			NetworkModel networkModel = NetworkMapperDescriptorToModel.descriptorToModel(topology);
			Assert.assertNotNull(networkModel.getNetworkElements());

			Assert.assertEquals(topologyDomainsCount, NetworkModelHelper.getDomains(networkModel).size());
			Assert.assertEquals(topologyDeviceCount, NetworkModelHelper.getDevices(networkModel).size());
			Assert.assertEquals(topologyInterfacesCount, NetworkModelHelper.getInterfaces(networkModel.getNetworkElements()).size());
			Assert.assertEquals(topologyLinksCount, NetworkModelHelper.getLinks(networkModel).size());

		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testMockMapperModel() {
		try {

			NetworkModel networkModel = NetworkMapperDescriptorToModel.descriptorToModel(MockNetworkDescriptor
					.newNetworkDescriptorWithNetworkDomain());

			List<Device> devices = NetworkModelHelper.getDevices(networkModel.getNetworkElements());
			Assert.assertNotNull(devices);
			Assert.assertEquals(devices.size(), 3);

			Assert.assertNotNull(devices);
			Assert.assertEquals(devices.get(0).getName(), "router:R-AS2-1");
			Assert.assertNotNull(devices.get(0).getInterfaces());
			Assert.assertEquals(devices.get(0).getInterfaces().size(), 3);
			Assert.assertEquals(devices.get(0).getInterfaces().get(0).getName(), "router:R-AS2-1:lt-1/2/0.51");
			Assert.assertEquals(devices.get(0).getInterfaces().get(1).getName(), "router:R-AS2-1:lt-1/2/0.100");
			Assert.assertEquals(devices.get(0).getInterfaces().get(2).getName(), "router:R-AS2-1:lo0.1");

			Assert.assertEquals(devices.get(1).getName(), "router:R-AS2-2");
			Assert.assertNotNull(devices.get(1).getInterfaces());
			Assert.assertEquals(devices.get(1).getInterfaces().size(), 3);
			Assert.assertEquals(devices.get(1).getInterfaces().get(0).getName(), "router:R-AS2-2:lt-1/2/0.102");
			Assert.assertEquals(devices.get(1).getInterfaces().get(1).getName(), "router:R-AS2-2:lt-1/2/0.101");
			Assert.assertEquals(devices.get(1).getInterfaces().get(2).getName(), "router:R-AS2-2:lo0.3");

			Assert.assertEquals(devices.get(2).getName(), "router:R-AS2-3");
			Assert.assertNotNull(devices.get(2).getInterfaces());
			Assert.assertEquals(devices.get(2).getInterfaces().size(), 2);
			Assert.assertEquals(devices.get(2).getInterfaces().get(0).getName(), "router:R-AS2-3:lt-1/2/0.103");
			Assert.assertEquals(devices.get(2).getInterfaces().get(1).getName(), "router:R-AS2-3:lo0.4");

		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testMockMapperNetworkDomain() {
		try {

			NetworkModel networkModel = NetworkMapperDescriptorToModel.descriptorToModel(MockNetworkDescriptor
					.newNetworkDescriptorWithNetworkDomain());

			List<NetworkDomain> networkDomains = NetworkModelHelper.getDomains(networkModel.getNetworkElements());
			Assert.assertNotNull(networkDomains);
			Assert.assertEquals(networkDomains.size(), 1);
			NetworkDomain networkDomain = (NetworkDomain) networkDomains.get(0);

			Assert.assertEquals(networkDomain.getHasDevice().size(), 3);

		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		}

	}
}
