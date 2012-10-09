package org.opennaas.extensions.vcpe.repository;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VCPENetBootstrapper implements IResourceBootstrapper {

	@Override
	public void bootstrap(Resource resource) throws ResourceException {

		VCPENetworkModel desiredScenario = loadModelFromDescriptor(
				(VCPENetworkDescriptor) resource.getResourceDescriptor());

		VCPENetworkModel currentModel = buildDesiredScenario(resource, desiredScenario);
		resource.setModel(currentModel);
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		VCPENetworkModel currentModel = unbuildScenario(resource, (VCPENetworkModel) resource.getModel());
		resource.setModel(currentModel);
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		// TODO Auto-generated method stub

	}

	private VCPENetworkModel loadModelFromDescriptor(VCPENetworkDescriptor descriptor)
			throws ResourceException {
		try {
			VCPENetworkModel model = (VCPENetworkModel) ObjectSerializer.fromXml(
					descriptor.getvCPEModel(), VCPENetworkModel.class);
			return model;
		} catch (SerializationException e) {
			throw new ResourceException(e);
		}
	}

	private VCPENetworkDescriptor storeModelIntoDescriptor(VCPENetworkModel model,
			VCPENetworkDescriptor descriptor) throws ResourceException {
		try {
			descriptor.setvCPEModel(ObjectSerializer.toXml(model));
			return descriptor;
		} catch (SerializationException e) {
			throw new ResourceException(e);
		}

	}

	private VCPENetworkModel buildDesiredScenario(Resource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		// createExternalLinks(resource, desiredScenario);

		createSubInterfaces(resource, desiredScenario);

		createLogicalRouters(resource, desiredScenario);

		// assignIPAddresses(resource, desiredScenario);

		// configureEGP(resource, desiredScenario);

		try {
			execute(desiredScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		// TODO Auto-generated method stub
		return desiredScenario;
	}

	private VCPENetworkModel unbuildScenario(Resource resource, VCPENetworkModel currentScenario) throws ResourceException {

		// unconfigureEGP(resource, currentScenario);

		// removeIPAddresses(resource, currentScenario);

		removeLogicalRouters(resource, currentScenario);

		removeSubInterfaces(resource, currentScenario);

		// destroyExternalLinks(resource, currentScenario);

		try {
			execute(currentScenario);
		} catch (ProtocolException e) {
			throw new ResourceException(e);
		}

		removeResources(currentScenario);

		// TODO Auto-generated method stub
		return currentScenario;
	}

	private void createLogicalRouters(Resource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		createLR(phy1, lr1, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		createLR(phy2, lr2, desiredScenario);
	}

	private void removeLogicalRouters(Resource resource, VCPENetworkModel desiredScenario) throws ResourceException {

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		removeLR(phy1, lr1, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		removeLR(phy2, lr2, desiredScenario);
	}

	private void createLR(Router phy, Router lr, VCPENetworkModel model) throws ResourceException {
		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));

		ComputerSystem lrModel = VCPEToRouterModelTranslator.vCPERouterToRouter(lr, model);

		IChassisCapability lrCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);
		lrCapability.createLogicalRouter(lrModel);
	}

	private void removeLR(Router phy, Router lr, VCPENetworkModel model) throws ResourceException {
		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));

		// remove LR from phyRouter config
		ComputerSystem lrModel = VCPEToRouterModelTranslator.vCPERouterToRouter(lr, model);
		IChassisCapability lrCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);
		lrCapability.deleteLogicalRouter(lrModel);

		// removing it from RM will be done after executing the queue
	}

	private void createSubInterfaces(Resource resource, VCPENetworkModel desiredScenario) throws ResourceException {
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE1_PHY_ROUTER);
		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE1_ROUTER);

		List<Interface> ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr1.getInterfaces());
		ifaces.add((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.UP1_INTERFACE_PEER));

		createInterfaces(phy1, ifaces, desiredScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.CPE2_PHY_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.VCPE2_ROUTER);

		ifaces = new ArrayList<Interface>();
		ifaces.addAll(lr2.getInterfaces());
		ifaces.add((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(desiredScenario, VCPETemplate.UP2_INTERFACE_PEER));

		createInterfaces(phy2, ifaces, desiredScenario);
	}

	private void removeSubInterfaces(Resource resource, VCPENetworkModel currentScenario) throws ResourceException {
		// SubInterfaces assigned to logical routers will be destroyed with them.
		// There is only need to remove other interfaces

		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(currentScenario, VCPETemplate.CPE1_PHY_ROUTER);
		List<Interface> ifaces = new ArrayList<Interface>();
		ifaces.add((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(currentScenario, VCPETemplate.UP1_INTERFACE_PEER));

		removeInterfaces(phy1, ifaces, currentScenario);

		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(currentScenario, VCPETemplate.CPE2_PHY_ROUTER);
		ifaces = new ArrayList<Interface>();
		ifaces.add((Interface) VCPENetworkModelHelper.getElementByNameInTemplate(currentScenario, VCPETemplate.UP2_INTERFACE_PEER));

		removeInterfaces(phy2, ifaces, currentScenario);

	}

	private void createInterfaces(Router phy, List<Interface> ifaces, VCPENetworkModel model) throws ResourceException {

		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));
		IChassisCapability chassisCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);

		for (Interface iface : ifaces) {
			NetworkPort port = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(iface, model);
			chassisCapability.createSubInterface(port);
			// Note: this call will also assign IP addresses to given interfaces
		}

	}

	private void removeInterfaces(Router phy, List<Interface> ifaces, VCPENetworkModel model) throws ResourceException {
		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));

		IChassisCapability chassisCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);

		for (Interface iface : ifaces) {
			NetworkPort port = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(iface, model);
			chassisCapability.deleteSubInterface(port);
		}
	}

	private void removeResources(VCPENetworkModel model) throws ResourceException {

		Router lr1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE1_ROUTER);
		Router lr2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.VCPE2_ROUTER);

		removeFromRM(lr1);
		removeFromRM(lr2);

	}

	private void removeFromRM(Router router) throws ResourceException {
		IResourceIdentifier lrId = getResourceManager().getIdentifierFromResourceName("router", router.getName());
		IResource lrResource = getResourceManager().getResource(lrId);
		if (lrResource.getState().equals(State.ACTIVE)) {
			getResourceManager().stopResource(lrId);
		}
		getResourceManager().removeResource(lrId);

	}

	private IResourceManager getResourceManager() throws ResourceException {
		try {
			return Activator.getResourceManagerService();
		} catch (ActivatorException e) {
			throw new ResourceException("Could not find ResourceManager", e);
		}
	}

	private void execute(VCPENetworkModel model) throws ResourceException, ProtocolException {
		Router phy1 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE1_PHY_ROUTER);
		Router phy2 = (Router) VCPENetworkModelHelper.getElementByNameInTemplate(model, VCPETemplate.CPE2_PHY_ROUTER);

		IResource phyResource1 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy1.getName()));
		IResource phyResource2 = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy2.getName()));

		execute(phyResource1);
		execute(phyResource2);
	}

	private void execute(IResource resource) throws ResourceException, ProtocolException {
		IQueueManagerCapability qCapability = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		qCapability.execute();
	}

}
