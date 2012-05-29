package org.opennaas.extensions.roadm.capability.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.alarms.CapabilityAlarm;
import org.opennaas.core.resources.alarms.IAlarmsRepository;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class MonitoringCapability extends AbstractCapability implements EventHandler, IMonitoringCapability {

	static Log					log				= LogFactory.getLog(MonitoringCapability.class);

	public static final String	CAPABILITY_NAME	= "monitoring";
	private String				resourceId		= "";
	private int					registrationNumber;

	public MonitoringCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Monitoring Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.roadm.capability.monitoring.IMonitoringCapability#clearAlarms()
	 */
	@Override
	public void clearAlarms() throws CapabilityException {
		log.info("Start of clearAlarms call");
		try {
			IAlarmsRepository alarmsRepo = Activator.getAlarmsRepositoryService();
			alarmsRepo.clearResourceAlarms(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
		log.info("End of clearAlarms call");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.roadm.capability.monitoring.IMonitoringCapability#getAlarms()
	 */
	@Override
	public List<ResourceAlarm> getAlarms() throws CapabilityException {
		log.info("Start of getAlarms call");
		List<ResourceAlarm> alarms = new ArrayList<ResourceAlarm>();
		try {
			IAlarmsRepository alarmsRepo = Activator.getAlarmsRepositoryService();
			alarms = alarmsRepo.getResourceAlarms(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		} catch (ResourceNotFoundException e) {
			throw new CapabilityException(e);
		}
		log.info("End of getAlarms call");
		return alarms;
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_NAME;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
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
	public void activate() throws CapabilityException {
		registerAsCapabilityAlarmListener();
		setState(State.ACTIVE);
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterAsCapabilityAlarmListener();
		setState(State.INACTIVE);
	}

	@Override
	public void handleEvent(Event event) {
		log.debug("Monitoring capability received an alarm!");
		try {
			Response response = executeProcessAlarmAction(MonitoringActionSet.PROCESS_ALARM, event);
			if (response.getStatus().equals(Response.Status.OK)) {
				log.info("Alarm processed");
			} else {
				log.error("Error processing alarm: process alarm action returned error " + response.getErrors());
			}
		} catch (CapabilityException e) {
			log.error("Error processing alarm", e);
		} catch (Exception e) {
			log.error("Error processing alarm", e);
		}
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

	/**
	 * Execute the ProcessAlarmAction skipping the queue
	 * 
	 * @param idOperation
	 * @param params
	 * @return a response
	 * @throws CapabilityException
	 */
	private Response executeProcessAlarmAction(String idOperation, Event params) throws CapabilityException {
		IAction action = createActionAndCheckParams(idOperation, params);
		action.setParams(params);
		action.setModelToUpdate(resource.getModel());
		log.debug("Executing process alarm action");
		return executeAction(action, idOperation);
	}

	/**
	 * @throws CapabilityException
	 */
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

	/**
	 * @throws CapabilityException
	 */
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
