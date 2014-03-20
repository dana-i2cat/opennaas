package org.opennaas.extensions.router.model;

/*
 * #%L
 * OpenNaaS :: CIM Model
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

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement
public class AggregatedOptions implements Serializable {

	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long	serialVersionUID	= -8960039625776709380L;

	private Map<String, String>	aggregationOptions;

	/**
	 * @return the aggregationOptions
	 */
	public Map<String, String> getAggregationOptions() {
		return aggregationOptions;
	}

	/**
	 * @param aggregationOptions
	 *            the aggregationOptions to set
	 */
	public void setAggregationOptions(Map<String, String> aggregationOptions) {
		this.aggregationOptions = aggregationOptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aggregationOptions == null) ? 0 : aggregationOptions.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregatedOptions other = (AggregatedOptions) obj;
		if (aggregationOptions == null) {
			if (other.aggregationOptions != null)
				return false;
		} else if (!aggregationOptions.equals(other.aggregationOptions))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AggregatedOptions [aggregationOptions=" + aggregationOptions + "]";
	}

}
