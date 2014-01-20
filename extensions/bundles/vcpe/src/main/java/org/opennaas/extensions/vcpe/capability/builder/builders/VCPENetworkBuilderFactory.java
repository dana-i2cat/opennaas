/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.vcpe.capability.builder.builders.mp.VCPEMultipleProvider;
import org.opennaas.extensions.vcpe.capability.builder.builders.sp.VCPESingleProvider;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;

/**
 * @author Jordi
 */
public class VCPENetworkBuilderFactory {

	/**
	 * Get the correct builder from templateName
	 * 
	 * @param criteria
	 * @return the builder
	 */
	public static IVCPENetworkBuilder getBuilder(String templateName) throws CapabilityException {
		IVCPENetworkBuilder builder = null;
		if (templateName.equals(ITemplate.SP_VCPE_TEMPLATE)) {
			builder = new VCPESingleProvider();
		} else if (templateName.equals(ITemplate.MP_VCPE_TEMPLATE)) {
			builder = new VCPEMultipleProvider();
		}

		if (builder == null)
			throw new CapabilityException("Failed to get builder. Unknown templateId: " + templateName);

		return builder;
	}
}
