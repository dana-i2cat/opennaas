package org.opennaas.itests.roadm.alarms;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;

import org.opennaas.extensions.roadm.wonesys.actionsets.WonesysAlarmsDriver;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSession;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarm;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarmFactory;
import org.opennaas.extensions.roadm.wonesys.transports.rawsocket.RawSocketTransport;
import org.opennaas.extensions.router.model.utils.OpticalSwitchFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class RawSocketAlarmsTest implements EventHandler {

	public static Log			log							= LogFactory.getLog(RawSocketAlarmsTest.class);

	private boolean				alarmReceived				= false;
	private int					alarmCounter				= 0;
	private List<WonesysAlarm>	receivedAlarms				= new ArrayList<WonesysAlarm>();

	public static final String	MSG_RCVD_EVENT_TOPIC		= RawSocketTransport.MSG_RCVD_EVENT_TOPIC;
	public static final String	MESSAGE_PROPERTY_NAME		= RawSocketTransport.MESSAGE_PROPERTY_NAME;
	public static final String	TRANSPORT_ID_PROPERTY_NAME	= RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME;
	public static final String	ARRIVAL_TIME_PROPERTY_NAME	= RawSocketTransport.ARRIVAL_TIME_PROPERTY_NAME;

	@Inject
	private IEventManager		eventManager;

	@Inject
	private BundleContext		bundleContext;

	private final Object		lock						= new Object();

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-luminis"),
					   noConsole(),
					   keepRuntimeFolder());
	}

	private TestInitInfo setUp() throws ProtocolException {

		String sessionID = "session1";

		String alarmTopic = WonesysAlarm.TOPIC;
		Properties properties = new Properties();
		properties.setProperty(WonesysAlarm.SESSION_ID_PROPERTY, sessionID);

		EventFilter filter = new EventFilter(alarmTopic);
		int regNum = eventManager.registerEventHandler(this, filter);

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session =
			new WonesysProtocolSession(protocolSessionContext, sessionID);
		session.connect();

		TestInitInfo initInfo = new TestInitInfo();
		initInfo.session = session;
		initInfo.regNum = regNum;

		return initInfo;
	}

	private void tearDown(TestInitInfo initInfo) throws ProtocolException {
		eventManager.unregisterHandler(initInfo.regNum);
		initInfo.session.disconnect();
	}

	/**
	 * Test to group all tests in this class, loading container only once.
	 *
	 * @throws ProtocolException
	 */
	@Test
	public void allRawSocketAlarmsTest() throws Exception {

		TestInitInfo initInfo = setUp();

		try {
			alarmsReceivedTest(initInfo);
			checkAlarmsAndCommandsTest(initInfo);
			// checkAllAlarmsAreSupportedTest();
			// checkEventChannelConfigChangedTest();
			// checkAlarmsStoredInAlarmHistory();
		} finally {
			tearDown(initInfo);
		}
	}

	/**
	 * Test that WonesysProtocol receives alarms correctly from transport and notifies listeners upon alarm reception.
	 */
	public void alarmsReceivedTest(TestInitInfo initInfo) throws InterruptedException {

		alarmReceived = false;

		generateAlarm(initInfo.session);

		synchronized (lock) {
			lock.wait(3000);
			// check that the alarm is received & listeners are notified
			Assert.assertTrue(alarmReceived);
		}

		alarmReceived = false;
		alarmCounter = 0;
		receivedAlarms.clear();
	}

	/**
	 * Tests that WonesysProtocolSession distinguishes between alarms and command responses, and only rises and alarm when alarms are received.
	 */
	public void checkAlarmsAndCommandsTest(TestInitInfo initInfo) throws InterruptedException {

		alarmReceived = false;
		alarmCounter = 0;
		receivedAlarms.clear();

		generateAlarm(initInfo.session);

		synchronized (lock) {
			lock.wait(3000);
			// check that the alarm is received & listeners are notified
			Assert.assertTrue(alarmReceived);
		}

		alarmReceived = false;

		// generate command response
		// FIXME XOR is incorrect
		String commandResponse = "59100117FFFF0B02FFFFFFFF0100000100"; // Set channel resp (OK)
		generateRawSocketEvent(initInfo.session.getWonesysTransport().getTransportID(), commandResponse);

		synchronized (lock) {
			lock.wait(3000);
			// check that no alarm has been received
			Assert.assertFalse(alarmReceived);
		}

		Assert.assertTrue(alarmCounter == 1);

		alarmReceived = false;
		alarmCounter = 0;
		receivedAlarms.clear();
	}

	@Test
	public void checkAllAlarmsAreSupportedTest() throws Exception {
		String chassis = "00";
		String slot = "01";

		OpticalSwitchFactory factory = new OpticalSwitchFactory();
		IModel model = factory.newPedrosaProteusOpticalSwitch();

		// Create one alarm message of each known type
		List<String> alarmMessages = new ArrayList<String>();
		alarmMessages.add("FFFF0000" + chassis + slot + "01FF80"); // CPLANCHANGED

		// Check alarms are known
		WonesysAlarm alarm;
		for (String alarmMessage : alarmMessages) {
			Map<String, Object> properties = WonesysAlarmFactory.loadAlarmProperties(alarmMessage);
			alarm = WonesysAlarmFactory.createAlarm(properties);

			boolean recognized = false;
			ResourceAlarm generatedAlarm = WonesysAlarmsDriver.wonesysAlarmToResourceAlarm(alarm, model, "testResource01");
			if (!generatedAlarm.getProperty(ResourceAlarm.ALARM_CODE_PROPERTY).equals("UNKNOWN")) {
				recognized = true;
			}
			Assert.assertTrue(recognized);
		}

		// check an unknown alarm is created
		String unknownAlarmMessage = "FFFF0000FFFFFFFFFF";
		Map<String, Object> properties = WonesysAlarmFactory.loadAlarmProperties(unknownAlarmMessage);
		alarm = WonesysAlarmFactory.createAlarm(properties);

		boolean recognized = false;
		ResourceAlarm generatedAlarm = WonesysAlarmsDriver.wonesysAlarmToResourceAlarm(alarm, model, "testResource01");
		if (!generatedAlarm.getProperty(ResourceAlarm.ALARM_CODE_PROPERTY).equals("UNKNOWN")) {
			recognized = true;
		}
		Assert.assertFalse(recognized);
	}

	// @Test //functionality not implemented (alarms don't trigger actions automatically)
	public void checkEventChannelConfigChangedTest() throws Exception {
		String chassis = "01";
		String slot = "01";

		// initBundles();

		// create resource with chassis and slot
		// create protocol context
		// start resource
		// manually change chanelConfig in MockProteus
		// launch channelConfigChanged alarm
		// check model has been updated

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session = new WonesysProtocolSession(protocolSessionContext, "session1");

		session.connect();

		String message = "FFFF0000" + chassis + slot + "01FF80";

		generateRawSocketEvent(session.getWonesysTransport().getTransportID(), message);

		// TODO check model has been refreshed

		session.disconnect();
	}

	// @Test //uncompleted test
	public void checkAlarmsStoredInAlarmHistory() throws Exception {
		String chassis = "01";
		String slot = "17";

		// initBundles();

		// create resource with chassis and slot
		// create protocol context
		// start resource
		// launch channelConfigChanged alarm
		// check alarm is stored

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session = new WonesysProtocolSession(protocolSessionContext, "session1");

		session.connect();

		String alarmMessage = "FFFF0000" + chassis + slot + "01FF80";

		generateRawSocketEvent(session.getWonesysTransport().getTransportID(), alarmMessage);

		// TODO check alarm history for given device contains generated alarm

		session.disconnect();
	}

	@Override
	public void handleEvent(Event event) {
		// TODO check received event is the alarm we have just triggered
		synchronized (lock) {
			alarmReceived = true;
			alarmCounter++;
			receivedAlarms.add((WonesysAlarm) event);
			lock.notify();
		}
	}

	private void generateAlarm(WonesysProtocolSession session) {
		String alarmMessage = "FFFF0100011700FF0300"; // PSROADM ERROR
		generateRawSocketEvent(session.getWonesysTransport().getTransportID(), alarmMessage);
	}

	private void generateRawSocketEvent(String transportId, String message) {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(MESSAGE_PROPERTY_NAME, message);
		properties.put(TRANSPORT_ID_PROPERTY_NAME, transportId);
		long creationTime = new Date().getTime();
		properties.put(ARRIVAL_TIME_PROPERTY_NAME, creationTime);

		Event event = new Event(MSG_RCVD_EVENT_TOPIC, properties);

		eventManager.publishEvent(event);
		log.debug("RawSocketTransport Event generated! " + message + " at " + creationTime);
	}

	class TestInitInfo {
		public WonesysProtocolSession	session;
		public int						regNum;
	}

}
