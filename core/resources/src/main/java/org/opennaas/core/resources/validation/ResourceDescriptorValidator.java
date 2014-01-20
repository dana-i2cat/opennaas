package org.opennaas.core.resources.validation;

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

import java.util.List;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Ensures that the resource descriptor is valid
 * 
 * @author Ali LAHLOU (Synchromedia, ETS)
 * 
 */
public class ResourceDescriptorValidator
{

	/**
	 * Validate if a resource descriptor is valid
	 * 
	 * @param ResourceDescriptor
	 *            The Resource descriptor that has to be validated
	 * @return True if valid, False if not
	 */
	public static boolean validateDescriptor(ResourceDescriptor resourceConfiguration) {

		boolean result = true;

		Information inf = resourceConfiguration.getInformation();
		Errors err = new BindException(inf, "inf");
		InformationValidator infValidator = new InformationValidator();
		infValidator.validate(inf, err);
		result = !err.hasErrors();

		CapabilityDescriptorValidator capValidator = new CapabilityDescriptorValidator();
		List<CapabilityDescriptor> capabilityDescriptors = resourceConfiguration.getCapabilityDescriptors();
		for (int i = 0; i < capabilityDescriptors.size(); i++) {
			capValidator.validate(capabilityDescriptors.get(i));
			result = result && !capValidator.hasErrors();
		}

		return result;
	}

}