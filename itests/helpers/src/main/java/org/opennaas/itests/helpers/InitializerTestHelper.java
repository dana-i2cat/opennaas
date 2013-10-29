package org.opennaas.itests.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.ProxyClassLoader;
import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class InitializerTestHelper {

	/**
	 * Stops all resources in give resourceManager
	 * 
	 * @param resourceManager
	 * @throws ResourceException
	 *             if fails to stop a resource.
	 */
	public static void stopResources(IResourceManager resourceManager) throws ResourceException {

		for (IResource resource : resourceManager.listResources()) {
			resourceManager.stopResource(resource.getResourceIdentifier());
		}
	}

	/**
	 * Remove all resources from given resourceManager.
	 * 
	 * It stops active resources prior removing them.
	 * 
	 * @param resourceManager
	 * @throws ResourceException
	 *             if fails to remove (or stop) a resource.
	 */
	public static void removeResources(IResourceManager resourceManager) throws ResourceException {
		List<IResource> resources = resourceManager.listResources();
		for (int i = resources.size() - 1; i >= 0; i--) {
			IResource resource = resources.get(i);
			if (resource.getState().equals(ILifecycle.State.ACTIVE))
				resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	public static IResource initResource(String name, String type, ArrayList<String> capabilitiesId, IResourceManager resourceManager,
			IProtocolManager protocolManager, ProtocolSessionContext context) throws Exception {
		IResource resource = null;

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory
				.newResourceDescriptor(name, type, capabilitiesId);

		resource = resourceManager.createResource(resourceDescriptor);

		// TODO Check this identifier
		IProtocolSessionManager protocolSessionManager = protocolManager
				.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(), context);

		return resource;

	}

	public static int containsCapability(IResource resource, String idCapability) {

		int pos = 0;
		for (ICapability capability : resource.getCapabilities()) {
			if (capability.getCapabilityInformation().getType().equals(idCapability)) {
				return pos;
			}
			pos++;
		}
		return -1;

	}

	public static Information getCapabilityInformation(String type) {
		Information information = new Information();
		information.setType(type);
		return information;
	}

	public static IProtocolSessionManager addSessionContext(IProtocolManager protocolManager, String resourceId, String resourceURI)
			throws ProtocolException {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, resourceURI);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		protocolSessionContext.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");

		protocolSessionManager.registerContext(protocolSessionContext);

		return protocolSessionManager;
	}

	/**
	 * 
	 * @param protocolManager
	 * @param resourceId
	 * @param resourceURI
	 * @param protocol
	 * @param sessionParameters
	 *            ProtocolSessionContext.AUTH_TYPE and other session parameters
	 * @return
	 * @throws ProtocolException
	 */
	public static IProtocolSessionManager addSessionContextWithSessionParams(IProtocolManager protocolManager, String resourceId, String resourceURI,
			String protocol, Map<String, Object> sessionParameters) throws ProtocolException {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, resourceURI);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				protocol);

		protocolSessionContext.getSessionParameters().putAll(sessionParameters);

		protocolSessionManager.registerContext(protocolSessionContext);

		return protocolSessionManager;
	}

	/**
	 * Creates a JAXRSClient with given clientInterface.
	 * 
	 * @param uri
	 *            the URI where the service is running
	 * @param clientInterface
	 *            interface class the client should has.
	 * @param providers
	 *            custom JAX-RS providers
	 * @param username
	 *            Basic authentication username
	 * @param password
	 *            Basic authentication password
	 * @return JAX-RX Client configured with given parameters.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createRestClient(String uri, Class<T> clientInterface, List<? extends Object> providers, String username, String password) {

		ProxyClassLoader classLoader = new ProxyClassLoader();
		classLoader.addLoader(clientInterface.getClassLoader());
		classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(uri);
		if (providers != null && !providers.isEmpty())
			bean.setProviders(providers);
		bean.setResourceClass(clientInterface);
		bean.setClassLoader(classLoader);
		if (username != null && password != null) {
			bean.setUsername(username);
			bean.setPassword(password);
		}

		return (T) bean.create();
	}

}
