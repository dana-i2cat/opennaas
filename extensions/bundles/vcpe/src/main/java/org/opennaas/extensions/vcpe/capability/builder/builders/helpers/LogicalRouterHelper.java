/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders.helpers;

import java.util.List;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapabilityLifecycle.State;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 */
public class LogicalRouterHelper extends GenericHelper {

	/**
	 * Create a Logical Router
	 * 
	 * @param phy
	 * @param lr
	 * @param model
	 * @throws ResourceException
	 */
	public static void createLR(Router phy, Router lr, VCPENetworkModel model) throws ResourceException {
		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));

		ComputerSystem lrModel = VCPEToRouterModelTranslator.vCPERouterToRouterWithoutIfaces(lr, model);

		IChassisCapability lrCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);
		lrCapability.createLogicalRouter(lrModel);
	}

	/**
	 * Remove a Logical Router
	 * 
	 * @param phy
	 * @param lr
	 * @param model
	 * @throws ResourceException
	 */
	public static void removeLR(Router phy, Router lr, VCPENetworkModel model) throws ResourceException {
		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));

		// remove LR from phyRouter config
		ComputerSystem lrModel = VCPEToRouterModelTranslator.vCPERouterToRouter(lr, model);
		IChassisCapability lrCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);
		lrCapability.deleteLogicalRouter(lrModel);
	}

	/**
	 * @param router
	 * @throws ResourceException
	 */
	public static void removeLRFromRM(Router router) throws ResourceException {
		IResourceIdentifier lrId = getResourceManager().getIdentifierFromResourceName("router", router.getName());
		IResource lrResource = getResourceManager().getResource(lrId);
		if (lrResource.getState().equals(State.ACTIVE)) {
			getResourceManager().stopResource(lrId);
		}
		getResourceManager().removeResource(lrId);
	}

	/**
	 * @param physical
	 * @param logical
	 * @throws ProtocolException
	 * @throws ResourceException
	 */
	public static void copyContextPhysicaltoLogical(Router physical, Router logical) throws ProtocolException, ResourceException {
		IProtocolSessionManager phyPSM = getProtocolManager().getProtocolSessionManager(
				getResourceManager().getIdentifierFromResourceName("router", physical.getName()).getId());

		IProtocolSessionManager logPSM = getProtocolManager().getProtocolSessionManager(
				getResourceManager().getIdentifierFromResourceName("router", logical.getName()).getId());

		List<ProtocolSessionContext> phyContexts = phyPSM.getRegisteredContexts();
		for (ProtocolSessionContext context : phyContexts) {
			logPSM.registerContext(context.clone());
		}
	}

}
