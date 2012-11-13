package org.opennaas.extensions.router.junos.actionssets.velocity.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class IPV4TemplatesTest extends VelocityTemplatesTest {
	// This class if for testing the velocity templates
	// to check input params and the output
	Log				log			= LogFactory.getLog(VelocityTemplatesTest.class);
	private String	template	= null;

	@Test
	public void testsetIpv4Template() {
		template = "/VM_files/configureIPv4.vm";
		IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("ipUtilsHelper", ipUtilsHelper);

		String message = callVelocity(template, newParamsInterfaceLT(), extraParams);
		Assert.assertNotNull(message);
		log.info(message);
	}

	private LogicalTunnelPort newParamsInterfaceLT() {
		LogicalTunnelPort ltp = new LogicalTunnelPort();
		ltp.setElementName("");
		ltp.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		ltp.setName("lt-0/3/2");
		ltp.setPeer_unit(101);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		ltp.addProtocolEndpoint(ip);
		return ltp;
	}

}
