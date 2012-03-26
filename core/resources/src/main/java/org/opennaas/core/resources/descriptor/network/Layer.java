package org.opennaas.core.resources.descriptor.network;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;

@Entity
public class Layer {

	@Id
	@GeneratedValue
	private long	id;

	@Basic
	private String	name;

	/**
	 * @return the name
	 */
	@XmlElement(name = "name", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Layer [name=" + name + "]";
	}

}
