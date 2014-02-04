/**
 * 
 */
package org.opennaas.extensions.router.capabilities.api.model.chassis;

/*
 * #%L
 * OpenNaaS :: CIM Model
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

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;

/**
 * @author Jordi
 */
@XmlRootElement
public class RemoveInterfacesFromLogicalRouterRequest {

	private ComputerSystem		logicalRouter;
	private List<LogicalPort>	interfaces;

	/**
	 * @return the logicalRouter
	 */
	public ComputerSystem getLogicalRouter() {
		return logicalRouter;
	}

	/**
	 * @param logicalRouter
	 *            the logicalRouter to set
	 */
	public void setLogicalRouter(ComputerSystem logicalRouter) {
		this.logicalRouter = logicalRouter;
	}

	/**
	 * @return the interfaces
	 */
	public List<LogicalPort> getInterfaces() {
		return interfaces;
	}

	/**
	 * @param interfaces
	 *            the interfaces to set
	 */
	public void setInterfaces(List<LogicalPort> interfaces) {
		this.interfaces = interfaces;
	}

}
