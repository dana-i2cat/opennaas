package org.opennaas.extensions.roadm.capability.monitoring;

import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.alarms.CapabilityAlarm;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class MonitoringCapability extends AbstractCapability implements EventHandler {

	static Log					log						= LogFactory.getLog(MonitoringCapability.class);

	public static final String	CAPABILITY_NAME			= "monitoring";

	public static final String	PROCESS_ALARM_ACTION	= "processAlarm";

	private String				resourceId				= "";
	private int					registrationNumber;

	public MonitoringCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Monitoring Capability");
	}

	@Override
	public Object sendMessage(String idOperation, Object params) throws CapabilityException {

		log.debug("Sending message to Monitoring Capability");
		try {
			IAction action = createAction(idOperation);
			action.setParams(params);
			action.setModelToUpdate(resource.getModel());

			if (idOperation.equals(MonitoringCapability.PROCESS_ALARM_ACTION)) {
				log.debug("Executing MonitoringCapability.PROCESS_ALARM_ACTION");
				// execute directly, skipping queue
				return executeAction(action, idOperation);

			} else {
				// queue action
				IQueueManagerService queueManager = Activator.getQueueManagerService(resourceId);
				queueManager.queueAction(action);
				return Response.queuedResponse(idOperation);
			}

		} catch (Exception e) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs
					.add(e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
			return Response.errorResponse(idOperation, errorMsgs);
		}
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
		registerAsCapabilityAlarmListener();
	}

	@Override
	protected void deactivateCapability() throws CapabilityException {
		unregisterAsCapabilityAlarmListener();
	}

	@Override
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/**
	 * Executes given action directly, without passing it to the queue. Given action will be executed with no ProtocolSessionManager (null)
	 * 
	 * @param action
	 * @return Response telling if action has gone ok or not
	 * @throws ActionException
	 */
	private Response executeAction(IAction action, String idOperation) throws ActionException {

		log.debug("Executing action " + idOperation + "...");

		// skip the queue and execute directly
		ActionResponse response = action.execute(null);

		if (response.getStatus().equals(ActionResponse.STATUS.OK)) {
			return Response.okResponse(idOperation);
		} else {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs
					.add(response.getInformation());
			return Response.errorResponse(idOperation, errorMsgs);
		}
	}

	private void registerAsCapabilityAlarmListener() throws CapabilityException {
		log.debug("Registering as CapabilityAlarm listener");

		try {

			// create filter to listen to CapabilityAlarms
			Properties filterProperties = new Properties();
			filterProperties.setProperty(CapabilityAlarm.RESOURCE_ID_PROPERTY, resourceId);
			EventFilter filter = new EventFilter(new String[] { CapabilityAlarm.TOPIC }, filterProperties);

			IEventManager eventManager = Activator.getEventManagerService();
			registrationNumber = eventManager.registerEventHandler(this, filter);

			log.debug("Registered as CapabilityAlarm listener!");

		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to register as alarm listener", e);
		}
	}

	private void unregisterAsCapabilityAlarmListener() throws CapabilityException {
		log.debug("Unregistering as CapabilityAlarm listener");
		try {
			IEventManager eventManager = Activator.getEventManagerService();
			eventManager.unregisterHandler(registrationNumber);
			log.debug("Unregistered as CapabilityAlarm listener!");

		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to unregister as CapabilityAlarm listener", e);
		}
	}

	@Override
	public void handleEvent(Event event) {
		log.debug("Monitoring capability received an alarm!");

		try {

			Response response = (Response) sendMessage(MonitoringCapability.PROCESS_ALARM_ACTION, event);
			if (response.getStatus().equals(Response.Status.OK)) {
				log.info("Alarm processed");
			} else {
				log.error("Error processing alarm: PROCESS_ALARM_ACTION returned error " + response.getErrors());
			}

		} catch (CapabilityException e) {
			log.error(e);
			log.error("Error processing alarm", e);
		} catch (Exception e) {
			log.error(e);
			log.error("Error processing alarm", e);
		}
	}
}
