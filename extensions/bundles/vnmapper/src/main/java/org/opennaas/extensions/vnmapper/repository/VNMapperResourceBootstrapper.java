package org.opennaas.extensions.vnmapper.repository;

/*
 * #%L
 * OpenNaaS :: VNMapper Resource
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
import org.opennaas.extensions.vnmapper.model.VNMapperModel;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class VNMapperResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(VNMapperResourceBootstrapper.class);

	private IModel	oldModel;

	public void bootstrap(Resource resource) throws ResourceException {

		log.info("Loading bootstrap to start resource...");
		resource.setModel(new VNMapperModel());
		// Add here all the necessary methods to populate resource model
		//
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		resource.setModel(new VNMapperModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
