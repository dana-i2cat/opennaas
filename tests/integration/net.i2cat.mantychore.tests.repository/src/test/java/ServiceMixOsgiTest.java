import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import utils.ConfigurerTestFactory;

@RunWith(JUnit4TestRunner.class)
public class ServiceMixOsgiTest {

	@Inject
	BundleContext	bundleContext	= null;

	@Configuration
	public static Option[] configure() {
		return ConfigurerTestFactory.newServiceMixTest();
	}

	@Test
	public void ListBundles() {
		System.out.println("This is running inside Felix. With all configuration set up like you specified. ");
		// feel free to add breakpoints and debug.
		for (Bundle b : bundleContext.getBundles()) {
			System.out.println("Bundle " + b.getBundleId() + " : " + b.getSymbolicName());
		}

	}

}
