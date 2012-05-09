package org.opennaas.extensions.network.repository.tests;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.extensions.network.mock.MockNetworkModel;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.network.repository.NetworkMapperDescriptorToModel;
import org.opennaas.extensions.network.repository.NetworkMapperModelToDescriptor;

public class NetworkDescriptorToMapperModelTest {

	@Test
	public void testModelToDescriptorWithMockModel() {

		NetworkTopology networkTopology = NetworkMapperModelToDescriptor.modelToDescriptor(MockNetworkModel.newNetworkModel());

		assertNotNull(networkTopology.getNetworkDomains());
		assertEquals(networkTopology.getNetworkDomains().size(), 1);

		/* network description */
		assertNotNull(networkTopology.getDevices());
		assertEquals(networkTopology.getDevices().get(0).getName(), "router:R-AS2-1");
		assertEquals(networkTopology.getDevices().get(1).getName(), "router:R-AS2-2");
		assertEquals(networkTopology.getDevices().get(2).getName(), "router:R-AS2-3");

		// notice that only references to topology elements start with #, names does not
		assertNotNull(networkTopology.getDevices());
		assertEquals(networkTopology.getDevices().get(0).getName(), "router:R-AS2-1");
		assertNotNull(networkTopology.getDevices().get(0).getHasInterfaces());
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().size(), 3);
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().get(0).getResource(), "#router:R-AS2-1:lt-1/2/0.51");
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().get(1).getResource(), "#router:R-AS2-1:lt-1/2/0.100");
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().get(2).getResource(), "#router:R-AS2-1:lo0.1");

		assertEquals(networkTopology.getDevices().get(1).getName(), "router:R-AS2-2");
		assertNotNull(networkTopology.getDevices().get(1).getHasInterfaces());
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().size(), 3);
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().get(0).getResource(), "#router:R-AS2-2:lt-1/2/0.102");
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().get(1).getResource(), "#router:R-AS2-2:lt-1/2/0.101");
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().get(2).getResource(), "#router:R-AS2-2:lo0.3");

		assertEquals(networkTopology.getDevices().get(2).getName(), "router:R-AS2-3");
		assertNotNull(networkTopology.getDevices().get(2).getHasInterfaces());
		assertEquals(networkTopology.getDevices().get(2).getHasInterfaces().size(), 2);
		assertEquals(networkTopology.getDevices().get(2).getHasInterfaces().get(0).getResource(), "#router:R-AS2-3:lt-1/2/0.103");
		assertEquals(networkTopology.getDevices().get(2).getHasInterfaces().get(1).getResource(), "#router:R-AS2-3:lo0.4");

		assertNotNull(networkTopology.getInterfaces());
		assertEquals(networkTopology.getInterfaces().size(), 9);

		assertEquals(networkTopology.getInterfaces().get(0).getName(), "router:R-AS2-1:lt-1/2/0.51");
		assertEquals(networkTopology.getInterfaces().get(0).getLinkTo().getName(), "#router:R1:lt-1/2/0.50");

		assertEquals(networkTopology.getInterfaces().get(1).getName(), "router:R-AS2-1:lt-1/2/0.100");
		assertEquals(networkTopology.getInterfaces().get(1).getLinkTo().getName(), "#router:R-AS2-2:lt-1/2/0.101");

		assertEquals(networkTopology.getInterfaces().get(2).getName(), "router:R-AS2-1:lo0.1");

		assertEquals(networkTopology.getInterfaces().get(3).getName(), "router:R-AS2-2:lt-1/2/0.102");
		assertEquals(networkTopology.getInterfaces().get(3).getLinkTo().getName(), "#router:R-AS2-3:lt-1/2/0.103");

		assertEquals(networkTopology.getInterfaces().get(4).getName(), "router:R-AS2-2:lt-1/2/0.101");
		assertEquals(networkTopology.getInterfaces().get(4).getLinkTo().getName(), "#router:R-AS2-1:lt-1/2/0.100");

		assertEquals(networkTopology.getInterfaces().get(5).getName(), "router:R-AS2-2:lo0.3");

		assertEquals(networkTopology.getInterfaces().get(6).getName(), "router:R-AS2-3:lt-1/2/0.103");
		assertEquals(networkTopology.getInterfaces().get(6).getLinkTo().getName(), "#router:R-AS2-2:lt-1/2/0.102");

		assertEquals(networkTopology.getInterfaces().get(7).getName(), "router:R-AS2-3:lo0.4");

	}

	@Test
	public void testModelToDescriptorAndViceversa() {

		try {
			NetworkModel model1 = MockNetworkModel.newNetworkModel();
			NetworkTopology topology1 = NetworkMapperModelToDescriptor.modelToDescriptor(model1);
			NetworkModel model2 = NetworkMapperDescriptorToModel.descriptorToModel(topology1);
			NetworkTopology topology2 = NetworkMapperModelToDescriptor.modelToDescriptor(model2);

			checkModels(model1, model2);
			Assert.assertEquals(topology1, topology2);

		} catch (ResourceException e) {
			Assert.fail("Error mapping descriptor to model: " + e.getMessage());
		}

	}

	private void checkModels(NetworkModel model1, NetworkModel model2) {

		Assert.assertEquals(model1.getNetworkElements().size(), model2.getNetworkElements().size());
		for (NetworkElement elem1 : model1.getNetworkElements()) {

			List<NetworkElement> tmp_elems = NetworkModelHelper.getNetworkElementsByClassName(elem1.getClass(), model2.getNetworkElements());
			Assert.assertFalse(tmp_elems.isEmpty());

			if (elem1.getName() != null) { // should be elements without name?? Links has no name, by now.
				int elemIndexInModel2 = NetworkModelHelper.getNetworkElementByName(elem1.getName(), tmp_elems);
				Assert.assertFalse("Elem " + elem1.getName() + " of model1 is not in model2.", elemIndexInModel2 == -1);
			}
		}

		for (Interface iface1 : NetworkModelHelper.getInterfaces(model1.getNetworkElements())) {
			Interface iface2 = NetworkModelHelper.getInterfaceByName(model2.getNetworkElements(), iface1.getName());
			Assert.assertNotNull("Interface " + iface1.getName() + " is not in model2", iface2);

			if (iface1.getLinkTo() != null) {
				Assert.assertNotNull(iface2.getLinkTo());
				Assert.assertEquals(iface1.getLinkTo().getName(), iface2.getLinkTo().getName());
			}

			if (iface1.getSwitchedTo() != null) {
				Assert.assertNotNull(iface2.getSwitchedTo());
				Assert.assertEquals(iface1.getSwitchedTo().getName(), iface2.getSwitchedTo().getName());
			}
		}
	}

}
