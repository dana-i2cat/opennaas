package org.opennaas.core.resources.api.model;

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

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement(name = "resourcesTypes")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceTypeListWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5397914714077525973L;

	@XmlElement(name = "resourceType")
	private Collection<String>	resourcesTypes;

	public Collection<String> getResourcesTypes() {
		return resourcesTypes;
	}

	public void setResourcesTypes(Collection<String> resourcesTypes) {
		this.resourcesTypes = resourcesTypes;
	}
}
