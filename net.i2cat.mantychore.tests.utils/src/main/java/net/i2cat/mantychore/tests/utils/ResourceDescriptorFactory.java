package net.i2cat.mantychore.tests.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.i2cat.mantychore.ProtocolConstants;

import com.iaasframework.capabilities.actionset.IActionSetConstants;
import com.iaasframework.capabilities.commandset.ICommandSetConstants;
import com.iaasframework.capabilities.model.IModelConstants;
import com.iaasframework.capabilities.protocol.IProtocolConstants;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;
import com.iaasframework.resources.core.descriptor.Information;
import com.iaasframework.resources.core.descriptor.ResourceDescriptor;

public class ResourceDescriptorFactory {

	public ResourceDescriptor newInstanceDescriptorJunOS() {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

		/* identifier */
		resourceDescriptor.setId(UUID.randomUUID().toString());

		/* resource information */
		Information information = new Information();
		information.setName("JunOS Test");
		information.setType("junos"); // Specifies id router

		resourceDescriptor.setInformation(information);

		/* add capability descriptors */
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		return null;

	}

	private CapabilityDescriptor newProtocolDescriptor(long id) {

		CapabilityDescriptor capabDescriptor = new CapabilityDescriptor();
		/* protocol info */
		Information information = new Information();
		information.setDescription("junos protocol");
		information.setName("junos protocol");
		information.setType(IProtocolConstants.PROTOCOL);
		information.setVersion("1.0.0");
		capabDescriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(newProperty(IProtocolConstants.PROTOCOL, "Netconf"));
		properties.add(newProperty(IProtocolConstants.PROTOCOL_VERSION, "1.0.0"));
		properties.add(newProperty(ProtocolConstants.PROTOCOL_URI, "virtual://foouser:foopass@fooserver:22/okServer"));

		/* netconf parameters */

		capabDescriptor.setCapabilityProperties(properties);

		capabDescriptor.setId(id);

		return capabDescriptor;

	}

	private CapabilityDescriptor newModelDescriptor(long id) {

		CapabilityDescriptor capabDescriptor = new CapabilityDescriptor();

		/* model info */
		Information information = new Information();
		information.setDescription("junos model");
		information.setName("junos model");
		information.setType(IModelConstants.MODEL);
		information.setVersion("1.0.0");
		capabDescriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(newProperty(IModelConstants.MODEL_NAME, "router"));
		properties.add(newProperty(IModelConstants.MODEL_VERSION, "1.0.0"));

		capabDescriptor.setCapabilityProperties(properties);

		capabDescriptor.setId(id);

		return capabDescriptor;

	}

	private CapabilityDescriptor newActionSetDescriptor(long id) {

		CapabilityDescriptor capabDescriptor = new CapabilityDescriptor();

		/* model info */
		Information information = new Information();
		information.setDescription("junos actionset");
		information.setName("junos actionset");
		information.setType(IActionSetConstants.ACTIONSET);
		information.setVersion("1.0.0");
		capabDescriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(newProperty(IActionSetConstants.ACTIONSET_NAME, "junos"));
		properties.add(newProperty(IActionSetConstants.ACTIONSET_VERSION, "1.0.0"));

		capabDescriptor.setCapabilityProperties(properties);

		capabDescriptor.setId(id);

		return capabDescriptor;

	}

	private CapabilityDescriptor newCommandSetDescriptor(long id) {

		CapabilityDescriptor capabDescriptor = new CapabilityDescriptor();

		/* model info */
		Information information = new Information();
		information.setDescription("junos commandset");
		information.setName("junos Commandset");
		information.setType(ICommandSetConstants.COMMANDSET);
		information.setVersion("1.0.0");
		capabDescriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(newProperty(ICommandSetConstants.COMMANDSET_NAME, "junos"));
		properties.add(newProperty(ICommandSetConstants.COMMANDSET_VERSION, "1.0.0"));

		capabDescriptor.setCapabilityProperties(properties);

		capabDescriptor.setId(id);

		return capabDescriptor;

	}

	private CapabilityProperty newProperty(String name, String value) {

		CapabilityProperty property = new CapabilityProperty();
		property.setName(name);
		property.setValue(value);
		return property;
	}
}
