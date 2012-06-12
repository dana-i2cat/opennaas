package org.opennaas.extensions.router.junos.actionssets.actions.test.ospf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.ConfigureOSPFInterfaceStatusAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;

public class ConfigureOSPFInterfaceStatusActionTest {
	Log													log	= LogFactory.getLog(ConfigureOSPFInterfaceStatusActionTest.class);
	private static ConfigureOSPFInterfaceStatusAction	action;
	static ActionTestHelper								helper;
	static ProtocolSessionManager						protocolsessionmanager;

	@BeforeClass
	public static void init() {

		action = new ConfigureOSPFInterfaceStatusAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {

		Assert.assertEquals("Wrong ActionID", ActionConstants.OSPF_ENABLE_INTERFACE + "/" + ActionConstants.OSPF_DISABLE_INTERFACE,
				action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNotNull("Null parameters", action.getParams());
	}

	@Test
	public void templateTest() {
		// this action always have this template as a default
		Assert.assertEquals("Not accepted param", "/VM_files/ospfConfigureInterfaceStatus.vm", action.getTemplate());
	}

	/**
	 * Create two OSPFProtocolEndpoint with state to enable
	 * 
	 * @throws IOException
	 */
	@Test
	public void executeActionTest() throws IOException {

		action.setModelToUpdate(craftModel());

		// Add params
		List<OSPFProtocolEndpoint> lOSPFProtocolEndpoints = getListOSPFProtocolEndpoint();
		action.setParams(lOSPFProtocolEndpoints);

		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID()
					.equals(ActionConstants.OSPF_ENABLE_INTERFACE + "/" + ActionConstants.OSPF_DISABLE_INTERFACE));
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		org.opennaas.extensions.router.model.System computerSystem = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(computerSystem);

	}

	/**
	 * Get the OSPFProtocolEndpoint's
	 * 
	 * @return LogicalPort
	 * @throws IOException
	 */
	private List<OSPFProtocolEndpoint> getListOSPFProtocolEndpoint() throws IOException {

		// Add the OSPF EndPoints
		List<OSPFProtocolEndpoint> lProtocolEndpoints = new ArrayList<OSPFProtocolEndpoint>();

		// Interface 1
		lProtocolEndpoints.add(getOSPFProtocolEndpoint("0.0.0.0", "fe-0/0/2", "1"));
		// Interface 2
		lProtocolEndpoints.add(getOSPFProtocolEndpoint("0.0.0.1", "fe-0/0/2", "2"));

		return lProtocolEndpoints;
	}

	/**
	 * Create a OSPFProtocolEndpoint from the params
	 * 
	 * @param areaId
	 * @param portName
	 * @return OSPFProtocolEndpoint
	 * @throws IOException
	 */
	private OSPFProtocolEndpoint getOSPFProtocolEndpoint(String areaId, String logicalPortName, String logicalPortNumber) throws IOException {

		OSPFProtocolEndpoint ospfProtocolEndpoint = new OSPFProtocolEndpoint();

		// Add the OSPF Area
		OSPFArea ospfArea = new OSPFArea();
		ospfArea.setAreaID(IPUtilsHelper.ipv4StringToLong(areaId));
		ospfProtocolEndpoint.setOSPFArea(ospfArea);
		ospfProtocolEndpoint.setName(logicalPortName + "." + logicalPortNumber);

		ospfProtocolEndpoint.setEnabledState(EnabledState.ENABLED);

		return ospfProtocolEndpoint;
	}

	private ComputerSystem craftModel() throws IOException {
		ComputerSystem model = new ComputerSystem();

		OSPFService ospfService = new OSPFService();

		OSPFAreaConfiguration config1 = new OSPFAreaConfiguration();
		OSPFProtocolEndpoint pep1 = getOSPFProtocolEndpoint("0.0.0.0", "fe-0/0/2", "1");
		pep1.getOSPFArea().setConfiguration(config1);
		ospfService.addOSPFAreaConfiguration(config1);

		OSPFAreaConfiguration config2 = new OSPFAreaConfiguration();
		OSPFProtocolEndpoint pep2 = getOSPFProtocolEndpoint("0.0.0.1", "fe-0/0/2", "2");
		pep2.getOSPFArea().setConfiguration(config2);
		ospfService.addOSPFAreaConfiguration(config2);

		model.addHostedService(ospfService);
		return model;
	}

}
