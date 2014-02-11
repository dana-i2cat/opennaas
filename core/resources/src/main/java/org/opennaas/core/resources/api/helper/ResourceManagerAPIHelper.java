package org.opennaas.core.resources.api.helper;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.api.model.ResourceInfo;
import org.opennaas.core.resources.api.model.ResourceListWrapper;
import org.opennaas.core.resources.capability.ICapability;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class ResourceManagerAPIHelper {

	public static ResourceListWrapper buildResourceListWrapper(List<IResource> resources) {

		ResourceListWrapper wrapper = new ResourceListWrapper();
		List<String> resourcesIds = new ArrayList<String>();

		if (resources != null)
			for (IResource resource : resources)
				resourcesIds.add(resource.getResourceIdentifier().getId());

		wrapper.setResources(resourcesIds);

		return wrapper;
	}

	public static ResourceInfo buildResourceInfo(IResource resource) {

		ResourceInfo resourceInfo = new ResourceInfo();

		IResourceIdentifier resourceIdentifier = resource.getResourceIdentifier();

		resourceInfo.setResourceId(resourceIdentifier.getId());
		resourceInfo.setType(resourceIdentifier.getType());

		resourceInfo.setState(resource.getState());
		resourceInfo.setName(resource.getResourceDescriptor().getInformation().getName());

		Collection<String> capabList = new ArrayList<String>();

		List<? extends ICapability> capabilities = resource.getCapabilities();
		for (ICapability capability : capabilities)
			capabList.add(capability.getCapabilityName());

		resourceInfo.setCapabilityNames(capabList);

		return resourceInfo;
	}

}
