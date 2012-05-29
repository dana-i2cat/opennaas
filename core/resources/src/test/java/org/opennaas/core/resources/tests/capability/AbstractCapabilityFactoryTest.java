package org.opennaas.core.resources.tests.capability;

import static org.junit.Assert.*;

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