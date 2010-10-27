package cat.i2cat.manticore.test.demo.jobs;

import java.util.Vector;

import org.apache.axis.message.addressing.EndpointReferenceType;

import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.common.constants.IPNetworkConstants;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionTypeAffectedRouters;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionTypeParameters;
import cat.i2cat.manticore.stubs.ipnetwork.QueueActionReq;
import cat.i2cat.manticore.stubs.ipnetwork.QueueActionResp;
import cat.i2cat.manticore.test.demo.values.StaticRoute;
import cat.i2cat.manticore.test.wrappers.ipnetwork.IPNetworkWrapper;

public class AddStaticRoutesJob{
	private Vector<StaticRoute> staticRoutesToAdd;
	protected EndpointReferenceType routerEPR;
	protected EndpointReferenceType ipnetworkEPR;
	
	public AddStaticRoutesJob(EndpointReferenceType ipnetworkEPR, EndpointReferenceType routerEPR, Vector<StaticRoute> staticRoutesToAdd) {
		this.routerEPR = routerEPR;
		this.ipnetworkEPR = ipnetworkEPR;
		this.staticRoutesToAdd = staticRoutesToAdd;
	}

	public IPNetworkActionType[] run() {
		try{
			QueueActionReq request = prepareRequest();
			QueueActionResp response = IPNetworkWrapper.queueAction(request, ipnetworkEPR);
			return response.getCurrentActionQueue();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private QueueActionReq prepareRequest() throws Exception{
		QueueActionReq request = new QueueActionReq();
		IPNetworkActionType[] actionList = new IPNetworkActionType[staticRoutesToAdd.size()];
		for(int x = 0; x<staticRoutesToAdd.size(); x++){
			IPNetworkActionType action = new IPNetworkActionType();
			actionList[x] = action;
			action.setActionID(IPNetworkConstants.INVOKE_ROUTER_ACTION);
			IPNetworkActionTypeAffectedRouters[] affectedRouters = new IPNetworkActionTypeAffectedRouters[1];
			affectedRouters[0] = new IPNetworkActionTypeAffectedRouters();
			affectedRouters[0].setRole(IPNetworkConstants.ROLE_TO);
			affectedRouters[0].setRouterId(routerEPR);
			action.setAffectedRouters(affectedRouters );
			IPNetworkActionTypeParameters[] parameters = null;
			if(staticRoutesToAdd.get(x).isIpv6()){
				parameters = new IPNetworkActionTypeParameters[4];
			}else{
				parameters = new IPNetworkActionTypeParameters[3];
			}
			parameters[0] = new IPNetworkActionTypeParameters();
			parameters[0].setName(IPNetworkConstants.ROUTER_ACTION_ID);
			parameters[0].setValue(EngineConstants.ADD_STATIC_ROUTE_ACTION);
			parameters[1] = new IPNetworkActionTypeParameters();
			parameters[1].setName(EngineConstants.ADDRESS_IPNETWORK);
			parameters[1].setValue(staticRoutesToAdd.get(x).getDestinationIP());
			parameters[2] = new IPNetworkActionTypeParameters();
			parameters[2].setName(EngineConstants.ADDRESS_NEXT_HOP);
			parameters[2].setValue(staticRoutesToAdd.get(x).getNextHopIP());
			if(staticRoutesToAdd.get(x).isIpv6()){
				parameters[3] = new IPNetworkActionTypeParameters();
				parameters[3].setName(EngineConstants.IPVERSION);
				parameters[3].setValue(EngineConstants.IPV6);
			}
			action.setParameters(parameters);
		}
		request.setNewAction(actionList);
		return request;
	}
}
