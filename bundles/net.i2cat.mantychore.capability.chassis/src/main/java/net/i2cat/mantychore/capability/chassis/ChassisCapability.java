package net.i2cat.mantychore.capability.chassis;

import java.util.Properties;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.message.ICapabilityMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChassisCapability implements ICapability {

	Logger			log			= LoggerFactory
										.getLogger(ChassisCapability.class);

	private String	resourceId	= "";

	public ChassisCapability(String resourceId) {
		this.resourceId = resourceId;
	}

	public void handleMessage(String message) {

	}

	public CapabilityDescriptor getCapabilityDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public Information getCapabilityInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendMessage(ICapabilityMessage arg0, String arg1) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	public void sendMessage(ICapabilityMessage arg0, Properties arg1) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	public void sendMessage(ICapabilityMessage arg0, String arg1, Object arg2) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	public void sendResponse(ICapabilityMessage arg0, String arg1) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	public void setCapabilityDescriptor(CapabilityDescriptor arg0) {
		// TODO Auto-generated method stub

	}

	public void setResource(IResource arg0) {
		// TODO Auto-generated method stub

	}

	public void activate() throws ResourceException {
		// TODO Auto-generated method stub

	}

	public void deactivate() throws ResourceException {
		// TODO Auto-generated method stub

	}

	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize() throws ResourceException {
		// TODO Auto-generated method stub

	}

	public void setState(State arg0) {
		// TODO Auto-generated method stub

	}

	public void shutdown() throws ResourceException {
		// TODO Auto-generated method stub

	}

}
