package org.opennaas.core.events.alarms.repository.test;

import java.util.List;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.core.resources.alarms.AlarmsRepository;
import org.opennaas.core.resources.alarms.ResourceAlarm;

import org.junit.Before;
import org.junit.Test;

public class AlarmsRepositoryTest {
	ResourceAlarm	resourceAlarm	= null;
	ResourceAlarm	resourceAlarm2	= null;
	ResourceAlarm	resourceAlarmB	= null;

	@Before
	public void setUp() {
		resourceAlarm = new ResourceAlarm(newProperties("1", "resourceId1", "code1", "description1"));
		resourceAlarm2 = new ResourceAlarm(newProperties("2", "resourceId2", "code2", "description2"));
		resourceAlarmB = new ResourceAlarm(newProperties("3", "resourceId1", "code3", "description3"));

	}

	@Test
	public void addResourceAlarmTest() {
		AlarmsRepository alarmsRepository = new AlarmsRepository();
		String resourceId = (String) resourceAlarm.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY);
		alarmsRepository.addResourceAlarm(resourceAlarm, resourceId);

		Assert.assertNotNull(alarmsRepository.getAlarmRepository().get(resourceId));

	}

	@Test
	public void getAlarmsTest() {
		AlarmsRepository alarmsRepository = new AlarmsRepository();
		String resourceId = (String) resourceAlarm.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY);
		alarmsRepository.addResourceAlarm(resourceAlarm, resourceId);
		String resourceId2 = (String) resourceAlarm2.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY);
		alarmsRepository.addResourceAlarm(resourceAlarm2, resourceId2);

		Assert.assertTrue(alarmsRepository.getAlarms().size() == 2);

	}

	@Test
	public void getResourceAlarmsTest() {
		AlarmsRepository alarmsRepository = new AlarmsRepository();
		String resourceId = (String) resourceAlarm.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY);
		alarmsRepository.addResourceAlarm(resourceAlarm, resourceId);
		alarmsRepository.addResourceAlarm(resourceAlarmB, resourceId);

		try {
			List<ResourceAlarm> resourceAlarms = alarmsRepository.getResourceAlarms(resourceId);
			Assert.assertTrue(resourceAlarms.size() == 2);

		} catch (ResourceNotFoundException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void getAlarmByIdTest() {
		AlarmsRepository alarmsRepository = new AlarmsRepository();
		String resourceId = (String) resourceAlarm.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY);
		String alarmId = alarmsRepository.addResourceAlarm(resourceAlarm, resourceId);

		try {
			ResourceAlarm alarm = alarmsRepository.getAlarmById(alarmId);
			Assert.assertTrue(alarm.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY).equals(resourceId));

		} catch (ResourceNotFoundException e) {
			Assert.fail(e.getMessage());
		}

	}

	private Map<String, Object> newProperties(String time, String resourceId, String alarmCode, String description) {
		Map<String, Object> properties1 = new HashMap<String, Object>();
		properties1.put(ResourceAlarm.ARRIVAL_TIME_PROPERTY, time);
		properties1.put(ResourceAlarm.RESOURCE_ID_PROPERTY, resourceId);
		properties1.put(ResourceAlarm.ALARM_CODE_PROPERTY, alarmCode);
		properties1.put(ResourceAlarm.DESCRIPTION_PROPERTY, description);

		return properties1;
	}

}
