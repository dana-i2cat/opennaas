package net.i2cat.mantychore.network.repository.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.i2cat.mantychore.network.model.MockNetworkModel;
import net.i2cat.mantychore.network.repository.NetworkMapperModelToDescriptor;

import org.junit.Test;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;

public class NetworkDescriptorToMapperModelTest {

	@Test
	public void testMockMapperModel() {

		NetworkTopology networkTopology = NetworkMapperModelToDescriptor.modelToDescriptor(MockNetworkModel.newNetworkModel());

		assertNotNull(networkTopology.getNetworkDomains());
		assertEquals(networkTopology.getNetworkDomains().size(), 1);

		/* network description */
		assertNotNull(networkTopology.getDevices());
		assertEquals(networkTopology.getDevices().get(0).getName(), "#router:R-AS2-1");
		assertEquals(networkTopology.getDevices().get(1).getName(), "#router:R-AS2-2");
		assertEquals(networkTopology.getDevices().get(2).getName(), "#router:R-AS2-3");

		assertNotNull(networkTopology.getDevices());
		assertEquals(networkTopology.getDevices().get(0).getName(), "#router:R-AS2-1");
		assertNotNull(networkTopology.getDevices().get(0).getHasInterfaces());
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().size(), 3);
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().get(0).getResource(), "#router:R-AS2-1:lt-1/2/0.51");
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().get(1).getResource(), "#router:R-AS2-1:lt-1/2/0.100");
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().get(2).getResource(), "#router:R-AS2-1:lo0.1");

		assertEquals(networkTopology.getDevices().get(1).getName(), "#router:R-AS2-2");
		assertNotNull(networkTopology.getDevices().get(1).getHasInterfaces());
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().size(), 3);
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().get(0).getResource(), "#router:R-AS2-2:lt-1/2/0.102");
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().get(1).getResource(), "#router:R-AS2-2:lt-1/2/0.101");
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().get(2).getResource(), "#router:R-AS2-2:lo0.3");

		assertEquals(networkTopology.getDevices().get(2).getName(), "#router:R-AS2-3");
		assertNotNull(networkTopology.getDevices().get(2).getHasInterfaces());
		assertEquals(networkTopology.getDevices().get(2).getHasInterfaces().size(), 2);
		assertEquals(networkTopology.getDevices().get(2).getHasInterfaces().get(0).getResource(), "#router:R-AS2-3:lt-1/2/0.103");
		assertEquals(networkTopology.getDevices().get(2).getHasInterfaces().get(1).getResource(), "#router:R-AS2-3:lo0.4");

		assertNotNull(networkTopology.getInterfaces());
		assertEquals(networkTopology.getInterfaces().size(), 9);

		assertEquals(networkTopology.getInterfaces().get(0).getName(), "#router:R-AS2-1:lt-1/2/0.51");
		assertEquals(networkTopology.getInterfaces().get(0).getLinkTo().getName(), "#router:R1:lt-1/2/0.50");

		assertEquals(networkTopology.getInterfaces().get(1).getName(), "#router:R-AS2-1:lt-1/2/0.100");
		assertEquals(networkTopology.getInterfaces().get(1).getLinkTo().getName(), "#router:R-AS2-2:lt-1/2/0.101");

		assertEquals(networkTopology.getInterfaces().get(2).getName(), "#router:R-AS2-1:lo0.1");

		assertEquals(networkTopology.getInterfaces().get(3).getName(), "#router:R-AS2-2:lt-1/2/0.102");
		assertEquals(networkTopology.getInterfaces().get(3).getLinkTo().getName(), "#router:R-AS2-3:lt-1/2/0.103");

		assertEquals(networkTopology.getInterfaces().get(4).getName(), "#router:R-AS2-2:lt-1/2/0.101");
		assertEquals(networkTopology.getInterfaces().get(4).getLinkTo().getName(), "#router:R-AS2-1:lt-1/2/0.100");

		assertEquals(networkTopology.getInterfaces().get(5).getName(), "#router:R-AS2-2:lo0.3");

		assertEquals(networkTopology.getInterfaces().get(6).getName(), "#router:R-AS2-3:lt-1/2/0.103");
		assertEquals(networkTopology.getInterfaces().get(6).getLinkTo().getName(), "#router:R-AS2-2:lt-1/2/0.102");

		assertEquals(networkTopology.getInterfaces().get(7).getName(), "#router:R-AS2-3:lo0.4");

	}

}
