package luminis;
import org.junit.Test;

public class RawSocketAlarmsTest {

	@Test
	public void alarmsReceivedTest() {
		// open socket to the device
		// generate alarm
		// check that the alarm is received
		// check listeners are notified
	}

	@Test
	public void checkAlarmsAndCommandsTest() {
		// open session
		// simulate an alarm is received
		// check that the alarm hook is executed

		// simulate a response is received
		// check command response hook is executed
	}

	@Test
	public void checkAllAlarmsAreSupportedTest() {
		// open session
		// while (Alarm alarmInLuminis: allAlarmsInLuminis) {
		// simulate alarm
		// check that the alarm is known
		// }
		// It includes unknown alarms

	}

	@Test
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
	public void checkNotifyEventChannelConfigChangedTest() {
		// throw notify event for ChannelConfigChanged
		// check that the refreshed model is executed

	}

}
