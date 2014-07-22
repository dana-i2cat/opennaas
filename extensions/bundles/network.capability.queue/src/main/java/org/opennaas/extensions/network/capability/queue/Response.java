/**
 * 
 */
package org.opennaas.extensions.network.capability.queue;

/*
 * #%L
 * OpenNaaS :: Network :: Queue Capability
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

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opennaas.core.resources.queue.QueueResponse;

/**
 * @author Jordi
 * 
 */
@XmlRootElement
@XmlType(name = "NetQueueResponse")
public class Response {

	private Map<String, QueueResponse>	response;

	/**
	 * @return the response
	 */
	public Map<String, QueueResponse> getResponse() {
		return response;
	}

	/**
	 * @param response
	 *            the response to set
	 */
	public void setResponse(Map<String, QueueResponse> response) {
		this.response = response;
	}

}
