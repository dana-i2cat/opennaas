package net.i2cat.mantychore.protocols.netconf;

import java.util.ArrayList;
import java.util.List;

import com.iaasframework.capabilities.protocol.IProtocolConstants;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;
import com.iaasframework.resources.core.descriptor.Information;

public class NetconfProtocolHelper {
	/* Descriptors to testing */

	public static CapabilityDescriptor newProtocolDescriptor(long id, String protocolURI) {

		CapabilityDescriptor capabDescriptor = new CapabilityDescriptor();
		/* protocol info */
		Information information = new Information();
		information.setDescription("junos protocol");
		information.setName("junos protocol");
		information.setType(IProtocolConstants.PROTOCOL);
		information.setVersion("1.0.0");
		capabDescriptor.setCapabilityInformation(information);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();

		properties.add(getCapabilityProperty(IProtocolConstants.PROTOCOL, "Netconf"));
		properties.add(getCapabilityProperty(IProtocolConstants.PROTOCOL_VERSION, "1.0.0"));
		properties.add(getCapabilityProperty(NetconfProtocolSession.PROTOCOL_URI, protocolURI));

		/* netconf parameters */

		capabDescriptor.setCapabilityProperties(properties);

		capabDescriptor.setId(id);

		return capabDescriptor;

	}

	private static CapabilityProperty getCapabilityProperty(String propertyName,
			String propertyValue) {
		CapabilityProperty capabilityProperty = new
				CapabilityProperty();
		capabilityProperty.setName(propertyName);
		capabilityProperty.setValue(propertyValue);
		return capabilityProperty;
	}

}
