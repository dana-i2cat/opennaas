package org.opennaas.client.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class ResourceManagerTest {

	private static final Logger	LOGGER	= Logger.getLogger(ResourceManagerTest.class);

	public static void main(String[] args) throws URISyntaxException {

		ResourceDescriptor desc1 = createResourceDescriptor("resource1");
		ResourceDescriptor desc2 = createResourceDescriptor("resource2");

		String resource1Id = createResource(desc1);
		String resource1Name = getResourceNameFromId(resource1Id);

		IResourceIdentifier identifier1 = new ResourceIdentifier(desc1.getInformation().getType(), resource1Id);

		startResource(identifier1);
		stopResource(identifier1);
		removeResource(identifier1);

		IResource resource1 = getResource(identifier1);
		listResources();
		getResourceDescriptors();
		getModel("084fe9f9-6e51-4e9b-9dce-bc03ceee5816");
	}

	private static void getResource(String id) throws URISyntaxException {
		Resource response = null;
		String url = "http://localhost:8888/opennaas/resources/getResource/" + id;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(Resource.class);
			LOGGER.info("resource: " + response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

	}

	private static void getModel(String id) throws URISyntaxException {
		String response = null;
		String url = "http://localhost:8888/opennaas/resources/getModel/" + id;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(String.class);
			LOGGER.info("resource: " + response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

	}

	/**
	 * 
	 */
	private static void getResourceDescriptors() {
		List<ResourceDescriptor> response = null;
		String url = "http://localhost:8888/opennaas/resources/getAllDescriptors/vcpenet";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(new GenericType<List<ResourceDescriptor>>() {
			});
			LOGGER.info("resourceId: " + response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static String createResource(ResourceDescriptor desc) {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/resources/create";
		IResource resource = null;
		String resourceId = null;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, desc);
			LOGGER.info("Response code: " + response.getStatus());
			resourceId = response.getEntity(String.class);
			LOGGER.info("resourceId: " + resourceId);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return resourceId;
	}

	private static String getResourceNameFromId(String resourceId) throws URISyntaxException {
		ClientResponse response = null;

		String methodPath = resourceId + "/name";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/resources/" + methodPath, null, null);
		String url = uri.toASCIIString();

		String resourceName = null;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
			resourceName = response.getEntity(String.class);
			LOGGER.info("resourceName: " + resourceName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return resourceName;
	}

	private static IResource modifyResource(IResourceIdentifier identifier, ResourceDescriptor desc) throws URISyntaxException {

		ClientResponse response = null;

		String methodPath = identifier.getId() + "/modify";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/resources/" + methodPath, null, null);
		String url = uri.toASCIIString();

		IResource resource = null;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, desc);
			LOGGER.info("Response code: " + response.getStatus());
			resource = response.getEntity(IResource.class);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return resource;
	}

	private static void startResource(IResourceIdentifier identifier) throws URISyntaxException {

		ClientResponse response = null;

		String methodPath = identifier.getId() + "/start";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/resources/" + methodPath, null, null);
		String url = uri.toASCIIString();

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static void stopResource(IResourceIdentifier identifier) throws URISyntaxException {
		ClientResponse response = null;

		String methodPath = identifier.getId() + "/stop";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/resources/" + methodPath, null, null);
		String url = uri.toASCIIString();

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static void removeResource(IResourceIdentifier identifier) throws URISyntaxException {
		ClientResponse response = null;

		String methodPath = identifier.getId() + "/remove";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/resources/" + methodPath, null, null);
		String url = uri.toASCIIString();

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).delete(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static List<IResource> listResources() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/resources/";

		GenericType<List<IResource>> genericType =
				new GenericType<List<IResource>>() {
				};

		List<IResource> resources = null;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
			resources = response.getEntity(genericType);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return resources;

	}

	private static void listResourceTypes() {

	}

	private static void listResourcesByType() {

	}

	private static IResource getResource(IResourceIdentifier identifier) throws URISyntaxException {

		ClientResponse response = null;
		String methodPath = identifier.getId();
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/resources/" + methodPath, null, null);
		String url = uri.toASCIIString();

		IResource resource = null;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
			resource = response.getEntity(IResource.class);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return resource;

	}

	private static ResourceDescriptor createResourceDescriptor(String resourceName) {

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor("junos", "10.10", "queue", ""));
		capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor("junos", "10.10", "chassis", ""));
		return ResourceHelper.newResourceDescriptor(capabilityDescriptors, "router", "", resourceName);
	}
}
