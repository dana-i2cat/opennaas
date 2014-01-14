package org.opennaas.extensions.power.capabilities;

/*
 * #%L
 * OpenNaaS :: Power :: Capabilities
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.capabilities.IPowerManagementController;
import org.opennaas.extensions.power.capabilities.driver.ConsumerDriverInstantiator;

public class PowerManagementCapability extends AbstractPowerConsumerCapability implements IPowerManagementCapability {

	public static String				CAPABILITY_TYPE	= "consumer_pw_mgt";

	private String						resourceId		= "";

	private IPowerManagementController	driver;

	public PowerManagementCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public boolean getPowerStatus() throws Exception {
		return getDriver().getPowerStatus();
	}

	@Override
	public boolean powerOn() throws Exception {
		if (getPowerStatus())
			throw new Exception("Already powered on");
		return getDriver().powerOn();
	}

	@Override
	public boolean powerOff() throws Exception {
		if (!getPowerStatus())
			throw new Exception("Already powered off");
		return getDriver().powerOff();
	}

	@Override
	public void resyncModel() throws Exception {
		// Nothing to do
	}

	private IPowerManagementController getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
	private IPowerManagementController instantiateDriver() throws Exception {

		// FIXME PDUDriverInstantiator should be unknown for the capability
		// capability should take the driver from an OSGI service.
		return ConsumerDriverInstantiator.create(resourceId, getPowernetId(), consumerId, descriptor);
	}
}
