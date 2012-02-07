package net.i2cat.mantychore.actionsets.junos.tests;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ChassisActionSet;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
