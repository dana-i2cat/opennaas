package cat.i2cat.manticore.test.demo.jobs;


import org.apache.axis.message.addressing.EndpointReferenceType;

import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.common.constants.IPNetworkConstants;
import cat.i2cat.manticore.stubs.ipnetwork.BGPConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.EBGPConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.IBGPConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionTypeAffectedRouters;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionTypeParameters;
import cat.i2cat.manticore.stubs.ipnetwork.QueueActionReq;
import cat.i2cat.manticore.stubs.ipnetwork.QueueActionResp;
import cat.i2cat.manticore.stubs.router.RouterType;
import cat.i2cat.manticore.test.demo.values.BGPValues;
import cat.i2cat.manticore.test.wrappers.ipnetwork.IPNetworkWrapper;

public class BGPActivationJob {
	private RouterType routerSelected; 
	private BGPValues values;
	private EndpointReferenceType ipnetworkEPR;
	
	public BGPActivationJob(RouterType routerSelected, EndpointReferenceType ipnetworkEPR, BGPValues values) {
		this.routerSelected = routerSelected;
		this.values = values;
		this.ipnetworkEPR = ipnetworkEPR;
	}

	public IPNetworkActionType[] run() {
		try{
			QueueActionReq request = prepareRequest();
			QueueActionResp response = IPNetworkWrapper.queueAction(request, ipnetworkEPR);
			return response.getCurrentActionQueue();
		} catch (Exception ex) {
			ex.printStackTrace();
			return new IPNetworkActionType[0];
		} 
	}
	
	private QueueActionReq prepareRequest() throws Exception{
		QueueActionReq request = new QueueActionReq();
		IPNetworkActionType[] actionList = new IPNetworkActionType[1];
		IPNetworkActionType action = new IPNetworkActionType();
		actionList[0] = action;
		action.setActionID(IPNetworkConstants.INVOKE_ROUTER_ACTION);
		IPNetworkActionTypeAffectedRouters[] affectedRouters = new IPNetworkActionTypeAffectedRouters[1];

		affectedRouters[0] = new IPNetworkActionTypeAffectedRouters();
		affectedRouters[0].setRole(IPNetworkConstants.ROLE_TO);
		affectedRouters[0].setRouterId(routerSelected.getRouterEPR());
		BGPConfigurationType bgpConfig = values.getDesiredBGPConfiguration();
		if(bgpConfig instanceof EBGPConfigurationType){
			EBGPConfigurationType ebgpConfigurationType = (EBGPConfigurationType)bgpConfig;
			affectedRouters[0].setEbgpConfiguration(ebgpConfigurationType);
		}else{
			IBGPConfigurationType ibgpConfigurationType = (IBGPConfigurationType)bgpConfig;
			affectedRouters[0].setIbgpConfiguration(ibgpConfigurationType);
		}
		action.setAffectedRouters(affectedRouters);
		
		IPNetworkActionTypeParameters[] parameters = new IPNetworkActionTypeParameters[1];
		parameters[0] = new IPNetworkActionTypeParameters();
		parameters[0].setName(IPNetworkConstants.ROUTER_ACTION_ID);
		parameters[0].setValue(EngineConstants.CONFIGURE_BGP_ROUTE_ACTION);
		action.setParameters(parameters);
		
		request.setNewAction(actionList);
		return request;
	}
}












