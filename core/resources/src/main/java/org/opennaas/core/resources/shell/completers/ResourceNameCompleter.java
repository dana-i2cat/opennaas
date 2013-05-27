package org.opennaas.core.resources.shell.completers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;

public class ResourceNameCompleter implements Completer {

	private Log log = LogFactory.getLog(ResourceNameCompleter.class);

	@Override
	public int complete(String buffer, int cursor, List<String> candidates) {
		
		StringsCompleter delegate = new StringsCompleter();

		try {
			List<IResource> list = Activator.getResourceManagerService().listResources();

			for (IResource resource : list) {
				String value = resource.getResourceDescriptor().getInformation().getType()
						+ ":" + resource.getResourceDescriptor().getInformation().getName();
				delegate.getStrings().add(value);
			}
		} catch (Exception e) {
			// log exception and ignore it 
			// (completer would have no options for completion))
			log.error(e);
		}

		return delegate.complete(buffer, cursor, candidates);
	}
}
