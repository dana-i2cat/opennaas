package net.i2cat.mantychore.capability.chassis.shell.completers;

import java.util.List;

import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;

public class ResourceNameCompleter implements Completer {

	Log	log	= LogFactory.getLog(ResourceNameCompleter.class);

	@Override
	public int complete(String arg0, int arg1, List<String> arg2) {

		IResourceManager resourceManager = null;
		List<IResource> list = null;
		StringsCompleter delegate = new StringsCompleter();

		try {
			resourceManager = Activator.getResourceManagerService();
			list = resourceManager.listResources();

			for (IResource resource : list) {
				String value = resource.getResourceDescriptor().getInformation().getType() + ":" + resource.getResourceDescriptor().getInformation()
						.getName();
				delegate.getStrings().add(value);
			}
		} catch (Exception e) {
			// log exception and ignore it (completer would have no options for completion))
			log.error(e);
		}

		return delegate.complete(arg0, arg1, arg2);
	}
}
