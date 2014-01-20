package org.opennaas.extensions.ofertie.ncl.provisioner.api;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.GenericListWrapper;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowNotFoundException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper.QoSPolicyRequestsWrapper;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
@Path("/flows")
public interface INCLProvisioner {

	/**
	 * Allocates a flow.
	 * 
	 * @param qosPolicyRequest
	 * @return flowId of allocated flow
	 * @throws AllocationException
	 * @throws ProvisionerException
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String allocateFlow(QosPolicyRequest qosPolicyRequest) throws FlowAllocationException, ProvisionerException;

	/**
	 * Updates already allocated flow having flowId. May cause re-allocating the flow.
	 * 
	 * @param flowId
	 *            of flow to update
	 * @param updatedQosPolicyRequest
	 * @return flowId of allocated flow
	 * @throws AllocationException
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String updateFlow(@PathParam("id") String flowId, QosPolicyRequest updatedQosPolicyRequest) throws FlowAllocationException,
			FlowNotFoundException,
			ProvisionerException;

	/**
	 * Deallocates an allocated flow.
	 * 
	 * @param flowId
	 *            id of flow to deallocate
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@Path("/{id}")
	@DELETE
	public void deallocateFlow(@PathParam("id") String flowId) throws FlowNotFoundException, ProvisionerException;

	/**
	 * Returns currently allocated QoS policies requests.
	 * 
	 * @return Currently allocated QoS policies requests
	 * @throws ProvisionerException
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public QoSPolicyRequestsWrapper readAllocatedFlows() throws ProvisionerException;

	/**
	 * Returns implementation of given flow
	 * 
	 * @param flowId
	 *            id of flow to query for
	 * @return Currently allocated NetOFFlow(s) that are the implementation of flow with given flowid
	 * @throws ProvisionerException
	 */
	@Path("/{id}/implementation")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public GenericListWrapper<NetOFFlow> getFlowImplementation(@PathParam("id") String flowId) throws ProvisionerException;

	/**
	 * Returns QoS network requirements for one flow
	 * 
	 * @param flowId
	 * @return allocated Circuit
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@GET
	@Path("/{flowId}")
	@Produces(MediaType.APPLICATION_XML)
	public QosPolicyRequest getFlow(@PathParam("flowId") String flowId) throws FlowNotFoundException, ProvisionerException;

	/**
	 * Returns latency for one flow
	 * 
	 * @param flowId
	 * @return latency
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@GET
	@Path("/{flowId}/latency")
	@Produces(MediaType.APPLICATION_XML)
	public Latency getLatency(@PathParam("flowId") String flowId) throws FlowNotFoundException,
			ProvisionerException;

	/**
	 * Updates latency for one flow
	 * 
	 * @param flowId
	 * @param latency
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@PUT
	@Path("/flows/{flowId}/latency")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public void updateLatency(@PathParam("flowId") String flowId, Latency latency)
			throws FlowNotFoundException, ProvisionerException, FlowAllocationException;

	/**
	 * Deletes latency for one flow
	 * 
	 * @param flowId
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@DELETE
	@Path("/flows/{flowId}/latency")
	@Produces(MediaType.APPLICATION_XML)
	public void deleteLatency(@PathParam("flowId") String flowId) throws FlowNotFoundException,
			ProvisionerException, FlowAllocationException;

	/**
	 * Returns jitter for one flow
	 * 
	 * @param flowId
	 * @return jitter
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@GET
	@Path("/{flowId}/jitter")
	@Produces(MediaType.APPLICATION_XML)
	public Jitter getJitter(@PathParam("flowId") String flowId) throws FlowNotFoundException,
			ProvisionerException;

	/**
	 * Updates jitter for one flow
	 * 
	 * @param flowId
	 * @param jitter
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@PUT
	@Path("/flows/{flowId}/jitter")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public void updateJitter(@PathParam("flowId") String flowId, Jitter jitter)
			throws FlowNotFoundException, ProvisionerException, FlowAllocationException;

	/**
	 * Deletes jitter for one flow
	 * 
	 * @param flowId
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@DELETE
	@Path("/flows/{flowId}/jitter")
	@Produces(MediaType.APPLICATION_XML)
	public void deleteJitter(@PathParam("flowId") String flowId) throws FlowNotFoundException,
			ProvisionerException, FlowAllocationException;

	/**
	 * Returns throughput for one flow
	 * 
	 * @param flowId
	 * @return throughput
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@GET
	@Path("/{flowId}/throughput")
	@Produces(MediaType.APPLICATION_XML)
	public Throughput getThroughput(@PathParam("flowId") String flowId) throws FlowNotFoundException,
			ProvisionerException;

	/**
	 * Updates throughput for one flow
	 * 
	 * @param flowId
	 * @param throughput
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@PUT
	@Path("/flows/{flowId}/throughput")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public void updateThroughput(@PathParam("flowId") String flowId, Throughput throughput)
			throws FlowNotFoundException, ProvisionerException, FlowAllocationException;

	/**
	 * Deletes throughput for one flow
	 * 
	 * @param flowId
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@DELETE
	@Path("/flows/{flowId}/throughput")
	@Produces(MediaType.APPLICATION_XML)
	public void deleteThroughput(@PathParam("flowId") String flowId) throws FlowNotFoundException,
			ProvisionerException, FlowAllocationException;

	/**
	 * Returns packet_loss for one flow
	 * 
	 * @param flowId
	 * @return packet_loss
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@GET
	@Path("/{flowId}/packet_loss")
	@Produces(MediaType.APPLICATION_XML)
	public PacketLoss getPacketLoss(@PathParam("flowId") String flowId) throws FlowNotFoundException,
			ProvisionerException;

	/**
	 * Updates packet_loss for one flow
	 * 
	 * @param flowId
	 * @param packet_loss
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@PUT
	@Path("/flows/{flowId}/packet_loss")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public void updatePacketLoss(@PathParam("flowId") String flowId, PacketLoss packetLoss)
			throws FlowNotFoundException, ProvisionerException, FlowAllocationException;

	/**
	 * Deletes packet_loss for one flow
	 * 
	 * @param flowId
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@DELETE
	@Path("/flows/{flowId}/latency")
	@Produces(MediaType.APPLICATION_XML)
	public void deletePacketLoss(@PathParam("flowId") String flowId) throws FlowNotFoundException,
			ProvisionerException, FlowAllocationException;

}
