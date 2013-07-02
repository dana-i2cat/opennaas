package org.opennaas.extensions.quantum.capability.apiv2;

import java.io.IOException;
import java.util.Dictionary;
import java.util.List;

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
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.Port;
import org.opennaas.extensions.quantum.model.QuantumModel;
import org.opennaas.extensions.quantum.model.QuantumModelController;
import org.opennaas.extensions.quantum.model.Subnet;
import org.opennaas.extensions.quantum.model.helper.QuantumModelHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Julio Carlos Barrera
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
				props.put("org.apache.cxf.rs.httpservice.context", "/");
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
	public List<Network> listNetworks() {
		log.debug("Quantum API - listNetworks()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSampleQuantumModel().getNetworks();
	}

	@Override
	public Network createNetwork(Network network) throws CapabilityException {

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

		log.info("Quantum API - Network created.");

		return network;

	}

	@Override
	public Network showNetwork(@PathParam("network_id") String networkId) {
		log.debug("Quantum API - showNetwork()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSampleNetwork(0);
	}

	@Override
	public Network updateNetwork(@PathParam("network_id") String networkId, Network updatedNetwork) {
		log.debug("Quantum API - updateNetwork()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSampleNetwork(0);
	}

	@Override
	public void deleteNetwork(@PathParam("network_id") String networkId) {
		log.debug("Quantum API - deleteNetwork()");
		// TODO Auto-generated method stub
	}

	// PORTS CRUD

	@Override
	public List<Port> listPorts() {
		log.debug("Quantum API - listPorts()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSampleQuantumModel().getNetworks().get(0).getPorts();
	}

	@Override
	public Port createPort(Port port) {
		log.debug("Quantum API - createPort()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSamplePort(0, 0);
	}

	@Override
	public Port showPort(@PathParam("port_id") String portId) {
		log.debug("Quantum API - showPort()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSamplePort(0, 0);
	}

	@Override
	public Port updatePort(@PathParam("port_id") String portId, Port updatedPort) {
		log.debug("Quantum API - updatePort()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSamplePort(0, 0);
	}

	@Override
	public void removePort(@PathParam("port_id") String portId) {
		log.debug("Quantum API - removePort()");
		// TODO Auto-generated method stub
	}

	// SUBNETS CRUD

	@Override
	public List<Subnet> listSubnets() {
		log.debug("Quantum API - listSubnets()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSampleQuantumModel().getNetworks().get(0).getSubnets();
	}

	@Override
	public Subnet createSubnet(Subnet subnet) {
		log.debug("Quantum API - createSubnet()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSampleSubnet(0, 0);
	}

	@Override
	public Subnet showSubnet(@PathParam("subnet_id") String subnetId) {
		log.debug("Quantum API - showSubnet()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSampleSubnet(0, 0);
	}

	@Override
	public Subnet updateSubnet(@PathParam("subnet_id") String subnetId, Subnet updatedSubnet) {
		log.debug("Quantum API - updateSubnet()");
		// TODO Auto-generated method stub
		return QuantumModelHelper.generateSampleSubnet(0, 0);
	}

	@Override
	public void removeSubnet(@PathParam("subnet_id") String subnetId) {
		log.debug("Quantum API - removeSubnet()");
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
