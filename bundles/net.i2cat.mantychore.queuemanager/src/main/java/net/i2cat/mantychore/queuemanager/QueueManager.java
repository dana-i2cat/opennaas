package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commons.AbstractMantychoreCapability;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionException;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.ErrorConstants;
import net.i2cat.mantychore.commons.IActionSetFactory;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.mantychore.queuemanager.wrappers.ProtocolNetconfWrapper;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.CapabilityProperty;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueManager extends AbstractMantychoreCapability implements
		IQueueManagerService {

	public final static String	QUEUE		= "queue";

	Logger						log			= LoggerFactory
													.getLogger(QueueManager.class);

	private String				resourceId	= "";

	public QueueManager(List<String> actionIds, IResource resource) {
		super(actionIds, resource, resource.getResourceDescriptor()
				.getCapabilityDescriptor(QUEUE));
		this.resourceId = resource.getResourceDescriptor().getId();
	}

	private final BlockingQueue<Action>	queue	= new LinkedBlockingQueue<Action>();

	public void empty() {
		queue.clear();

	}

	public List<ActionResponse> execute() throws ProtocolException,
			CapabilityException, ActionException {

		List<ActionResponse> responses = new Vector<ActionResponse>();

		for (Action action : queue) {

			/* use pool for get protocol session */
			log.info("getting protocol session...");
			responses.add(executeActionWithProtocol(action));

			queue.remove(action);
		}

		// TODO TO IMPLEMENT ROLLBACK A A BETTER EXCEPTION CONTROL
		executeActionWithProtocol(getCommit());

		return responses;

	}

	private Action getCommit() throws CapabilityException, ActionException {

		CapabilityDescriptor commitDescriptor = newCapabilityDescriptor(descriptor);

		for (CapabilityProperty property : commitDescriptor.getCapabilityProperties()) {
			log.info("-> property: " + property.getName() + " - " + property.getValue());

		}

		IActionSetFactory actionFactory = (IActionSetFactory) getCapability(
				IActionSetFactory.class, Activator.getContext(),
				createFilterProperties(commitDescriptor));
		Action action = actionFactory.createAction(ActionConstants.COMMIT);
		action.setDescriptor(commitDescriptor);

		return action;
	}

	private CapabilityDescriptor newCapabilityDescriptor(
			CapabilityDescriptor descriptor) {
		CapabilityDescriptor newDescriptor = new CapabilityDescriptor();

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		for (CapabilityProperty capabProperty : descriptor.getCapabilityProperties()) {
			CapabilityProperty property = new CapabilityProperty();
			property.setId(capabProperty.getId());
			property.setName(capabProperty.getName());
			property.setValue(capabProperty.getValue());
			properties.add(property);

		}

		newDescriptor.setCapabilityProperties(properties);
		newDescriptor.getProperty(ResourceDescriptorConstants.ACTION_CAPABILITY).setValue("basic");

		return newDescriptor;
	}

	/*
	 * necessary to get some capability type
	 */
	protected Properties createFilterProperties(
			CapabilityDescriptor capabilityDescriptor) {
		Properties properties = new Properties();

		properties
				.put(ResourceDescriptorConstants.ACTION_CAPABILITY,
						capabilityDescriptor
								.getPropertyValue(ResourceDescriptorConstants.ACTION_CAPABILITY));

		properties
				.put(ResourceDescriptorConstants.ACTION_NAME,
						capabilityDescriptor
								.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME));

		properties
				.put(ResourceDescriptorConstants.ACTION_VERSION,
						capabilityDescriptor
								.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION));

		properties
				.put(ResourceDescriptorConstants.ACTION_PROTOCOL,
						capabilityDescriptor
								.getPropertyValue(ResourceDescriptorConstants.ACTION_PROTOCOL));

		return properties;
	}

	public ActionResponse executeActionWithProtocol(Action action)
			throws ActionException, ProtocolException {
		ProtocolNetconfWrapper protocolWrapper = new ProtocolNetconfWrapper();
		protocolWrapper.setBundleContext(Activator.getContext()); // add osgi
																	// context

		IProtocolSessionManager protocolSessionManager = protocolWrapper
				.getProtocolSessionManager(resourceId);

		ActionResponse response = action.execute(Activator.getContext(),
				protocolSessionManager);

		return response;

	}

	public List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		for (Action action : queue) {
			actions.add(action);
		}
		return actions;
	}

	public void queueAction(Action action) {
		queue.add(action);

	}

	@Override
	public Response sendMessage(String idOperation, Object paramsModel) {
		// Check if it is an available operation
		if (!actionIds.contains(idOperation)) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs.add(ErrorConstants.ERROR_CAPABILITY);
			Response.errorResponse(idOperation, errorMsgs);
		}

		return Response.okResponse(idOperation);
	}

	@Override
	protected void activateCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void deactivateCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

}
