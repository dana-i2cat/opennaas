package org.opennaas.extensions.ofertie.ncl.provisioner.components;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import java.io.IOException;

import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.ofertie.ncl.notification.INCLNotifierClient;
import org.opennaas.extensions.ofertie.ncl.notification.NCLNotifierClient;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.User;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ClientFactory {

	public static INCLNotifierClient createClient(String username) throws ProvisionerException {

		UserManager manager = new UserManager();

		User user;
		try {
			user = manager.getUser(username);
			if (user == null)
				throw new ProvisionerException("Could not find user with username :" + username);

			return new NCLNotifierClient(user.getSdnModuleUri());

		} catch (IOException e) {
			throw new ProvisionerException(e);
		} catch (SerializationException e) {
			throw new ProvisionerException(e);
		}

	}

}
