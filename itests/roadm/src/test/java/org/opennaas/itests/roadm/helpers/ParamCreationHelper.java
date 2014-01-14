package org.opennaas.itests.roadm.helpers;

/*
 * #%L
 * OpenNaaS :: iTests :: ROADM
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

import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.VLANEndpoint;

public class ParamCreationHelper {

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public static Object newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName("fe-0/3/2");
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public static Object newParamsInterfaceLT() {
		LogicalTunnelPort ltp = new LogicalTunnelPort();
		ltp.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		ltp.setName("lt-0/3/2");
		ltp.setPeer_unit(101);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		ltp.addProtocolEndpoint(ip);

		return ltp;
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public static Object newParamsInterfaceLT_VLAN() {
		LogicalTunnelPort ltp = new LogicalTunnelPort();
		ltp.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		ltp.setName("lt-0/3/2");
		ltp.setPeer_unit(101);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		ltp.addProtocolEndpoint(ip);
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(2);
		ltp.addProtocolEndpoint(vlan);
		return ltp;
	}

	public static Object newParamsInterfaceEtherVLAN() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		eth.setName("fe-0/3/2");
		eth.setPortNumber(2);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(2);
		eth.addProtocolEndpoint(vlan);
		return eth;
	}
}
