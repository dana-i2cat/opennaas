package org.opennaas.extensions.capability.macbridge.vlanawarebridge;

/*
 * #%L
 * OpenNaaS :: MAC Bridge :: VLAN-Aware Bridge capability
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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;

/**
 * A capability that partially implements the management functions specified in the standard IEEE 802.1q
 * 
 * @author Eduard Grasa
 */
@Path("/")
public interface IVLANAwareBridgeCapability extends ICapability {

	/**
	 * Add a new VLAN Configuration to the VLAN database
	 * 
	 * @param vlanConfiguration
	 * @throws CapabilityException
	 */
	@POST
	@Path("/createVLANConfiguration")
	@Consumes(MediaType.APPLICATION_XML)
	public void createVLANConfiguration(VLANConfiguration vlanConriguration) throws CapabilityException;

	/**
	 * Delete a VLAN Configuration from the VLAN database
	 * 
	 * @param vlanId
	 *            the id of the VLAN
	 * @throws CapabilityException
	 */
	@DELETE
	@Path("/deleteVLANConfiguration")
	public void deleteVLANConfiguration(@QueryParam("vlanID") int vlanId) throws CapabilityException;

	/**
	 * 
	 * @param entry
	 * @throws CapabilityException
	 */
	@POST
	@Path("/addStaticVLAN")
	@Consumes(MediaType.APPLICATION_XML)
	public void addStaticVLANRegistrationEntryToFilteringDatabase(StaticVLANRegistrationEntry entry) throws CapabilityException;

	/**
	 * @param vlanID
	 * @throws CapabilityException
	 */
	@DELETE
	@Path("/deleteStaticVLAN")
	public void deleteStaticVLANRegistrationEntryFromFilteringDatabase(@QueryParam("vlanID") int vlanID) throws CapabilityException;
}
