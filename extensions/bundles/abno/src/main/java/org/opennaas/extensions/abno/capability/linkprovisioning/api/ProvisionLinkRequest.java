package org.opennaas.extensions.abno.capability.linkprovisioning.api;

/*
 * #%L
 * OpenNaaS :: XIFI ABNO
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

import org.opennaas.extensions.abno.capability.linkprovisioning.ILinkProvisioningCapability;

/**
 * Request for {@link ILinkProvisioningCapability#provisionLink(ProvisionLinkRequest)} method
 * 
 * @author Julio Carlos Barrera
 *
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProvisionLinkRequest {

	public static enum Operation {
		WLAN_PATH_PROVISIONING
	}

	public static enum OperationType {
		XifiWF
	}

	private String			srcRegion;
	private String			dstRegion;
	private String			srcMACAddress;
	private String			dstMACAddress;
	private String			srcInterface;
	private String			dstInterface;
	private Operation		operation;
	private OperationType	operationType;

	public ProvisionLinkRequest(String srcRegion, String dstRegion, String srcMACAddress, String dstMACAddress, String srcInterface,
			String dstInterface, Operation operation, OperationType operationType) {
		super();
		this.srcRegion = srcRegion;
		this.dstRegion = dstRegion;
		this.srcMACAddress = srcMACAddress;
		this.dstMACAddress = dstMACAddress;
		this.srcInterface = srcInterface;
		this.dstInterface = dstInterface;
		this.operation = operation;
		this.operationType = operationType;
	}

	public String getSrcRegion() {
		return srcRegion;
	}

	public void setSrcRegion(String srcRegion) {
		this.srcRegion = srcRegion;
	}

	public String getDstRegion() {
		return dstRegion;
	}

	public void setDstRegion(String dstRegion) {
		this.dstRegion = dstRegion;
	}

	public String getSrcMACAddress() {
		return srcMACAddress;
	}

	public void setSrcMACAddress(String srcMACAddress) {
		this.srcMACAddress = srcMACAddress;
	}

	public String getDstMACAddress() {
		return dstMACAddress;
	}

	public void setDstMACAddress(String dstMACAddress) {
		this.dstMACAddress = dstMACAddress;
	}

	public String getSrcInterface() {
		return srcInterface;
	}

	public void setSrcInterface(String srcInterface) {
		this.srcInterface = srcInterface;
	}

	public String getDstInterface() {
		return dstInterface;
	}

	public void setDstInterface(String dstInterface) {
		this.dstInterface = dstInterface;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dstInterface == null) ? 0 : dstInterface.hashCode());
		result = prime * result + ((dstMACAddress == null) ? 0 : dstMACAddress.hashCode());
		result = prime * result + ((dstRegion == null) ? 0 : dstRegion.hashCode());
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((operationType == null) ? 0 : operationType.hashCode());
		result = prime * result + ((srcInterface == null) ? 0 : srcInterface.hashCode());
		result = prime * result + ((srcMACAddress == null) ? 0 : srcMACAddress.hashCode());
		result = prime * result + ((srcRegion == null) ? 0 : srcRegion.hashCode());
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
		ProvisionLinkRequest other = (ProvisionLinkRequest) obj;
		if (dstInterface == null) {
			if (other.dstInterface != null)
				return false;
		} else if (!dstInterface.equals(other.dstInterface))
			return false;
		if (dstMACAddress == null) {
			if (other.dstMACAddress != null)
				return false;
		} else if (!dstMACAddress.equals(other.dstMACAddress))
			return false;
		if (dstRegion == null) {
			if (other.dstRegion != null)
				return false;
		} else if (!dstRegion.equals(other.dstRegion))
			return false;
		if (operation != other.operation)
			return false;
		if (operationType != other.operationType)
			return false;
		if (srcInterface == null) {
			if (other.srcInterface != null)
				return false;
		} else if (!srcInterface.equals(other.srcInterface))
			return false;
		if (srcMACAddress == null) {
			if (other.srcMACAddress != null)
				return false;
		} else if (!srcMACAddress.equals(other.srcMACAddress))
			return false;
		if (srcRegion == null) {
			if (other.srcRegion != null)
				return false;
		} else if (!srcRegion.equals(other.srcRegion))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProvisionLinkRequest [srcRegion=" + srcRegion + ", dstRegion=" + dstRegion + ", srcMACAddress=" + srcMACAddress
				+ ", dstMACAddress=" + dstMACAddress + ", srcInterface=" + srcInterface + ", dstInterface=" + dstInterface
				+ ", operation=" + operation + ", operationType=" + operationType + "]";
	}

}
