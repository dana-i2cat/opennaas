package net.i2cat.mantychore.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.RegistryUtil;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMantychoreCapability extends AbstractCapability {
	protected List<String>	actionIds	= new ArrayList<String>();

	Logger					log			= LoggerFactory
												.getLogger(AbstractMantychoreCapability.class);

	public AbstractMantychoreCapability(List<String> actionIds,
			IResource resource, CapabilityDescriptor capabilityDescriptor) {

		super(capabilityDescriptor);
		super.resource = resource;
		this.actionIds = actionIds;
	}

	public List<String> getActionIds() {
		return actionIds;

	}

	public Object getCapability(Class clazz, BundleContext context,
			Properties filterProperties) throws CapabilityException {

		try {
			Filter filter = RegistryUtil.createServiceFilter(clazz.getName(),
					filterProperties);

			return RegistryUtil.getServiceFromRegistry(context, filter);

		} catch (Exception e) {
			log.error("Error creating capability " + e.getMessage(), e);
			CapabilityException cEx = new CapabilityException(
					"Error creating capability ", e);
			cEx.initCause(e);
			throw cEx;
		}

	}

	public abstract Response sendMessage(String idOperation, Object paramsModel);

}
