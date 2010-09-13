import static org.junit.Assert.assertNotNull;
import net.i2cat.mantychore.tests.utils.ConfigurerTestFactory;

import org.apache.felix.karaf.testing.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import com.iaasframework.capabilities.model.IResourceModelFactory;

@RunWith(JUnit4TestRunner.class)
public class ModelTest extends AbstractIntegrationTest {
	IResourceModelFactory	resourceModel	= null;

	@Configuration
	public static Option[] configure() {
		return ConfigurerTestFactory.newModelTest();

	}

	@Before
	public void setup() {
		// resourceModel = getOsgiService(IResourceModelFactory.class, "",
		// 20000);
	}

	@Test
	public void testAll() {
		// IaaSIntegrationTestsHelper.listBundles(bundleContext);
		// bundleContextShouldNotBeNull();
		// testModelFactoryRegistered();

	}

	private void testModelFactoryRegistered() {
		assertNotNull(resourceModel);
	}

	public void bundleContextShouldNotBeNull() {
		assertNotNull(bundleContext);
	}

}