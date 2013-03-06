/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates.mp;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;
import org.opennaas.extensions.vcpe.model.IPNetworkDomain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

/**
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * @author Jordi
 */
public class MultipleProviderTemplate implements ITemplate {

	private String				templateType	= ITemplate.MP_VCPE_TEMPLATE;

	private MPTemplateSuggestor	suggestor;

	/**
	 * @throws VCPENetworkManagerException
	 * 
	 */
	public MultipleProviderTemplate() throws VCPENetworkManagerException {
		suggestor = new MPTemplateSuggestor();
		suggestor.initialize();
	}

	public String getTemplateType() {
		return templateType;
	}

	/**
	 * 
	 * @param initialModel
	 *            model with user preferences. It MAY not be a complete model.
	 * @return complete model with all required values.
	 */
	@Override
	public VCPENetworkModel buildModel(VCPENetworkModel initialModel) {
		VCPENetworkModel model = MPTemplateModelBuilder.generateModel();
		model = partialCopy(initialModel, model);
		return model;
	}

	@Override
	public VCPENetworkModel getPhysicalInfrastructureSuggestion() throws VCPENetworkManagerException {

		VCPENetworkModel generated = MPTemplateModelBuilder.generatePhysicalElements();
		VCPENetworkModel suggestion = suggestor.getSuggestionForPhysicalModel(generated);
		return suggestion;
	}

	@Override
	public VCPENetworkModel getLogicalInfrastructureSuggestion(VCPENetworkModel physicalInfrastructure) throws VCPENetworkManagerException {
		// assuming given physicalInfrastructure is complete
		return getLogicalInfrastructureSuggestionFromCompletePhysical(physicalInfrastructure);
	}

	private VCPENetworkModel getLogicalInfrastructureSuggestionFromCompletePhysical(VCPENetworkModel physicalInfrastructure) {
		VCPENetworkModel generatedLogical = MPTemplateModelBuilder.generateLogicalElements();
		VCPENetworkModel suggestedLogical = suggestor.getSuggestionForLogicalModel(physicalInfrastructure, generatedLogical);
		VCPENetworkModel completeSuggestion = MPTemplateModelBuilder.mapLogicalAndPhysical(physicalInfrastructure, suggestedLogical);

		return completeSuggestion;
	}

	private VCPENetworkModel partialCopy(VCPENetworkModel source, VCPENetworkModel destination) {
		VCPENetworkElement dstElement;
		for (VCPENetworkElement srcElement : source.getElements()) {
			dstElement = VCPENetworkModelHelper.getElementByTemplateName(destination.getElements(), srcElement.getTemplateName());
			if (dstElement != null) {
				dstElement.setName(srcElement.getName());
				if (srcElement instanceof Interface && dstElement instanceof Interface) {
					VCPENetworkModelHelper.copyInterface((Interface) dstElement, (Interface) srcElement);
				} else if (srcElement instanceof IPNetworkDomain && dstElement instanceof IPNetworkDomain) {
					((IPNetworkDomain) dstElement).setASNumber(((IPNetworkDomain) srcElement).getASNumber());
					((IPNetworkDomain) dstElement).setIPAddressRanges(((IPNetworkDomain) srcElement).getIPAddressRanges());
				}
			}
		}
		return destination;
	}

}
