import gim.core.IPowerConsumer;
import gim.core.IPowerDelivery;
import gim.core.IPowerSupply;
import gim.core.entities.GIModel;
import gim.core.entities.PowerConsumer;
import gim.core.entities.PowerSupply;
import gim.core.entities.pdu.PDU;
import gim.core.entities.pdu.PDUPort;
import gim.energy.Energy;
import gim.energy.energyClass;
import gim.energy.energyType;
import gim.load.MeasuredLoad;
import gim.log.PowerMonitorLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import snmp.APCDriver_SNMP;
import controller.APCPDUPowerControllerDriver;
import controller.PDUPortPowerController;
import controller.PDUPowerController;
import controller.PDUPowerControllerDriver;

public class APCDriverTest {

	private final String		pduIPAddress	= "udp:193.1.29.121/161";

	private GIModel				model;

	APCDriver_SNMP				snmpDriver;
	PDUPowerControllerDriver	apcDriver;
	PDUPowerController			pduController;
	PDUPortPowerController		routerController;

	@Before
	public void initModelAndControllers() throws IOException {

		model = initModel();
		initControllers();
	}

	@Test
	public void testPowerMonitorAndSupplyFromRouterController() throws Exception {

		boolean status = routerController.getPowerStatus();
		System.out.println("Port Status: " + status);
		Assert.assertTrue("Port should be powered on", status);

		MeasuredLoad ml = routerController.getCurrentPowerMetrics();
		Assert.assertNotNull(ml);
		System.out.println("Router Voltage @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getVoltage() + " Volts");
		System.out.println("Router Current @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getCurrent() + " Amps");
		System.out.println("Router Power @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getPower() + "KW");
		System.out.println("Router Energy @ " + ml.getReadingTime().toLocaleString() + ": " + ml.getEnergy() + "KWh");
		Assert.assertFalse(ml.getVoltage() == 0);
		Assert.assertFalse(ml.getCurrent() == 0);
		Assert.assertFalse(ml.getPower() == 0);
		Assert.assertFalse(ml.getEnergy() == 0);

		Energy e1 = routerController.getAggregatedEnergy();
		Energy e = model.getSupplies().get(0).getEnergy();
		Assert.assertEquals(e.getEnergyClass(), e1.getEnergyClass());
		Assert.assertEquals(e.getEnergyType(), e1.getEnergyType());
		Assert.assertEquals(e.getCO2perKw(), e1.getCO2perKw());

		Assert.assertEquals(model.getSupplies().get(0).getPricePerUnit(), routerController.getAggregatedPricePerEnergyUnit());

		PDUPort port = (PDUPort) model.getDeliveries().get(0);
		Assert.assertTrue(port.getPowerMonitorLog().getMeasuredLoads().contains(ml));
	}

	/**
	 * This test may interrupt power to a physical device. It is ignored to ensure test battery is not causing service disruption.
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testPowerManagementFromRouterController() throws Exception {

		boolean status = routerController.getPowerStatus();
		System.out.println("Port Status: " + status);
		Assert.assertTrue("Port should be powered on", status);

		routerController.powerOff();
		status = routerController.getPowerStatus();
		System.out.println("Power Status: " + status);
		Assert.assertFalse("Must be powered off", status);

		routerController.powerOn();
		status = routerController.getPowerStatus();
		System.out.println("Power Status: " + status);
		Assert.assertTrue("Must be powered on", status);
	}

	private GIModel initModel() {

		Energy e = new Energy(energyClass.Green, energyType.Wind, 0.11, 100);

		PowerSupply supply = new PowerSupply();
		supply.setEnergy(e);
		supply.setPricePerUnit(0.12);
		// supply.setRatedLoad(ratedLoad);

		PDU pdu = new PDU();
		pdu.setPowerSupplies(new ArrayList<IPowerSupply>(Arrays.asList(supply)));
		// pdu.setDeliveryRatedLoad(deliveryRatedLoad);

		PDUPort port = new PDUPort();
		port.setPdu(pdu);
		port.setPowerMonitorLog(new PowerMonitorLog(1, 2));
		// port.setDeliveryRatedLoad(deliveryRatedLoad);

		PowerConsumer router = new PowerConsumer();
		router.setPowerDeliveries(new ArrayList<IPowerDelivery>(Arrays.asList(port)));
		// router.setRatedLoad(ratedLoad);

		port.setPowerConsumers(new ArrayList<IPowerConsumer>(Arrays.asList(router)));

		GIModel model = new GIModel();
		model.setConsumers(new ArrayList<IPowerConsumer>(Arrays.asList(router)));
		model.setDeliveries(new ArrayList<IPowerDelivery>(Arrays.asList(port, pdu)));
		model.setSupplies(new ArrayList<IPowerSupply>(Arrays.asList(supply)));

		return model;
	}

	private void initControllers() throws IOException {

		snmpDriver = new APCDriver_SNMP(pduIPAddress);
		PDUPort port = (PDUPort) model.getDeliveries().get(0);

		Map<PDUPort, Integer> outletIndexes = new HashMap<PDUPort, Integer>();
		outletIndexes.put(port, 1); // port has outletIndex = 1

		APCPDUPowerControllerDriver apcDriver = new APCPDUPowerControllerDriver();
		apcDriver.setDriver(snmpDriver);
		apcDriver.setOutletIndexes(outletIndexes);
		this.apcDriver = apcDriver;

		pduController = new PDUPowerController();
		pduController.setPdu(port.getPdu());
		pduController.setDriver(apcDriver);

		routerController = new PDUPortPowerController();
		routerController.setPduController(pduController);
		routerController.setPort(port);

	}
}
