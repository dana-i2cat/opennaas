package org.opennaas.extensions.router.junos.actionssets.tests;

import java.util.List;

import org.opennaas.extensions.router.junos.actionssets.QueueActionSet;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueueActionSetTest {
	private static QueueActionSet	queueActions;
	private List<String>			actionsNames;
	Log								log	= LogFactory.getLog(ChassisActionSetTest.class);

	@BeforeClass
	public static void testBasicActionSet() {
		queueActions = new QueueActionSet();
	}

	@Test
	public void getActionNamesTest() {
		actionsNames = queueActions.getActionNames();
		assert (actionsNames.size() != 0);
		for (String names : actionsNames) {
			log.info(names);
		}
	}

	@Test
	public void getActionSetIdTest() {
		String actionSetId = queueActions.getActionSetId();
		assert (actionSetId != null);
		assert (actionSetId.equalsIgnoreCase("queueActionSet"));
	}

	@Test
	public void getActionClassNameTest() {
		actionsNames = queueActions.getActionNames();
		for (String names : actionsNames) {
			String action = queueActions.getAction(names).getName();
			assert (action != null);
			log.info(action);
		}
	}

	@Test
	public void getActionTest() {
		actionsNames = queueActions.getActionNames();
		try {
			for (String names : actionsNames) {
				Action action = queueActions.obtainAction(names);
				assert (action.getActionID() != null);
				// assert (action.getActionID().equalsIgnoreCase(names));
			}
		} catch (ActionException e) {
			log.error(e.getMessage());
			Assert.fail(e.getLocalizedMessage());
		}
	}

}
