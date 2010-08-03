package net.i2cat.mantychore.models.router.internal;

import javax.jws.WebMethod;
import javax.jws.WebService;

import net.i2cat.mantychore.models.router.RouterModel;
import net.i2cat.mantychore.models.router.RouterModelService;

/**
 * Internal implementation of our example OSGi service
 */
@WebService
public class RouterModelServiceImpl implements RouterModelService {

	private RouterModel	model	= null;

	public RouterModelServiceImpl() {
		model = new RouterModel();
	}

	@WebMethod
	public RouterModel getModel() {
		return model;

	}
}