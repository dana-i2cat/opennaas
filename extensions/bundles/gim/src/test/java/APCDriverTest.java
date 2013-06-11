import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.extensions.gim.controller.APCPDUPowerControllerDriver;
import org.opennaas.extensions.gim.controller.PDUPortPowerController;
import org.opennaas.extensions.gim.controller.PDUPowerController;
import org.opennaas.extensions.gim.controller.PDUPowerControllerDriver;
import org.opennaas.extensions.gim.controller.snmp.APCDriver_SNMP;
import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.energy.EnergyClass;
import org.opennaas.extensions.gim.model.energy.EnergyType;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

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

		Energy e = new Energy(EnergyClass.Green, EnergyType.Wind, 0.11, 100);

		PowerSupply supply = new PowerSupply();
		supply.setEnergy(e);
		supply.setPricePerUnit(0.12);
		// supply.setRatedLoad(ratedLoad);

		PDU pdu = new PDU();
		pdu.setPowerSupplies(new ArrayList<IPowerSupply>(Arrays.asList(supply)));
		// pdu.setDeliveryRatedLoad(deliveryRatedLoad);

		PDUPort port = new PDUPort();
		port.setId("0001");
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

		Map<String, Integer> outletIndexes = new HashMap<String, Integer>();
		outletIndexes.put(port.getId(), 1); // port has outletIndex = 1

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
