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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.validation.CapabilityDescriptorValidator;
import org.opennaas.core.resources.validation.ValidationException;

/**
 * The AbstractCapabilityFactory is responsible for instantiating and initializing new capabilities for a resource
 * 
 * @author Mathieu Lemay
 * 
 */
public abstract class AbstractCapabilityFactory implements ICapabilityFactory {
	/**
	 * Validator object used to ensure that the capability descriptor has the required information in it
	 */
	protected CapabilityDescriptorValidator	validator	= null;

	Log										log			= LogFactory.getLog(AbstractCapabilityFactory.class);

	// FIXME Other method to has identified a factory??
	private String							type		= "";

	public ICapability create(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		ICapability capability = null;

		try {
			log.debug("Validating capability descriptor...");
			doCapabilityDescriptorValidation(capabilityDescriptor);
		} catch (ValidationException ex) {
			throw new CapabilityException("Error creating capability", ex);
		}

		log.debug("Creating new Capability...");
		capability = createCapability(capabilityDescriptor, resourceId);
		log.debug("Created capability!");

		return capability;
	}

	private void doCapabilityDescriptorValidation(CapabilityDescriptor capabilityDescriptor)
			throws ValidationException {

		if (validator != null) {
			validator.validate(capabilityDescriptor);

			if (validator.hasErrors()) {
				throw new ValidationException(validator.getErrors());
			}
		}
	}

	/**
	 * Creates a capability based on a capability descriptor and returns it.
	 * 
	 * @param capabilityDescriptor
	 * @throws capabilityException
	 */
	public abstract ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId)
			throws CapabilityException;

	/**
	 * @param capabilityDescriptorValidator
	 *            the capabilityDescriptorValidator to set
	 */
	public void setCapabilityDescriptorValidator(CapabilityDescriptorValidator validator) {
		this.validator = validator;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}