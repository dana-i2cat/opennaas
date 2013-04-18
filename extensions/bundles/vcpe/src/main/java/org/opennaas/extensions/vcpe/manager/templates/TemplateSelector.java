/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.templates.mp.MultipleProviderTemplate;
import org.opennaas.extensions.vcpe.manager.templates.sp.SingleProviderTemplate;
import org.opennaas.extensions.vcpe.manager.templates.sp.v6.SingleProviderV6Template;

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
		} else if (templateId.equals(ITemplate.SP_V6_VCPE_TEMPLATE)) {
			iTemplate = new SingleProviderV6Template();
		}

		if (iTemplate == null)
			throw new VCPENetworkManagerException("Failed to get template. Unknown templateId: " + templateId);

		return iTemplate;
	}
}
