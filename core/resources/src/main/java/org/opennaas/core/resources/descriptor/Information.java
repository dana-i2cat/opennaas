package org.opennaas.core.resources.descriptor;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

/**
 * Describes the top level properties about a component in the framework.
 * 
 * @author Mathieu Lemay (ITI)
 * 
 */

@Embeddable
public class Information {
	/** The type of information **/
	@Basic
	private String	type;
	/** The name or alias **/
	@Basic
	private String	name;
	/** The version **/
	@Basic
	private String	version;
	/** Description **/
	@Basic
	private String	description;

	public Information() {
	}

	public Information(String type, String name, String version) {
		this.type = type;
		this.name = name;
		this.version = version;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Information information = new Information();
		information.setDescription(description);
		information.setName(name);
		information.setType(type);
		information.setVersion(version);
		return information;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Information other = (Information) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Type: " + type + ", Name: " + name + ", Version: " + version
				+ ", Description: " + description;
	}
}
