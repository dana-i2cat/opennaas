import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.models.router.RouterModelFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.iaasframework.capabilities.model.IResourceModel;
import com.iaasframework.capabilities.model.IResourceModelFactory;
import com.iaasframework.capabilities.model.ModelException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;

public class TestModelRouter {
	IResourceModelFactory			modelFactory					= null;

	private CapabilityDescriptor	badFormedCapabilityDescriptor	= null;
	private CapabilityDescriptor	wellFormedCapabilityDescriptor	= null;

	@Before
	public void setUp() {
		modelFactory = new RouterModelFactory();

		wellFormedCapabilityDescriptor = new CapabilityDescriptor();
		List<CapabilityProperty> capabilities = new ArrayList<CapabilityProperty>();
		capabilities.add(createCapability("routerName", "RouterTest"));
		wellFormedCapabilityDescriptor.setCapabilityProperties(capabilities);

		badFormedCapabilityDescriptor = new CapabilityDescriptor();

	}

	private CapabilityProperty createCapability(String name, String value) {
		CapabilityProperty capabilityProperty = new CapabilityProperty();
		capabilityProperty.setName(name);
		capabilityProperty.setValue(value);
		return capabilityProperty;
	}

	@Test
	public void testBadFormedCapabilityDescriptor() {
		try {
			modelFactory
					.createResourceModelInstance(badFormedCapabilityDescriptor);

		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(true);
			return;
		}
		Assert.assertTrue(false);

	}

	@Test
	public void testWellFormedCapabilityDescriptor() {
		IResourceModel routerResource = null;
		try {
			routerResource = modelFactory
					.createResourceModelInstance(wellFormedCapabilityDescriptor);
			routerResource.toXML();

		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(false);
			return;
		}
		Assert.assertNotNull(routerResource);
	}

}
