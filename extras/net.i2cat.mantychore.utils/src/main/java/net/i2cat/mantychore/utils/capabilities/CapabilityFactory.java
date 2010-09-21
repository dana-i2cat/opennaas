package net.i2cat.mantychore.utils.capabilities;

import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

import com.iaasframework.capabilities.actionset.ActionSetCapability;
import com.iaasframework.capabilities.actionset.ActionSetCapabilityFactory;
import com.iaasframework.capabilities.actionset.IActionSetConstants;
import com.iaasframework.capabilities.commandset.CommandSetCapability;
import com.iaasframework.capabilities.commandset.CommandSetCapabilityFactory;
import com.iaasframework.capabilities.commandset.ICommandSetConstants;
import com.iaasframework.capabilities.model.IModelConstants;
import com.iaasframework.capabilities.model.ModelCapability;
import com.iaasframework.capabilities.model.ModelCapabilityFactory;
import com.iaasframework.capabilities.protocol.IProtocolConstants;
import com.iaasframework.capabilities.protocol.ProtocolCapability;
import com.iaasframework.capabilities.protocol.ProtocolCapabilityFactory;
import com.iaasframework.resources.core.RegistryUtil;
import com.iaasframework.resources.core.capability.CapabilityException;
import com.iaasframework.resources.core.capability.ICapabilityFactory;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class CapabilityFactory {

	public ProtocolCapability newProtocolCapability(BundleContext context, CapabilityDescriptor descriptor, String resourceId)
			throws CapabilityException {
		ProtocolCapabilityFactory protocolCapabilityFactory = (ProtocolCapabilityFactory) getFactory(context, IProtocolConstants.PROTOCOL, "1.0.0");
		ProtocolCapability protocolCapability = (ProtocolCapability) protocolCapabilityFactory.create(descriptor, resourceId);
		return protocolCapability;
	}

	public CommandSetCapability newCommandSetCapability(BundleContext context, CapabilityDescriptor descriptor, String resourceId)
			throws CapabilityException {
		CommandSetCapabilityFactory commandSetCapabilityFactory = (CommandSetCapabilityFactory) getFactory(context, ICommandSetConstants.COMMANDSET,
				"1.0.0");
		CommandSetCapability commandSetCapability = (CommandSetCapability) commandSetCapabilityFactory.create(descriptor, resourceId);
		return commandSetCapability;
	}

	public ModelCapability newModelCapability(BundleContext context, CapabilityDescriptor descriptor, String resourceId) throws CapabilityException {
		ModelCapabilityFactory modelCapabilityFactory = (ModelCapabilityFactory) getFactory(context, IModelConstants.MODEL, "1.0.0");
		ModelCapability modelCapability = (ModelCapability) modelCapabilityFactory.create(descriptor, resourceId);
		return modelCapability;
	}

	public ActionSetCapability newActionSetCapability(BundleContext context, CapabilityDescriptor descriptor, String resourceId)
			throws CapabilityException {
		ActionSetCapabilityFactory actionSetCapabilityFactory = (ActionSetCapabilityFactory) getFactory(context, IActionSetConstants.ACTIONSET,
				"1.0.0");
		ActionSetCapability actionSetCapability = (ActionSetCapability) actionSetCapabilityFactory.create(descriptor, resourceId);
		return actionSetCapability;
	}

	private ICapabilityFactory getFactory(BundleContext context, String name, String version) {
		ICapabilityFactory capabilityFactory = null;
		try {
			Filter filter = RegistryUtil.createServiceFilter(ICapabilityFactory.class
					.getName(), createFilterProperties(name, version));
			capabilityFactory = (ICapabilityFactory) RegistryUtil.getServiceFromRegistry(
					context, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return capabilityFactory;
	}

	private Properties createFilterProperties(String name, String version) {
		Properties properties = new Properties();
		properties.put("capability", name);
		properties.put("capability.version", version);

		return properties;
	}
}
