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
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

//TODO avoid these imports, they refer to plugin-related data 
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;

/**
 * Resource Descriptor with JPA and JAXB annotations to provide both persistence and XML marshaling capabilities.
 * 
 * @author Mathieu Lemay (ITI)
 * 
 */
@XmlRootElement
// need to tell JAXB this relationship :S
// TODO find a way to add these classes to JAXB context dynamically (when their bundles load)
// and move them out of core.
@XmlSeeAlso({ VCPENetworkDescriptor.class })
@Entity
@NamedQueries({ @NamedQuery(name = "resourceDescriptor.findByType", query = "select r from ResourceDescriptor r where r.information.type = :type") })
public class ResourceDescriptor {

	public static final String			VIRTUAL				= "virtual";
	public static final String			HOSTED_BY			= "hostedBy";

	private static final long			serialVersionUID	= -8571009012048021984L;

	// id is already stored in ResourceIdentifier.id
	// this id will be re-set by ResourceRepository.initResource() with value in ResourceIdentifier.id
	// ResourceRepository.loadResource() uses this id to set ResourceIdentifier.id.
	@Id
	private String						id;

	@Embedded
	private Information					information;

	@OneToMany(cascade = CascadeType.ALL)
	private List<CapabilityDescriptor>	capabilityDescriptors;

	@Basic
	private String						profileId;

	private String						fileTopology;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "NETWORK_TOPOLOGY")
	private NetworkTopology				networkTopology;

	@ElementCollection
	@MapKeyColumn(name = "name")
	@Column(name = "resourceId")
	@CollectionTable(name = "RESOURCE_REFERENCES")
	private Map<String, String>			resourceReferences;

	/**
	 * necessary parameter to configure a ssh connection
	 */

	@ElementCollection
	@MapKeyColumn(name = "name")
	@Column(name = "value")
	@CollectionTable(name = "RESOURCEDESCRIPTOR_PROPERTIES", joinColumns = @JoinColumn(name = "resourcedescriptor_id"))
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

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	@XmlElement(name = "fileTopology")
	public String getFileTopology() {
		return fileTopology;
	}

	public void setFileTopology(String networkFileDescriptor) {
		this.fileTopology = networkFileDescriptor;
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

	public NetworkTopology getNetworkTopology() {
		return networkTopology;
	}

	public void setNetworkTopology(NetworkTopology networkTopology) {
		this.networkTopology = networkTopology;
	}

	public Map<String, String> getResourceReferences() {
		return resourceReferences;
	}

	public void setResourceReferences(Map<String, String> resourceReferences) {
		this.resourceReferences = resourceReferences;
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
		resourceDescriptor.setProperties(new HashMap<String, String>(properties));

		// TODO THE NETWORK DESCRIPTOR IS NOT CLONED. A NETWORK RESOURCE HAVE NOT TO BE CLONED

		return resourceDescriptor;
	}

}