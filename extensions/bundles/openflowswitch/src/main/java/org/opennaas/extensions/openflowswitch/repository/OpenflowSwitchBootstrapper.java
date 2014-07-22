package org.opennaas.extensions.openflowswitch.repository;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
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

		OpenflowSwitchModel model = prepareNewModel();

		resource.setModel(model);

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

	private OpenflowSwitchModel prepareNewModel() {

		OpenflowSwitchModel model = new OpenflowSwitchModel();
		OFFlowTable flowTable = new OFFlowTable();
		model.getOfTables().add(flowTable);

		return model;
	}
}
