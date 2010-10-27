package cat.i2cat.manticore.test.demo.values;

import java.util.HashMap;

import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.stubs.ipnetwork.RIPSubInterfaceConfigurationType;

/**
 * 
 * @author Xavi Barrera
 *
 */
public class RIPSubInterfaceValues {
	private HashMap<String, RIPSubInterfaceConfigurationType> ripSubInterfaceConfigurations;

	public RIPSubInterfaceValues() {
		ripSubInterfaceConfigurations = new HashMap<String, RIPSubInterfaceConfigurationType>();
	}

	public HashMap<String, RIPSubInterfaceConfigurationType> getRipSubInterfaceConfigurations() {
		return ripSubInterfaceConfigurations;
	}

	public void setRipSubInterfaceConfigurations(HashMap<String, RIPSubInterfaceConfigurationType> ripSubInterfaceConfigurations) {
		this.ripSubInterfaceConfigurations = ripSubInterfaceConfigurations;
	}

	public RIPSubInterfaceConfigurationType getRIPSubInterfaceConfiguration(String location, String subInterfaceID) {
		String key = location+"/"+subInterfaceID;
		RIPSubInterfaceConfigurationType ripSubIntConfig = ripSubInterfaceConfigurations.get(key);
		if(ripSubIntConfig == null){
			ripSubIntConfig = initializeRIPSubInterfaceConfiguration();
			ripSubInterfaceConfigurations.put(key, ripSubIntConfig);
		}
		return ripSubIntConfig;
	}
	
	public boolean contains(String location, String subinterfaceID){
		String key = location+"/"+subinterfaceID;
		RIPSubInterfaceConfigurationType ripSubIntConfig = ripSubInterfaceConfigurations.get(key);
		if(ripSubIntConfig == null){
			return false;
		}
		return true;
	}
	
	public RIPSubInterfaceConfigurationType initializeRIPSubInterfaceConfiguration(){
		RIPSubInterfaceConfigurationType config = new RIPSubInterfaceConfigurationType();
		config.setReceive(EngineConstants.RIP_BOTH);
		config.setSend(EngineConstants.RIP_MULTICAST);
		return config;
	}
}
