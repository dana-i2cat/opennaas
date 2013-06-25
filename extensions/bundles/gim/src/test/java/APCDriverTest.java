import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.extensions.gim.controller.APCPDUPowerControllerDriver;
import org.opennaas.extensions.gim.controller.BasicConsumerController;
import org.opennaas.extensions.gim.controller.BasicDeliveryController;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;
import org.opennaas.extensions.gim.controller.PDUController;
import org.opennaas.extensions.gim.controller.PDUPowerControllerDriver;
import org.opennaas.extensions.gim.controller.RouterWithPDUPowerController;
import org.opennaas.extensions.gim.controller.snmp.APCDriver_SNMP;
import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.energy.EnergyClass;
import org.opennaas.extensions.gim.model.energy.EnergyType;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class APCDriverTest {

	private final String			pduIPAddress	= "udp:193.1.29.121/161";

	private GIModel					model;

	APCDriver_SNMP					snmpDriver;
	PDUPowerControllerDriver		apcDriver;
	PDUController					pduController;
	RouterWithPDUPowerController	routerController;

	BasicConsumerController			consumerController;

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

		PowerSource source = model.getDeliveries().get(0).getPowerSources().get(0);
		Assert.assertTrue(source.getPowerMonitorLog().getMeasuredLoads().contains(ml));
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

	@Test
	public void testConsumerController() throws ModelElementNotFoundException, Exception {

		String consumerId = "consumer-0001";
		String receptorId = "consumer-0001/receptor-0001";

		Assert.assertEquals(model.getSupplies().get(0).getEnergy(), consumerController.getAggregatedEnergy(consumerId));

		consumerController.getAggregatedEnergyPrice(consumerId);

	}

	private GIModel initModel() {

		Energy e = new Energy(EnergyClass.Green, EnergyType.Wind, 0.11, 100);

		PowerSupply supply = new PowerSupply();
		supply.setId("supply-0001");
		supply.setEnergy(e);
		supply.setPricePerUnit(0.12);
		// supply.setRatedLoad(ratedLoad);

		PowerSource source = new PowerSource();
		source.setEnergy(supply.getEnergy());
		source.setPricePerUnit(supply.getPricePerUnit());
		source.setPowerState(true);
		source.setId("supply-0001/source-0001");
		source.setElementId("supply-0001");
		source.setPowerMonitorLog(new PowerMonitorLog(1, 2));
		supply.setPowerSources(new ArrayList<PowerSource>(Arrays.asList(source)));

		PowerDelivery delivery = new PowerDelivery();
		delivery.setId("delivery-0001");

		PowerReceptor receptor = new PowerReceptor();
		receptor.setId("delivery-0001/receptor-0001");
		receptor.setElementId("delivery-0001");
		receptor.setPowerState(true);
		receptor.setAttachedTo(source);
		delivery.setPowerReceptors(new ArrayList<PowerReceptor>(Arrays.asList(receptor)));

		PowerSource source2 = new PowerSource();
		source2.setEnergy(supply.getEnergy()); // this data should be filled in with the output of IDeliveryController calculateAggregatedEnergy
		source2.setPricePerUnit(supply.getPricePerUnit()); // this data should be filled in with the output of IDeliveryController
															// calculateAggregatedEnergyPrice
		source2.setPowerState(true);
		source2.setId("delivery-0001/source-0001");
		source2.setElementId("delivery-0001");
		source2.setPowerMonitorLog(new PowerMonitorLog(1, 2));
		delivery.setPowerSources(new ArrayList<PowerSource>(Arrays.asList(source2)));

		PowerConsumer consumer = new PowerConsumer();
		consumer.setId("consumer-0001");

		PowerReceptor receptor2 = new PowerReceptor();
		receptor2.setId("consumer-0001/receptor-0001");
		receptor2.setElementId("consumer-0001");
		receptor2.setPowerState(true);
		receptor2.setAttachedTo(source2);
		consumer.setPowerReceptors(new ArrayList<PowerReceptor>(Arrays.asList(receptor2)));

		GIModel model = new GIModel();
		model.setConsumers(new ArrayList<PowerConsumer>(Arrays.asList(consumer)));
		model.setDeliveries(new ArrayList<PowerDelivery>(Arrays.asList(delivery)));
		model.setSupplies(new ArrayList<PowerSupply>(Arrays.asList(supply)));

		return model;
	}

	private void initControllers() throws IOException {

		PowerDelivery delivery = model.getDeliveries().get(0);
		// create PDU model from delivery
		PDU pdu = new PDU();
		pdu.setId(delivery.getId());
		List<PDUPort> pduPorts = new ArrayList<PDUPort>(delivery.getPowerSources().size());
		PowerSource source;
		for (int i = 0; i < delivery.getPowerSources().size(); i++) {
			source = delivery.getPowerSources().get(i);
			PDUPort port = new PDUPort();
			port.setId(source.getId());
			port.setOutletIndex(i + 1);
			pduPorts.add(port);
		}
		pdu.setPowerSources(pduPorts);

		snmpDriver = new APCDriver_SNMP(pduIPAddress);
		PDUPort port = (PDUPort) pdu.getPowerSources().get(0);

		Map<String, Integer> outletIndexes = new HashMap<String, Integer>();
		outletIndexes.put(port.getId(), port.getOutletIndex());

		APCPDUPowerControllerDriver apcDriver = new APCPDUPowerControllerDriver();
		apcDriver.setDriver(snmpDriver);
		apcDriver.setOutletIndexes(outletIndexes);
		apcDriver.setPdu(pdu);
		this.apcDriver = apcDriver;

		BasicDeliveryController deliveryController = new BasicDeliveryController();
		deliveryController.setModel(model);

		pduController = new PDUController();
		pduController.setDeliveryController(deliveryController);
		pduController.setDeliveryId(pdu.getId());
		pduController.setDriver(apcDriver);

		consumerController = new BasicConsumerController();
		consumerController.setModel(model);

		routerController = new RouterWithPDUPowerController();
		routerController.setConsumerController(consumerController);
		routerController.setPduController(pduController);
		routerController.setConsumerId("consumer-0001");
	}
}
