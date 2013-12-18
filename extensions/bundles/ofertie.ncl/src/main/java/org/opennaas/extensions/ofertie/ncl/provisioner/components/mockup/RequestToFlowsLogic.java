package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup;

import java.util.List;

import org.opennaas.extensions.ofertie.ncl.helpers.FlowRequestParser;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IPathFinder;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IRequestToFlowsLogic;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

public class RequestToFlowsLogic implements IRequestToFlowsLogic {

	public static final String	DEFAULT_FLOW_PRIORITY	= "32000";

	private NCLModel			nclModel;

	private IPathFinder			pathFinder;

	public NCLModel getNclModel() {
		return nclModel;
	}

	public void setNclModel(NCLModel nclModel) {
		this.nclModel = nclModel;
	}

	/**
	 * @return the pathFinder
	 */
	public IPathFinder getPathFinder() {
		return pathFinder;
	}

	/**
	 * @param pathFinder
	 *            the pathFinder to set
	 */
	public void setPathFinder(IPathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}

	@Override
	public List<NetOFFlow> getRequiredFlowsToSatisfyRequest(FlowRequest flowRequest) throws Exception {

		Route route = getPathFinder().findPathForRequest(flowRequest);
		List<NetOFFlow> flows = FlowRequestParser.parseFlowRequestIntoSDNFlow(flowRequest, route);

		return flows;
	}

}
