package net.i2cat.mantychore.models.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.model.IResourceModel;
import com.iaasframework.capabilities.model.IResourceModelFactory;
import com.iaasframework.capabilities.model.ModelException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class RouterModelFactory implements IResourceModelFactory {
	/** The logger **/
	Logger	log	= LoggerFactory.getLogger(RouterModelFactory.class);

	public synchronized IResourceModel createResourceModelInstance(CapabilityDescriptor descriptor) throws ModelException {

		// if (descriptor.getPropertyValue("Transport Identifier") == null) {
		// throw new
		// ModelException("The Transport Identifier property is required to create an router model instance");
		// }

		log.info("Creating router model...");
		RouterModel router = new RouterModel();
		log.info("Router model created");
		// BeanValidator beanValidator = new BeanValidator();
		// Errors errors;
		// beanValidator.validate(router, errors);

		// router.setTransportIdentifier(descriptor.getPropertyValue("Transport Identifier"));

		// FIXME call the model validators before returning the instance? They
		// should apply even if the model is empty.?

		return router;
	}
}