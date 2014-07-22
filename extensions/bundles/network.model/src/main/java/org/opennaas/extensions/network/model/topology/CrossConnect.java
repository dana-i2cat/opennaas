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

/**
 * A (collection of) network element(s) that can be represented as a subnetwork connection (ITU-T G.805 terminology). A cross connect is an internal
 * data transport within a device or domain, unlike Links which transport data between two devices or domains.
 * 
 * @author isart
 * 
 */
public class CrossConnect extends NetworkConnection {

	Interface	source;
	Interface	sink;

	public Interface getSource() {
		return source;
	}

	public void setSource(Interface source) {
		this.source = source;
	}

	public Interface getSink() {
		return sink;
	}

	public void setSink(Interface sink) {
		this.sink = sink;
	}
}
