package net.i2cat.mantychore.utils.capabilities;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.constants.ProtocolConstants;

import com.iaasframework.capabilities.actionset.IActionSetConstants;
import com.iaasframework.capabilities.commandset.ICommandSetConstants;
import com.iaasframework.capabilities.model.IModelConstants;
import com.iaasframework.capabilities.protocol.IProtocolConstants;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;
import com.iaasframework.resources.core.descriptor.Information;

public class DescriptorFactory {
	private static final String	CAPABILITIES_VERSION	= "1.0.0";
	private static final String	TYPE					= "junos";
	private static final String	TYPE_MODEL				= "router";
	private static final String	TYPE_PROTOCOL			= "Netconf";

	public CapabilityDescriptor newModelDescriptor(long id) {
		CapabilityDescriptor descriptor = new CapabilityDescriptor();
		/* configure descriptor */

		/* model info */
		Information information = new Information();
		information.setDescription("junos model");
		information.setName("junos model");
		information.setType(IModelConstants.MODEL);
		information.setVersion(CAPABILITIES_VERSION);
		descriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(newProperty(IModelConstants.MODEL_NAME, TYPE_MODEL));
		properties.add(newProperty(IModelConstants.MODEL_VERSION, CAPABILITIES_VERSION));

		descriptor.setCapabilityProperties(properties);

		descriptor.setId(id);

		return descriptor;
	}

	public CapabilityDescriptor newActionSetDescriptor(long id) {
		CapabilityDescriptor descriptor = new CapabilityDescriptor();
		/* configure descriptor */

		/* action info */
		Information information = new Information();
		information.setDescription("junos actionset");
		information.setName("junos actionset");
		information.setType(IActionSetConstants.ACTIONSET);
		information.setVersion(CAPABILITIES_VERSION);
		descriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(newProperty(IActionSetConstants.ACTIONSET_NAME, TYPE));
		properties.add(newProperty(IActionSetConstants.ACTIONSET_VERSION, CAPABILITIES_VERSION));

		descriptor.setCapabilityProperties(properties);

		descriptor.setId(id);

		return descriptor;
	}

	public CapabilityDescriptor newProtocolDescriptor(long id, String uri) {
		CapabilityDescriptor descriptor = new CapabilityDescriptor();

		/* configure descriptor */
		/* protocol info */
		Information information = new Information();
		information.setDescription("junos protocol");
		information.setName("junos protocol");
		information.setType(IProtocolConstants.PROTOCOL);
		information.setVersion(CAPABILITIES_VERSION);
		descriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(newProperty(IProtocolConstants.PROTOCOL, TYPE_PROTOCOL));
		properties.add(newProperty(IProtocolConstants.PROTOCOL_VERSION, CAPABILITIES_VERSION));
		properties.add(newProperty(ProtocolConstants.PROTOCOL_URI, uri));
		// type of uri "virtual://foouser:foopass@fooserver:22/okServer"
		/* netconf parameters */

		descriptor.setCapabilityProperties(properties);

		descriptor.setId(id);

		return descriptor;
	}

	public CapabilityDescriptor newCommandSetDescriptor(long id) {
		CapabilityDescriptor descriptor = new CapabilityDescriptor();
		/* configure descriptor */

		/* command info */
		Information information = new Information();
		information.setDescription("junos commandset");
		information.setName("junos Commandset");
		information.setType(ICommandSetConstants.COMMANDSET);
		information.setVersion(CAPABILITIES_VERSION);
		descriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(newProperty(ICommandSetConstants.COMMANDSET_NAME, TYPE));
		properties.add(newProperty(ICommandSetConstants.COMMANDSET_VERSION, CAPABILITIES_VERSION));

		descriptor.setCapabilityProperties(properties);

		descriptor.setId(id);

		return descriptor;
	}

	private CapabilityProperty newProperty(String name, String value) {

		CapabilityProperty property = new CapabilityProperty();
		property.setName(name);
		property.setValue(value);
		return property;
	}

}
