package net.i2cat.luminis.capability.monitoring;

import java.util.Vector;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.ActivatorException;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.queue.QueueResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class MonitoringCapability extends AbstractCapability implements EventHandler {

	static Log					log				= LogFactory.getLog(MonitoringCapability.class);

	public static final String	CAPABILITY_NAME	= "monitoring";

	private String				resourceId		= "";

	public MonitoringCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Monitoring Capability");
	}

	@Override
	public Object sendMessage(String idOperation, Object params) throws CapabilityException {
		log.debug("Sending message to Monitoring Capability");

		try {
			IQueueManagerService queueManager = Activator.getQueueManagerService(resourceId);
			IAction action = createAction(idOperation);

			Object[] newParams = new Object[2];
			newParams[0] = params;
			newParams[1] = this;
			action.setParams(newParams);

			action.setModelToUpdate(resource.getModel());

			// FIXME Actions of this capability should skip queue and be executed directly
			queueManager.queueAction(action);

		} catch (Exception e) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs
					.add(e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
			return Response.errorResponse(idOperation, errorMsgs);
		}

		return Response.okResponse(idOperation);
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getMonitoringActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
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

	@Override
	public void handleEvent(Event event) {
		log.info("Monitoring capability received a message!");
		Response resp;
		try {
			resp = (Response) sendMessage(ActionConstants.PROCESSALARM, event);
			if (resp.getStatus().equals(Response.Status.ERROR)) {
				log.error("Error queuing alarm processing action! " + resp.getInformation());
			} else {
				log.info("Alarm processing action queued! " + resp.getInformation());
			}
			QueueResponse response = Activator.getQueueManagerService(resourceId).execute();

			if (!response.isOk()) {
				log.error("Error processing alarm! " + response.toString());
			} else {
				log.info("Alarm processing finished! " + response.toString());
			}

		} catch (CapabilityException e) {
			log.error("Error processing alarm! " + e.getLocalizedMessage());
		} catch (ProtocolException e) {
			log.error("Error processing alarm! " + e.getLocalizedMessage());
		} catch (ActivatorException e) {
			log.error("Error processing alarm! Could not exec queue. " + e.getLocalizedMessage());
		}
	}

}
