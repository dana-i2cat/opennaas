/**
 * 
 */
package org.opennaas.web.utils;

import java.util.List;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;

/**
 * @author Jordi
 * 
 */
public class DescriptorUtils {

	/**
	 * @return
	 */
	public static CapabilityDescriptor getCapabilityDescriptor(String name, String description, String type, String actionName, String actionVersion) {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		List<CapabilityProperty> listProperties = capabilityDescriptor.getCapabilityProperties();
		listProperties.add(getCapabilityPropery("actionset.name", actionName));
		listProperties.add(getCapabilityPropery("actionset.version", actionVersion));

		capabilityDescriptor.setCapabilityInformation(getInformation(name, description, type, null));

		return capabilityDescriptor;
	}

	/**
	 * @return
	 */
	public static CapabilityProperty getCapabilityPropery(String name, String value) {
		CapabilityProperty capabilityProperty = new CapabilityProperty();
		capabilityProperty.setName(name);
		capabilityProperty.setValue(value);
		return capabilityProperty;
	}

	/**
	 * @return
	 */
	public static Information getInformation(String name, String description, String type, String version) {
		Information information = new Information();
		information.setDescription(description);
		information.setName(name);
		information.setType(type);
		information.setVersion(version);
		return information;
	}
}
