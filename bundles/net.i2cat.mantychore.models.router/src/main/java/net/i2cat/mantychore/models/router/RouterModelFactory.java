package net.i2cat.mantychore.models.router;

import com.iaasframework.capabilities.model.IResourceModel;
import com.iaasframework.capabilities.model.IResourceModelFactory;
import com.iaasframework.capabilities.model.ModelException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class RouterModelFactory implements IResourceModelFactory {

	public synchronized IResourceModel createResourceModelInstance(CapabilityDescriptor descriptor) throws ModelException {

		// if (descriptor.getPropertyValue("Transport Identifier") == null) {
		// throw new
		// ModelException("The Transport Identifier property is required to create an router model instance");
		// }

		RouterModel router = new RouterModel();

		// router.setTransportIdentifier(descriptor.getPropertyValue("Transport Identifier"));

		// FIXME call the model validators before returning the instance? They
		// should apply even if the model is empty.?

		return router;
	}
}