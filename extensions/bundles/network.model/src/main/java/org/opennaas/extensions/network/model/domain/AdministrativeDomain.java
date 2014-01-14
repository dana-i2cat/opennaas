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

import org.opennaas.extensions.network.model.topology.NetworkElement;

/**
 * An entity that acts as a the administrator of a collection of resources. The administrator is the entity who actually controls and provisions the
 * resources. An administrative domain can include computing, visualization, storage and network resources. An Administrative Domain does not say
 * anything about the data plane or the Location. For that, see Network Domain and Location. The administrator enforces policies, and should not be
 * confused with the (economic) owner, who decides on the policy. The administrator and owner of a network element are often, but not always, the same
 * entity! For example, a link between domain 1 and domain 2 is owned by domain 1. So domain 1 effectively decides on the policy of the terminating
 * interfaces of the link. Thus also the interface of this link in domain 2. That interface is than owned by domain 1, but administrated by domain 2.
 * A clear example of this is in so-called “open” optical exchanges. The advantage of administrative domains is that a device including all its
 * interfaces belongs to a single administrative domain. In this RDF description, an Administrative domain only hasNetworkElements of type Device. The
 * Interfaces of the device are implied to reside in the domain due to the hasInterface property. Beside the properties given here, you may want to
 * use the vCard:ORG property to describe the name of the Adminstrator.
 * 
 * @author isart
 * 
 */
public class AdministrativeDomain {

	List<NetworkElement>	domainElements;

	public List<NetworkElement> getDomainElements() {
		return domainElements;
	}

	public void setDomainElements(List<NetworkElement> domainElements) {
		this.domainElements = domainElements;
	}
}
