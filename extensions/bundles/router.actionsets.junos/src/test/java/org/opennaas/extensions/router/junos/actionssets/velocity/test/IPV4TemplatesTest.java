package org.opennaas.extensions.router.junos.actionssets.velocity.test;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

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
