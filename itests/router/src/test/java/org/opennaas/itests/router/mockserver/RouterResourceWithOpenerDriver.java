package org.opennaas.itests.router.mockserver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.opennaas.core.endpoints.WSEndpointListenerHandler;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.router.TestsConstants;
import org.osgi.framework.BundleContext;

public class RouterResourceWithOpenerDriver {

	@Inject
	protected IResourceManager			resourceManager;

	@Inject
	protected IProtocolManager			protocolManager;

	@Inject
	protected BundleContext				context;

	protected IResource					routerResource;
	protected WSEndpointListenerHandler	listenerHandler;

	private final static String			RESOURCE_INFO_NAME	= "Router with opener driver";

	public void startResource(String serverURL) throws ResourceException, ProtocolException, InterruptedException {

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor chassisCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.OPENER_ACTIONSET_NAME,
				TestsConstants.OPENER_ACTIONSET_VERSION,
				TestsConstants.CHASSIS_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(chassisCapabilityDescriptor);

		CapabilityDescriptor ipCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.OPENER_ACTIONSET_NAME,
				TestsConstants.OPENER_ACTIONSET_VERSION,
				TestsConstants.IP_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(ipCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor(TestsConstants.OPENER_ACTIONSET_NAME,
				TestsConstants.OPENER_ACTIONSET_VERSION);
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, TestsConstants.RESOURCE_TYPE,
				TestsConstants.RESOURCE_URI,
				RESOURCE_INFO_NAME);

		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContext(protocolManager, routerResource.getResourceIdentifier().getId(), serverURL,
				TestsConstants.OPENER_PROTOCOL, "noauth");

		// Start resource

		listenerHandler = new WSEndpointListenerHandler();
		listenerHandler.registerWSEndpointListener(context, IChassisCapability.class);
		resourceManager.startResource(routerResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBePublished();

	}

	protected void stopResource() throws ResourceException, InterruptedException {
		resourceManager.stopResource(routerResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBeUnpublished();
		resourceManager.removeResource(routerResource.getResourceIdentifier());

	}

	protected IQueueManagerCapability getQueue() throws ResourceException {

		IQueueManagerCapability queue = (IQueueManagerCapability) routerResource.getCapabilityByInterface(IQueueManagerCapability.class);
		Assert.assertNotNull(queue);
		return queue;

	}

	protected ICapability getCapability(Class<? extends ICapability> clazz) throws ResourceException {
		ICapability capab = routerResource.getCapabilityByInterface(clazz);
		Assert.assertNotNull(capab);
		return capab;
	}
}
