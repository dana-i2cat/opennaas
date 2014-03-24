package org.opennaas.extensions.router.capability.vlanbridge;

/*
 * #%L
 * OpenNaaS :: Router :: VLAN bridge Capability
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.ModelElementNotFoundException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capabilities.api.helper.VLANBridgeApiHelper;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomain;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.InterfaceVLANOptions;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.utils.ModelHelper;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class VLANBridgeCapability extends AbstractCapability implements IVLANBridgeCapability {

	public static final String	CAPABILITY_TYPE	= "vlanbridge";
	Log							log				= LogFactory.getLog(VLANBridgeCapability.class);
	private String				resourceId		= "";

	public VLANBridgeCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) {
		super(capabilityDescriptor);
		this.resourceId = resourceId;
		log.debug("Built new VLANBridge Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.caactivatepability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IVLANBridgeCapability.class.getName());
		super.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivate()
	 */
	@Override
	public void deactivate() throws CapabilityException {
		registration.unregister();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String actionSetName = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String actionSetVersion = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);
		try {
			return Activator.getActionSetService(getCapabilityName(), actionSetName, actionSetVersion);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#queueAction(org.opennaas.core.resources.action.IAction)
	 */
	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	/**
	 * 
	 * @return QueuemanagerService this capability is associated to (the one of the resource it belongs to).
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

	@Override
	public BridgeDomains getBridgeDomains() {

		log.info("Start of getBridgeDomains call");

		ComputerSystem system = (ComputerSystem) this.resource.getModel();

		List<org.opennaas.extensions.router.model.BridgeDomain> bridgeDomains = system.getHostedCollectionByType(
				new org.opennaas.extensions.router.model.BridgeDomain());

		BridgeDomains domains = VLANBridgeApiHelper.buildApiBridgeDomains(bridgeDomains);

		log.info("End of getBridgeDomains call");

		return domains;
	}

	@Override
	public BridgeDomain getBridgeDomain(String domainName) throws ModelElementNotFoundException, CapabilityException {

		log.info("Start of getBridgeDomain call");

		ComputerSystem system = (ComputerSystem) this.resource.getModel();

		List<org.opennaas.extensions.router.model.BridgeDomain> bridgeDomains = system.getHostedCollectionByType(
				new org.opennaas.extensions.router.model.BridgeDomain());

		org.opennaas.extensions.router.model.BridgeDomain modelBrDomain = ModelHelper.getBridgeDomainByName(bridgeDomains,
				domainName);

		if (modelBrDomain == null)
			throw new ModelElementNotFoundException("No such BridgeDomain in model with name " + domainName);

		BridgeDomain apiBrDomain = VLANBridgeApiHelper.buildApiBridgeDomain(modelBrDomain);

		log.info("End of getBridgeDomain call");

		return apiBrDomain;
	}

	@Override
	public void createBridgeDomain(BridgeDomain bridgeDomain) throws CapabilityException {
		log.info("Start of createBridgeDomain call");

		IAction action = createActionAndCheckParams(VLANBridgeActionSet.CREATE_VLAN_BRIDGE_DOMAIN_ACTION, bridgeDomain);
		queueAction(action);

		log.info("End of createBridgeDomain call");
	}

	@Override
	public void updateBridgeDomain(String domainName, BridgeDomain bridgeDomain) throws ModelElementNotFoundException, CapabilityException {
		// TODO call action

	}

	@Override
	public void deleteBridgeDomain(String domainName) throws ModelElementNotFoundException, CapabilityException {
		log.info("Start of deleteBridgeDomain call");

		IAction action = createActionAndCheckParams(VLANBridgeActionSet.DELETE_VLAN_BRIDGE_DOMAIN_ACTION, domainName);
		queueAction(action);

		log.info("End of deleteBridgeDomain call");

	}

	@Override
	public InterfaceVLANOptions getInterfaceVLANOptions(String ifaceName) throws ModelElementNotFoundException {
		// FIXME call action
		InterfaceVLANOptions ivlanOpt = new InterfaceVLANOptions();

		Map<String, String> vlanOptions = new HashMap<String, String>();

		vlanOptions.put("port-mode", "trunk");
		vlanOptions.put("native-vlan-id", "102");

		ivlanOpt.setVlanOptions(vlanOptions);

		return ivlanOpt;
	}

	@Override
	public void setInterfaceVLANOptions(String ifaceName, InterfaceVLANOptions vlanOptions) throws ModelElementNotFoundException,
			CapabilityException {

	}

}
