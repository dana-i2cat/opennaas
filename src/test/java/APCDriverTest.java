import gim.IPowerDelivery;
import gim.IPowerSupply;
import gim.energy.Energy;
import gim.energy.energyClass;
import gim.energy.energyType;
import gim.load.MeasuredLoad;
import gim.resources.PDUPort;
import gim.resources.PowerDeliveryResource;
import gim.resources.PowerSupplyResource;
import gim.resources.RouterResource;
import gim.resources.SinglePoweredNetworkResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

public class APCDriverTest {

	@Test
	public void testEverythingFromRouterWITHTargetOutlet() throws IOException {
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
		// status = router1.getPowerStatus();
		// System.out.println("Router Status: " + status);
		// Assert.assertFalse("Router must be powered off", status);

		// router1.powerOn();
		// status = router1.getPowerStatus();
		// System.out.println("Router Status: " + status);
		// Assert.assertTrue("Router must be powered on", status);

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

	@Test
	public void testEverythingFromRouterWithNOTargetOutlet() throws Exception {

		String pduIPAddress = "udp:193.1.29.121/161";
		double pricePerKWh = 0.11;
		Energy e = new Energy(energyClass.Green, energyType.Wind, pricePerKWh);

		IPowerSupply powerSupply = new PowerSupplyResource(e, pricePerKWh);

		PowerDeliveryResource pdu = new PowerDeliveryResource(pduIPAddress);
		pdu.setPowerSupplies(new ArrayList<IPowerSupply>(Arrays.asList(powerSupply)));

		PDUPort port = new PDUPort();
		port.setId(0);
		port.setParentPDU(pdu);

		pdu.setPorts(new ArrayList<PDUPort>(Arrays.asList(port)));
		pdu.getPortOutletIndexes().put(port.getId(), 1);

		SinglePoweredNetworkResource resource = new SinglePoweredNetworkResource();
		// assign the resource to the pdu port
		resource.setPowerDeliveries(new ArrayList<IPowerDelivery>(Arrays.asList(port)));

		boolean status = resource.getPowerStatus();
		System.out.println("Port Status: " + status);
		Assert.assertTrue("Port should be powered on", status);

		// port.powerOff();
		// status = port.getPowerStatus();
		// System.out.println("Port Status: " + status);
		// Assert.assertFalse("Port must be powered off", status);

		// port.powerOn();
		// status = port.getPowerStatus();
		// System.out.println("Port Status: " + status);
		// Assert.assertTrue("Port must be powered on", status);

		MeasuredLoad ml = resource.getCurrentPowerMetrics();
		System.out.println("Router Voltage @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getVoltage() + " Volts");
		System.out.println("Router Current @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getCurrent() + " Amps");
		System.out.println("Router Power @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getPower() + "KW");
		System.out.println("Router Energy @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getEnergy() + "KWh");
		Assert.assertFalse(ml.getVoltage() == 0);
		Assert.assertFalse(ml.getCurrent() == 0);
		Assert.assertFalse(ml.getPower() == 0);
		Assert.assertFalse(ml.getEnergy() == 0);

		Energy e1 = resource.getPowerSupplies().get(0).getEnergy();
		Assert.assertEquals(e.getEnergyClass(), e1.getEnergyClass());
		Assert.assertEquals(e.getEnergyType(), e1.getEnergyType());
		Assert.assertEquals(e.getCO2perKw(), e1.getCO2perKw());
	}
}
