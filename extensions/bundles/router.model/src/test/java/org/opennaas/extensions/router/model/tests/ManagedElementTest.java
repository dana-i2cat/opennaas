package org.opennaas.extensions.router.model.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.extensions.router.model.Association;
import org.opennaas.extensions.router.model.Component;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.ManagedElement;

public class ManagedElementTest {
	Log				log			= LogFactory.getLog(ManagedElementTest.class);

	// A crowd is composed by a group of persons.
	ManagedElement	school		= new ComputerSystem();
	ManagedElement	students	= new ComputerSystem();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		school = new ComputerSystem(); // ManagedSystemElement is abstract, any
		// concrete child class will do.
		students = new ComputerSystem(); // ManagedSystemElement is abstract,
		// any concrete child class will do.
	}

	@After
	public void tearDown() throws Exception {
		school = null;
		students = null;
	}

	@Test
	public void componentAddOrRemoveTest() {

		log.debug("Testing ComponentAddorRemoveTest...");
		Component comp = (Component) Association.link(Component.class, school, students); // person
		// is
		// part
		// of
		// crowd

		Assert.assertTrue("PERSON contains CROWD as a group component", students.getFromAssociatedElementsByType(Component.class).contains(school));
		Assert.assertTrue("CROWD contains PERSON as a part component", school.getToAssociatedElementsByType(Component.class).contains(students));

		// remove by component association reference
		comp.unlink();

		Assert.assertTrue("CROWN doesn't contain PERSON as a part component", !school.getToAssociatedElementsByType(Component.class).contains(
				students));
		Assert.assertTrue("PERSON doesn't contain CROWD as a group component", !students.getFromAssociatedElementsByType(Component.class).contains(
				school));

		log.debug("Tested");
	}

}
