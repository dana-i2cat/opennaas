package org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.openflowswitch.model.OFFlow;

/**
 * Ryu specific flow
 * 
 * @author Julio Carlos Barrera
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RyuOFFlow extends OFFlow {

	// DPID, switch ID, mandatory
	private String	dpid;

	// Opaque controller-issued identifier. (int), default value: 0
	private String	cookie;

	// Mask used to restrict the cookie bits. (int), default value: 0
	private String	cookieMask;

	// Table ID to put the flow in. (int) default value: 0
	private String	tableId;

	// Idle time before discarding (seconds). (int) default value: 0
	private String	idleTimeout;

	// Max time before discarding (seconds). (int) default value: 0
	private String	hardTimeout;

	// Buffered packet to apply to, or OFP_NO_BUFFER. (int) default value: OFP_NO_BUFFER = 0xffffffff
	private String	bufferId;

	// Bitmap of OFPFF_* flags. (int) default value: 0
	private String	flags;

	/**
	 * Default constructor
	 */
	public RyuOFFlow() {
	}

	/**
	 * Copy constructor
	 * 
	 * @param ryuFlow
	 */
	public RyuOFFlow(RyuOFFlow ryuFlow) {
		super(ryuFlow);
		this.dpid = ryuFlow.dpid;
		this.cookie = ryuFlow.cookie;
		this.cookieMask = ryuFlow.cookieMask;
		this.tableId = ryuFlow.tableId;
		this.idleTimeout = ryuFlow.idleTimeout;
		this.hardTimeout = ryuFlow.hardTimeout;
		this.bufferId = ryuFlow.bufferId;
		this.flags = ryuFlow.flags;
	}

	/**
	 * Copy constructor based on OFFlow and dpid
	 * 
	 * @param ofFlow
	 *            OFFlow to copy
	 * @param dpid
	 */
	public RyuOFFlow(OFFlow ofFlow, String dpid) {
		super(ofFlow);
		this.dpid = dpid;
	}

	public String getDpid() {
		return dpid;
	}

	public void setDpid(String dpid) {
		this.dpid = dpid;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getCookieMask() {
		return cookieMask;
	}

	public void setCookieMask(String cookieMask) {
		this.cookieMask = cookieMask;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(String idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public String getHardTimeout() {
		return hardTimeout;
	}

	public void setHardTimeout(String hardTimeout) {
		this.hardTimeout = hardTimeout;
	}

	public String getBufferId() {
		return bufferId;
	}

	public void setBufferId(String bufferId) {
		this.bufferId = bufferId;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bufferId == null) ? 0 : bufferId.hashCode());
		result = prime * result + ((cookie == null) ? 0 : cookie.hashCode());
		result = prime * result + ((cookieMask == null) ? 0 : cookieMask.hashCode());
		result = prime * result + ((dpid == null) ? 0 : dpid.hashCode());
		result = prime * result + ((flags == null) ? 0 : flags.hashCode());
		result = prime * result + ((hardTimeout == null) ? 0 : hardTimeout.hashCode());
		result = prime * result + ((idleTimeout == null) ? 0 : idleTimeout.hashCode());
		result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RyuOFFlow other = (RyuOFFlow) obj;
		if (bufferId == null) {
			if (other.bufferId != null)
				return false;
		} else if (!bufferId.equals(other.bufferId))
			return false;
		if (cookie == null) {
			if (other.cookie != null)
				return false;
		} else if (!cookie.equals(other.cookie))
			return false;
		if (cookieMask == null) {
			if (other.cookieMask != null)
				return false;
		} else if (!cookieMask.equals(other.cookieMask))
			return false;
		if (dpid == null) {
			if (other.dpid != null)
				return false;
		} else if (!dpid.equals(other.dpid))
			return false;
		if (flags == null) {
			if (other.flags != null)
				return false;
		} else if (!flags.equals(other.flags))
			return false;
		if (hardTimeout == null) {
			if (other.hardTimeout != null)
				return false;
		} else if (!hardTimeout.equals(other.hardTimeout))
			return false;
		if (idleTimeout == null) {
			if (other.idleTimeout != null)
				return false;
		} else if (!idleTimeout.equals(other.idleTimeout))
			return false;
		if (tableId == null) {
			if (other.tableId != null)
				return false;
		} else if (!tableId.equals(other.tableId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RyuOFFlow [dpid=" + dpid + ", cookie=" + cookie + ", cookieMask=" + cookieMask + ", tableId=" + tableId
				+ ", idleTimeout=" + idleTimeout + ", hardTimeout=" + hardTimeout + ", bufferId=" + bufferId + ", flags=" + flags + ", name=" + name
				+ ", priority=" + priority + ", active=" + active + ", actions=" + actions + ", match=" + match + "]";
	}

}
