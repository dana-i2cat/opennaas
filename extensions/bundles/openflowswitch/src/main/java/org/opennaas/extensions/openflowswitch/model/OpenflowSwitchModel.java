package org.opennaas.extensions.openflowswitch.model;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OpenflowSwitchModel implements IModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1377911113047348716L;

	private String				switchId;
	private List<OFFlowTable>	ofTables;

	public OpenflowSwitchModel() {
		switchId = new String();
		ofTables = new ArrayList<OFFlowTable>();
	}

	public String getSwitchId() {
		return switchId;
	}

	public void setSwitchId(String switchId) {
		this.switchId = switchId;
	}

	public List<OFFlowTable> getOfTables() {
		return ofTables;
	}

	public void setOfTables(List<OFFlowTable> ofTables) {
		this.ofTables = ofTables;
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}

	@Override
	public String toXml() throws SerializationException {
		return ObjectSerializer.toXml(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ofTables == null) ? 0 : ofTables.hashCode());
		result = prime * result + ((switchId == null) ? 0 : switchId.hashCode());
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
		OpenflowSwitchModel other = (OpenflowSwitchModel) obj;
		if (ofTables == null) {
			if (other.ofTables != null)
				return false;
		} else if (!ofTables.equals(other.ofTables))
			return false;
		if (switchId == null) {
			if (other.switchId != null)
				return false;
		} else if (!switchId.equals(other.switchId))
			return false;
		return true;
	}

}
