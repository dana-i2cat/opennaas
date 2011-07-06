import java.util.List;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.ConfigureVLANAction;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class SetVLANActionTest {

	private static ConfigureVLANAction	action;
	Log									log	= LogFactory.getLog(SetVLANActionTest.class);
	static ActionTestHelper				helper;
	static ProtocolSessionManager		protocolsessionmanager;

	@BeforeClass
	public static void init() {
		action = new ConfigureVLANAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void TestActionID() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.GETCONFIG, action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have params

	}

	@Test
	public void templateTest() {
		// this action always have this template as a default
		Assert.assertEquals("Not accepted param", "/VM_files/getconfiguration.vm", action.getTemplate());
	}

	public Object prepareParams() {

		return null;
	}

	@Test
	public void testExecute() {

		// prepare params for this test

		action.setParams(null);
		try {
			ActionResponse response = action.execute(protocolsessionmanager);
		} catch (ActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		// for test
		net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) action.getModelToUpdate();
		List<LogicalDevice> ld = routerModel.getLogicalDevices();
		for (LogicalDevice l : ld) {
			if (l instanceof EthernetPort) {
				EthernetPort ethport = (EthernetPort) l;

				// show data of ETH
				// name, linkTecnology
				// Only check the modified interface
				if (ethport.getElementName().equalsIgnoreCase("fe-0/1/2")) {
					if (ethport.getPortNumber() == 0) {
						Assert.assertEquals(ethport.getLinkTechnology().toString(), "ETHERNET");
					} else {
						Assert.assertNotSame(ethport.getLinkTechnology().toString(), "ETHERNET");
					}

					List<ProtocolEndpoint> pp = ethport.getProtocolEndpoint();
					for (ProtocolEndpoint p : pp) {
						if (p instanceof VLANEndpoint) {
							// show tha VLAN setted for this LT
							Assert.assertEquals(((VLANEndpoint) p).getVlanID(), 1);
						}

					}
				}
			}
		}
	}
}
