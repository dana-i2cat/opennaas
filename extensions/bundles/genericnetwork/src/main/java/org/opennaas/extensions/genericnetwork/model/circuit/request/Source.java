package org.opennaas.extensions.genericnetwork.model.circuit.request;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"address",
		"transportPort",
		"linkPort"
})
@XmlRootElement(name = "source")
public class Source {

	@XmlElement(required = true)
	protected String	address;
	@XmlElement(required = true)
	protected String	transportPort;
	@XmlElement(required = true)
	protected String	linkPort;

	/**
	 * Gets the value of the address property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the value of the address property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAddress(String value) {
		this.address = value;
	}

	/**
	 * Gets the value of the transport port property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTransportPort() {
		return transportPort;
	}

	/**
	 * Sets the value of the transport port property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTransportPort(String value) {
		this.transportPort = value;
	}

	/**
	 * Gets the value of the link port property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLinkPort() {
		return linkPort;
	}

	/**
	 * Sets the value of the link port property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLinkPort(String linkPort) {
		this.linkPort = linkPort;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
