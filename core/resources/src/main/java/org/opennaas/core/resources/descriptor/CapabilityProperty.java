package org.opennaas.core.resources.descriptor;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Property for capability. Used by the CapabilityDescriptor to provide the properties needed to instantiate the capability
 * 
 * @author Mathieu Lemay (ITI)
 * 
 */
@Entity
public class CapabilityProperty {
	@Id
	@GeneratedValue
	private long	id;

	@Basic
	private String	name;

	@Basic
	private String	value;

	public CapabilityProperty() {

	}

	public CapabilityProperty(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		CapabilityProperty capabilityProperty = new CapabilityProperty();
		capabilityProperty.setName(name);
		capabilityProperty.setValue(value);
		return capabilityProperty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		CapabilityProperty other = (CapabilityProperty) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
