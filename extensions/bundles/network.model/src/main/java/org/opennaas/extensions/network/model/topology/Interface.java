package org.opennaas.extensions.network.model.topology;

/*
 * #%L
 * OpenNaaS :: Network :: Model
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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A configured interface. Thus an entity that can transport data at a given layer.
 * 
 * @author isart
 * 
 */
@XmlRootElement
public class Interface extends ConnectionPoint {

	Device			device;

	Link			linkTo;
	CrossConnect	switchedTo;

	/**
	 * end-to-end path
	 */
	Path			connectedTo;

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Path getConnectedTo() {
		return connectedTo;
	}

	public void setConnectedTo(Path connectedTo) {
		this.connectedTo = connectedTo;
	}

	public Link getLinkTo() {
		return linkTo;
	}

	public void setLinkTo(Link linkTo) {
		this.linkTo = linkTo;
	}

	public CrossConnect getSwitchedTo() {
		return switchedTo;
	}

	public void setSwitchedTo(CrossConnect switchedTo) {
		this.switchedTo = switchedTo;
	}
}
