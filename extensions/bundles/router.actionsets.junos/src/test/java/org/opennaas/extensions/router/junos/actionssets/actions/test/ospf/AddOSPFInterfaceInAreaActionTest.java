package org.opennaas.extensions.router.junos.actionssets.actions.test.ospf;

import java.io.IOException;

import junit.framework.Assert;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.ospf.AddOSPFInterfaceInAreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;

public class AddOSPFInterfaceInAreaActionTest {
	Log											log	= LogFactory.getLog(AddOSPFInterfaceInAreaActionTest.class);
	private static AddOSPFInterfaceInAreaAction	action;
	static ActionTestHelper						helper;
	static ProtocolSessionManager				protocolsessionmanager;

	@BeforeClass
	public static void init() {

		action = new AddOSPFInterfaceInAreaAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {

		Assert.assertEquals("Wrong ActionID", ActionConstants.OSPF_ADD_INTERFACE_IN_AREA,
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
		Assert.assertEquals("Not accepted param", "/VM_files/ospfAddInterfaceInArea.vm", action.getTemplate());
	}

	/**
	 * Create two OSPFProtocolEndpoint with state to enable
	 * 
	 * @throws IOException
	 */
	@Test
	public void executeActionTest() throws IOException {

		action.setModelToUpdate(new ComputerSystem());

		// Add params
		OSPFArea ospfArea = getOSPFArea();
		action.setParams(ospfArea);

		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID()
					.equals(ActionConstants.OSPF_ADD_INTERFACE_IN_AREA));
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}

		org.opennaas.extensions.router.model.System computerSystem = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(computerSystem);
	}

	/**
	 * Get the OSPFProtocolEndpoint's
	 * 
	 * @return OSPFArea
	 * @throws IOException
	 */
	private OSPFArea getOSPFArea() throws IOException {

		// Add OSPFArea and areaId = 0.0.0.0
		OSPFArea ospfArea = new OSPFArea();
		ospfArea.setAreaID(0);

		// Interface 1
		ospfArea.addEndpointInArea(getOSPFProtocolEndpoint("fe-0/0/2", "1"));
		// Interface 2
		ospfArea.addEndpointInArea(getOSPFProtocolEndpoint("fe-0/0/2", "2"));

		return ospfArea;
	}

	/**
	 * Create a OSPFProtocolEndpoint from the params
	 * 
	 * @param areaId
	 * @param portName
	 * @return OSPFProtocolEndpoint
	 * @throws IOException
	 */
	private OSPFProtocolEndpoint getOSPFProtocolEndpoint(String logicalPortName, String logicalPortNumber) throws IOException {

		OSPFProtocolEndpoint ospfProtocolEndpoint = new OSPFProtocolEndpoint();
		ospfProtocolEndpoint.setName(logicalPortName + "." + logicalPortNumber);
		ospfProtocolEndpoint.setEnabledState(EnabledState.ENABLED);

		return ospfProtocolEndpoint;
	}
}
