package net.i2cat.mantychore.model.tests;

import net.i2cat.mantychore.model.Association;
import net.i2cat.mantychore.model.Component;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.ManagedElement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedElementTest {
	Logger			log			= LoggerFactory.getLogger(ManagedElementTest.class);

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

	@Test
	public void dependencyAddOrRemoveTest() {
		// FIXME, IS IT NECESSARY USE THIS TEST
		// ManagedElement leader = students; // this person is a leader, crowd
		// depends on leader
		// Dependency leadership = (Dependency)
		// Association.link(Dependency.class,leader, school); // crowd depends
		// on leader
		//		
		// assertTrue("LEADER is depended by CROWD",
		// leader.getToAssociationByElement(school).equals(leadership));
		// assertTrue("CROWD depends on LEADER",
		// school.getFromAssociationByElement(leader).equals(leadership));
		//		
		// assertTrue("CROWD depends on LEADER",
		// school.getFromAssociationsByType(Dependency.class).contains(leader));
		// assertTrue("LEADER is depended by CROWD",
		// leader.getToAssociationsByType(Dependency.class).contains(school) );
		//		
		// // remove by dependency reference
		// leadership.unlink();
		//		
		// assertTrue("LEADER is depended by CROWD",
		// leader.getToAssociationByElement(school) == null);
		// assertTrue("CROWD depends on LEADER",
		// school.getFromAssociationByElement(leader) == null);
		//		
		// assertTrue("CROWD depends on LEADER",
		// !school.getFromAssociationsByType(Dependency.class).contains(leader));
		// assertTrue("LEADER is depended by CROWD",
		// !leader.getToAssociationsByType(Dependency.class).contains(school) );
	}
}
