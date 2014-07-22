package org.opennaas.extensions.router.capability.staticroute;

/*
 * #%L
 * OpenNaaS :: Router :: Static route capability
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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capabilities.api.helper.StaticRouteApiHelper;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRoute;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRouteCollection;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.NextHopRoute;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * @author Jordi Puig
 * @author Adrian Rosello Rey (i2CAT)
 */
public class StaticRouteCapability extends AbstractCapability implements IStaticRouteCapability {

	public static String	CAPABILITY_TYPE				= "staticroute";
	public final static int	PREFERENCE_DEFAULT_VALUE	= -1;

	Log						log							= LogFactory.getLog(StaticRouteCapability.class);

	private String			resourceId					= "";

	/**
	 * StaticRouteCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public StaticRouteCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new StaticRoute Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IStaticRouteCapability.class.getName());
		super.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivate()
	 */
	@Override
	public void deactivate() throws CapabilityException {
		registration.unregister();
		super.deactivate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityName()
	 */
	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#queueAction(org.opennaas.core.resources.action.IAction)
	 */
	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	/**
	 * Return the Static Route ActionSet
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);
		try {
			return Activator.getStaticRouteActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * IStaticRoute Implementation
	 */

	public void createStaticRoute(StaticRoute staticRoute) throws CapabilityException {
		log.info("Start of createStaticRoute call");

		IAction action = createActionAndCheckParams(StaticRouteActionSet.STATIC_ROUTE_CREATE, staticRoute);
		queueAction(action);

		log.info("End of createStaticRoute call");
	}

	@Override
	public void createStaticRoute(String netIdIpAdress, String nextHopIpAddress, boolean isDiscard, int preference) throws CapabilityException {

		StaticRoute staticRoute = new StaticRoute();
		staticRoute.setNetIdIpAdress(netIdIpAdress);
		staticRoute.setNextHopIpAddress(nextHopIpAddress);
		staticRoute.setDiscard(isDiscard);
		staticRoute.setPreference(preference);

		createStaticRoute(staticRoute);
	}

	@Override
	@Deprecated
	public void createStaticRoute(String netIdIpAdress, String maskIpAdress, String nextHopIpAddress, String isDiscard) throws CapabilityException {

		if (IPUtilsHelper.validateIpAddressPattern(netIdIpAdress))
			netIdIpAdress = netIdIpAdress + "/" + IPUtilsHelper.parseLongToShortIpv4NetMask(maskIpAdress);
		else
			netIdIpAdress = netIdIpAdress + "/" + maskIpAdress;
		createStaticRoute(netIdIpAdress, nextHopIpAddress, Boolean.parseBoolean(isDiscard), PREFERENCE_DEFAULT_VALUE);

	}

	@Override
	public void deleteStaticRoute(String netIdIpAdress, String nextHopIpAddress) throws CapabilityException {
		log.info("Start of deleteStaticRoute call");
		String[] aParams = new String[2];
		aParams[0] = netIdIpAdress;
		aParams[1] = nextHopIpAddress;

		IAction action = createActionAndCheckParams(StaticRouteActionSet.STATIC_ROUTE_DELETE, aParams);
		queueAction(action);
		log.info("End of deleteStaticRoute call");
	}

	@Deprecated
	@Override
	public void deleteStaticRoute(String netIdIpAdress, String maskIpAdress, String nextHopIpAddress) throws CapabilityException {

		if (IPUtilsHelper.validateIpAddressPattern(netIdIpAdress))
			netIdIpAdress = netIdIpAdress + "/" + IPUtilsHelper.parseLongToShortIpv4NetMask(maskIpAdress);
		else
			netIdIpAdress = netIdIpAdress + "/" + maskIpAdress;
		deleteStaticRoute(netIdIpAdress, nextHopIpAddress);

	}

	/**
	 * 
	 * @return QueuemanagerService this capability is associated to.
	 * @throws CapabilityException
	 *             if desired queueManagerService could not be retrieved.
	 */
	private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
	}

	@Override
	public StaticRouteCollection getStaticRoutes() throws CapabilityException {

		StaticRouteCollection src;

		ComputerSystem model = (ComputerSystem) this.resource.getModel();
		List<NextHopRoute> nextHops = model.getNextHopRoute();

		src = StaticRouteApiHelper.buildStaticRouteCollection(nextHops);

		return src;
	}
}
