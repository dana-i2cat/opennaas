package org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * List Wrapper class storing a {@link List} of {@link NetOFFlow}
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(name = "NetOFFlows")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetOFFlows {

	@XmlElement(name = "NetOFFlow")
	private List<NetOFFlow>	list;

	public NetOFFlows() {
	}

	public NetOFFlows(List<NetOFFlow> list) {
		this.list = list;
	}

	public List<NetOFFlow> getList() {
		return list;
	}

	public void setList(List<NetOFFlow> list) {
		this.list = list;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NetOFFlows other = (NetOFFlows) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		return true;
	}

}
