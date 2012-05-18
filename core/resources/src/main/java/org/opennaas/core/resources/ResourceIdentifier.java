package org.opennaas.core.resources;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class provides a simple resource identifier implementation
 * 
 * @author root
 * 
 */
@Entity
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "resourceIdentifier")
public class ResourceIdentifier implements IResourceIdentifier {

	/** The type of the resource **/
	@Basic
	private String	type;

	/** The id of the resource **/
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	private String	id	= null;

	public ResourceIdentifier() {
		this.id = generateId();
		type = "Default String";
	}

	public ResourceIdentifier(String type) {
		this.type = type;
		this.id = generateId();
	}

	public ResourceIdentifier(String type, String id) {
		this.type = type;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	@XmlTransient
	public URI getURI() {
		try {
			String textUri = "http://" + InetAddress.getLocalHost().getHostName() + "/resources/"
					+ type + "/" + id.toString();
			return new URI(textUri);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ResourceIdentifier other = (ResourceIdentifier) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return type + "/" + id.toString();
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	private String generateId() {
		return UUID.randomUUID().toString();
	}

}
