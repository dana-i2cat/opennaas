/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

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

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.templates.mp.MultipleProviderTemplate;
import org.opennaas.extensions.vcpe.manager.templates.sp.SingleProviderTemplate;

/**
 * @author Jordi
 */
public class TemplateSelector {

	/**
	 * Return the correct ITemplate from the templateId
	 * 
	 * @param templateId
	 * @return ITemplate
	 * @throws VCPENetworkManagerException
	 */
	public static ITemplate getTemplate(String templateId) throws VCPENetworkManagerException {
		ITemplate iTemplate = null;
		if (templateId.equals(ITemplate.SP_VCPE_TEMPLATE)) {
			iTemplate = new SingleProviderTemplate();
		} else if (templateId.equals(ITemplate.MP_VCPE_TEMPLATE)) {
			iTemplate = new MultipleProviderTemplate();
		}

		if (iTemplate == null)
			throw new VCPENetworkManagerException("Failed to get template. Unknown templateId: " + templateId);

		return iTemplate;
	}
}
