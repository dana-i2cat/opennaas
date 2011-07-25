import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.repository.MantychoreBootstrapper;
import net.i2cat.mantychore.repository.MantychoreBootstrapperFactory;
import net.i2cat.mantychore.repository.MantychoreRepository;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;

import org.junit.Assert;
import org.junit.Test;

public class MantychoreBootstrapperTest {

	MantychoreRepository	mantychoreRepository;
	String					resourceType	= "router";
	String					persistenceUnit	= "ResourceCore";

	@Test
	public void bootstrapperExecutesCapabilitiesStartUp() {

		// initialize mock
		CapabilityDescriptor mockCapabilityDescriptor = MockResource.createCapabilityDescriptor("capab1");
		CapabilityDescriptor mockQueueCapabilityDescriptor = MockResource.createCapabilityDescriptor("queue");

		ICapability factory1Capability = null;
		ICapability queueCapability = null;
		try {
			factory1Capability = new MockCapabilityFactory("capab1").create(mockCapabilityDescriptor, "001");
			queueCapability = new MockQueueCapabilityFactory("queue").create(mockQueueCapabilityDescriptor, "001");
		} catch (CapabilityException e1) {
			e1.printStackTrace();
			Assert.fail(e1.getLocalizedMessage());
		}

		List<ICapability> capabilities = new ArrayList<ICapability>();
		capabilities.add(factory1Capability);
		capabilities.add(queueCapability);

		IResource mockResource = createMock(IResource.class);
		mockResource.setModel((ManagedElement) anyObject());
		expect(mockResource.getCapabilities()).andReturn(capabilities);
		expect(mockResource.getProfile()).andReturn(null);
		expect(mockResource.getModel()).andReturn(new ComputerSystem()).anyTimes();
		replay(mockResource);

		// start test

		MantychoreBootstrapperFactory factory = new MantychoreBootstrapperFactory();
		MantychoreBootstrapper bootstrapper = (MantychoreBootstrapper) factory.createResourceBootstrapper();

		try {
			bootstrapper.bootstrap(mockResource);
		} catch (ResourceException e1) {
			e1.printStackTrace();
			Assert.fail(e1.getLocalizedMessage());
		}

		verify(mockResource);

		Assert.assertTrue(((MockCapability) capabilities.get(0)).sentStartUp); // factory1
		Assert.assertTrue(((MockQueueCapability) capabilities.get(1)).sentMessage); // queue

	}

	private ResourceDescriptor newResourceDescriptor(String type, String name) {

		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

		Map<String, String> properties = new HashMap<String, String>();

		/* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		/* factory1 descriptor */
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor("factory1"));

		/* factory2 descriptor */
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor("queue"));

		resourceDescriptor.setProperties(properties);
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		resourceDescriptor.setId("JunosTest");
		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(type);
		information.setName(name);
		resourceDescriptor.setInformation(information);
		return resourceDescriptor;
	}

}
