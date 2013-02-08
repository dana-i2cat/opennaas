/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders;

import org.opennaas.core.resources.capability.CapabilityException;
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
