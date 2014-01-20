package org.opennaas.extensions.network.model.domain;

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

import java.util.List;

import org.opennaas.extensions.network.model.topology.ConnectionPoint;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.NetworkElement;

public class NetworkDomain extends NetworkElement {

	List<Device>			hasDevice;
	List<ConnectionPoint>	hasInterface;

	public List<Device> getHasDevice() {
		return hasDevice;
	}

	public void setHasDevice(List<Device> hasDevice) {
		this.hasDevice = hasDevice;
	}

	public List<ConnectionPoint> getHasInterface() {
		return hasInterface;
	}

	public void setHasInterface(List<ConnectionPoint> hasInterface) {
		this.hasInterface = hasInterface;
	}

}
