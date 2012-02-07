package net.i2cat.mantychore.network.model.topology;

import net.i2cat.mantychore.network.model.layer.Layer;

public class TransportNetworkElement extends NetworkElement {
	
	Layer layer;

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}
}
