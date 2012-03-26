package org.opennaas.extensions.router.capability.ospf.shell.completers;

import java.util.List;

import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;

public class ResourceNameCompleter implements Completer {

	@Override
	public int complete(String arg0, int arg1, List<String> arg2) {

		IResourceManager resourceManager = null;
		List<IResource> list = null;
		StringsCompleter delegate = new StringsCompleter();

		try {
			resourceManager = Activator.getResourceManagerService();
			list = resourceManager.listResources();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (IResource resource : list) {
			String value = resource.getResourceDescriptor().getInformation().getType() + ":" + resource.getResourceDescriptor().getInformation()
					.getName();
			delegate.getStrings().add(value);
		}

		return delegate.complete(arg0, arg1, arg2);
	}
}
