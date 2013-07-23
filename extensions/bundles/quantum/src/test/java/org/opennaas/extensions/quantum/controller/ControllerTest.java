package org.opennaas.extensions.quantum.controller;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.extensions.quantum.QuantumException;
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.NetworkModel;
import org.opennaas.extensions.quantum.model.QuantumModel;
import org.opennaas.extensions.quantum.model.QuantumModelController;
import org.opennaas.extensions.quantum.network.builder.NetworkBuilderHelper;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class ControllerTest {

	private static final Log		log			= LogFactory.getLog(ControllerTest.class);

	private static final String		NET01_ID	= "abcd-1234-abcd-1234";
	private static final String		NET01_NAME	= "net01";
	private static final String		NET02_ID	= "efgh-5678-efgh-5678";
	private static final String		NET02_NAME	= "net02";
	private static final String		TENANT01_ID	= "tenant01";

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
}
