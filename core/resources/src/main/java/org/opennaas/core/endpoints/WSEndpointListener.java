package org.opennaas.core.endpoints;

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
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointListener;

/**
 * A simple implementation of the OSGI EndpointListener.
 * 
 * Depending on the filter configured when creating the WSEndpointListenerHandler service, it will receive notifications every time the defined
 * endpoint(s) is either created or deleted. The endpoint filter is defined by the WSEndpointListenerHandler and it's used to subscribe the
 * WSEndpoitnListener to specifics endpoint publications.
 * 
 * WSEndpointListener must be published as an OSGI service in order to receive endpoint notifications, and it must contain the
 * {@link #ENDPOINT_LISTENER_SCOPE} property with the filter of the endpoint to listen.
 * 
 * 
 * For example, if you want to listen to a single to endpoints published by the interface org.opennas.core.resources.IResourceManager:
 * 
 * <pre>
 * (objectClass = org.opennaas.core.resources.IResourceManager)
 * </pre>
 * 
 * Or you can listen for all endpoints
 * 
 * <pre>
 * (objectClass = *)
 * </pre>
 * 
 * Once the endpoint is created or removed, WSEndpointListener notifies it to the {@link WSEndpointListenerHandler} by changing it publish state
 * attribute.
 * 
 * @see WSEndpointListenerHandler#registerWSEndpointListener
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class WSEndpointListener implements EndpointListener {

	private WSEndpointListenerHandler	listenerHandler;

	Log									log	= LogFactory.getLog(WSEndpointListener.class);

	/**
	 * Creates a WSEndpointListener with a specific {@link WSEndpointListenerHandler}, which will receive the endpoint notification by calling it
	 * {@link WSEndpointListenerHandler#changeEndpointPublishedState(Boolean)} method.
	 * 
	 * @param listenerHandler
	 */
	public WSEndpointListener(WSEndpointListenerHandler listenerHandler) {

		this.listenerHandler = listenerHandler;

	}

	/**
	 * Method will be called if the endpoint matches one of the filters registered with the {@link #ENDPOINT_LISTENER_SCOPE} service property once
	 * it's created. It will notify the {@link WSEndpointListenerHandler} that the endpoint is published.
	 * 
	 */
	@Override
	public void endpointAdded(EndpointDescription endpoint, String matchedFilter) {

		listenerHandler.changeEndpointPublishedState(true);

	}

	/**
	 * Method will be called if the endpoint matches one of the filters registered with the {@link #ENDPOINT_LISTENER_SCOPE} service property once
	 * it's removed. It will notify the {@link WSEndpointListenerHandler} that the endpoint is not published.
	 * 
	 */
	@Override
	public void endpointRemoved(EndpointDescription endpoint, String matchedFilter) {

		listenerHandler.changeEndpointPublishedState(false);

	}

}
