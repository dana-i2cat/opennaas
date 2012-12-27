/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import java.util.List;

/**
 * @author Jordi
 */
public class Link {

	private String		name;
	private String		templateName;
	private String		type;
	private String		id;
	private List<Link>	implementedBy;
	private Interface	source;
	private Interface	destination;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the implementedBy
	 */
	public List<Link> getImplementedBy() {
		return implementedBy;
	}

	/**
	 * @param implementedBy
	 *            the implementedBy to set
	 */
	public void setImplementedBy(List<Link> implementedBy) {
		this.implementedBy = implementedBy;
	}

	/**
	 * @return the source
	 */
	public Interface getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(Interface source) {
		this.source = source;
	}

	/**
	 * @return the destination
	 */
	public Interface getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Interface destination) {
		this.destination = destination;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Link [name=" + name + ", templateName=" + templateName + ", type=" + type + ", id=" + id + ", implementedBy=" + implementedBy + ", source=" + source + ", destination=" + destination + "]";
	}

}
