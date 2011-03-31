package net.i2cat.mantychore.commons;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;

public abstract class AbstractMantychoreCapability extends AbstractCapability {
	protected List<String>	actionIds	= new ArrayList<String>();

	public AbstractMantychoreCapability(List<String> actionIds,
			IResource resource, CapabilityDescriptor capabilityDescriptor) {

		super(capabilityDescriptor);
		super.resource = resource;
		this.actionIds = actionIds;
	}

	public List<String> getActionIds() {
		return actionIds;

	}

	public abstract Response sendMessage(String idOperation, Object paramsModel);

}
