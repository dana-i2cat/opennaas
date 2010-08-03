package net.i2cat.mantychore.models.router;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Provides information about the model.
 */

@WebService
public interface RouterModelService {

	@WebMethod
	public RouterModel getModel();
}
