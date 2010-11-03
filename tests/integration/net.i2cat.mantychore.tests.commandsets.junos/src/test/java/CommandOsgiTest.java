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
public class CommandOsgiTest {

	@Inject
	BundleContext	bundleContext	= null;

	@Configuration
	public static Option[] configure() {
		return ConfigurerTestFactory.newServiceMixTest();
	}

	@Test
	public void ListBundles() {
		System.out.println("This is running inside Felix. With all configuration set up like you specified. ");
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listBundles(bundleContext);
		CommandSetCapabilityClient commandClient = 
		
		
		
		/* initialize client */

	}

	public static void listBundles(BundleContext bundleContext) {
		Bundle b = null;
		for (int i = 0; i < bundleContext.getBundles().length; i++) {
			b = bundleContext.getBundles()[i];
			System.out.println(b.toString() + " : " + getStateString(b.getState()));
			if (getStateString(b.getState()).equals("INSTALLED")) {
				try {
					b.start();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	private static String getStateString(int value) {
		if (value == Bundle.ACTIVE) {
			return "ACTIVE";
		} else if (value == Bundle.INSTALLED) {
			return "INSTALLED";
		} else if (value == Bundle.RESOLVED) {
			return "RESOLVED";
		} else if (value == Bundle.UNINSTALLED) {
			return "UNINSTALLED";
		}

		return "UNKNOWN";
	}

}
