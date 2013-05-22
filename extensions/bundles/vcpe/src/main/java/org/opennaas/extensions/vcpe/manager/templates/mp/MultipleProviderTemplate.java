/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates.mp;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;
import org.opennaas.extensions.vcpe.model.IPNetworkDomain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;
import org.opennaas.extensions.vcpe.model.routing.RoutingConfiguration;
import org.opennaas.extensions.vcpe.model.routing.StaticRouteConfiguration;

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
		model = configureRouting(model);
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
					((IPNetworkDomain) dstElement).setOwner(((IPNetworkDomain) srcElement).getOwner());
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

	private VCPENetworkModel configureRouting(VCPENetworkModel model) {
		// reset routing configuration for LRs
		for (LogicalRouter lr : Iterables.filter(VCPENetworkModelHelper.getRouters(model.getElements()), LogicalRouter.class)) {
			lr.setRoutingConfiguration(new RoutingConfiguration());
		}

		configureStaticRoutes(model);
		return model;
	}

	/**
	 * Creates MP-template required static routes in given model.
	 * 
	 * Creates following static routes:
	 * 
	 * in LR1: <br/>
	 * static route LAN_CLIENT.getIPAddressRange() next-hop LR_CLIENT_IFACE_UP1.getIPAddress() (through LR_1_IFACE_DOWN) <br/>
	 * static route 0.0.0.0/0 next-hop WAN1_IFACE_DOWN.getIPAddress() (through LR_1_IFACE_UP) <br/>
	 * 
	 * in LR2: <br/>
	 * static route LAN_CLIENT.getIPAddressRange() next-hop LR_CLIENT_IFACE_UP2.getIPAddress() (through LR_2_IFACE_DOWN) <br/>
	 * static route 0.0.0.0/0 next-hop WAN2_IFACE_DOWN.getIPAddress() (through LR_2_IFACE_UP) <br/>
	 * 
	 * in LR_CLIENT: <br/>
	 * static route LAN_CLIENT.getIPAddressRange() next-hop LAN_CLIENT_IFACE_UP.getIPAddress() (through LR_CLIENT_IFACE_DOWN) <br/>
	 * static route WAN1.getIPAddressRange() next-hop LR_1_IFACE_DOWN.getIPAddress() (through LR_CLIENT_IFACE_UP1) <br/>
	 * static route WAN2.getIPAddressRange() next-hop LR_2_IFACE_DOWN.getIPAddress() (through LR_CLIENT_IFACE_UP2) <br/>
	 * static route 0.0.0.0/0 next-hop LR_1_IFACE_DOWN.getIPAddress() (through LR_CLIENT_IFACE_UP1) //default provider is WAN1 <br/>
	 * 
	 * Documented in issue OPENNAAS-869.
	 * 
	 * @param model
	 * @return given model with required static routes included
	 */
	private VCPENetworkModel configureStaticRoutes(VCPENetworkModel model) {

		String allRoutes = "0.0.0.0/0";
		RoutingConfiguration config;

		List<String> clientIPRanges = ((IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LAN_CLIENT))
				.getIPAddressRanges();
		List<String> wan1IPRanges = ((IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.WAN1))
				.getIPAddressRanges();
		List<String> wan2IPRanges = ((IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.WAN2))
				.getIPAddressRanges();

		// LR_1
		config = ((Router) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LR_1_ROUTER))
				.getRoutingConfiguration();
		config.setStaticRoutes(new ArrayList<StaticRouteConfiguration>(2));

		// static route LAN_CLIENT.getIPAddressRange() next-hop LR_CLIENT_IFACE_UP1.getIPAddress() (through LR_1_IFACE_DOWN)
		for (String ipRange : clientIPRanges) {
			config.getStaticRoutes().add(new StaticRouteConfiguration(
					ipRange,
					((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LR_CLIENT_IFACE_UP1)).getIpAddress()
					, false));
		}
		// static route 0.0.0.0/0 next-hop WAN1_IFACE_DOWN.getIPAddress() (through LR_1_IFACE_UP)
		config.getStaticRoutes().add(new StaticRouteConfiguration(
				allRoutes,
				((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.WAN1_IFACE_DOWN)).getIpAddress()
				, false));

		// LR_2
		config = ((Router) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LR_2_ROUTER))
				.getRoutingConfiguration();
		config.setStaticRoutes(new ArrayList<StaticRouteConfiguration>(2));
		// static route LAN_CLIENT.getIPAddressRange() next-hop LR_CLIENT_IFACE_UP2.getIPAddress() (through LR_2_IFACE_DOWN)
		for (String ipRange : clientIPRanges) {
			config.getStaticRoutes().add(new StaticRouteConfiguration(
					ipRange,
					((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LR_CLIENT_IFACE_UP2)).getIpAddress()
					, false));
		}
		// static route 0.0.0.0/0 next-hop WAN2_IFACE_DOWN.getIPAddress() (through LR_2_IFACE_UP)
		config.getStaticRoutes().add(new StaticRouteConfiguration(
				allRoutes,
				((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.WAN2_IFACE_DOWN)).getIpAddress()
				, false));

		// LR_CLIENT
		config = ((Router) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LR_CLIENT_ROUTER))
				.getRoutingConfiguration();
		config.setStaticRoutes(new ArrayList<StaticRouteConfiguration>(4));
		// static route LAN_CLIENT.getIPAddressRange() next-hop LAN_CLIENT_IFACE_UP.getIPAddress() (through LR_CLIENT_IFACE_DOWN)
		for (String ipRange : clientIPRanges) {
			config.getStaticRoutes().add(new StaticRouteConfiguration(
					ipRange,
					((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LAN_CLIENT_IFACE_UP)).getIpAddress()
					, false));
		}
		// static route WAN1.getIPAddressRange() next-hop LR_1_IFACE_DOWN.getIPAddress() (through LR_CLIENT_IFACE_UP1)
		for (String ipRange : wan1IPRanges) {
			config.getStaticRoutes().add(new StaticRouteConfiguration(
					ipRange,
					((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LR_1_IFACE_DOWN)).getIpAddress()
					, false));
		}
		// static route WAN2.getIPAddressRange() next-hop LR_2_IFACE_DOWN.getIPAddress() (through LR_CLIENT_IFACE_UP2)
		for (String ipRange : wan2IPRanges) {
			config.getStaticRoutes().add(new StaticRouteConfiguration(
					ipRange,
					((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LR_2_IFACE_DOWN)).getIpAddress()
					, false));
		}
		// static route 0.0.0.0/0 next-hop LR_1_IFACE_DOWN.getIPAddress() (through LR_CLIENT_IFACE_UP1) //default provider is WAN1
		config.getStaticRoutes().add(new StaticRouteConfiguration(
				allRoutes,
				((Interface) VCPENetworkModelHelper.getElementByTemplateName(model, TemplateConstants.LR_1_IFACE_DOWN)).getIpAddress()
				, false));

		return model;
	}
}
