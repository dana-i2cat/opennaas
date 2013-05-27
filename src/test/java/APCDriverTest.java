import gim.load.MeasuredLoad;
import gim.resources.PowerDeliveryResource;
import gim.resources.RouterResource;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class APCDriverTest {

	@Test
	public void testEverythingFromRouter() throws IOException {
		String PDUIP = "udp:193.1.29.121/161";
		int routerOutletNumber = 1;

		PowerDeliveryResource PDU = new PowerDeliveryResource(PDUIP);
		RouterResource router1 = new RouterResource(PDU, routerOutletNumber);

		String pduName = router1.getPowerSupplyDeviceName();
		System.out.println("Router Power Supply : " + pduName);
		Assert.assertNotNull(pduName);

		String outletName = router1.getPowerSupplyOutletName(routerOutletNumber);
		System.out.println("Router Outlet ID: " + outletName);
		Assert.assertNotNull(outletName);

		boolean status = router1.getPowerStatus();
		System.out.println("Router Status: " + status);
		Assert.assertTrue("Router should be powered on", status);

		// router1.powerOff();

		status = router1.getPowerStatus();
		System.out.println("Router Status: " + status);
		// Assert.assertFalse("Router must be powered off", status);

		// router1.powerOn();

		status = router1.getPowerStatus();
		System.out.println("Router Status: " + status);
		Assert.assertTrue("Router must be powered on", status);

		MeasuredLoad ml = router1.getCurrentRouterPower();
		Assert.assertNotNull(ml);

		System.out.println("Router Energy Class (PowerSupply) " + router1.getEnergyClass());
		System.out.println("Router Energy Type (PowerSupply) " + router1.getEnergyType());
		System.out.println("Router Energy CO2perKW: " + router1.getCO2PerKw());
		Assert.assertNotNull(router1.getEnergyClass());
		Assert.assertNotNull(router1.getEnergyType());
		Assert.assertFalse(router1.getCO2PerKw() == 0);

		System.out.println("Router Voltage @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getVoltage() + " Volts");
		System.out.println("Router Current @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getCurrent() + " Amps");
		System.out.println("Router Power @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getPower() + "KW");
		System.out.println("Router Energy @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getEnergy() + "KWh");
		Assert.assertFalse(ml.getVoltage() == 0);
		Assert.assertFalse(ml.getCurrent() == 0);
		Assert.assertFalse(ml.getPower() == 0);
		Assert.assertFalse(ml.getEnergy() == 0);

		int time = 0, monintoringDuration = router1.getPowerMonitoringSampleDuration(), sampleTime = router1.getPowerMonitoringSamplePeriod();

		while (time < monintoringDuration) {

			if (time % sampleTime == 0)
				router1.readToPowerLog();

			time++;
		}

	}
}
