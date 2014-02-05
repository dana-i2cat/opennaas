package org.opennaas.extensions.router.capabilities.api.helper;

/*
 * #%L
 * OpenNaaS :: Router :: IP Capability
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

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.router.capabilities.api.model.ip.IPAddresses;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class IPApi2ModelTranslator {

	public static IPAddresses ipPEPs2IPAddresses(List<IPProtocolEndpoint> peps) {
		IPAddresses ipAddresses = new IPAddresses();
		// arraylist size is fixed knowing that ipPEP2IPAddresses will return 2 elements for each pep at most.
		List<String> completeAddresses = new ArrayList<String>(peps.size() * 2);
		for (IPProtocolEndpoint pep : peps) {
			completeAddresses.addAll(ipPEP2IPAddresses(pep));
		}
		ipAddresses.setIpAddresses(completeAddresses);
		return ipAddresses;
	}

	public static List<String> ipPEP2IPAddresses(IPProtocolEndpoint pep) {

		String ipv4 = pep.getIPv4Address();
		String mask = pep.getSubnetMask();

		String ipv6 = pep.getIPv6Address();
		Short prefix = pep.getPrefixLength();

		String completeipv4 = null;
		String completeipv6 = null;

		if (ipv4 != null && mask != null) {
			completeipv4 = ipv4 + "/" + mask;
		}

		if (ipv6 != null && prefix != null) {
			completeipv6 = ipv6 + "/" + prefix;
		}

		List<String> completeAddresses = new ArrayList<String>(2);
		if (completeipv4 != null)
			completeAddresses.add(completeipv4);
		if (completeipv6 != null)
			completeAddresses.add(completeipv6);

		return completeAddresses;
	}

}
