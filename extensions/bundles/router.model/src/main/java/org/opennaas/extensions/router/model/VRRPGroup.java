package org.opennaas.extensions.router.model;

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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Julio Carlos Barrera
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VRRPGroup extends Service implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7958971959845082335L;

	private int					vrrpName;
	private String				virtualIPAddress;
	private String				virtualLinkAddress;

	public VRRPGroup() {
	}

	public int getVrrpName() {
		return vrrpName;
	}

	public void setVrrpName(int vrrpName) {
		this.vrrpName = vrrpName;
	}

	public String getVirtualIPAddress() {
		return virtualIPAddress;
	}

	public void setVirtualIPAddress(String virtualIPAddress) {
		this.virtualIPAddress = virtualIPAddress;
	}

	public String getVirtualLinkAddress() {
		return virtualLinkAddress;
	}

	public void setVirtualLinkAddress(String virtualLinkAddress) {
		this.virtualLinkAddress = virtualLinkAddress;
	}

}
