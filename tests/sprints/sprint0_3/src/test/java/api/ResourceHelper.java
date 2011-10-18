package api;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.ICapability;

public class ResourceHelper {

	public static int containsCapability(IResource resource, String idCapability) {

		int pos = 0;
		for (ICapability capability : resource.getCapabilities()) {
			if (capability.getCapabilityInformation().getType().equals(idCapability)) {
				return pos;
			}
			pos++;
		}
		return -1;

	}
}
