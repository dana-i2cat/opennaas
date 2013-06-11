package org.opennaas.extensions.pdu.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.pdu.capability.AbstractNotQueueingCapability;
import org.opennaas.extensions.pdu.model.PDUModel;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(PDUResourceBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		resetModel(resource);
		// Add here all the necessary methods to populate resource model

		PDU pdu = new PDU();
		pdu.setName(resource.getResourceDescriptor().getInformation().getName());
		((PDUModel) resource.getModel()).setPdu(pdu);

		// add pdu to GIModel
		// getGIModel().setDeliveries(new ArrayList<IPowerDelivery>(Arrays.asList(pdu)));

		try {
			for (ICapability capability : resource.getCapabilities()) {
				if (capability instanceof AbstractNotQueueingCapability) {
					((AbstractNotQueueingCapability) capability).resyncModel();
				}
			}
		} catch (Exception e) {
			throw new ResourceException("Error during resource startup. Failed to execute capabilities resyncModel.", e);
		}

	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		oldModel = resource.getModel();
		resource.setModel(new PDUModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		// remove pdu from GIModel
		// PDU pdu = ((PDUModel) resource.getModel()).getPdu();
		// getGIModel().getDeliveries().remove(pdu);

		((PDUModel) resource.getModel()).setPdu(null);
		resource.setModel(oldModel);
	}

	// FIXME return instance from an osgi service
	private GIModel getGIModel() {
		return new GIModel();
	}

}
