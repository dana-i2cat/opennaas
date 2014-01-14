/**
 * 
 */
package org.opennaas.extensions.vcpe.manager;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author Jordi
 */
public class VCPENetworkManagerException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 */
	public VCPENetworkManagerException() {
	}

	/**
	 * @param message
	 */
	public VCPENetworkManagerException(String message) {
		super(Response.serverError().entity(message).build());
	}

	/**
	 * @param status
	 */
	public VCPENetworkManagerException(int status) {
		super(status);
	}

	/**
	 * @param response
	 */
	public VCPENetworkManagerException(Response response) {
		super(response);
	}

	/**
	 * @param cause
	 */
	public VCPENetworkManagerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param cause
	 * @param status
	 */
	public VCPENetworkManagerException(Throwable cause, int status) {
		super(cause, status);
	}

	/**
	 * @param cause
	 * @param status
	 */
	public VCPENetworkManagerException(Throwable cause, Status status) {
		super(cause, status);
	}

	/**
	 * @param cause
	 * @param response
	 */
	public VCPENetworkManagerException(Throwable cause, Response response) {
		super(cause, response);
	}

}
