package luminis;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarmEvent;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarmFactory;
import net.i2cat.nexus.events.EventFilter;
import net.i2cat.nexus.events.IEventManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class RawSocketAlarmsTest extends AbstractIntegrationTest implements EventHandler {

	static Log					log				= LogFactory.getLog(RawSocketAlarmsTest.class);

	private boolean				alarmReceived	= false;
	private int					alarmCounter	= 0;
	private List<WonesysAlarm>	receivedAlarms	= new ArrayList<WonesysAlarm>();

	private IEventManager		eventManager;

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getLuminisTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.events")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		eventManager = getOsgiService(IEventManager.class, 2000);
		log.info("INFO: Initialized!");

	}

	/**
	 * Test that WonesysProtocol receives alarms correctly from transport and notifies listeners upon alarm reception.
	 */
	@Test
	public void alarmsReceivedTest() {

		initBundles();

		String alarmTopic = "net/i2cat/luminis/protocols/wonesys/alarms/RECEIVED";

		EventFilter filter = new EventFilter(alarmTopic);
		eventManager.registerEventHandler(this, filter);

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, "session1");

			alarmReceived = false;

			// generate alarm
			String alarm = "FFFF0100011700FF0300"; // PSROADM ERROR
			// session.messageReceived(alarm);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// check that the alarm is received & listeners are notified
			Assert.assertTrue(alarmReceived);

			session.disconnect();

			alarmReceived = false;

		} catch (ProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void handleEvent(Event event) {
		// TODO check received event is the alarm we have just triggered
		receivedAlarms.add(((WonesysAlarmEvent) event).getAlarm());
		alarmReceived = true;
		alarmCounter++;
	}

	/**
	 * Tests that WonesysProtocolSession distinguishes between alarms and command responses.
	 */
	@Test
	public void checkAlarmsAndCommandsTest() {

		initBundles();

		String alarmTopic = "net/i2cat/luminis/protocols/wonesys/alarms/RECEIVED";

		EventFilter filter = new EventFilter(alarmTopic);
		eventManager.registerEventHandler(this, filter);

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, "session1");

			alarmReceived = false;

			// generate alarm
			String alarm = "FFFF0100011700FF0300"; // PSROADM ERROR
			// session.messageReceived(alarm);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// check that the alarm is received & listeners are notified
			Assert.assertTrue(alarmReceived);

			alarmReceived = false;

			// FIXME XOR is incorrect
			String commandResponse = "59100117FFFF0B02FFFFFFFF0100000100"; // Set channel resp (OK)
			// session.messageReceived(commandResponse);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// check that no alarm has been received
			Assert.assertFalse(alarmReceived);

		} catch (ProtocolException e2) {
			e2.printStackTrace();
			Assert.fail(e2.getLocalizedMessage());
		}
	}

	@Test
	public void checkAllAlarmsAreSupportedTest() {
		// open session
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, "session1");

			// TODO create one alarm message of each known type
			List<String> alarmMessages = new ArrayList<String>();

			// Check alarms are known
			WonesysAlarm alarm;
			for (String alarmMessage : alarmMessages) {
				alarm = WonesysAlarmFactory.createAlarm(alarmMessage);
				Assert.assertFalse(alarm.getTypeID().equals(WonesysAlarm.Type.UNKNOWN));
			}

			// check an unknown alarm is created
			String unknownAlarmMessage = "FFFFFFFFFFFFFFFFFFFF";
			alarm = WonesysAlarmFactory.createAlarm(unknownAlarmMessage);
			Assert.assertTrue(alarm.getTypeID().equals(WonesysAlarm.Type.UNKNOWN));

			session.disconnect();

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Already tested in first test
	 */
	// @Test
	public void checkAlarmsNotifyEventTest() {
		// open session
		// while (Alarm alarmInLuminis: allAlarmsInLuminis) {
		// simulate alarm
		// check that the alarm is known
		// throw notify event
		// check that it is received by listeners
		// }

	}

	@Test
	public void checkEventChannelConfigChangedTest() {
		String chassis = "01";
		String slot = "17";

		initBundles();

		// create resource with chassis and slot
		// create protocol context
		// start resource
		// manually change chanelConfig in MockProteus
		// launch channelConfigChanged alarm
		// check model has been updated

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, "session1");

			String chassisSlot = "0117";
			// session.messageReceived("FFFF0000" + chassis + slot + "01FF80");

			// TODO check model has been refreshed

			session.disconnect();

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void checkAlarmsStoredInAlarmHistory() {
		String chassis = "01";
		String slot = "17";

		initBundles();

		// create resource with chassis and slot
		// create protocol context
		// start resource
		// manually change chanelConfig in MockProteus
		// launch channelConfigChanged alarm
		// check model has been updated

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, "session1");

			String chassisSlot = "0117";
			String alarmMessage = "FFFF0000" + chassis + slot + "01FF80";
			// session.messageReceived(alarmMessage);

			// TODO check alarm history for given device contains generated alarm

			session.disconnect();

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

}
