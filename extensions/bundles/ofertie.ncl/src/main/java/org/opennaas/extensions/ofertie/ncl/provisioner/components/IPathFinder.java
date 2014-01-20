package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;

public interface IPathFinder {

	public Route findPathForRequest(QosPolicyRequest qosPolicyRequest) throws Exception;

}
