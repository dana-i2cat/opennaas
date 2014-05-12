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

import java.util.Arrays;
import java.util.List;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.contentprovisioning.mediaencoder.api.IMediaEncoder;
import org.opennaas.extensions.contentprovisioning.mediaencoder.client.MediaEncoderClientFactory;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.repository.OpenflowSwitchRepository;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
public class ContentProvisioning implements IContentProvisioning {

	private IResourceManager	rm;

	private String				switchResourceName;
	private String				mediaEncoderBaseURL;
	private String				flowInputPort;
	private String				flowOutputPort;

	private IMediaEncoder		mediaEncoderClient;

	private List<String>		flowIds;
	private String				streamId;
	private boolean				streaming	= false;
	private final Object		lock		= new Object();

	/**
	 * @param rm
	 *            the rm to set
	 */
	public void setRm(IResourceManager rm) {
		this.rm = rm;
	}

	/**
	 * @param switchResourceName
	 *            the switchResourceName to set
	 */
	public void setSwitchResourceName(String switchResourceName) {
		this.switchResourceName = switchResourceName;
	}

	/**
	 * 
	 * @param mediaEncoderBaseURL
	 *            the Media Encoder base URL to set
	 */
	public void setMediaEncoderBaseURL(String mediaEncoderBaseURL) {
		this.mediaEncoderBaseURL = mediaEncoderBaseURL;
	}

	public void afterPropertiesSet() throws Exception {
		// initialize media encoder client
		mediaEncoderClient = MediaEncoderClientFactory.getClient(mediaEncoderBaseURL);
	}

	/**
	 * @return the flowInputPort
	 */
	public String getFlowInputPort() {
		return flowInputPort;
	}

	/**
	 * @param flowInputPort
	 *            the flowInputPort to set
	 */
	public void setFlowInputPort(String flowInputPort) {
		this.flowInputPort = flowInputPort;
	}

	/**
	 * @return the flowOutputPort
	 */
	public String getFlowOutputPort() {
		return flowOutputPort;
	}

	/**
	 * @param flowOutputPort
	 *            the flowOutputPort to set
	 */
	public void setFlowOutputPort(String flowOutputPort) {
		this.flowOutputPort = flowOutputPort;
	}

	@Override
	public void startStream() throws Exception {
		synchronized (lock) {
			if (streaming)
				throw new Exception("Already streaming");

			String inputPort = getFlowInputPort();
			String outputPort = getFlowOutputPort();

			flowIds = allocateFlowsInSwitch(inputPort, outputPort);
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
			deallocateFlowsInSwitch(flowIds);
			flowIds.clear();
			streaming = false;
		}
	}

	private List<String> allocateFlowsInSwitch(String inputPort, String outputPort) throws ResourceException {

		FloodlightOFFlow flow1 = FlowFactory.newFlow(inputPort, outputPort);
		FloodlightOFFlow flow2 = FlowFactory.newFlow(outputPort, inputPort);

		getOFForwardingCapability().createOpenflowForwardingRule(flow1);
		getOFForwardingCapability().createOpenflowForwardingRule(flow2);

		return Arrays.asList(flow1.getName(), flow2.getName());
	}

	private void deallocateFlowsInSwitch(List<String> flowIds) throws ResourceException {
		for (String flowId : flowIds) {
			getOFForwardingCapability().removeOpenflowForwardingRule(flowId);
		}
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

			IResource switchResource = rm.getResource(rm.getIdentifierFromResourceName(OpenflowSwitchRepository.OF_SWITCH_RESOURCE_TYPE,
					switchResourceName));
			return (IOpenflowForwardingCapability) switchResource.getCapabilityByInterface(IOpenflowForwardingCapability.class);

		} catch (ResourceException e) {
			throw new ResourceException("Unable to get desired OF switch resource", e);
		}
	}

}
