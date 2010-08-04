import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.models.router.RouterModelFactory;

import com.iaasframework.capabilities.model.IResourceModel;
import com.iaasframework.capabilities.model.IResourceModelFactory;
import com.iaasframework.capabilities.model.ModelException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;

public class TestModelRouter {
	IResourceModelFactory	modelFactory	= null;

	public void testWellFormedModel() {
		modelFactory = new RouterModelFactory();

		List<CapabilityProperty> capabilities = new ArrayList<CapabilityProperty>();
		CapabilityDescriptor capabDescriptor = new CapabilityDescriptor();
		capabDescriptor.setCapabilityProperties(capabilities);

		try {
			IResourceModel routerResource = modelFactory.createResourceModelInstance(capabDescriptor);
			routerResource.toXML();

		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
