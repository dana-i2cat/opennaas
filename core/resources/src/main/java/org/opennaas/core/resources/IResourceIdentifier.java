package org.opennaas.core.resources;

import java.net.URI;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This interface contains methods pertaining to resource IDs. An resource ID is a URL that contains the type of resource and an identifier that
 * uniquely identifies a resource within that type. The implementing class is responsible for creating the ID and populating the fields
 * 
 * @author Scott Campbell
 * 
 */
@XmlSeeAlso(ResourceIdentifier.class)
public interface IResourceIdentifier
{
	/**
	 * get the full URI
	 * 
	 * @return the full URI
	 */
	public URI getURI();

	/**
	 * get the type portion of the URI
	 * 
	 * @return String the type portion of the URI which is the second last section.
	 */
	public String getType();

	/**
	 * get the ID portion of the URI
	 * 
	 * @return String the ID portion of the URI which is the last section
	 */
	public String getId();
}
