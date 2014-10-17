package org.opennaas.extensions.ofertie.ncl.notification.api;

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

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;

/**
 * 
 * @author Julio Carlos Barrera
 *
 */
@Path("/flows")
public interface INCLNotificationAPI {

	/**
	 * Flow created notification.
	 */
	@PUT
	@Path("/{flow_id}")
	@Produces(MediaType.APPLICATION_XML)
	public void flowCreated(@PathParam("flow_id") String flowId, QosPolicy qosPolicy);

	/**
	 * Flow updated notification modifying latency.
	 */
	@PUT
	@Path("/{flow_id}/latency")
	@Produces(MediaType.APPLICATION_XML)
	public void flowUpdated(String flowId, Latency latency);

	/**
	 * Flow updated notification modifying jitter.
	 */
	@PUT
	@Path("/{flow_id}/jitter")
	@Produces(MediaType.APPLICATION_XML)
	public void flowUpdated(String flowId, Jitter jitter);

	/**
	 * Flow updated notification modifying throughput.
	 */
	@PUT
	@Path("/{flow_id}/throughput")
	@Produces(MediaType.APPLICATION_XML)
	public void flowUpdated(String flowId, Throughput throughput);

	/**
	 * Flow updated notification modifying packetLoss.
	 */
	@PUT
	@Path("/{flow_id}/packet_loss")
	@Produces(MediaType.APPLICATION_XML)
	public void flowUpdated(String flowId, PacketLoss packetLoss);

	/**
	 * Flow rejected notification.
	 */
	@DELETE
	@Path("/{flow_id}")
	@Produces(MediaType.APPLICATION_XML)
	public void flowRejected(@PathParam("flow_id") String flowId, QosPolicy qosPolicy);

	/**
	 * Update flow notification because latency.
	 */
	@DELETE
	@Path("/{flow_id}/latency")
	@Produces(MediaType.APPLICATION_XML)
	public void flowRejected(String flowId, Latency latency);

	/**
	 * Update flow notification because jitter.
	 */
	@DELETE
	@Path("/{flow_id}/jitter")
	@Produces(MediaType.APPLICATION_XML)
	public void flowRejected(String flowId, Jitter jitter);

	/**
	 * Update flow notification because throughput.
	 */
	@DELETE
	@Path("/{flow_id}/throughput")
	@Produces(MediaType.APPLICATION_XML)
	public void flowRejected(String flowId, Throughput throughput);

	/**
	 * Update flow notification because packetLoss.
	 */
	@DELETE
	@Path("/{flow_id}/packet_loss")
	@Produces(MediaType.APPLICATION_XML)
	public void flowRejected(String flowId, PacketLoss packetLoss);

}
