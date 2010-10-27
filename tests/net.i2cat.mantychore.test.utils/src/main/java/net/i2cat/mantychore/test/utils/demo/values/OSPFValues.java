package cat.i2cat.manticore.test.demo.values;

import java.util.HashMap;
import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFAreaConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFRouterConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFSubInterfaceConfigurationType;
import cat.i2cat.manticore.stubs.router.RouterType;

/**
 * 
 * @author Xavi Barrera
 *
 */
public class OSPFValues {
	private HashMap <String, OSPFRouterConfigurationType> ospfConfigurations;
	private HashMap <String, OSPFSubInterfaceValues> ospfSubInterfaceValues;
	
	public OSPFValues(RouterType[] routers){
		ospfConfigurations = new HashMap <String, OSPFRouterConfigurationType>();
		for(RouterType router : routers){
			OSPFRouterConfigurationType ospfConfig = initializeOSPFConfiguration();
			ospfConfigurations.put(router.getRouterName(), ospfConfig);
		}
		ospfSubInterfaceValues = new HashMap <String, OSPFSubInterfaceValues>();
		for(RouterType router : routers){
			OSPFSubInterfaceValues ospfSubIntConfig = new OSPFSubInterfaceValues(router);
			ospfSubInterfaceValues.put(router.getRouterName(), ospfSubIntConfig);
		}
	}
	
	public boolean addArea(String routerName, OSPFAreaConfigurationType area){
		OSPFRouterConfigurationType ospfConfig = ospfConfigurations.get(routerName);
		if(area.getAreaType().compareTo(EngineConstants.OSPF_TYPE_NON_BACKBONE)!=0){
			OSPFAreaConfigurationType storedArea = getAreaByType(ospfConfig, area.getAreaType());
			if(storedArea!=null){
				return false;
			}
		}
		OSPFAreaConfigurationType storedArea = getAreaByID(ospfConfig, area.getAreaID());
		if(storedArea!=null){
			return false;
		}
		OSPFAreaConfigurationType[] oldAreas = ospfConfig.getListAreas();
		int lenghtAreas = 0;
		if(oldAreas!=null){
			lenghtAreas = oldAreas.length;
		}
		OSPFAreaConfigurationType[] newAreas = new OSPFAreaConfigurationType[lenghtAreas+1];
		for(int i=0; i<lenghtAreas; i++){
			newAreas[i] = oldAreas[i];
		}
		newAreas[lenghtAreas] = area;
		ospfConfig.setListAreas(newAreas);
		
		return true;
	}
	
	private OSPFAreaConfigurationType getAreaByType(OSPFRouterConfigurationType ospfConfig, String type){
		if(ospfConfig.getListAreas()!=null){
			for(OSPFAreaConfigurationType area : ospfConfig.getListAreas()){
				if(area.getAreaType().compareTo(type)==0){
					return area;
				}
			}
		}
		return null;
	}
	
	private OSPFAreaConfigurationType getAreaByID(OSPFRouterConfigurationType ospfConfig, String id){
		if(ospfConfig.getListAreas()!=null){
			for(OSPFAreaConfigurationType area : ospfConfig.getListAreas()){
				if(area.getAreaID().compareTo(id)==0){
					return area;
				}
			}
		}
		return null;
	}
	
	public OSPFAreaConfigurationType getArea(String routerName, String id){
		OSPFRouterConfigurationType ospfConfig = ospfConfigurations.get(routerName);
		return getAreaByID(ospfConfig, id);
	}

	public void removeArea(String id, String routerName) {
		OSPFRouterConfigurationType ospfConfig = ospfConfigurations.get(routerName);
		OSPFAreaConfigurationType area = getAreaByID(ospfConfig, id);
		if(area!=null){
			OSPFAreaConfigurationType[] oldAreas = ospfConfig.getListAreas();
			OSPFAreaConfigurationType[] newAreas = new OSPFAreaConfigurationType[oldAreas.length-1];
			int k=0;
			for(int i=0; i<oldAreas.length; i++){
				if(oldAreas[i].getAreaID().compareTo(area.getAreaID())!=0)
				newAreas[k] = oldAreas[i];
				k++;
			}
			ospfConfig.setListAreas(newAreas);
		}
		
	}

	public OSPFRouterConfigurationType getOSPFConfigurationByRouterName(String routerSelected) {
		return ospfConfigurations.get(routerSelected);
	}

	public void addOSPFConfiguration(String routerSelected,OSPFRouterConfigurationType ospfConfig) {
		ospfConfigurations.put(routerSelected, ospfConfig);	
	}
	
	public OSPFSubInterfaceConfigurationType getOSPFSubInterfaceConfiguration(String routerName, String location, String subInterfaceID){
		OSPFSubInterfaceValues subIntValues = ospfSubInterfaceValues.get(routerName);
		OSPFSubInterfaceConfigurationType ospfSubIntConfig = subIntValues.getOSPFSubInterfaceConfiguration(location, subInterfaceID);
		return ospfSubIntConfig;
	}
	
	public boolean checkOSPFSubInterfaceConfigurations(String routerName, String location, String subInterfaceID){
		OSPFSubInterfaceValues subIntValues = ospfSubInterfaceValues.get(routerName);
		if(subIntValues==null){
			return false;
		}
		return subIntValues.contains(location, subInterfaceID);
	}

	public HashMap<String, OSPFSubInterfaceConfigurationType> getAllOSPFSubInterfaceConfiguration(RouterType router) {
		OSPFSubInterfaceValues subIntValues = ospfSubInterfaceValues.get(router.getRouterName());
		if(subIntValues==null){
			subIntValues = new OSPFSubInterfaceValues(null);
			ospfSubInterfaceValues.put(router.getRouterName(), subIntValues);
		}
		return subIntValues.getOspfSubInConfigurations();
	}
	
	public OSPFRouterConfigurationType initializeOSPFConfiguration(){
		OSPFRouterConfigurationType ospfConfig = new OSPFRouterConfigurationType();
		ospfConfig.setRedistributeProtocol(new String[]{EngineConstants.OSPF_DIRECT});
		return ospfConfig;
	}
	
	public OSPFAreaConfigurationType initializeOSPFAreaConfiguration(){
		OSPFAreaConfigurationType areaConfig = new OSPFAreaConfigurationType();
		areaConfig.setAreaID("0.0.0.0");
		areaConfig.setAreaType(EngineConstants.OSPF_TYPE_BACKBONE);
		areaConfig.setDeadInterval(40);
		areaConfig.setHelloInterval(120);
		areaConfig.setLSA_RestransmitInterval(3);
		areaConfig.setTransitDelayInterval(1);
		areaConfig.setNSSADefaultRouteMetric(0);
		areaConfig.setNSSASummaries(false);
		areaConfig.setStubDefaultRouteMetric(0);
		areaConfig.setStubSummaries(false);

		return areaConfig;
	}

}
