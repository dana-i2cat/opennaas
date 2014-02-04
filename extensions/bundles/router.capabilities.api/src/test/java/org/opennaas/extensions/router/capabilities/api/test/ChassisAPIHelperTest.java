package org.opennaas.extensions.router.capabilities.api.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.router.capabilities.api.helper.ChassisAPIHelper;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfo;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.VLANEndpoint;

/**
 * {@link ChassisAPIHelper} class Unit tests
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class ChassisAPIHelperTest {

	private static final String	IFACE				= "ge-2/0/0";
	private static final String	PORT				= "13";
	private static final String	IFACE_NAME			= IFACE + "." + PORT;
	private static final String	IFACE_VLAN			= "13";
	private static final String	IFACE_STATE			= "OK";
	private static final String	IFACE_DESCRIPTION	= "Test description.";

	@Test
	public void testInterfaceInfo2NetworkPort() {
		InterfaceInfo ii = buildValidInterfaceInfo();
		NetworkPort np = ChassisAPIHelper.interfaceInfo2NetworkPort(ii);

		Assert.assertNotNull("Generated NetworkPort should be not null", np);
		Assert.assertEquals("Name must be " + IFACE, IFACE, np.getName());
		Assert.assertEquals("Port must be " + PORT, PORT, String.valueOf(np.getPortNumber()));

		List<ProtocolEndpoint> pe = np.getProtocolEndpoint();
		Assert.assertNotNull("Generated NetworkPort must contain ProtocolEndpoints.", pe);
		Assert.assertTrue("Generated NetworkPort must contain just 1 ProtocolEndpoint.", pe.size() == 1);
		Assert.assertTrue("NetworkPort's ProtocolEndpoint must be a VLANEndpoint.", pe.get(0) instanceof VLANEndpoint);

		VLANEndpoint vep = (VLANEndpoint) pe.get(0);
		int vlan = vep.getVlanID();
		Assert.assertEquals("VLAN ID must be " + IFACE_VLAN, IFACE_VLAN, String.valueOf(vlan));

		Assert.assertEquals("Description must be " + IFACE_DESCRIPTION, IFACE_DESCRIPTION, np.getDescription());
	}

	private static InterfaceInfo buildValidInterfaceInfo() {
		InterfaceInfo ii = new InterfaceInfo();
		ii.setName(IFACE_NAME);
		ii.setVlan(IFACE_VLAN);
		ii.setState(IFACE_STATE);
		ii.setDescription(IFACE_DESCRIPTION);

		return ii;
	}
}
