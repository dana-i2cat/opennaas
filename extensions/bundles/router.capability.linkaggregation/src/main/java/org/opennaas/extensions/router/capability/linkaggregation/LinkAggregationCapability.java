package org.opennaas.extensions.router.capability.linkaggregation;

/*
 * #%L
 * OpenNaaS :: Router :: Link Aggregation Capability
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.capability.linkaggregation.api.AggregatedInterface;

public class LinkAggregationCapability extends AbstractCapability implements ILinkAggregationCapability {

	public static final String	CAPABILITY_TYPE	= "linkaggregation";

	private Log					log				= LogFactory.getLog(LinkAggregationCapability.class);

	private String				resourceId		= "";

	public LinkAggregationCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Link Aggregation Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), ILinkAggregationCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		registration.unregister();
		super.deactivate();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getLinkAggregationActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	// *********************************************
	// * ILinkAggregationCapability implementation *
	// *********************************************

	@Override
	public InterfacesNamesList getAggregatedInterfaces() throws CapabilityException {
		// FIXME skeleton method with a dummy implementation
		InterfacesNamesList interfacesNamesList = new InterfacesNamesList();

		List<String> namesList = new ArrayList<String>();
		namesList.add("ae0");
		namesList.add("ae1");
		namesList.add("ae2");

		interfacesNamesList.setInterfaces(namesList);

		return interfacesNamesList;
	}

	@Override
	public AggregatedInterface getAggregatedInterface(String aggregatedInterfaceId) {
		// FIXME skeleton method with a dummy implementation
		AggregatedInterface aggregatedInterface = new AggregatedInterface();

		aggregatedInterface.setId(aggregatedInterfaceId);

		List<String> interfacesNames = new ArrayList<String>();
		interfacesNames.add("ge-0/0/0");
		interfacesNames.add("ge-0/0/1");
		interfacesNames.add("ge-0/0/2");
		aggregatedInterface.setInterfacesNames(interfacesNames);

		Map<String, String> aggregationOptions = new HashMap<String, String>();
		aggregationOptions.put("minimum-links", "1");
		aggregationOptions.put("link-speed", "1g");
		aggregatedInterface.setAggregationOptions(aggregationOptions);

		return aggregatedInterface;
	}

	@Override
	public void createAggregatedInterface(AggregatedInterface aggregatedInterface) throws CapabilityException {
		// FIXME skeleton method with a dummy implementation

	}

	@Override
	public void removeAggregatedInterface(String aggregatedInterfaceId) throws CapabilityException {
		// FIXME skeleton method with a dummy implementation

	}

	/**
	 * 
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
