/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates.mp;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;
import org.opennaas.extensions.vcpe.model.IPNetworkDomain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

import com.google.common.collect.Iterables;

/**
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * @author Jordi
 */
public class MultipleProviderTemplate implements ITemplate {

	/**
	 * FIXME TO BE REMOVED
	 */
	private static final boolean	APPLY_WORKAROUND	= true;

	private String					templateType		= ITemplate.MP_VCPE_TEMPLATE;

	private MPTemplateSuggestor		suggestor;

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
	 *            model with user preferences. It MAY not contain all elements, but MUST contain every configurable element (including physical
	 *            infrastructure)
	 * @return complete model with all required values.
	 */
	@Override
	public VCPENetworkModel buildModel(VCPENetworkModel initialModel) {

		// WORKAROUND starts here
		// When given initialModel does not contain physical elements, this workaround must be executed
		if (APPLY_WORKAROUND) {
			initialModel = buildModelWithPhyInfrastructureWorkaround(initialModel);
		}
		// WORKAROUND ends here

		VCPENetworkModel model = MPTemplateModelBuilder.generateModel();
		model = partialCopy(initialModel, model);
		model = hierarchicalLRnames(model);
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

	/**
	 * Copies data in elements from given source to elements in given destination with same templateName. No element is created within this method.
	 * Does not copy references to other elements (interfaces, physicalRouter, etc.)
	 * 
	 * @param source
	 * @param destination
	 * @return destination updated with values in source.
	 */
	private VCPENetworkModel partialCopy(VCPENetworkModel source, VCPENetworkModel destination) {
		destination.setName(source.getName());
		destination.setId(source.getId());

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

	private VCPENetworkModel hierarchicalLRnames(VCPENetworkModel model) {
		for (LogicalRouter lr : Iterables.filter(VCPENetworkModelHelper.getRouters(model.getElements()), LogicalRouter.class)) {
			lr.setName(model.getName() + "-" + lr.getName());
		}
		return model;
	}

	/**
	 * FIXME TO BE REMOVED, part of WORKAROUND
	 * 
	 * @param initialModel
	 *            model with user preferences. Without physicalInfrastructure elements
	 * @return given initialModel merged with suggested physical one.
	 */
	private VCPENetworkModel buildModelWithPhyInfrastructureWorkaround(VCPENetworkModel initialModelLogical) {
		// Merge initialModelLogical with suggested physical
		VCPENetworkModel phy = MPTemplateModelBuilder.generatePhysicalElements();
		phy = suggestor.getSuggestionForPhysicalModel(phy);

		VCPENetworkModel logical = MPTemplateModelBuilder.generateLogicalElements();
		logical = partialCopy(initialModelLogical, logical);

		VCPENetworkModel logicalWithSuggestedPhysical = MPTemplateModelBuilder.mapLogicalAndPhysical(phy, logical);
		return logicalWithSuggestedPhysical;
	}

}
