/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.templates.sp.MultipleProviderTemplate;
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
