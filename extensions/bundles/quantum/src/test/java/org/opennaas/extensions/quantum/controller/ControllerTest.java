package org.opennaas.extensions.quantum.controller;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.extensions.bod.capability.l2bod.BoDLink;
import org.opennaas.extensions.quantum.QuantumException;
import org.opennaas.extensions.quantum.model.AutobahnElement;
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.NetworkModel;
import org.opennaas.extensions.quantum.model.QuantumModel;
import org.opennaas.extensions.quantum.model.QuantumModelController;
import org.opennaas.extensions.quantum.model.Resource;
import org.opennaas.extensions.quantum.network.builder.NetworkBuilderHelper;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class ControllerTest {

	private static final Log		log						= LogFactory.getLog(ControllerTest.class);

	private static final String		NET01_ID				= "abcd-1234-abcd-1234";
	private static final String		NET01_NAME				= "net01";
	private static final String		NET02_ID				= "efgh-5678-efgh-5678";
	private static final String		NET02_NAME				= "net02";
	private static final String		TENANT01_ID				= "tenant01";
	private static final String		TENANT02_ID				= "tenant02";

	private static final String		AUTOBAHN_RESOURCE_ID	= "autobahn01";
	private static final String		IFACE1_NAME				= "interface1";
	private static final String		IFACE2_NAME				= "interface2";
	private static final String		IFACE3_NAME				= "interface3";
	private static final String		IFACE4_NAME				= "interface4";
	private int						LINK_CAPACITY			= 100;
	private int						VLAN_ID_25				= 25;
	private int						VLAN_ID_26				= 26;

	private QuantumModel			quantumModel;
	private QuantumModelController	controller;
	private NetworkModel			model1;
	private NetworkModel			model2;

	@Test
	public void addAndRemoveNetworksTest() throws QuantumException {

		quantumModel = new QuantumModel();
		controller = new QuantumModelController();
		model1 = new NetworkModel();
		model1.setQuantumNetworkId(NET01_ID);
		model2 = new NetworkModel();
		model2.setQuantumNetworkId(NET02_ID);

		Assert.assertTrue("Quantum model should not have any network.", quantumModel.getNetworks().isEmpty());

		Network network1 = NetworkBuilderHelper.createSampleNetwork(NET01_ID, NET01_NAME, TENANT01_ID);
		Network network2 = NetworkBuilderHelper.createSampleNetwork(NET02_ID, NET02_NAME, TENANT01_ID);

		controller.addNetwork(quantumModel, network1);
		Assert.assertTrue("Quantum model should contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertFalse("Quantum model should not contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertEquals("Quantum model should only contain one network.", 1, quantumModel.getNetworks().size());

		controller.addNetwork(quantumModel, network2);
		Assert.assertTrue("Quantum model should contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertTrue("Quantum model should contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertEquals("Quantum model should contain two networks.", 2, quantumModel.getNetworks().size());

		controller.removeNetwork(quantumModel, model1);
		Assert.assertFalse("Quantum model should not contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertTrue("Quantum model should contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertEquals("Quantum model should only contain one network.", 1, quantumModel.getNetworks().size());

		controller.removeNetwork(quantumModel, model2);
		Assert.assertFalse("Quantum model should not contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertFalse("Quantum model should not contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertTrue("Quantum model should not have any network.", quantumModel.getNetworks().isEmpty());

	}

	@Test
	public void updateNetworkTest() throws QuantumException {

		quantumModel = new QuantumModel();
		controller = new QuantumModelController();
		model1 = new NetworkModel();
		model1.setQuantumNetworkId(NET01_ID);
		model2 = new NetworkModel();
		model2.setQuantumNetworkId(NET02_ID);

		Assert.assertTrue("Quantum model should not have any network.", quantumModel.getNetworks().isEmpty());

		Network network1 = NetworkBuilderHelper.createSampleNetwork(NET01_ID, NET01_NAME, TENANT01_ID);
		Network network2 = NetworkBuilderHelper.createSampleNetwork(NET02_ID, NET02_NAME, TENANT01_ID);
		Network network1Updated = NetworkBuilderHelper.createSampleNetwork(NET01_ID, NET02_NAME, TENANT02_ID);

		controller.addNetwork(quantumModel, network1);
		Assert.assertTrue("Quantum model should contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertFalse("Quantum model should not contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertEquals("Quantum model should only contain one network.", 1, quantumModel.getNetworks().size());

		controller.addNetwork(quantumModel, network2);
		Assert.assertTrue("Quantum model should contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertTrue("Quantum model should contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertEquals("Quantum model should contain two networks.", 2, quantumModel.getNetworks().size());

		controller.updateNetwork(network1.getId(), quantumModel, network1Updated);
		Assert.assertFalse("Quantum model should not contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertTrue("Quantum model should contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertTrue("Quantum model should contain network1 updated", quantumModel.getNetworks().contains(network1Updated));
		Assert.assertEquals("Quantum model should contain two networks.", 2, quantumModel.getNetworks().size());

		controller.removeNetwork(quantumModel, model1);
		controller.removeNetwork(quantumModel, model2);

		Assert.assertFalse("Quantum model should not contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertFalse("Quantum model should not contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertFalse("Quantum model should contain network1 updated", quantumModel.getNetworks().contains(network1Updated));
		Assert.assertTrue("Quantum model should not have any network.", quantumModel.getNetworks().isEmpty());

	}

	@Test(expected = QuantumException.class)
	public void addSameNetworkTwiceTest() throws QuantumException {

		quantumModel = new QuantumModel();
		controller = new QuantumModelController();

		Assert.assertTrue("Quantum model should not have any network.", quantumModel.getNetworks().isEmpty());

		Network network1 = NetworkBuilderHelper.createSampleNetwork(NET01_ID, NET01_NAME, TENANT01_ID);

		controller.addNetwork(quantumModel, network1);
		Assert.assertTrue("Quantum model should contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertEquals("Quantum model should only contain one network.", 1, quantumModel.getNetworks().size());

		controller.addNetwork(quantumModel, network1);
	}

	@Test(expected = QuantumException.class)
	public void removeUnexistingNetworkTest() throws QuantumException {

		quantumModel = new QuantumModel();
		controller = new QuantumModelController();
		model1 = new NetworkModel();
		model1.setQuantumNetworkId(NET01_ID);
		model2 = new NetworkModel();
		model2.setQuantumNetworkId(NET02_ID);

		Assert.assertTrue("Quantum model should not have any network.", quantumModel.getNetworks().isEmpty());

		Network network1 = NetworkBuilderHelper.createSampleNetwork(NET01_ID, NET01_NAME, TENANT01_ID);
		Network network2 = NetworkBuilderHelper.createSampleNetwork(NET02_ID, NET02_NAME, TENANT01_ID);

		controller.addNetwork(quantumModel, network1);
		Assert.assertTrue("Quantum model should contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertFalse("Quantum model should not contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertEquals("Quantum model should only contain one network.", 1, quantumModel.getNetworks().size());

		controller.removeNetwork(quantumModel, model2);

	}

	@Test(expected = QuantumException.class)
	public void updateUnexistingNetworkTest() throws QuantumException {
		quantumModel = new QuantumModel();
		controller = new QuantumModelController();
		model1 = new NetworkModel();
		model1.setQuantumNetworkId(NET01_ID);
		model2 = new NetworkModel();
		model2.setQuantumNetworkId(NET02_ID);

		Assert.assertTrue("Quantum model should not have any network.", quantumModel.getNetworks().isEmpty());

		Network network1 = NetworkBuilderHelper.createSampleNetwork(NET01_ID, NET01_NAME, TENANT01_ID);
		Network network2 = NetworkBuilderHelper.createSampleNetwork(NET02_ID, NET02_NAME, TENANT01_ID);
		Network network2Updated = NetworkBuilderHelper.createSampleNetwork(NET02_ID, NET02_NAME, TENANT02_ID);

		controller.addNetwork(quantumModel, network1);
		Assert.assertTrue("Quantum model should contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertFalse("Quantum model should not contain network2", quantumModel.getNetworks().contains(network2));
		Assert.assertEquals("Quantum model should only contain one network.", 1, quantumModel.getNetworks().size());

		controller.updateNetwork(NET02_ID, quantumModel, network2Updated);

	}

	@Test
	public void addAndRemoveAutobahnResourceToQuantumModelTest() throws QuantumException {

		Resource resource1 = prepareAutobahnResource(IFACE1_NAME, IFACE2_NAME, LINK_CAPACITY, VLAN_ID_25);
		Resource resource2 = prepareAutobahnResource(IFACE3_NAME, IFACE4_NAME, LINK_CAPACITY, VLAN_ID_26);

		quantumModel = new QuantumModel();
		controller = new QuantumModelController();
		model1 = new NetworkModel();
		model1.setQuantumNetworkId(NET01_ID);
		model1.addResource(resource1);
		model2 = new NetworkModel();
		model2.setQuantumNetworkId(NET02_ID);
		model2.addResource(resource2);

		Assert.assertTrue("Quantum model should not have any network.", quantumModel.getNetworks().isEmpty());

		Network network1 = NetworkBuilderHelper.createSampleNetwork(NET01_ID, NET01_NAME, TENANT01_ID);
		Network network2 = NetworkBuilderHelper.createSampleNetwork(NET02_ID, NET02_NAME, TENANT01_ID);

		controller.addNetwork(quantumModel, network1);
		controller.addNetwork(quantumModel, network2);
		Assert.assertEquals("Quantum model should only contain two networks.", 2, quantumModel.getNetworks().size());
		Assert.assertTrue("Quantum model should contain network1", quantumModel.getNetworks().contains(network1));
		Assert.assertTrue("Quantum model should contain network2", quantumModel.getNetworks().contains(network2));

		Assert.assertTrue("Quantum model should not contain any network model.", quantumModel.getNetworksModel().isEmpty());

		controller.addNetworkModelToQuantumModel(quantumModel, model1);

		Assert.assertEquals("Quantum model should only contain one network model.", 1, quantumModel.getNetworksModel().size());
		Assert.assertTrue("Quantum model should contain netmodel1.", quantumModel.getNetworksModel().contains(model1));
		Assert.assertFalse("Quantum model should not contain netmodel2.", quantumModel.getNetworksModel().contains(model2));

		controller.addNetworkModelToQuantumModel(quantumModel, model2);
		Assert.assertEquals("Quantum model should contain two network models.", 2, quantumModel.getNetworksModel().size());
		Assert.assertTrue("Quantum model should contain netmodel1.", quantumModel.getNetworksModel().contains(model1));
		Assert.assertTrue("Quantum model should contain netmodel2.", quantumModel.getNetworksModel().contains(model2));

		controller.removeNetworkModelFromQuantumModel(quantumModel, model2);
		controller.removeNetwork(quantumModel, model2);
		Assert.assertEquals("Quantum model should only contain one network model.", 1, quantumModel.getNetworksModel().size());
		Assert.assertTrue("Quantum model should contain netmodel1.", quantumModel.getNetworksModel().contains(model1));
		Assert.assertFalse("Quantum model should not contain netmodel2.", quantumModel.getNetworksModel().contains(model2));

		controller.removeNetworkModelFromQuantumModel(quantumModel, model1);
		controller.removeNetwork(quantumModel, model1);
		Assert.assertTrue("Quantum model should not contain any network model.", quantumModel.getNetworksModel().isEmpty());
		Assert.assertFalse("Quantum model should not contain netmodel1.", quantumModel.getNetworksModel().contains(model1));
		Assert.assertFalse("Quantum model should not contain netmodel2.", quantumModel.getNetworksModel().contains(model2));
	}

	private Resource prepareAutobahnResource(String iface1Name, String iface2Name, int linkCapacity, int vlanId) {

		AutobahnElement autobahnElem = new AutobahnElement();
		BoDLink link = NetworkBuilderHelper.createSampleBoDLink(iface1Name, iface2Name, linkCapacity, vlanId);
		autobahnElem.addLink(link);
		Resource resource = NetworkBuilderHelper.createSampleAutobahnResource(AUTOBAHN_RESOURCE_ID, autobahnElem);

		return resource;
	}
}
