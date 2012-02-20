package net.i2cat.mantychore.actionsets.junos.actions.test.ospf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.ospf.EnableOSPFInInterfaceAction;
import net.i2cat.mantychore.actionsets.junos.actions.test.ActionTestHelper;
import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.OSPFArea;
import net.i2cat.mantychore.model.OSPFProtocolEndpoint;
import net.i2cat.mantychore.model.ProtocolEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;

public class EnableOSPFInInterfaceActionTest {
	Log											log	= LogFactory.getLog(EnableOSPFInInterfaceActionTest.class);
	private static EnableOSPFInInterfaceAction	action;
	static ActionTestHelper						helper;
	static ProtocolSessionManager				protocolsessionmanager;

	@BeforeClass
	public static void init() {

		action = new EnableOSPFInInterfaceAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {

		Assert.assertEquals("Wrong ActionID", ActionConstants.OSPF_ENABLE_INTERFACE, action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNotNull("Null parameters", action.getParams());
	}

	@Test
	public void templateTest() {
		// this action always have this template as a default
		Assert.assertEquals("Not accepted param", "/VM_files/enableOSPFInterface.vm", action.getTemplate());
	}

	@Test
	public void executeActionTest() throws IOException {
		// enable OSPF in Interface
		action.setModelToUpdate(new ComputerSystem());

		// Add LogicalPort
		LogicalPort logicalPort = getLogicalPort();
		action.setParams(logicalPort);

		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID().equals("enableOSPFInInterface"));

		} catch (ActionException e) {

			e.printStackTrace();
			Assert.fail();
		}

		net.i2cat.mantychore.model.System computerSystem = (net.i2cat.mantychore.model.System) action.getModelToUpdate();
		Assert.assertNotNull(computerSystem);

	}

	/**
	 * @return LogicalPort
	 * @throws IOException
	 */
	private LogicalPort getLogicalPort() throws IOException {
		// The logical is the action parameter
		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName("fe-0/0/2");

		// Add the OSPF EndPoint and the ospf area
		List<ProtocolEndpoint> lProtocolEndpoints = new ArrayList<ProtocolEndpoint>();
		OSPFProtocolEndpoint ospfProtocolEndpoint = new OSPFProtocolEndpoint();
		lProtocolEndpoints.add(ospfProtocolEndpoint);

		// Add the OSPF Area
		OSPFArea ospfArea = new OSPFArea();
		ospfArea.setAreaID(IPUtilsHelper.ipv4StringToLong("0.0.0.0"));
		ospfProtocolEndpoint.setOSPFArea(ospfArea);

		logicalPort.setProtocolEndpoints(lProtocolEndpoints);

		return logicalPort;
	}
}
