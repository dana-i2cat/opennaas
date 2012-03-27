package org.opennaas.extensions.network.model.topology;

import org.opennaas.extensions.network.model.layer.Layer;

public class TransportNetworkElement extends NetworkElement {

	Layer layer;

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}
}
