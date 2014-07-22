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

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.mock.MockAction;
import org.opennaas.core.resources.mock.MockActionSet;
import org.opennaas.core.resources.mock.MockActionTwo;
import org.opennaas.core.resources.mock.MockCapAction;
import org.opennaas.core.resources.mock.MockCapActionTwo;
import org.opennaas.core.resources.mock.MockCapability;
import org.opennaas.core.resources.profile.IProfile;

public class CapabilityTest {
	private static Log				log						= LogFactory.getLog(CapabilityTest.class);
	private static MockCapability	capability				= null;
	private static IProfile			profile;

	private static String			actionIdMock			= "actionMock";
	private static String			actionIdCapabilityTwo	= "actionCapabilityTwo";
	private static String			actionIdCapability		= "actionCapability";

	@BeforeClass
	public static void setUp() {

		/* init Profile */
		ActionSet actionSet = new MockActionSet();
		MockAction mockAction = new MockAction();
		actionSet.putAction(actionIdMock, mockAction.getClass());

		MockActionTwo mockActionTwo = new MockActionTwo();
		actionSet.putAction(actionIdCapabilityTwo, mockActionTwo.getClass());

		capability = new MockCapability(getMockCapabilityDescriptor());

		// profile = new MockProfile();
		// profile.addActionSetForCapability(actionSet, capability.getCapabilityInformation().getType());

		Resource resource = new Resource();
		// resource.setProfile(profile);

		capability.setResource(resource);

		/* init actionSet */
		ActionSet actionSetCapability = new MockActionSet();
		MockCapAction actionCapability = new MockCapAction();
		actionSetCapability.putAction(actionIdCapability, actionCapability.getClass());

		MockCapActionTwo actionCapabilityTwo = new MockCapActionTwo();
		actionSetCapability.putAction(actionIdCapabilityTwo, actionCapabilityTwo.getClass());
		capability.setActionSet(actionSetCapability);

	}

	private static CapabilityDescriptor getMockCapabilityDescriptor() {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		Information information = new Information();
		information.setName("Mock capability");
		information.setType("Mock");
		information.setVersion("0.0.1");
		capabilityDescriptor.setCapabilityInformation(information);
		return capabilityDescriptor;
	}

	@Test
	public void printInfo() {
		log.info(capability.toString());
	}

	@Test
	public void testInitialize() throws ResourceException {
		capability.initialize();
		Assert.assertEquals(ICapabilityLifecycle.State.INITIALIZED, capability.getState());
		MockCapability mockCapability = capability;
		Assert.assertEquals(mockCapability.getState(), ICapabilityLifecycle.State.INITIALIZED);
	}

	@Test
	public void testActivate() throws ResourceException {
		capability.activate();
		Assert.assertEquals(ICapabilityLifecycle.State.ACTIVE, capability.getState());
		MockCapability mockCapability = capability;
		Assert.assertEquals(mockCapability.getState(), ICapabilityLifecycle.State.ACTIVE);
	}

	// @Test FIXME need to test with well formed ->IProtocolSessionManager psm
	// public void testCreateAction() throws ResourceException {
	//
	// Action action;
	// IProtocolSessionManager psm = new ProtocolSessionManager("deviceID");
	// try {
	// log.info("INFO: Checking action Capability");
	// action = capability.createAction(actionIdCapability);
	// action.execute(psm);
	//
	// Assert.assertTrue(action instanceof MockCapAction);
	//
	// log.info("INFO: Checking action actionID");
	// action = capability.createAction(actionIdMock);
	// action.execute(psm);
	// Assert.assertTrue(action instanceof MockAction);
	//
	// log.info("INFO: Checking action capability two, it must use mock action two, because of Profile");
	// action = capability.createAction(actionIdCapabilityTwo);
	// action.execute(psm);
	//
	// Assert.assertTrue(action instanceof MockActionTwo);
	//
	// } catch (ActionException e) {
	// Assert.fail(e.getLocalizedMessage());
	// }
	//
	// }

	@Test
	public void testDeactivate() throws ResourceException {
		capability.deactivate();
		Assert.assertEquals(ICapabilityLifecycle.State.INACTIVE, capability.getState());
		MockCapability mockCapability = capability;
		Assert.assertEquals(mockCapability.getState(), ICapabilityLifecycle.State.INACTIVE);
	}

	@Test
	public void testShutdown() throws ResourceException {
		capability.shutdown();
		Assert.assertEquals(ICapabilityLifecycle.State.SHUTDOWN, capability.getState());
		MockCapability mockCapability = capability;
		Assert.assertEquals(mockCapability.getState(), ICapabilityLifecycle.State.SHUTDOWN);
	}
}
