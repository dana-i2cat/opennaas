package net.i2cat.nexus.resourcemanager.soapendpoint;

import javax.xml.bind.annotation.XmlElement;

import net.i2cat.nexus.resources.ILifecycle.State;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.capability.ICapability;

public class CapabilityData{

	/** The descriptor for this capability **/
	private CapabilityDescriptor capabilityDescriptor;

	/** The capability current state **/
	private State state;
	
	public CapabilityData(){
	}
	
	public CapabilityData(ICapability capability){
		capabilityDescriptor = capability.getCapabilityDescriptor();
		state = capability.getState();
	}
	
	@XmlElement(name="capabilityDescriptor")
	public CapabilityDescriptor getCapabilityDescriptor() {
		return capabilityDescriptor;
	}
	
	public void setCapabilityDescriptor(CapabilityDescriptor capabilityDescriptor) {
		this.capabilityDescriptor = capabilityDescriptor;
	}
	
	@XmlElement(name="state")
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
}