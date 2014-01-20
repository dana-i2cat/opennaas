package org.opennaas.extensions.transports.virtual;

/*
 * #%L
 * OpenNaaS :: Transport :: Virtual
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
 * This interface must be implemented by VirtualTransportProviders that have to provide a response to the requests that the virtual transport
 * receives. VirtualTransportProviders have to be published at the OSGi registry, by the corresponding engine bundles (for example, the ome6500 bundle
 * will publish an OME6500VirtualTransportProvider).
 * 
 * @author eduardgrasa
 * 
 */
public interface IVirtualTransportProvider {
	public static final String	TRANSPORT_VIRTUALTRANSPORTPROVIDER	= "transport.virtualTransportProvider";

	public Object getMessageTransportResponse(Object request);

	public byte[] getStreamTransportReponse(byte[] request);
}
