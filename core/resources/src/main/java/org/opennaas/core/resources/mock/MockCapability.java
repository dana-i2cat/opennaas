package org.opennaas.core.resources.mock;

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
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class MockCapability extends AbstractCapability {

	public boolean	sentStartUp	= false;
	public boolean	sentMessage	= false;

	public MockCapability(CapabilityDescriptor descriptor) {
		super(descriptor);
		// TODO Auto-generated constructor stub
	}

	Log	log	= LogFactory.getLog(MockCapability.class);

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		return super.actionSet;
	}

	public void setActionSet(IActionSet actionSet) {
		super.actionSet = actionSet;

	}

	public void sendRefreshActions() {
		sentStartUp = true;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		log.info("MOCK CAPABILITY: queued action!!");
		sentMessage = true;
	}

	@Override
	public String getCapabilityName() {
		return "mockCapability";

	}
}
