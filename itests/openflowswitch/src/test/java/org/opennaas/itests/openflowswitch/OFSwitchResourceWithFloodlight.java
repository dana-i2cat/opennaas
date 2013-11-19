package org.opennaas.itests.openflowswitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.osgi.framework.BundleContext;

public abstract class OFSwitchResourceWithFloodlight {

	@Inject
	protected IResourceManager			resourceManager;

	@Inject
	protected IProtocolManager			protocolManager;

	@Inject
	protected BundleContext				context;

	protected IResource					ofSwitchResource;
	protected WSEndpointListenerHandler	listenerHandler;

	private final static String			RESOURCE_INFO_NAME	= "OpenflowSwitch";

	private static final String			ACTIONSET_NAME		= "floodlight";
	private static final String			ACTIONSET_VERSION	= "0.90";
	private static final String			MOCK_URI			= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String			RESOURCE_TYPE		= "openflowswitch";

	private static final String			PROTOCOL			= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;
	private static final String			SWITCH_ID_NAME		= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;

	protected void startResource(String serverURL, String switchId) throws ResourceException, ProtocolException, InterruptedException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ofForwardingDescriptor = ResourceHelper.newCapabilityDescriptor(ACTIONSET_NAME,
				ACTIONSET_VERSION,
				OpenflowForwardingCapability.CAPABILITY_TYPE,
				MOCK_URI);
		lCapabilityDescriptors.add(ofForwardingDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE,
				MOCK_URI, RESOURCE_INFO_NAME);

		ofSwitchResource = resourceManager.createResource(resourceDescriptor);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(SWITCH_ID_NAME, switchId);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, ofSwitchResource.getResourceIdentifier().getId(), serverURL,
				PROTOCOL, sessionParameters);

		// Start resource
		listenerHandler = new WSEndpointListenerHandler();
		listenerHandler.registerWSEndpointListener(context, IOpenflowForwardingCapability.class);
		resourceManager.startResource(ofSwitchResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBePublished();

	}

	protected void stopResource() throws ResourceException, InterruptedException {
		resourceManager.stopResource(ofSwitchResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBeUnpublished();
		resourceManager.removeResource(ofSwitchResource.getResourceIdentifier());
	}

	protected ICapability getCapability(Class<? extends ICapability> clazz) throws ResourceException {
		ICapability capab = ofSwitchResource.getCapabilityByInterface(clazz);
		Assert.assertNotNull(capab);
		return capab;
	}

}
