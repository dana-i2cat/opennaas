package cat.i2cat.manticore.test.demo.values;

import java.util.HashMap;

import cat.i2cat.manticore.stubs.ipnetwork.RIPRouterConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.RIPSubInterfaceConfigurationType;

/**
 * 
 * @author Xavi Barrera
 *
 */
public class RIPValues {
	private HashMap<String, RIPRouterConfigurationType> ripConfigurations;
	private HashMap<String, RIPSubInterfaceValues> ripSubInterfacesValues;
	
	public RIPValues() {
		ripConfigurations = new HashMap<String, RIPRouterConfigurationType>();
		ripSubInterfacesValues = new HashMap<String, RIPSubInterfaceValues>();
	}

	public HashMap<String, RIPRouterConfigurationType> getRipConfigurations() {
		return ripConfigurations;
	}

	public void setRipConfigurations(
			HashMap<String, RIPRouterConfigurationType> ripConfigurations) {
		this.ripConfigurations = ripConfigurations;
	}
	
	public RIPRouterConfigurationType getRIPConfigurationByRouterName(String routerName){
		RIPRouterConfigurationType ripConfig = ripConfigurations.get(routerName);
		if(ripConfig == null){
			ripConfig = initializeRIPConfiguration();
			ripConfigurations.put(routerName, ripConfig);
		}
		
		return ripConfig;
	}

	public HashMap<String, RIPSubInterfaceValues> getRipSubInterfacesValues() {
		return ripSubInterfacesValues;
	}

	public void setRipSubInterfacesValues(
			HashMap<String, RIPSubInterfaceValues> ripSubInterfacesValues) {
		this.ripSubInterfacesValues = ripSubInterfacesValues;
	}
	
	public RIPSubInterfaceConfigurationType getRIPSubInterfaceConfiguration(String routerName, String location, String subInterfaceID){
		RIPSubInterfaceValues subInterfaceValues = ripSubInterfacesValues.get(routerName);
		if(subInterfaceValues==null){
			subInterfaceValues = new RIPSubInterfaceValues();
			ripSubInterfacesValues.put(routerName, subInterfaceValues);
		}
		return subInterfaceValues.getRIPSubInterfaceConfiguration(location, subInterfaceID);
	}
	
	public boolean checkRIPSubInterfaceConfiguration(String routerName, String location, String subInterfaceID){
		RIPSubInterfaceValues subInterfaceValues = ripSubInterfacesValues.get(routerName);
		if(subInterfaceValues==null){
			return false;
		}
		return subInterfaceValues.contains(location, subInterfaceID);
	}

	public HashMap<String, RIPSubInterfaceConfigurationType> getAllRIPSubInterfaceConfiguration(String routerName) {
		RIPSubInterfaceValues subInterfaceValues = ripSubInterfacesValues.get(routerName);
		if(subInterfaceValues==null){
			subInterfaceValues = new RIPSubInterfaceValues();
			ripSubInterfacesValues.put(routerName, subInterfaceValues);
		}
		return subInterfaceValues.getRipSubInterfaceConfigurations();
	}
	
	public RIPRouterConfigurationType initializeRIPConfiguration(){
		RIPRouterConfigurationType config = new RIPRouterConfigurationType();
		config.setTimeout(180);
		config.setUpdateTimer(30);
		config.setGarbageCollTimer(180);
		config.setSplitHorizon(true);
		config.setMetric(1);
		config.setPoisonedReversed(false);
		
		return config;
	}
	

	
	
}
