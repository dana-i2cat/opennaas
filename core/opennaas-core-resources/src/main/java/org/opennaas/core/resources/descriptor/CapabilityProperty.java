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
		capabilityProperty.setId(id);
		capabilityProperty.setName(name);
		capabilityProperty.setValue(value);
		return capabilityProperty;
	}
}
