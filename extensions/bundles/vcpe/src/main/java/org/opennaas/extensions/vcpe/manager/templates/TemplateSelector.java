/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.templates.vcpe.Template;

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
		ITemplate iTemplate = new Template();
		if (templateId.equals(ITemplate.SP_VCPE_TEMPLATE)) {
			iTemplate = new Template();
		}
		return iTemplate;
	}
}
