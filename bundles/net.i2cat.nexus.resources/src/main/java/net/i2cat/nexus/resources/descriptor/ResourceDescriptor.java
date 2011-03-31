package net.i2cat.nexus.resources.descriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.CollectionOfElements;

/**
 * Resource Descriptor with JPA and JAXB annotations to provide both persistence
 * and XML marshaling capabilities.
 * 
 * @author Mathieu Lemay (ITI)
 * 
 */
@XmlRootElement
@Entity
@org.hibernate.annotations.NamedQueries({ @org.hibernate.annotations.NamedQuery(name = "resourceDescriptor.findByType", query = "from ResourceDescriptor r where r.information.type = :type") })
public class ResourceDescriptor {

	private static final long			serialVersionUID	= -8571009012048021984L;

	@Id
	private String						id;

	@Embedded
	private Information					information;

	@OneToMany(cascade = CascadeType.ALL)
	private List<CapabilityDescriptor>	capabilityDescriptors;

	/**
	 * necessary parameter to configure a ssh connection
	 */
	// TODO TO TEST!
	@CollectionOfElements
	Map<String, String>					properties			= new HashMap<String, String>();

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
}