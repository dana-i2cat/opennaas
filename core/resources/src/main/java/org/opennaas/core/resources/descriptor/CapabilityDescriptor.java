package org.opennaas.core.resources.descriptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;

/**
 * This class is used to define a capability. The ICapabilityFactory will use this class to get the information it needs to instantiate the new
 * capability.
 * 
 * @author Mathieu Lemay (ITI)
 * 
 */
@Entity
public class CapabilityDescriptor {

	@Id
	@GeneratedValue
	private long						id;

	/** Module Information */
	@Embedded
	private Information					capabilityInformation;

	/** Capability Enabled Status **/
	@Basic
	private Boolean						enabled;

	/** Configuration Properties for the capability **/
	@OneToMany(cascade = CascadeType.ALL)
	private List<CapabilityProperty>	capabilityProperties;

	public CapabilityDescriptor() {
		capabilityProperties = new ArrayList<CapabilityProperty>();
	}

	@XmlElement(name = "capabilityProperty")
	public List<CapabilityProperty> getCapabilityProperties() {
		return capabilityProperties;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setCapabilityProperties(List<CapabilityProperty> properties) {
		this.capabilityProperties = properties;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setCapabilityInformation(Information capabilityInformation) {
		this.capabilityInformation = capabilityInformation;
	}

	@XmlElement(name = "information")
	public Information getCapabilityInformation() {
		return capabilityInformation;
	}

	/**
	 * Get the CapabilityProperty who's name field matches the given name
	 * 
	 * @param name
	 * @return
	 */
	public CapabilityProperty getProperty(String name) {
		Iterator<CapabilityProperty> propertyIterator = capabilityProperties.iterator();
		while (propertyIterator.hasNext()) {
			CapabilityProperty property = propertyIterator.next();
			if (property.getName().equals(name))
				return property;
		}
		return null;
	}

	/**
	 * Get the value of the property who's name field matches the given name
	 * 
	 * @param name
	 * @return
	 */
	public String getPropertyValue(String name) {
		CapabilityProperty property;
		if ((property = getProperty(name)) != null) {
			return property.getValue();
		}
		return null;
	}

	/**
	 * Convert the capability properties list into a String
	 * 
	 * @return
	 */
	public String capabilityPropertiesToString() {
		String propertiesString = new String();
		for (int i = 0; i < capabilityProperties.size(); i++) {
			propertiesString += capabilityProperties.get(i).getName() + "="
					+ capabilityProperties.get(i).getValue() + ", ";
		}
		return propertiesString;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		if (capabilityInformation != null) {
			capabilityDescriptor.setCapabilityInformation((Information) capabilityInformation.clone());
		}
		capabilityDescriptor.setEnabled(enabled);

		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();
		for (CapabilityProperty propertyToCopy : capabilityProperties) {
			properties.add((CapabilityProperty) propertyToCopy.clone());
		}
		capabilityDescriptor.setCapabilityProperties(properties);

		return capabilityDescriptor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((capabilityInformation == null) ? 0 : capabilityInformation.hashCode());
		result = prime * result + ((capabilityProperties == null) ? 0 : capabilityProperties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CapabilityDescriptor other = (CapabilityDescriptor) obj;
		if (capabilityInformation == null) {
			if (other.capabilityInformation != null)
				return false;
		} else if (!capabilityInformation.equals(other.capabilityInformation))
			return false;

		if (capabilityProperties == null) {
			if (other.capabilityProperties != null)
				return false;
		} else {
			if (other.capabilityProperties == null)
				return false;
			if (capabilityProperties.size() != other.capabilityProperties.size())
				return false;
			for (CapabilityProperty prop : capabilityProperties) {
				if (!other.capabilityProperties.contains(prop))
					return false;
			}
		}

		return true;
	}

}
