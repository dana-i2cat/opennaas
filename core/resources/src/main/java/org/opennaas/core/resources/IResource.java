package org.opennaas.core.resources;

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

import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;

/**
 * The top level resource interface
 * 
 * @author Mathieu Lemay (c)2009 Inocybe Technologies inc.
 * 
 */
public interface IResource {

	public IResourceIdentifier getResourceIdentifier();

	// public void setResourceIdentifier(IResourceIdentifier identifier);

	/**
	 * Get the resource instance descriptor
	 * 
	 * @return
	 */
	public ResourceDescriptor getResourceDescriptor();

	// /**
	// * Set the resource descriptor
	// *
	// * @param resourceDescriptor
	// */
	// public void setResourceDescriptor(ResourceDescriptor resourceDescriptor);

	// /**
	// * Set all the capabilities of this resource
	// *
	// * @param capabilities
	// * the resource capabilities
	// */
	// public void setCapabilities(List<? extends ICapability> capabilities);

	/**
	 * Get all the capabilities of this resource
	 * 
	 * @return the engine capabilities
	 */
	public List<? extends ICapability> getCapabilities();

	// /**
	// * Add a capability to this resource
	// *
	// * @param capability
	// * the actual capability
	// */
	// public void addCapability(ICapability capability);
	//
	// /**
	// * Remove a capability from this resource
	// *
	// * @param information
	// * the information of the capability
	// * @return the removed capability (null if the capability does not exist)
	// */
	// public ICapability removeCapability(Information information);

	/**
	 * Get a particular capability form this resource
	 * 
	 * @return the capability
	 */
	public ICapability getCapability(Information information);

	/**
	 * Get capability by type
	 * 
	 * @return the capability
	 * @throws ResourceException
	 *             if this resource does not have any capability of given type
	 */
	public ICapability getCapabilityByType(String type) throws ResourceException;

	/**
	 * Get capabilities of this resource that implement given interface
	 * 
	 * @param interfaze
	 * @return Capabilities of this resource with given interface
	 */
	public List<ICapability> getCapabilitiesByInterface(Class<? extends ICapability> interfaze);

	/**
	 * Get first capability in this resource capabilities implementing given interface
	 * 
	 * @param interfaze
	 * @return Capability of this resource with given interface
	 * @throws ResourceException
	 *             if this resource does not have any capability of given type
	 * 
	 */
	public ICapability getCapabilityByInterface(Class<? extends ICapability> interfaze) throws ResourceException;

	// /**
	// * Start the Resource. The resource must already be instantiated and in the initialized state. This method will perform the necessary
	// * bootstrapping to transition from INITIALIZED to ACTIVE State
	// *
	// * @throws CorruptStateException
	// */
	// public void start() throws ResourceException, CorruptStateException;
	//
	// /**
	// * Stop the resource. This method will transition to the SHUTDOWN State
	// *
	// * @throws CorruptStateException
	// */
	// public void stop() throws ResourceException, CorruptStateException;

	public void setModel(IModel model);

	public IModel getModel();

	// public IProfile getProfile();

	// public void setProfile(IProfile profile);

	// public IResourceBootstrapper getBootstrapper();

	// public void setBootstrapper(IResourceBootstrapper bootstrapper);

	/**
	 * @return current Lifecycle state of this resource.
	 */
	public State getState();

}
