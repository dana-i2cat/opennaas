package org.opennaas.extensions.abno.capability.linkprovisioning;

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

/**
 * ABNO Link Provisioning Capability
 * 
 * @author Julio Carlos Barrera
 *
 */
public interface ILinkProvisioningCapability {

	public static final String	CAPABILITY_TYPE	= "linkprovisioning";

	public static enum OperationType {
		XifiWF
	}

	public void provisionLink(String srcRegion, String dstRegion, String srcMACAddress, String dstMACAddress, String srcInterface,
			String dstInterface, OperationType operationType);
}
