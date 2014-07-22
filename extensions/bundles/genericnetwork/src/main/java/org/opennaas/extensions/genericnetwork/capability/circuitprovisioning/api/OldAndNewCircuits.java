package org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.api;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;

/**
 * Wrapper request class of two {@link List}'s of {@link Circuit}'s
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(namespace = "opennaas.api", name = "replaceCircuits")
@XmlAccessorType(XmlAccessType.FIELD)
public class OldAndNewCircuits {

	@XmlElementWrapper(name = "oldCircuits")
	@XmlElement(name = "circuit")
	private List<Circuit>	oldCircuits;

	@XmlElementWrapper(name = "newCircuits")
	@XmlElement(name = "circuit")
	private List<Circuit>	newCircuits;

	public List<Circuit> getOldCircuits() {
		return oldCircuits;
	}

	public void setOldCircuits(List<Circuit> oldCircuits) {
		this.oldCircuits = oldCircuits;
	}

	public List<Circuit> getNewCircuits() {
		return newCircuits;
	}

	public void setNewCircuits(List<Circuit> newCircuits) {
		this.newCircuits = newCircuits;
	}

}
