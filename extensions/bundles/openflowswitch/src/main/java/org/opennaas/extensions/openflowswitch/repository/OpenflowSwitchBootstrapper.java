package org.opennaas.extensions.openflowswitch.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class OpenflowSwitchBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(OpenflowSwitchBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");

		resource.setModel(new OpenflowSwitchModel());

	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {
		resource.setModel(oldModel);
	}

	/**
	 * Since we're not going to read information from real switch, we would not implement the reset model method, so no one can delete the model
	 * information. Model will be reset only during bootstrap.
	 * 
	 */
	@Override
	public void resetModel(Resource resource) throws ResourceException {
		log.info("Ignoring resetModel signal.");

	}

}
