package cat.i2cat.manticore.test.demo.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;

import cat.i2cat.manticore.common.constants.IPNetworkConstants;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionType;
import cat.i2cat.manticore.stubs.ipnetwork.IPNetworkActionTypeAffectedRouters;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFRouterConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFSubInterfaceConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.PhysicalInterfaceType;
import cat.i2cat.manticore.stubs.ipnetwork.QueueActionReq;
import cat.i2cat.manticore.stubs.ipnetwork.QueueActionResp;
import cat.i2cat.manticore.stubs.ipnetwork.SubInterfaceType;
import cat.i2cat.manticore.stubs.router.RouterType;
import cat.i2cat.manticore.test.demo.values.OSPFValues;
import cat.i2cat.manticore.test.wrappers.ipnetwork.IPNetworkWrapper;


public class OSPFActivationJob{
	private OSPFValues values;
	private RouterType[] routersSelected;
	private EndpointReferenceType ipnetworkEPR;

	public OSPFActivationJob(RouterType[] routersSelected, EndpointReferenceType ipnetworkEPR, OSPFValues values) {
		this.values = values;
		this.routersSelected = routersSelected;
		this.ipnetworkEPR = ipnetworkEPR;
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
		IPNetworkActionType[] actionList = new IPNetworkActionType[1];
		IPNetworkActionType action = new IPNetworkActionType();
		actionList[0] = action;
		action.setActionID(IPNetworkConstants.INVOKE_CONFIGURE_OSPF);
		IPNetworkActionTypeAffectedRouters[] affectedRouters = new IPNetworkActionTypeAffectedRouters[routersSelected.length];
		for(int x = 0; x<routersSelected.length; x++){
			affectedRouters[x] = new IPNetworkActionTypeAffectedRouters();
			affectedRouters[x].setRole(IPNetworkConstants.INVOKE_CONFIGURE_RIP_OSPF_ROLE);
			affectedRouters[x].setRouterId(routersSelected[x].getRouterEPR());
			OSPFRouterConfigurationType  ospfConfiguration = values.getOSPFConfigurationByRouterName(routersSelected[x].getRouterName());
			affectedRouters[x].setOspfConfiguration(ospfConfiguration);
			
			List<PhysicalInterfaceType> affectedInterfaces = new ArrayList<PhysicalInterfaceType>();
			for(cat.i2cat.manticore.stubs.router.PhysicalInterfaceType phy : routersSelected[x].getPhysicalInterfaces()){
				PhysicalInterfaceType phyaffected = null;
				List<SubInterfaceType> affectedSubInterfaces = new ArrayList<SubInterfaceType>();
				for(cat.i2cat.manticore.stubs.router.SubInterfaceType subInt : phy.getSubInterfaces()){
					if(values.checkOSPFSubInterfaceConfigurations(routersSelected[x].getRouterName(), phy.getLocation(), subInt.getIdentifier())){
						SubInterfaceType subIntType = new SubInterfaceType();
						OSPFSubInterfaceConfigurationType  ospfSubIntConfig = values.getOSPFSubInterfaceConfiguration(routersSelected[x].getRouterName(), phy.getLocation(), subInt.getIdentifier());
						subIntType.setOSPFSubInterfaceConfiguration(ospfSubIntConfig);
						subIntType.setIdentifier(subInt.getIdentifier());
						subIntType.setVlanID(subInt.getVlanID());
						subIntType.setPeerUnit(subInt.getPeerUnit());
						affectedSubInterfaces.add(subIntType);
					}
				}
				if(affectedSubInterfaces.size()>0){
					phyaffected = new PhysicalInterfaceType();
					phyaffected.setLocation(phy.getLocation());
					SubInterfaceType[] subInterfaces = new SubInterfaceType[affectedSubInterfaces.size()];
					for(int i=0; i<affectedSubInterfaces.size(); i++){
						subInterfaces[i] = affectedSubInterfaces.get(i);
					}
					phyaffected.setSubInterfaces(subInterfaces);
					affectedInterfaces.add(phyaffected);
				}
			}
			if(affectedInterfaces.size()>0){
				PhysicalInterfaceType[] affectedInterfacesArray = new PhysicalInterfaceType[affectedInterfaces.size()];
				for(int i=0; i<affectedInterfaces.size(); i++){
					affectedInterfacesArray[i] =  affectedInterfaces.get(i);
				}
				affectedRouters[x].setAffectedInterfaces(affectedInterfacesArray);
			}
		}
		action.setAffectedRouters(affectedRouters);
		request.setNewAction(actionList);
		return request;
	}
}
