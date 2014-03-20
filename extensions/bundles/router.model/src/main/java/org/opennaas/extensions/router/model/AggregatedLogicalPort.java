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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an aggregated logical port, which is a logical port being an aggregation of multiple (at least one) interfaces.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement
public class AggregatedLogicalPort extends LogicalPort {

	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long	serialVersionUID	= -3675664143218134609L;

	private List<String>		interfaces;

	private AggregatedOptions	aggregatedOptions;

	/**
	 * @return the names of interfaces that form this AggregatedLogicalPort
	 */
	public List<String> getInterfaces() {
		return interfaces;
	}

	/**
	 * @param interfaces
	 *            the names of interfaces that form this AggregatedLogicalPort
	 */
	public void setInterfaces(List<String> interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * @return the aggregatedOptions
	 */
	public AggregatedOptions getAggregatedOptions() {
		return aggregatedOptions;
	}

	/**
	 * @param aggregatedOptions
	 *            the aggregatedOptions to set
	 */
	public void setAggregatedOptions(AggregatedOptions aggregatedOptions) {
		this.aggregatedOptions = aggregatedOptions;
	}

}
