package org.opennaas.itests.roadm.helpers;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
