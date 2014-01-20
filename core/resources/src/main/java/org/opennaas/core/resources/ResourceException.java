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

import org.opennaas.core.resources.descriptor.Information;

/**
 * Base Resource Exception
 * 
 * @author Scott Campbell
 * 
 */
public class ResourceException extends Exception {
	private static final long	serialVersionUID	= -5669367817669690129L;

	/** Resource Information */
	Information					resourceInformation	= null;

	public ResourceException() {
		super();
	}

	public ResourceException(String msg) {
		super(msg);
	}

	public ResourceException(Exception e) {
		super(e);
	}

	public ResourceException(String msg, Exception e) {
		super(msg, e);
	}

	public ResourceException(String msg, Information information) {
		super(msg);
		this.resourceInformation = information;
	}

	public Information getResourceInformation() {
		return resourceInformation;
	}

	public void setResourceInformation(Information resourceInformation) {
		this.resourceInformation = resourceInformation;
	}
}
