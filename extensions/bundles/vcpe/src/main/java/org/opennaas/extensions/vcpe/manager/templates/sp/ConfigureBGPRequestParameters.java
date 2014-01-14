package org.opennaas.extensions.vcpe.manager.templates.sp;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.AutonomousSystem;
import org.opennaas.extensions.router.model.BGPPeerGroup;
import org.opennaas.extensions.router.model.BGPService;
import org.opennaas.extensions.router.model.Policy;

@XmlRootElement
public class ConfigureBGPRequestParameters {

	BGPService			bgpService;

	AutonomousSystem	as;

	List<BGPPeerGroup>	groups;

	List<Policy>		policies;

	/*
	 * TMP DATA
	 */

	/**
	 * IP address ranges used by customer
	 */
	List<String>		clientIPRanges;

	/**
	 * Gateway address of the local LAN connection
	 */
	String				gatewayIPAddress;

	/**
	 * IP address of upstream link (on the CPE)
	 */
	String				upAddr1;
	String				upAddr2;
	/**
	 * IPv6 address of upstream link (on the CPE)
	 */
	String				upAddr1v6;
	String				upAddr2v6;

	/**
	 * IP address of remote end of upstream link
	 */
	String				upRemoteAddr1;
	String				upRemoteAddr2;
	/**
	 * IPv6 address of remote end of upstream link
	 */
	String				upRemoteAddr1v6;
	String				upRemoteAddr2v6;

	/**
	 * VLANs for the upstream links
	 */
	int					upVlan1;
	int					upVlan2;

	/**
	 * IP address of inter link (on the CPE)
	 */
	String				interAddr1;
	String				interAddr2;
	/**
	 * IPv6 address of inter link (on the CPE)
	 */
	String				interAddr1v6;
	String				interAddr2v6;

	/**
	 * IP address of lo0 interface on each CPE
	 */
	String				loAddr1;
	String				loAddr2;
	/**
	 * IP address of lo0 interface on each CPE
	 */
	String				loAddr1v6;
	String				loAddr2v6;

	/**
	 * Autonomous System number assigned to this client
	 */
	String				clientASNumber;
	/**
	 * AS number of the ISP, 1213 for HEAnet
	 */
	String				remoteASNum;

	/**
	 * BGP service global config
	 */
	String				pathSelection				= "cisco-non-deterministic";
	int					holdTime					= 45;
	String				keep						= "all";

	/**
	 * BGP per circuit config
	 */
	int					prefixLimitMax				= 20;
	int					prefixLimitTeardown			= 75;
	String				prefixLimitTeardownTimeout	= "forever";

}
