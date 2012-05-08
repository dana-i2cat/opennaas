package org.opennaas.core.resources.alarms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ResourceNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class AlarmsRepository implements IAlarmsRepository, EventHandler {
	/** logger */
	private Log									log					= LogFactory.getLog(AlarmsRepository.class);

	/** The map of running resource instances **/
	protected Map<String, List<AlarmElement>>	alarmsRepository	= null;

	public AlarmsRepository() {
		alarmsRepository = new HashMap<String, List<AlarmElement>>();

	}

	/**
	 * Blueprint callback (executed when EventManager is available)
	 *
	 * @param eventManager
	 */
	public void setEventManager(IEventManager eventManager) {
		/* register alarms repository */
		try {
			EventFilter filter = new EventFilter(ResourceAlarm.TOPIC);

			eventManager.registerEventHandler(this, filter);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.info("Alarms repository is registered!!");

	}

	@Override
	public String addResourceAlarm(ResourceAlarm resourceAlarm, String resourceId) {
		String alarmId = UUID.randomUUID().toString();
		List<AlarmElement> alarms = null;

		if (alarmsRepository.containsKey(resourceId)) {
			alarms = alarmsRepository.get(resourceId);
		} else {
			alarms = new ArrayList<AlarmElement>();
		}

		AlarmElement alarmElement = new AlarmElement(resourceAlarm, alarmId);
		alarms.add(alarmElement);
		alarmsRepository.put(resourceId, alarms);

		return alarmId;
	}

	@Override
	public void clear() {
		alarmsRepository.clear();

	}

	@Override
	public void clearResourceAlarms(String resourceId) {

		List<AlarmElement> alarmElems = alarmsRepository.get(resourceId);
		if (alarmElems != null) {
			alarmsRepository.remove(resourceId);
		}
		log.debug("Alarms cleared for resource " + resourceId);
	}

	@Override
	public ResourceAlarm getAlarmById(String alarmId) throws ResourceNotFoundException {
		Set<String> keys = alarmsRepository.keySet();

		for (String key : keys) {
			List<AlarmElement> alarms = alarmsRepository.get(key);
			for (AlarmElement elemAlarm : alarms) {
				if (elemAlarm.id.equals(alarmId))
					return elemAlarm.alarm;
			}

		}
		throw new ResourceNotFoundException("Alarm not found in repository");
	}

	@Override
	public List<ResourceAlarm> getAlarms() {
		List<ResourceAlarm> resourceAlarms = new ArrayList<ResourceAlarm>();
		Set<String> keys = alarmsRepository.keySet();
		for (String key : keys) {
			List<AlarmElement> alarmElems = alarmsRepository.get(key);
			if (alarmElems != null) {
				for (AlarmElement alarmElem : alarmElems) {
					resourceAlarms.add(alarmElem.alarm);
				}
			}
		}
		return resourceAlarms;
	}

	@Override
	public List<ResourceAlarm> getResourceAlarms(String resourceId) throws ResourceNotFoundException {
		List<ResourceAlarm> resourceAlarms = new ArrayList<ResourceAlarm>();
		List<AlarmElement> alarmElems = alarmsRepository.get(resourceId);
		if (alarmElems != null) {
			for (AlarmElement alarmElem : alarmElems) {
				resourceAlarms.add(alarmElem.alarm);
			}
		}
		return resourceAlarms;
	}

	public Map<String, List<AlarmElement>> getAlarmRepository() {
		return alarmsRepository;
	}

	class AlarmElement {

		ResourceAlarm	alarm	= null;
		String			id		= null;

		public AlarmElement(ResourceAlarm resourceAlarm, String alarmId) {
			alarm = resourceAlarm;
			id = alarmId;
		}

		public ResourceAlarm getAlarm() {
			return alarm;
		}

		public void setAlarm(ResourceAlarm alarm) {
			this.alarm = alarm;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public boolean equals(Object obj) {
			AlarmElement toCompare = (AlarmElement) obj;
			return (this.id.equals(toCompare.id));
		}
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof ResourceAlarm) {
			ResourceAlarm resourceAlarm = (ResourceAlarm) event;
			String resourceId = (String) resourceAlarm.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY);

			addResourceAlarm(resourceAlarm, resourceId);
		}
	}
}
