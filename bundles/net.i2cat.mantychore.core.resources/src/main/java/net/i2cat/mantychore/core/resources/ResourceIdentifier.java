package net.i2cat.mantychore.core.resources;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	private String type;

	/** The id of the resource **/
	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id = null;

	public ResourceIdentifier() {
		this.id = UUID.randomUUID().toString();
		type = "Default String";
	}

	public ResourceIdentifier(String type) {
		this.type = type;
		this.id = UUID.randomUUID().toString();
	}
	
	public ResourceIdentifier(String type, String id) {
		this.type = type;
		this.id = id;
	}

	@XmlTransient
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
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof ResourceIdentifier)) {
			return false;
		}

		return ((ResourceIdentifier) obj).getId().equals(this.id);
	}

	public String toString() {
		return type + "/" + id.toString();
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
