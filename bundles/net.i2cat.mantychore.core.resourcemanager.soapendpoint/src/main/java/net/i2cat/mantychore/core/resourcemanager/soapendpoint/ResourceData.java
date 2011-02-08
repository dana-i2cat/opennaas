package net.i2cat.mantychore.core.resourcemanager.soapendpoint;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.i2cat.mantychore.core.resources.IResource;
import net.i2cat.mantychore.core.resources.ILifecycle.State;
import net.i2cat.mantychore.core.resources.capability.ICapability;

@XmlRootElement
public class ResourceData {
	
	/** The id of the resource **/
	private String id;
	
	/** The type of the resource **/
	private String type;
	
	/** The name or alias **/
	private String name;
	
	/** The version **/
	private String version;
	
	/** The engine current state **/
	private State state;
	
	/** Description **/
	private String description;

	/** The resource capability data **/
	private List<CapabilityData> capabilitiesData = null;

	public ResourceData(){
	}
	
	public ResourceData(IResource resource){
		id = resource.getResourceIdentifier().getId().toString();
		type = resource.getResourceDescriptor().getInformation().getType();
		name = resource.getResourceDescriptor().getInformation().getName();
		version = resource.getResourceDescriptor().getInformation().getVersion();
		state = resource.getState();
		description = resource.getResourceDescriptor().getInformation().getDescription();
		capabilitiesData = getCapabilityDataFromCapability(resource.getCapabilities());
	}
	
	public List<CapabilityData> getCapabilityDataFromCapability(List<ICapability> capabilities){
		CapabilityData capabilityData = null;
		List<CapabilityData> capabilitiesData = new ArrayList<CapabilityData>();
		
		for(ICapability capability:capabilities){
			capabilityData = new CapabilityData(capability);
			capabilitiesData.add(capabilityData);
		}
		
		return capabilitiesData;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@XmlElement(name="state")
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name="capabilitiesData")
	public List<CapabilityData> getCapabilitiesData() {
		return capabilitiesData;
	}

	public void setCapabilitiesData(List<CapabilityData> capabilitiesData) {
		this.capabilitiesData = capabilitiesData;
	}
}