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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class RepositoryHelper {
	static Log	log	= LogFactory
							.getLog(RepositoryHelper.class);

	public static ResourceDescriptor newResourceDescriptor(String type, String name) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Map<String, String> properties = new HashMap<String, String>();
		resourceDescriptor.setProperties(properties);
		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(type);
		information.setName(name);
		resourceDescriptor.setInformation(information);
		return resourceDescriptor;
	}

	public static CapabilityDescriptor newConnectionsCapabilityDescriptor() {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, "proteus");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_VERSION, "1.0");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		capabilityDescriptor.getCapabilityProperties().add(property);

		Information capabilityInformation = new Information();
		capabilityInformation.setType("connections");
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);

		return capabilityDescriptor;
	}

	public static CapabilityDescriptor newQueueCapabilityDescriptor() {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		// TODO IS IT EXIT A BETTER METHOD TO PASS THE URI
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("") || uri.isEmpty()) {
			log.info("INFO: Getting uri param from terminal");
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.PROTOCOL_URI, uri);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, "proteus");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_VERSION, "1.0");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_PROTOCOL, "wonesys");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		capabilityDescriptor.getCapabilityProperties().add(property);

		Information capabilityInformation = new Information();
		capabilityInformation.setType("queue");
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);

		return capabilityDescriptor;
	}

}
