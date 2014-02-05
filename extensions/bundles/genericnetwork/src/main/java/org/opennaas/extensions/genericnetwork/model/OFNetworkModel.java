package org.opennaas.extensions.genericnetwork.model;

/*
 * #%L
 * OpenNaaS :: OF Network
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OFNetworkModel implements IModel {

	/**
	 * Auto-generated serial version number
	 */
	private static final long				serialVersionUID	= -3223373735906486372L;

	// FIXME: don't store this in the model.
	// capability should read this information each time it is asked for it.
	/**
	 * Maps deviceId and Flows in each
	 */
	private Map<String, List<NetOFFlow>>	netFlowsPerResource;

	/**
	 * Maps device ID in OFNetworkModel and resource ID in OpenNaaS
	 */
	private Map<String, String>				deviceResourceMap;

	public OFNetworkModel() {
		deviceResourceMap = new HashMap<String, String>();
		netFlowsPerResource = new HashMap<String, List<NetOFFlow>>();
	}

	public Map<String, List<NetOFFlow>> getNetFlowsPerResource() {
		return netFlowsPerResource;
	}

	public void setNetFlowsPerResource(Map<String, List<NetOFFlow>> netFlowsPerResource) {
		this.netFlowsPerResource = netFlowsPerResource;
	}

	public Map<String, String> getDeviceResourceMap() {
		if (deviceResourceMap == null) {
			deviceResourceMap = new HashMap<String, String>();
		}
		return deviceResourceMap;
	}

	public void setDeviceResourceMap(Map<String, String> deviceResourceMap) {
		this.deviceResourceMap = deviceResourceMap;
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>(0);
	}

	@Override
	public String toXml() throws SerializationException {
		return ObjectSerializer.toXml(this);
	}

}
