package org.opennaas.extensions.queuemanager;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueResponse;

@Path("/")
public interface IQueueManagerCapability extends ICapability {

	@POST
	@Path("/execute")
	@Produces(MediaType.APPLICATION_XML)
	public QueueResponse execute() throws ProtocolException,
			CapabilityException, ActionException;

	@POST
	@Path("/clear")
	public void clear();

	// TODO move to an other interface:
	// the fact that this method is not exported through ws suggest it should be placed in an other interface
	public void queueAction(IAction action) throws CapabilityException;

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_XML)
	public List<IAction> getActions();

	@POST
	@Path("/listIds")
	@Produces(MediaType.APPLICATION_XML)
	public List<String> getActionsId();

	@POST
	@Path("/modify")
	@Consumes(MediaType.APPLICATION_XML)
	public void modify(ModifyParams modifyParams) throws CapabilityException;
}
