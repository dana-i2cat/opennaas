/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;

/**
 * @author Jordi
 */
public class TemplateSelector {

	private static final String	BASIC_TEMPLATE	= "basic.template";

	/**
	 * Return the correct ITemplate from the templateId
	 * 
	 * @param templateId
	 * @return ITemplate
	 * @throws VCPENetworkManagerException
	 */
	public static ITemplate getTemplate(String templateId) throws VCPENetworkManagerException {
		ITemplate iTemplate = new Template();
		if (templateId.equals(BASIC_TEMPLATE)) {
			iTemplate = new Template();
		}
		return iTemplate;
	}
}
