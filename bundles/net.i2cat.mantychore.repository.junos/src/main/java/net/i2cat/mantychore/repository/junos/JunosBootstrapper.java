package net.i2cat.mantychore.repository.junos;

import com.iaasframework.capabilities.actionset.ActionSetCapabilityClient;
import com.iaasframework.resources.core.IResource;
import com.iaasframework.resources.core.IResourceBootstrapper;
import com.iaasframework.resources.core.ResourceException;

public class JunosBootstrapper implements IResourceBootstrapper {

	public void bootstrap(IResource resource) throws ResourceException {
		ActionSetCapabilityClient actionClient = new ActionSetCapabilityClient(
				resource.getResourceIdentifier().getId().toString());
		actionClient.executeAction("GetConfiguration", null);
	}

}
