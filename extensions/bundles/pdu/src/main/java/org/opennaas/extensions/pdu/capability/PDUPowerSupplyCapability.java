package org.opennaas.extensions.pdu.capability;

/*
 * #%L
 * OpenNaaS :: PDU Resource
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.capabilities.IPowerSupplyController;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.pdu.Activator;

public class PDUPowerSupplyCapability extends AbstractPDUCapability implements IPDUPowerSupplyCapability {

	private static Log				log				= LogFactory.getLog(PDUPowerSupplyCapability.class);

	public static String			CAPABILITY_TYPE	= "pdu_pw_sup";
	private String					resourceId		= "";

	private IPowerSupplyController	driver;

	public PDUPowerSupplyCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new PDUPowerSupplyCapability Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		// try{
		// driver = instantiateDriver();
		// } catch (Exception e) {
		// throw new CapabilityException(e);
		// }
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IPDUPowerSupplyCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		driver = null;
		unregisterService();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	// IPowerSupplyController methods

	public Energy getAggregatedEnergy() throws Exception {
		return getDriver().getAggregatedEnergy();
	}

	public double getAggregatedPricePerEnergyUnit() throws Exception {
		return getDriver().getAggregatedPricePerEnergyUnit();
	}

	private IPowerSupplyController getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
	private IPowerSupplyController instantiateDriver() throws Exception {

		String ip = getCapabilityDescriptor().getPropertyValue("pdu.driver.ipaddress");
		String deliveryId = getCapabilityDescriptor().getPropertyValue("powernet.delivery.id");

		// FIXME PDUDriverInstantiator should be unknown for the capability
		// capability should take the driver from an OSGI service.
		return PDUDriverInstantiator.create(resourceId, getPowernetId(), deliveryId, ip);
	}

	@Override
	public void resyncModel() throws Exception {
		// Nothing to do
	}

}
