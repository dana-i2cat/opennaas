/**
 * 
 */
package org.opennaas.web.services;

import java.util.List;

import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.web.entities.VCPENetwork;

/**
 * @author Jordi
 */
public class ResourceService extends GenericService {

	/**
	 * Call a rest url to get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 */
	@SuppressWarnings("unchecked")
	public List<VCPENetwork> getAll() {
		String url = getURL("resources/getResources");
		return (List<VCPENetwork>) opennaasRest.post(url, List.class);
	}

	/**
	 * Call a rest url to stop a resource
	 * 
	 * @param id
	 */
	public void stop(String id) {
		String url = getURL("resources/stop/" + id);
		opennaasRest.post(url);
	}

	/**
	 * Call a rest url to start a resource
	 * 
	 * @param id
	 */
	public void start(String id) {
		String url = getURL("resources/start/" + id);
		opennaasRest.post(url);
	}

	/**
	 * Call a rest url to create a resource
	 * 
	 * @param descriptor
	 * @return id
	 */
	public String create(ResourceDescriptor descriptor) {
		String url = getURL("resources/create");
		String id = (String) opennaasRest
				.post(url, descriptor, String.class);
		return id;
	}

	/**
	 * Call a rest url to update a resource
	 * 
	 * @param descriptor
	 * @return id
	 */
	public String update(ResourceDescriptor descriptor) {
		String url = getURL("resources/modify");
		return (String) opennaasRest.post(url, descriptor, String.class);
	}

	/**
	 * Call a rest url to delete a resource
	 * 
	 * @param id
	 */
	public void delete(String id) {
		String url = getURL("resources/delete/" + id);
		opennaasRest.post(url);
	}

	/**
	 * Call a rest url to get a resource by id = id
	 * 
	 * @param id
	 * @return VCPENetwork
	 */
	public VCPENetwork getById(String id) {
		String url = getURL("resources/getResourceById/" + id);
		return opennaasRest.post(url).getEntity(VCPENetwork.class);
	}
}
