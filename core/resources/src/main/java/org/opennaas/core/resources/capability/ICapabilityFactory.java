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
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

/**
 * This interface must be implemented by the classes that create capabilities of a given resource. ICapabilityFactories act as helper classes of
 * IResourceRepositories, and initialize the resource capabilities by getting them from the right location (OSGi registry, explicitly calling new,
 * method injection, ...)
 * 
 * @author Mathieu Lemay (ITI)
 * 
 */
public interface ICapabilityFactory {
	/**
	 * Use the information in the engine descriptor to create an instance of a module. Once it is created, add it to the engine context
	 * 
	 * @param moduleDescriptor
	 *            the object that contains the engine configuration, model and modules
	 * @return a newly instantiated engine module
	 * @throws ResourceException
	 */
	public ICapability create(CapabilityDescriptor moduleDescriptor, String resourceId) throws CapabilityException;

	public ICapability create(IResource resource) throws CapabilityException;

	public String getType();

	public void setType(String type);

}
