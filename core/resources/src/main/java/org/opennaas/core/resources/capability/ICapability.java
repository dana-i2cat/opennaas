package org.opennaas.core.resources.capability;

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

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;

/**
 * Basic interface all resource capabilities must implement
 * 
 * @author Mathieu Lemay - ITI
 * @version 1.0
 */

public interface ICapability {

	/**
	 * Get the name of this capability. Name must be unique for capabilities (must not be two capabilities with same name)
	 * 
	 * @return
	 */
	public String getCapabilityName();

	/**
	 * Get the descriptor for this capability
	 * 
	 */
	public CapabilityDescriptor getCapabilityDescriptor();

	/**
	 * Set the descriptor for this capability
	 * 
	 * @param descriptor
	 */
	// TODO REMOVE FROM THIS INTERFACE
	public void setCapabilityDescriptor(CapabilityDescriptor descriptor);

	/**
	 * Get the information from this capability
	 * 
	 * @return
	 */
	public Information getCapabilityInformation();

	/**
	 * The resource where this capability belongs
	 * 
	 * @param resource
	 */
	// TODO REMOVE FROM THIS INTERFACE
	public void setResource(IResource resource);

}
