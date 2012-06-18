package org.opennaas.extensions.capability.macbridge.vlanawarebridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

/**
 * @author Isart Canyameres
 * @author Jordi Puig
 * @author Eduard Grasa
 */
public class VLANAwareBridgeCapability extends AbstractCapability implements IVLANAwareBridgeCapability {

	public static String	CAPABILITY_TYPE	= "VLANAwareBridge";

	Log						log				= LogFactory.getLog(VLANAwareBridgeCapability.class);

	private String			resourceId		= "";

	/**
	 * OSPFCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public VLANAwareBridgeCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new VLAN Aware Bridge Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	/**
	 * Return the OSPF ActionSet
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getVLANAwareBridgeActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}
	
	/**
	 * Add a new VLAN Configuration to the VLAN database
	 * @param vlanConfiguration
	 * @throws CapabilityException
	 */
	@Override
	public void createVLANConfiguration(VLANConfiguration vlanConfiguration) throws CapabilityException{
		queueAction(
				createActionAndCheckParams(
						VLANAwareBridgeActionSet.CREATE_VLAN_CONFIGURATION, vlanConfiguration));
	}
	
	/**
	 * Delete a VLAN Configuration from the VLAN database
	 * @param vlanId the id of the VLAN
	 * @throws CapabilityException
	 */
	@Override
	public void deleteVLANConfiguration(int vlanId) throws CapabilityException{
		queueAction(
				createActionAndCheckParams(
						VLANAwareBridgeActionSet.DELETE_VLAN_CONFIGURATION, vlanId));
	}
	
	/**
	 * 
	 * @param entry
	 * @throws CapabilityException
	 */
	@Override
	public void addStaticVLANRegistrationEntryToFilteringDatabase(StaticVLANRegistrationEntry entry) throws CapabilityException{
		queueAction(
				createActionAndCheckParams(
						VLANAwareBridgeActionSet.ADD_STATIC_VLAN_REGISTRATION_ENTRY_TO_FILTERING_DATABASE, entry));
	}
	
	/**
	 * @param vlanID
	 * @throws CapabilityException
	 */
	@Override
	public void deleteStaticVLANRegistrationEntryFromFilteringDatabase(int vlanID) throws CapabilityException{
		queueAction(
				createActionAndCheckParams(
						VLANAwareBridgeActionSet.DELETE_STATIC_VLAN_REGISTRATION_ENTRY_FROM_FILTERING_DATABASE, vlanID));
	}

	/**
	 * @return QueuemanagerService this capability is associated to.
	 * @throws CapabilityException
	 *             if desired queueManagerService could not be retrieved.
	 */
	private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
	}
}
