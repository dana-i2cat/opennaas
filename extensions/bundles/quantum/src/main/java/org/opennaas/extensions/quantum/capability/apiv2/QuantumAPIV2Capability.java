package org.opennaas.extensions.quantum.capability.apiv2;

import javax.ws.rs.PathParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.quantum.Activator;
import org.opennaas.extensions.quantum.model.QuantumModel;

/**
 * @author Julio Carlos Barrera
 * 
 */
public class QuantumAPIV2Capability extends AbstractCapability implements IQuantumAPIV2Capability {

	public static final String	CAPABILITY_TYPE	= "quantum-apiv2";

	private Log					log				= LogFactory.getLog(QuantumAPIV2Capability.class);

	private String				resourceId		= "";

	public QuantumAPIV2Capability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Quantum Networks Capability");
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

	// NETWORKS CRUD

	@Override
	public QuantumModel listNetworks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuantumModel createNetwork(QuantumModel network) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuantumModel showNetwork(@PathParam("network_id") String networkId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuantumModel updateNetwork(@PathParam("network_id") String networkId, QuantumModel updatedNetwork) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteNetwork(@PathParam("network_id") String networkId) {
		// TODO Auto-generated method stub

	}

	// PORTS CRUD

	@Override
	public QuantumModel listPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuantumModel createPort(QuantumModel port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuantumModel showPort(@PathParam("port_id") String portId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuantumModel updatePort(@PathParam("port_id") String portId, QuantumModel updatedPort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removePort(@PathParam("port_id") String portId) {
		// TODO Auto-generated method stub

	}

	// SUBNETS CRUD

	@Override
	public QuantumModel listSubnets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuantumModel createSubnet(QuantumModel subnet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuantumModel showSubnet(@PathParam("subnet_id") String subnetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuantumModel updateSubnet(@PathParam("subnet_id") String subnetId, QuantumModel updatedSubnet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSubnet(@PathParam("subnet_id") String subnetId) {
		// TODO Auto-generated method stub

	}

}
