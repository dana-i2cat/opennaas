package org.opennaas.core.resources;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
