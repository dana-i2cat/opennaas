
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.equinox;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.workingDirectory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.felix.karaf.testing.AbstractIntegrationTest;
import org.apache.felix.karaf.testing.Helper;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

import com.iaasframework.capabilities.actionset.ActionSetCapability;
import com.iaasframework.capabilities.actionset.IActionFactory;
import com.iaasframework.capabilities.actionset.IActionSetConstants;
import com.iaasframework.resources.core.capability.CapabilityException;
import com.iaasframework.resources.core.capability.ICapabilityFactory;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;
import com.iaasframework.resources.core.descriptor.Information;
import com.iaasframework.extras.itesthelper.IaaSIntegrationTestsHelper;

/**
 * Unit test the ActionSetEngineModuleFactory.
 * 
 * @author Scott Campbell (CRC)
 * 
 */

@RunWith(JUnit4TestRunner.class)
public class ActionSetTest extends AbstractIntegrationTest{
	
	Logger logger = Logger.getLogger(ActionSetTest.class);

	@Inject
	private BundleContext bundleContext = null;

	/* The class under test */
	private ICapabilityFactory capabilityFactory = null;
	private CapabilityDescriptor capabilityDescriptor = null;
	private String resourceId = "test-resource123";

	@Configuration
	public static Option[] configuration() {


	}

	@Before
	public void setup() {
		logger.debug("Setting up for the test");

		// Register the mock ActionSet Service to the OSGI Registry
		registerActionSetService();

		// get the module factory from the registry;
		capabilityFactory = getOsgiService(ICapabilityFactory.class, "capability=ActionSet", 20000);

		//initialize the metadata for the test module.
		initializeCapabilityDescriptor();
	}
	
	private void initializeCapabilityDescriptor() {
		Information information = new Information("ActionSet", "GenericActionSet", "1.0.0");

		CapabilityProperty nameProperty = new CapabilityProperty(IActionSetConstants.ACTIONSET_NAME, "TestActionSet");
		CapabilityProperty versionProperty = new CapabilityProperty(IActionSetConstants.ACTIONSET_VERSION, "1.0.0");

		List<CapabilityProperty> capabilityProperties = new ArrayList<CapabilityProperty>();
		capabilityProperties.add(nameProperty);
		capabilityProperties.add(versionProperty);

		capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityProperties(capabilityProperties);
		capabilityDescriptor.setCapabilityInformation(information);
	}

	public void testCreateModule() throws CapabilityException {
		ActionSetCapability capability = null;

		logger.debug("Calling Create capability with capability descriptor: "
				+ capabilityDescriptor.getCapabilityInformation().toString());
		capability = (ActionSetCapability) capabilityFactory.create(capabilityDescriptor, resourceId);

		assertNotNull(capability);
		assertNotNull(capability.getActionFactory());
	}

	private void registerActionSetService() {

		IActionFactory actionFactory = new FakeActionFactory();

		Properties serviceProperties = new Properties();
		serviceProperties.put(IActionSetConstants.ACTIONSET_NAME, "TestActionSet");
		serviceProperties.put(IActionSetConstants.ACTIONSET_VERSION, "1.0.0");

		logger.info("Manually publishing ActionSet");
		bundleContext.registerService(IActionFactory.class.getName(), actionFactory, serviceProperties);
	}
	
	@Test
	public void testAll() throws CapabilityException {
		IaaSIntegrationTestsHelper.listBundles(bundleContext);
		bundleContextShouldNotBeNull();
		testModuleFactoryRegistered();
		testCreateModule();
	}
    
    private void bundleContextShouldNotBeNull(){
        assertNotNull(bundleContext);
     }
    
    private void testModuleFactoryRegistered(){
    	assertNotNull(capabilityFactory);
    }