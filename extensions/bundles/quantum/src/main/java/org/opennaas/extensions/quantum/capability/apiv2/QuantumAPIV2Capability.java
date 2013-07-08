package org.opennaas.extensions.quantum.capability.apiv2;

import java.io.IOException;
import java.util.Dictionary;

import javax.ws.rs.PathParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.quantum.Activator;
import org.opennaas.extensions.quantum.QuantumException;
import org.opennaas.extensions.quantum.capability.extensions.l3.shell.IQuantumL3Capability;
import org.opennaas.extensions.quantum.model.Attachment;
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.Port;
import org.opennaas.extensions.quantum.model.QuantumModel;
import org.opennaas.extensions.quantum.model.QuantumModelController;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Julio Carlos Barrera
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class QuantumAPIV2Capability extends AbstractCapability implements IQuantumAPIV2Capability, IQuantumL3Capability {

	public static final String		CAPABILITY_TYPE	= "quantum-apiv2";

	private Log						log				= LogFactory.getLog(QuantumAPIV2Capability.class);

	private String					resourceId		= "";

	private QuantumModelController	controller;

	public QuantumAPIV2Capability(CapabilityDescriptor descriptor, String resourceId) {

		super(descriptor);
		this.resourceId = resourceId;
		controller = new QuantumModelController();

		log.debug("Built new Quantum Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IQuantumAPIV2Capability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getQuantumAPIV2ActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * Register the capability like a web service through DOSGi
	 *
	 * @param name
	 * @param resourceId
	 * @return
	 * @throws CapabilityException
	 */
	protected ServiceRegistration registerService(BundleContext bundleContext, String capabilityName, String resourceType, String resourceName,
			String ifaceName, Dictionary<String, String> props) throws CapabilityException {
		try {
			ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(bundleContext);
			String url = configurationAdmin.getProperty("org.opennaas", "ws.rest.url");
			if (props != null) {
				// Rest
				props.put("service.exported.interfaces", "*");
				props.put("service.exported.configs", "org.apache.cxf.rs");
				props.put("service.exported.intents", "HTTP");
				// props.put("org.apache.cxf.rs.httpservice.context", url + "/" + resourceType + "/" + resourceName + "/" + capabilityName);
				props.put("org.apache.cxf.rs.httpservice.context", "/networkService/v1.1");
				props.put("org.apache.cxf.rs.address", "/");
				props.put("org.apache.cxf.httpservice.requirefilter", "false");
				// JSON provider
				props.put("org.apache.cxf.rs.provider", "org.opennaas.extensions.quantum.utils.CustomJSONProvider");
			}
			log.info("Registering ws: \n " +
					"in url: " + props.get("org.apache.cxf.rs.address") + "\n" +
					"in context: " + props.get("org.apache.cxf.rs.httpservice.context"));
			registration = bundleContext.registerService(ifaceName, this, props);
		} catch (IOException e) {
			throw new CapabilityException(e);
		}
		return registration;
	}

	// NETWORKS CRUD

	@Override
	public Network createNetwork(String tenantId, Network network) throws CapabilityException {

		log.info("Quantum API - Create Network request received.");

		try {

			IResource quantumResource = getResource();
			QuantumModel quantumModel = (QuantumModel) quantumResource.getModel();

			controller.addNetwork(quantumModel, network);

		} catch (ActivatorException ae) {
			log.error("Error creating Quantum network - ", ae);
			throw new CapabilityException(ae);
		} catch (ResourceException re) {
			log.error("Error creating Quantum network - ", re);
			throw new CapabilityException(re);
		} catch (QuantumException qe) {
			throw new CapabilityException(qe);
		}

		log.info("Quantum API - Network " + network.getId() + " created.");

		return network;

	}

	@Override
	public Network updateNetwork(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, Network updatedNetwork)
			throws CapabilityException {
		log.info("Quantum API - Update network " + networkId + " request received.");
		try {

			IResource quantumResource = getResource();
			QuantumModel quantumModel = (QuantumModel) quantumResource.getModel();

			controller.updateNetwork(networkId, quantumModel, updatedNetwork);

		} catch (ActivatorException ae) {
			log.error("Error creating Quantum network - ", ae);
			throw new CapabilityException(ae);
		} catch (ResourceException re) {
			log.error("Error creating Quantum network - ", re);
			throw new CapabilityException(re);
		} catch (QuantumException qe) {
			throw new CapabilityException(qe);
		}

		return updatedNetwork;
	}

	@Override
	public void deleteNetwork(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId) throws CapabilityException {
		log.info("Quantum API - Delete network " + networkId + " request received");

		try {

			IResource quantumResource = getResource();
			QuantumModel quantumModel = (QuantumModel) quantumResource.getModel();

			controller.removeNetwork(quantumModel, networkId);

		} catch (ActivatorException ae) {
			log.error("Error creating Quantum network - ", ae);
			throw new CapabilityException(ae);
		} catch (ResourceException re) {
			log.error("Error creating Quantum network - ", re);
			throw new CapabilityException(re);
		} catch (QuantumException qe) {
			log.error("Error creating Quantum network - ", qe);
			throw new CapabilityException(qe);
		}

		log.info("Quantum API - Network " + networkId + " removed.");
	}

	// PORTS CRUD

	@Override
	public Port createPort(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, Port port) throws CapabilityException {
		log.info("Quantum API - Creating port request received.");

		try {

			IResource quantumResource = getResource();
			QuantumModel quantumModel = (QuantumModel) quantumResource.getModel();

			controller.createPort(quantumModel, networkId, port);

		} catch (ActivatorException ae) {
			log.error("Error updating Quantum port - ", ae);
			throw new CapabilityException(ae);
		} catch (ResourceException re) {
			log.error("Error updating Quantum port - ", re);
			throw new CapabilityException(re);
		} catch (QuantumException qe) {
			log.error("Error updating Quantum port - ", qe);
			throw new CapabilityException(qe);
		}

		log.info("Quantum API - Port created in Quantum model.");

		return port;
	}

	@Override
	public Port updatePort(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, @PathParam("port_id") String portId,
			Port updatedPort) throws CapabilityException {

		log.info("Quantum API - Updating port " + portId + " from network " + networkId);

		try {

			IResource quantumResource = getResource();
			QuantumModel quantumModel = (QuantumModel) quantumResource.getModel();

			controller.updatePort(quantumModel, networkId, updatedPort);

		} catch (ActivatorException ae) {
			log.error("Error updating Quantum port - ", ae);
			throw new CapabilityException(ae);
		} catch (ResourceException re) {
			log.error("Error updating Quantum port - ", re);
			throw new CapabilityException(re);
		} catch (QuantumException qe) {
			log.error("Error updating Quantum port - ", qe);
			throw new CapabilityException(qe);
		}

		log.info("Quantum API - Port updated.");

		return updatedPort;
	}

	@Override
	public void removePort(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, @PathParam("port_id") String portId)
			throws CapabilityException {

		log.info("Quantum API - Remove port request received");

		try {

			IResource quantumResource = getResource();
			QuantumModel quantumModel = (QuantumModel) quantumResource.getModel();

			controller.removePort(quantumModel, networkId, portId);

		} catch (ActivatorException ae) {
			log.error("Error updating Quantum port - ", ae);
			throw new CapabilityException(ae);
		} catch (ResourceException re) {
			log.error("Error updating Quantum port - ", re);
			throw new CapabilityException(re);
		} catch (QuantumException qe) {
			log.error("Error updating Quantum port - ", qe);
			throw new CapabilityException(qe);
		}

		log.info("Quantum API - Port removed.");
	}

	@Override
	public Attachment createAttachment(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId,
			@PathParam("port_id") String portId, Attachment attachment) throws CapabilityException {
		// TODO Auto-generated method stub

		return attachment;
	}

	@Override
	public Attachment updateAttachment(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId,
			@PathParam("port_id") String portId, Attachment attachment) throws CapabilityException {
		log.info("Quantum API - Creating attachment for port " + portId);

		try {

			IResource quantumResource = getResource();
			QuantumModel quantumModel = (QuantumModel) quantumResource.getModel();

			controller.updateAttachment(quantumModel, networkId, portId, attachment);

		} catch (ActivatorException ae) {
			log.error("Error updating Quantum port - ", ae);
			throw new CapabilityException(ae);
		} catch (ResourceException re) {
			log.error("Error updating Quantum port - ", re);
			throw new CapabilityException(re);
		} catch (QuantumException qe) {
			log.error("Error updating Quantum port - ", qe);
			throw new CapabilityException(qe);
		}

		log.info("Quantum API - Attachment created");

		return attachment;
	}

	@Override
	public void removeAttachment(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId,
			@PathParam("port_id") String portId, @PathParam("attachment_id") String attachmentId) {
		// TODO Auto-generated method stub

	}

	private IResource getResource() throws ResourceException, ActivatorException {
		IResourceManager resourceManager = getResourceManager();
		return resourceManager.getResourceById(resourceId);
	}

	private IResourceManager getResourceManager() throws ActivatorException {
		return Activator.getResourceManagerService();
	}

}
