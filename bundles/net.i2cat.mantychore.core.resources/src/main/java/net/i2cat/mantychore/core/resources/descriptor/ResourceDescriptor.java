package net.i2cat.mantychore.core.resources.descriptor;

import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Resource Descriptor with JPA and JAXB annotations
 * to provide both persistence and XML marshaling capabilities.
 * @author Mathieu Lemay (ITI)
 *
 */
@XmlRootElement
@Entity
@org.hibernate.annotations.NamedQueries({
    @org.hibernate.annotations.NamedQuery(
        name = "resourceDescriptor.findByType", 
        query="from ResourceDescriptor r where r.information.type = :type") 
}) 
public class ResourceDescriptor {
	
	private static final long serialVersionUID = -8571009012048021984L;
	
	@Id 
	private String id;
	
	@Embedded
	private Information information;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<CapabilityDescriptor> capabilityDescriptors;
	
	@XmlElement(name="capabilityDescriptors")
	public List<CapabilityDescriptor> getCapabilityDescriptors() {
	  return capabilityDescriptors;
	}

	public void setCapabilityDescriptors(List<CapabilityDescriptor> capabilityDescriptors) {
		this.capabilityDescriptors = capabilityDescriptors;  
	}
	
	@XmlElement(name="information")
	public Information getInformation() {
		return information;
	}

	public void setInformation(Information information) {
		this.information = information;
	}
		
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public CapabilityDescriptor getCapabilityDescriptor(String capabilityType){
		for(int i=0; i<capabilityDescriptors.size(); i++){
			if (capabilityDescriptors.get(i).getCapabilityInformation().getType().equals(capabilityType)){
				return capabilityDescriptors.get(i);
			}
		}
		
		return null;
	}
}