/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

/**
 * @author Jordi
 */
public class TemplateSelector {

	public static final String	BASIC_TEMPLATE	= "basic.template";

	/**
	 * Return the correct ITemplate from the templateId
	 * 
	 * @param templateId
	 * @return ITemplate
	 */
	public static ITemplate getTemplate(String templateId) {
		ITemplate iTemplate = new BasicTemplate();
		if (templateId.equals(BASIC_TEMPLATE)) {
			iTemplate = new BasicTemplate();
		}
		return iTemplate;
	}
}
