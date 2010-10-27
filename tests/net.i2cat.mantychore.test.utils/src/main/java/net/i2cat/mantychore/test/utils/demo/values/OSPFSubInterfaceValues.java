package cat.i2cat.manticore.test.demo.values;

import java.util.HashMap;

import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.stubs.ipnetwork.OSPFSubInterfaceConfigurationType;
import cat.i2cat.manticore.stubs.router.PhysicalInterfaceType;
import cat.i2cat.manticore.stubs.router.SubInterfaceType;
import cat.i2cat.manticore.stubs.router.RouterType;

/**
 * 
 * @author Xavi Barrera
 *
 */
public class OSPFSubInterfaceValues {
	private HashMap<String, OSPFSubInterfaceConfigurationType> ospfSubInConfigurations;
	
	public OSPFSubInterfaceValues(RouterType router){
		ospfSubInConfigurations = new HashMap<String, OSPFSubInterfaceConfigurationType>();
//		for(PhysicalInterfaceType phy : router.getPhysicalInterfaces()){
//			if(phy.getType().compareTo(EngineConstants.ETHERNET_TYPE)==0 || 
//					phy.getType().compareTo(EngineConstants.LOGICAL_TUNNEL_TYPE)==0){
//				for(SubInterfaceType subInt : phy.getSubInterfaces()){
//					OSPFSubInterfaceConfigurationType ospfSubIntConfig = initializeOSPFSubInterfaceConfiguration();
//					ospfSubInConfigurations.put(phy.getLocation()+"."+subInt.getIdentifier(), ospfSubIntConfig);
//				}
//			}
//		}
	}

	public HashMap<String, OSPFSubInterfaceConfigurationType> getOspfSubInConfigurations() {
		return ospfSubInConfigurations;
	}

	public void setOspfSubInConfigurations(
			HashMap<String, OSPFSubInterfaceConfigurationType> ospfSubInConfigurations) {
		this.ospfSubInConfigurations = ospfSubInConfigurations;
	}
	
	public OSPFSubInterfaceConfigurationType getOSPFSubInterfaceConfiguration(String location, String subInterfaceID){
		OSPFSubInterfaceConfigurationType result = ospfSubInConfigurations.get(location+"."+subInterfaceID);
		if(result == null){
			result = initializeOSPFSubInterfaceConfiguration();
			ospfSubInConfigurations.put(location+"."+subInterfaceID, result);
		}
		return result;
	}

	public boolean contains(String location, String subInterfaceID) {
		String key = location+"."+subInterfaceID;
		OSPFSubInterfaceConfigurationType result = ospfSubInConfigurations.get(key);
		if(result == null){
			return false;
		}
		return true;
	}
	public OSPFSubInterfaceConfigurationType initializeOSPFSubInterfaceConfiguration(){
		OSPFSubInterfaceConfigurationType ospfSubInt = new OSPFSubInterfaceConfigurationType();
		ospfSubInt.setCost(1);
		ospfSubInt.setPriority(128);
		
		return ospfSubInt;
	}
}
