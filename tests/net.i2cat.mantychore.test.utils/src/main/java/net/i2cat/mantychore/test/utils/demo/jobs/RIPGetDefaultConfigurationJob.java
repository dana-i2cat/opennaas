package cat.i2cat.manticore.test.demo.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.common.utils.UtilFunctions;
import cat.i2cat.manticore.stubs.ipnetwork.GetDefaultNetworksReq;
import cat.i2cat.manticore.stubs.ipnetwork.GetDefaultNetworksResp;
import cat.i2cat.manticore.stubs.ipnetwork.GetDefaultNetworksRespSelectedRouters;
import cat.i2cat.manticore.stubs.router.RouterType;
import cat.i2cat.manticore.test.wrappers.ipnetwork.IPNetworkWrapper;

/**
 * 
 * @author Xavi Barrera
 *
 */
public class RIPGetDefaultConfigurationJob{
	private RouterType[] routersSelected;
	private HashMap<String, String[]> getDefaultNetworks;
	private EndpointReferenceType ipnetworkEPR; 
	
	public RIPGetDefaultConfigurationJob(RouterType[] routersSelected, EndpointReferenceType ipnetworkEPR) {
		this.routersSelected = routersSelected;
		this.getDefaultNetworks = new HashMap<String, String[]>();
		this.ipnetworkEPR = ipnetworkEPR;
	}

	public HashMap<String, String[]>  run() {
		try{
			GetDefaultNetworksReq request = prepareRequest();
			GetDefaultNetworksResp response = IPNetworkWrapper.getDefaultNetworks(request , ipnetworkEPR);
			extractGetDefaultNetworks(response);
			return getDefaultNetworks;
		} catch (Exception ex) {
			return new HashMap<String, String[]>();
		}
	}

	private GetDefaultNetworksReq prepareRequest() {
		GetDefaultNetworksReq request = new GetDefaultNetworksReq();
		EndpointReferenceType[] selectedRouters = new EndpointReferenceType[routersSelected.length];
		for(int i=0; i<routersSelected.length; i++){
			selectedRouters[i] = routersSelected[i].getRouterEPR();
		}
		request.setSelectedRouters(selectedRouters);
		request.setProtocolType(EngineConstants.IPV4);
		return request;
	}

	private void extractGetDefaultNetworks(GetDefaultNetworksResp response){
		GetDefaultNetworksRespSelectedRouters[]  defaultNetworks = response.getSelectedRouters();
		for(int i=0; i<defaultNetworks.length; i++){
			String[] dfn = defaultNetworks[i].getNetworkAddresses();
			EndpointReferenceType epr = defaultNetworks[i].getRouterId();
			RouterType router =  getRouter(epr);
			if(router!=null){
				getDefaultNetworks.put(router.getRouterName(), removePossibleNulls(dfn));
			}
		}
	}
	
	private String[] removePossibleNulls(String[] announcedNetworks){
		List<String> resultList = new ArrayList<String>();
		for(int i=0; i<announcedNetworks.length; i++){
			if(announcedNetworks[i]!=null){
				resultList.add(announcedNetworks[i]);
			}
		}
		String[] result = new String[resultList.size()];
		for(int i=0; i<resultList.size(); i++){
			result[i]=resultList.get(i);
		}
		return result;
	}
	
	private RouterType getRouter(EndpointReferenceType epr){
		for(RouterType router : routersSelected){
			if(UtilFunctions.compare2EndpointReferenceType(router.getRouterEPR(), epr)){
				return router;
			}
		}
		return null;
	}
}
