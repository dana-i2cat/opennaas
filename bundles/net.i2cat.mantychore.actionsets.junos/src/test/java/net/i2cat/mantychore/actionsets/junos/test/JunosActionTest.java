package net.i2cat.mantychore.actionsets.junos.test;

import com.iaasframework.capabilities.actionset.ActionSetCapability;
import com.iaasframework.capabilities.actionset.ActionSetCapabilityFactory;
import com.iaasframework.resources.core.capability.CapabilityException;

public class JunosActionTest {

	private ProtocolCapabilityFactory	protocolCapabilityFactory;

	private void createAndInitializeProtocolCapability() throws CapabilityException {
		protocolCapabilityFactory = (ProtocolCapabilityFactory) getCapabilityFactory("protocol", "1.0.0");
		protocolCapability = (ProtocolCapability) protocolCapabilityFactory.create(getProtocolCapabilityDescriptor(), resourceId);
		protocolCapability.initialize();
		protocolCapability.activate();
	}

	private void createAndInitializeCommandSetCapability() throws CapabilityException {
		commandSetCapabilityFactory = (CommandSetCapabilityFactory) getCapabilityFactory("CommandSet", "1.0.0");
		commandSetCapability = (CommandSetCapability) commandSetCapabilityFactory.create(getCommandSetCapabilityDescriptor(), resourceId);
		commandSetCapability.initialize();
		commandSetCapability.activate();
	}

	private void createAndInitializeModelCapability() throws CapabilityException {
		modelCapabilityFactory = (ModelCapabilityFactory) getCapabilityFactory("Model", "1.0.0");
		modelCapability = (ModelCapability) modelCapabilityFactory.create(getModelCapabilityDescriptor(), resourceId);
		modelCapability.initialize();
		modelCapability.activate();
	}

	private void createAndInitializeActionSetCapability() throws CapabilityException {
		actionSetCapabilityFactory = (ActionSetCapabilityFactory) getCapabilityFactory("ActionSet", "1.0.0");
		;
		actionSetCapability = (ActionSetCapability) actionSetCapabilityFactory.create(getActionSetCapabilityDescriptor(), resourceId);
		actionSetCapability.initialize();
		actionSetCapability.activate();
	}

}
