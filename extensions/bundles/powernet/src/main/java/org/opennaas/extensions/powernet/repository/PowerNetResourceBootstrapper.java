package org.opennaas.extensions.powernet.repository;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.powernet.model.PowerNetModel;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class PowerNetResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(PowerNetResourceBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		oldModel = resource.getModel();

		// Add here all the necessary methods to populate resource model

		PowerNetModel model = new PowerNetModel();
		model.setId(resource.getResourceIdentifier().getId());
		model.setConsumers(new ArrayList<IPowerConsumer>());
		model.setDeliveries(new ArrayList<IPowerDelivery>());
		model.setSupplies(new ArrayList<IPowerSupply>());
		resource.setModel(model);
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		resource.setModel(new PowerNetModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
