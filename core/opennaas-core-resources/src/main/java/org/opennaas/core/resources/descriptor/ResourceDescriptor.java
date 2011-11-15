package org.opennaas.core.resources.descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Resource Descriptor with JPA and JAXB annotations to provide both persistence and XML marshaling capabilities.
 * 
 * @author Mathieu Lemay (ITI)
 * 
 */
@XmlRootElement
@Entity
@org.hibernate.annotations.NamedQueries({ @org.hibernate.annotations.NamedQuery(name = "resourceDescriptor.findByType", query = "from ResourceDescriptor r where r.information.type = :type") })
public class ResourceDescriptor {

	public static final String			VIRTUAL				= "virtual";

	private static final long			serialVersionUID	= -8571009012048021984L;

	@Id
	private String						id;

	@Embedded
	private Information					information;

	@OneToMany(cascade = CascadeType.ALL)
	private List<CapabilityDescriptor>	capabilityDescriptors;
	
	@Basic
	private String						profileId			= "";


	@Embedded
	private NetworkInfo 			networkDescriptor;
	
	
	
	@XmlElement(name = "networkInfo")
	public NetworkInfo getNetworkInfo() {
		return networkDescriptor;
	}

	public void setNetworkInfo(NetworkInfo networkDescriptor) {
		this.networkDescriptor = networkDescriptor;
	}
	

	/**
	 * necessary parameter to configure a ssh connection
	 */
	
    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="resourcedescriptor_properties", joinColumns=@JoinColumn(name="resourcedescriptor_id"))
	Map<String,String> properties = new HashMap<String, String>();


	@XmlElement(name = "capabilityDescriptors")
	public List<CapabilityDescriptor> getCapabilityDescriptors() {
		return capabilityDescriptors;
	}

	public void setCapabilityDescriptors(
			List<CapabilityDescriptor> capabilityDescriptors) {
		this.capabilityDescriptors = capabilityDescriptors;
	}

	@XmlElement(name = "information")
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

	public CapabilityDescriptor getCapabilityDescriptor(String capabilityType) {
		for (int i = 0; i < capabilityDescriptors.size(); i++) {
			if (capabilityDescriptors.get(i).getCapabilityInformation()
					.getType().equals(capabilityType)) {
				return capabilityDescriptors.get(i);
			}
		}
		// FIXME RETURN NULL?
		return null;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public boolean removeCapabilityDescriptor(String capabilityType) {
		for (int i = 0; i < capabilityDescriptors.size(); i++) {
			if (capabilityDescriptors.get(i).getCapabilityInformation()
					.getType().equals(capabilityType)) {
				capabilityDescriptors.remove(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		resourceDescriptor.setId(id);
		resourceDescriptor.setInformation((Information) information.clone());
		resourceDescriptor.setProfileId(profileId);

		List<CapabilityDescriptor> newCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		for (CapabilityDescriptor capabilityDescriptorToCopy : capabilityDescriptors) {
			newCapabilityDescriptors.add((CapabilityDescriptor) capabilityDescriptorToCopy.clone());
		}
		resourceDescriptor.setCapabilityDescriptors(newCapabilityDescriptors);
		resourceDescriptor.setProperties((HashMap<String, String>) ((HashMap) properties).clone());

		return resourceDescriptor;
	}

	// public IModel getModel() {
	// return model;
	// }
	//
	// public void setModel(IModel model) {
	// this.model = model;
	// }
}