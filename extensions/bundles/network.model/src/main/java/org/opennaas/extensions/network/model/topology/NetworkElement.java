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

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.network.model.domain.AdministrativeDomain;
import org.opennaas.extensions.network.model.physical.Location;

/**
 * A network element is an abstract class which describe an elements in a computer network.
 * 
 * @author isart
 * 
 */
public abstract class NetworkElement {
	String						name;

	Location					locatedAt;

	List<AdministrativeDomain>	inAdminDomains	= new ArrayList<AdministrativeDomain>();

	public List<AdministrativeDomain> getInAdminDomains() {
		return inAdminDomains;
	}

	public void setInAdminDomains(List<AdministrativeDomain> inAdminDomains) {
		this.inAdminDomains = inAdminDomains;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocatedAt() {
		return locatedAt;
	}

	public void setLocatedAt(Location locatedAt) {
		this.locatedAt = locatedAt;
	}
}
