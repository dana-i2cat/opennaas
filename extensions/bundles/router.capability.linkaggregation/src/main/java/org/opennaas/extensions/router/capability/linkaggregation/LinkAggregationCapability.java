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
import java.util.List;

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
import org.opennaas.extensions.router.capability.linkaggregation.api.LinkAggregationAPIAdapter;
import org.opennaas.extensions.router.model.AggregatedLogicalPort;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.utils.ModelHelper;

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

		List<AggregatedLogicalPort> aggregators = ModelHelper.getAggregatedLogicalPorts((System) resource.getModel());

		List<String> namesList = new ArrayList<String>(aggregators.size());
		for (AggregatedLogicalPort aggregator : aggregators) {
			namesList.add(aggregator.getElementName());
		}

		InterfacesNamesList interfacesNamesList = new InterfacesNamesList();
		interfacesNamesList.setInterfaces(namesList);
		return interfacesNamesList;
	}

	@Override
	public AggregatedInterface getAggregatedInterface(String aggregatedInterfaceId) {

		List<AggregatedLogicalPort> allAggregators = ModelHelper.getAggregatedLogicalPorts((System) resource.getModel());
		AggregatedLogicalPort aggregator = (AggregatedLogicalPort) ModelHelper.getManagedElementByElementName(allAggregators, aggregatedInterfaceId);

		return LinkAggregationAPIAdapter.model2Api(aggregator);
	}

	@Override
	public void createAggregatedInterface(AggregatedInterface aggregatedInterface, boolean force) throws CapabilityException {

		AggregatedLogicalPort aggregator = LinkAggregationAPIAdapter.api2Model(aggregatedInterface);

		Object[] actionParams = new Object[2];
		actionParams[0] = aggregator;
		actionParams[1] = Boolean.valueOf(force);
		
		IAction action = createActionAndCheckParams(LinkAggregationActionSet.CREATE_AGGREGATED_INTERFACE, actionParams);
		queueAction(action);

	}

	@Override
	public void removeAggregatedInterface(String aggregatedInterfaceId) throws CapabilityException {

		IAction action = createActionAndCheckParams(LinkAggregationActionSet.REMOVE_AGGREGATED_INTERFACE, aggregatedInterfaceId);
		queueAction(action);

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
