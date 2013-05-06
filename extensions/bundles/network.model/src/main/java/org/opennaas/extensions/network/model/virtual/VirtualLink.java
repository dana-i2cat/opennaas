package org.opennaas.extensions.network.model.virtual;

import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.Path;

public class VirtualLink extends Link {

	private Path	implementedBy;

	public Path getImplementedBy() {
		return implementedBy;
	}

	public void setImplementedBy(Path implementedBy) {
		this.implementedBy = implementedBy;
	}

}
