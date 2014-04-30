package org.opennaas.extensions.contentprovisioning;

/*
 * #%L
 * OpenNaaS :: Content Provisioning
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

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class ContentProvisioning implements IContentProvisioning {

	private IResourceManager	rm;

	private String				resourceName;
	private String				resourceType	= "openflowswitch";

	private String				flowId;
	private String				streamId;
	private boolean				streaming		= false;
	private final Object		lock			= new Object();

	/**
	 * @param rm
	 *            the rm to set
	 */
	public void setRm(IResourceManager rm) {
		this.rm = rm;
	}

	/**
	 * @param resourceName
	 *            the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	@Override
	public void startStream() throws Exception {
		synchronized (lock) {
			if (streaming)
				throw new Exception("Already streaming");

			flowId = allocateFlowInSwitch();
			streamId = startStreamInServer();
			streaming = true;
		}
	}

	@Override
	public void stopStream() throws Exception {
		synchronized (lock) {
			if (!streaming)
				throw new Exception("Not streaming. Nothing to stop.");

			stopStreamInServer(streamId);
			deallocateFlowInSwitch(flowId);
			streaming = false;
		}
	}

	private String allocateFlowInSwitch() throws ResourceException {

		FloodlightOFFlow flow = FlowFactory.newFlow();

		getOFForwardingCapability().createOpenflowForwardingRule(flow);

		return flow.getName();
	}

	private void deallocateFlowInSwitch(String flowId) throws ResourceException {
		getOFForwardingCapability().removeOpenflowForwardingRule(flowId);
	}

	private String startStreamInServer() {
		// TODO Auto-generated method stub
		return null;
	}

	private void stopStreamInServer(String streamId) {
		// TODO Auto-generated method stub
	}

	private IOpenflowForwardingCapability getOFForwardingCapability() throws ResourceException {
		try {

			IResource resource = rm.getResource(rm.getIdentifierFromResourceName(resourceType, resourceName));
			return (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);

		} catch (ResourceException e) {
			throw new ResourceException("Unable to get desired resource", e);
		}
	}

}
