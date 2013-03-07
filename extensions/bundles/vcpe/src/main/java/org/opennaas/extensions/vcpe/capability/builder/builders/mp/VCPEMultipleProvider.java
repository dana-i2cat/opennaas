/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders.mp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.vcpe.capability.builder.builders.IVCPENetworkBuilder;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.GenericHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.IPHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.InterfaceHelper;
import org.opennaas.extensions.vcpe.capability.builder.builders.helpers.LogicalRouterHelper;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
import org.opennaas.extensions.vcpe.model.PhysicalRouter;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class VCPEMultipleProvider implements IVCPENetworkBuilder {

	private Log	log	= LogFactory.getLog(VCPEMultipleProvider.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.capability.builder.builders.IVCPENetworkBuilder#build(org.opennaas.core.resources.IResource,
	 * org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public VCPENetworkModel build(IResource vcpe, VCPENetworkModel desiredScenario) throws ResourceException {
		log.info("Build the vCPE multiple provider network of the resource: " + ((VCPENetworkModel) vcpe.getModel()).getId());

		boolean lrsCreated = false;

		try {

			// Create logical routers in physical and start
			createLogicalRouters(vcpe, desiredScenario);
			executePhysicalRouters(desiredScenario);
			lrsCreated = true;
			startLogicalRouters(vcpe, desiredScenario);

			// Configure subinterfaces
			createSubInterfaces(vcpe, desiredScenario);
			assignIPAddresses(vcpe, desiredScenario);
			executeLogicalRouters(desiredScenario);

			// TODO Configure routing protocols
			// configureStaticRoutes(vcpe, desiredScenario);
			// executeLogicalRouters(desiredScenario);

			// TODO return created model, not the desired one
			vcpe.setModel(desiredScenario);
			((VCPENetworkModel) vcpe.getModel()).setCreated(true);

			return (VCPENetworkModel) vcpe.getModel();

		} catch (ResourceException e) {
			// rollback
			destroy(vcpe, desiredScenario, lrsCreated);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.capability.builder.builders.IVCPENetworkBuilder#destroy(org.opennaas.core.resources.IResource,
	 * org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public VCPENetworkModel destroy(IResource vcpe, VCPENetworkModel currentScenario) throws ResourceException {
		return destroy(vcpe, currentScenario, true);
	}

	private VCPENetworkModel destroy(IResource vcpe, VCPENetworkModel currentScenario, boolean destroyLRs) throws ResourceException {
		log.info("Destroy the vCPE multiple provider network of the resource: " + ((VCPENetworkModel) vcpe.getModel()).getId());
		if (destroyLRs) {
			// Destroy logical routers and this will destroy
			// their subinterfaces and routing protocols
			stopLogicalRouters(vcpe, currentScenario);
			removeLogicalRouters(vcpe, currentScenario);
			executePhysicalRouters(currentScenario);
			removeLogicalRouterResources(currentScenario);
		}

		// return the model after deleting all LR and subInterfaces from it
		VCPENetworkModel model = new VCPENetworkModel();
		model.setId(currentScenario.getId());
		model.setName(currentScenario.getName());
		model.setCreated(false);
		vcpe.setModel(model);

		return (VCPENetworkModel) vcpe.getModel();
	}

	private void createSubInterfaces(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		for (LogicalRouter lr : getLogicalRouters(model)) {
			InterfaceHelper.createInterfaces(lr, lr.getInterfaces(), model);
		}
	}

	private void assignIPAddresses(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		for (LogicalRouter lr : getLogicalRouters(model)) {
			for (Interface iface : lr.getInterfaces()) {
				IPHelper.setIP(lr, iface, model);
			}
		}
	}

	private void configureStaticRoutes(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		// TODO Auto-generated method stub
		throw new ResourceException("Not implemented operation");
	}

	private void createLogicalRouters(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		for (Router router : getLogicalRouters(model)) {
			LogicalRouterHelper.createLR(((LogicalRouter) router).getPhysicalRouter(), router, model);
		}
	}

	private void removeLogicalRouters(IResource vcpe, VCPENetworkModel model) throws ResourceException {
		for (Router router : getLogicalRouters(model)) {
			LogicalRouterHelper.removeLR(((LogicalRouter) router).getPhysicalRouter(), router, model);
		}
	}

	private void removeLogicalRouterResources(VCPENetworkModel model) throws ResourceException {
		for (Router router : getLogicalRouters(model)) {
			LogicalRouterHelper.removeLRFromRM(router);
		}
	}

	private void startLogicalRouters(IResource vcpe, VCPENetworkModel desiredScenario) throws ResourceException {
		IResourceManager rm = GenericHelper.getResourceManager();
		for (LogicalRouter router : getLogicalRouters(desiredScenario)) {
			try {
				LogicalRouterHelper.copyContextPhysicaltoLogical(router.getPhysicalRouter(), router);
			} catch (ProtocolException e) {
				throw new ResourceException("Failed to start logical louters", e);
			}
			rm.startResource(rm.getIdentifierFromResourceName("router", router.getName()));
		}
	}

	private void stopLogicalRouters(IResource vcpe, VCPENetworkModel desiredScenario) throws ResourceException {
		IResourceManager rm = GenericHelper.getResourceManager();
		for (Router router : getLogicalRouters(desiredScenario)) {
			IResourceIdentifier id = rm.getIdentifierFromResourceName("router", router.getName());
			if (rm.getResource(id).getState().equals(State.ACTIVE)) {
				rm.stopResource(id);
			}
		}
	}

	private void executeLogicalRouters(VCPENetworkModel desiredScenario) throws ResourceException {
		log.info("Execute the logical routers");
		try {
			IResourceManager rm = GenericHelper.getResourceManager();
			for (LogicalRouter lr : getLogicalRouters(desiredScenario)) {
				IResource lrResource = rm.getResource(rm.getIdentifierFromResourceName("router", lr.getName()));
				GenericHelper.executeQueue(lrResource);
			}
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}
	}

	private void executePhysicalRouters(VCPENetworkModel desiredScenario) throws ResourceException {
		log.info("Execute the physical routers");
		try {
			IResourceManager rm = GenericHelper.getResourceManager();
			for (PhysicalRouter router : getPhysicalRouters(desiredScenario)) {
				IResource routerResource = rm.getResource(rm.getIdentifierFromResourceName("router", router.getName()));
				GenericHelper.executeQueue(routerResource);
			}
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}
	}

	private Iterable<LogicalRouter> getLogicalRouters(VCPENetworkModel model) {
		return Iterables.filter(VCPENetworkModelHelper.getRouters(model.getElements()), LogicalRouter.class);
	}

	private Iterable<PhysicalRouter> getPhysicalRouters(VCPENetworkModel model) {
		return Iterables.filter(VCPENetworkModelHelper.getRouters(model.getElements()), PhysicalRouter.class);
	}
}
