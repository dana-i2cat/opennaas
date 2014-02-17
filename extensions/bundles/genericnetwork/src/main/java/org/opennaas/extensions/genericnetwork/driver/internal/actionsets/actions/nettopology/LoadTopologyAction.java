package org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.nettopology;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.genericnetwork.capability.nettopology.NetTopologyActionSet;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;

/**
 * Reads topology from a file and stores it in the resource model.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class LoadTopologyAction extends Action {

	public LoadTopologyAction() {
		setActionID(NetTopologyActionSet.LOAD_TOPOLOGY);
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (!(params instanceof String)) {
			throw new ActionException(getActionID() + " should receive a path to a topology file");
		}

		try {
			// Check file exists
			FileInputStream file = new FileInputStream((String) params);
		} catch (FileNotFoundException e) {
			throw new ActionException(getActionID() + " should receive a path to an existing topology file", e);
		}

		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		Topology topology;
		try {

			topology = loadTopologyFromFile();
			updateModel(topology);
			return ActionResponse.okResponse(getActionID());

		} catch (FileNotFoundException e) {
			throw new ActionException(getActionID() + " should receive a path to an existing topology file", e);
		} catch (SerializationException e) {
			throw new ActionException("Invalid topology file: " + params, e);
		}
	}

	private Topology loadTopologyFromFile() throws FileNotFoundException, SerializationException {
		FileInputStream file = new FileInputStream((String) params);
		return ObjectSerializer.fromXml(file, Topology.class);
	}

	private void updateModel(Topology topology) {
		((GenericNetworkModel) modelToUpdate).setTopology(topology);
	}
}
