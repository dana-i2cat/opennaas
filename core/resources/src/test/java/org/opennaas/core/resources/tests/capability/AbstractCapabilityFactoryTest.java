package org.opennaas.core.resources.tests.capability;

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

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.mock.MockCapabilityFactory;
import org.opennaas.core.resources.validation.CapabilityDescriptorValidator;

/**
 * Test class for the AbstractCapabilityFactory
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class AbstractCapabilityFactoryTest
{
	// The class under test
	MockCapabilityFactory			capabilityFactory		= null;
	private CapabilityDescriptor	capabilityDescriptor	= null;
	private String					resourceId				= null;

	@Before
	public void setup() {
		// initialize the capability factory that is under test
		Information information = new Information("TestCapability", "CapabilityName", "1.0.0");
		List<Information> typeList = new ArrayList<Information>();
		typeList.add(information);
		CapabilityDescriptorValidator validator = new CapabilityDescriptorValidator();
		resourceId = "resource123";

		capabilityFactory = new MockCapabilityFactory();
		capabilityFactory.setCapabilityDescriptorValidator(validator);
	}

	@Test
	public void testCreateCapabilitySuceeds() throws ResourceException {
		capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityInformation(new Information("TestCapability", "CapabilityName", "1.0.0"));
		ICapability capability = null;
		capability = capabilityFactory.create(capabilityDescriptor, resourceId);
		assertNotNull(capability);
	}

	@Test(expected = CapabilityException.class)
	public void testCapabilityDescriptorValidationFails() throws ResourceException {
		// Create the module descriptor
		capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityInformation(new Information());
		ICapability capability = null;
		capability = capabilityFactory.create(capabilityDescriptor, resourceId);
		assertNotNull(capability);
	}
}