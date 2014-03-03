package org.opennaas.extensions.genericnetwork.test.driver.internal.actionsets;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingParamsMapping;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.FindPathForRequestAction;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class FindPathForRequestActionTest {

	private final static String			MAPPING_URL	= "src/test/resources/routeMapping.xml";
	private final static String			ROUTE_URL	= "src/test/resources/sampleXMLRoutes.xml";

	private FindPathForRequestAction	action;
	private Map<String, Object>			params;

	/**
	 * Assures that the checkParams does not fail with a circuitRequest and two valids urls.
	 * 
	 * @throws ActionException
	 */
	@Test
	public void checkParamsTest() throws ActionException {

		CircuitRequest request = new CircuitRequest();

		params.put(PathFindingParamsMapping.REQUEST_KEY, request);
		params.put(PathFindingParamsMapping.ROUTES_FILE_KEY, ROUTE_URL);
		params.put(PathFindingParamsMapping.ROUTES_MAPPING_KEY, MAPPING_URL);

		action.checkParams(params);

	}

	/**
	 * Assures that the checkParams fails with null parameters.
	 * 
	 * @throws ActionException
	 */
	@Test(expected = ActionException.class)
	public void checkNullParamsTest() throws ActionException {

		params = null;
		action.checkParams(params);

	}

	/**
	 * Assures that the checkParams fails with not valid parameters.
	 * 
	 * @throws ActionException
	 */
	@Test(expected = ActionException.class)
	public void checkBadParamsTest() throws ActionException {

		String badParams = new String();

		action.checkParams(badParams);

	}

	/**
	 * Assures that the checkParams fails with empty Hashmap.
	 * 
	 * @throws ActionException
	 */
	@Test(expected = ActionException.class)
	public void checkEmptyParamsTest() throws ActionException {

		action.checkParams(params);

	}

	/**
	 * Assures that the checkParams fails without route file.
	 * 
	 * @throws ActionException
	 */
	@Test(expected = ActionException.class)
	public void checkParamsWithoutRoutesFileTest() throws ActionException {

		CircuitRequest request = new CircuitRequest();

		params.put(PathFindingParamsMapping.REQUEST_KEY, request);
		params.put(PathFindingParamsMapping.ROUTES_MAPPING_KEY, MAPPING_URL);

		action.checkParams(params);

	}

	/**
	 * Assures that the checkParams fails without map file.
	 * 
	 * @throws ActionException
	 */
	@Test(expected = ActionException.class)
	public void checkParamsWithoutMappingFileTest() throws ActionException {

		CircuitRequest request = new CircuitRequest();

		params.put(PathFindingParamsMapping.REQUEST_KEY, request);
		params.put(PathFindingParamsMapping.ROUTES_FILE_KEY, ROUTE_URL);

		action.checkParams(params);

	}

	/**
	 * Assures that the checkParams fails without CircuitRequest.
	 * 
	 * @throws ActionException
	 */
	@Test(expected = ActionException.class)
	public void checkParamsWithoutRequestTest() throws ActionException {

		params.put(PathFindingParamsMapping.ROUTES_MAPPING_KEY, MAPPING_URL);
		params.put(PathFindingParamsMapping.ROUTES_FILE_KEY, ROUTE_URL);

		action.checkParams(params);

	}

	@Before
	public void prepareTest() throws IOException, SerializationException {

		action = new FindPathForRequestAction();
		params = new HashMap<String, Object>();

	}

}
