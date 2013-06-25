package org.opennaas.extensions.powernet.repository;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.powernet.model.PowerNetModel;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
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

		resetModel(resource);

	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		PowerNetModel model = new PowerNetModel();
		model.setId(resource.getResourceIdentifier().getId());
		model.setConsumers(new ArrayList<PowerConsumer>());
		model.setDeliveries(new ArrayList<PowerDelivery>());
		model.setSupplies(new ArrayList<PowerSupply>());
		resource.setModel(model);
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
