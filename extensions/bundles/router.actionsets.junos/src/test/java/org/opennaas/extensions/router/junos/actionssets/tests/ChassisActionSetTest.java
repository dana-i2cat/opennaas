package org.opennaas.extensions.router.junos.actionssets.tests;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.router.junos.actionssets.ChassisActionSet;

public class ChassisActionSetTest {
	private static ChassisActionSet	chassis;
	private List<String>			actionsNames;
	Log								log	= LogFactory.getLog(ChassisActionSetTest.class);

	@BeforeClass
	public static void testChassisActionSet() {
		chassis = new ChassisActionSet();
	}

	@Test
	public void getActionNamesTest() {
		actionsNames = chassis.getActionNames();
		assert (actionsNames.size() != 0);
		for (String names : actionsNames) {
			log.info(names);
		}
	}

	@Test
	public void getActionSetIdTest() {
		String actionSetId = chassis.getActionSetId();
		assert (actionSetId != null);
		assert (actionSetId.equalsIgnoreCase("chassisActionSet"));
	}

	@Test
	public void getActionClassNameTest() {
		actionsNames = chassis.getActionNames();
		for (String names : actionsNames) {
			String action = chassis.getAction(names).getName();
			assert (action != null);
			log.info(action);
		}
	}

	@Test
	public void getActionTest() {
		actionsNames = chassis.getActionNames();
		try {
			for (String names : actionsNames) {
				Action action = chassis.obtainAction(names);
				assert (action.getActionID() != null);
				// assert (action.getActionID().equalsIgnoreCase(names));
			}
		} catch (ActionException e) {
			log.error(e.getMessage());
			Assert.fail(e.getLocalizedMessage());
		}
	}

}
