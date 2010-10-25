package net.i2cat.mantychore.models.router;

import java.util.ArrayList;
import java.util.List;

import com.iaasframework.capabilities.model.IModelConstants;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;
import com.iaasframework.resources.core.descriptor.Information;

public class RouterModelHelper {

	public static CapabilityDescriptor newModelDescriptor(long id) {

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

	private static CapabilityProperty newProperty(String name, String value) {

		CapabilityProperty property = new CapabilityProperty();
		property.setName(name);
		property.setValue(value);
		return property;
	}

}
