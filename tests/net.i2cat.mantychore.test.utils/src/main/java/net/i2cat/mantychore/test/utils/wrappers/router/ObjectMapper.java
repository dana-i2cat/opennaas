package cat.i2cat.manticore.test.wrappers.router;

import java.util.ArrayList;
import java.util.List;

import cat.i2cat.manticore.stubs.router.AccessConfigurationType;
import cat.i2cat.manticore.stubs.router.AdvancedBGPPolicyType;
import cat.i2cat.manticore.stubs.router.BGPPolicyType;
import cat.i2cat.manticore.stubs.router.IPConfigurationType;
import cat.i2cat.manticore.stubs.router.LocationType;
import cat.i2cat.manticore.stubs.router.PhysicalInterfaceType;
import cat.i2cat.manticore.stubs.router.RouterType;
import cat.i2cat.manticore.stubs.router.SimpleBGPPolicyType;
import cat.i2cat.manticore.stubs.router.SubInterfaceType;
import cat.i2cat.manticore.stubs.router.UserAccountType;
//import cat.i2cat.manticore.ui.rcp.model.router.AccessConfiguration;
//import cat.i2cat.manticore.ui.rcp.model.router.IPConfiguration;
//import cat.i2cat.manticore.ui.rcp.model.router.Location;
//import cat.i2cat.manticore.ui.rcp.model.router.PhysicalInterface;
//import cat.i2cat.manticore.ui.rcp.model.router.RouterInstance;
//import cat.i2cat.manticore.ui.rcp.model.router.SubInterface;
//import cat.i2cat.manticore.ui.rcp.model.router.UserAccount;

/**
 * This class converts stubs objects to model objects for the router.
 * 
 * @author Laia Ferrao
 *
 */
public class ObjectMapper {
//	/**
//	 * Converts a PhysicalInterfaceType (from the stubs) to a PhysicalInterface (router model)
//	 * 
//	 * @param pint
//	 * @return
//	 */
//	public static PhysicalInterface physicalInterfaceType2PhysicalInterface(PhysicalInterfaceType physicalInterfaceType) {
//		PhysicalInterface physicalInterface = new PhysicalInterface();
//		physicalInterface.setLocation(physicalInterfaceType.getLocation());
//		physicalInterface.setStatus(physicalInterfaceType.getStatus());
//		physicalInterface.setLinkStatus(physicalInterfaceType.getLinkStatus());
//		physicalInterface.setKeepalive(Integer.toString(physicalInterfaceType.getKeepalive()));
//		physicalInterface.setMacAddress(physicalInterfaceType.getMacAddress());
//		physicalInterface.setType(physicalInterfaceType.getType());
//		physicalInterface.setLinkMode(physicalInterfaceType.getLinkMode());
//		
//		if (physicalInterfaceType.getSubInterfaces() != null) {
//			List<SubInterface> subs = new ArrayList<SubInterface>();
//			for (int i = 0; i < physicalInterfaceType.getSubInterfaces().length; i++) {
//				SubInterface sub = subInterfaceType2SubInterface(physicalInterfaceType.getSubInterfaces(i));
//				subs.add(sub);
//				sub.setPhysicalInterface(physicalInterface);
//			}
//			physicalInterface.setSubInterfaces(subs);
//		}
//		return physicalInterface;
//	}
//	
//	/**
//	 * Converts a PhysicalInterface (from the model) to a PhysicalInterfaceType (from the stubs)
//	 * 
//	 * @param pint
//	 * @return
//	 */
//	public static PhysicalInterfaceType physicalInterface2PhysicalInterfaceType(PhysicalInterface physicalInterface) {
//		try {
//			PhysicalInterfaceType physicalInterfaceType = new PhysicalInterfaceType();
//			physicalInterfaceType.setLocation(physicalInterface.getLocation());
//			physicalInterfaceType.setStatus(physicalInterface.getStatus());
//			physicalInterfaceType.setLinkStatus(physicalInterface.getLinkStatus());
//			physicalInterfaceType.setKeepalive(Integer.parseInt(physicalInterface.getKeepalive()));
//			physicalInterfaceType.setMacAddress(physicalInterface.getMacAddress());
//			physicalInterfaceType.setType(physicalInterface.getType());
//			physicalInterfaceType.setLinkMode(physicalInterface.getLinkMode());
//			
//			if (physicalInterface.getSubInterfaces() != null) {
//				int size = physicalInterface.getSubInterfaces().size();
//				SubInterfaceType[] subs = new SubInterfaceType[size];
//				for (int i = 0; i < size; i++) {
//					SubInterfaceType sub = subInterface2SubInterfaceType(physicalInterface.getSubInterfaces().get(i));
//					subs[i] = sub;
//					sub.setPhysicalInterfaceID(physicalInterface.getLocation());
//				}
//				physicalInterfaceType.setSubInterfaces(subs);
//			}
//			
//			return physicalInterfaceType;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * Converts a PhysicalInterface (from the model) to a PhysicalInterfaceType (from the stubs) and adds the requested subInterface
//	 * 
//	 * @param pint
//	 * @param subt
//	 * @return
//	 */
//	public static PhysicalInterfaceType physicalInterface2PhysicalInterfaceTypeAddSubInterface(PhysicalInterface physicalInterface, SubInterfaceType subInterfaceType) {
//		try {
//			PhysicalInterfaceType physicalInterfaceType = new PhysicalInterfaceType();
//			physicalInterfaceType.setLocation(physicalInterface.getLocation());
//			physicalInterfaceType.setStatus(physicalInterface.getStatus());
//			physicalInterfaceType.setLinkStatus(physicalInterface.getLinkStatus());
//			physicalInterfaceType.setKeepalive(Integer.parseInt(physicalInterface.getKeepalive()));
//			physicalInterfaceType.setMacAddress(physicalInterface.getMacAddress());
//			physicalInterfaceType.setType(physicalInterface.getType());
//			physicalInterfaceType.setLinkMode(physicalInterface.getLinkMode());
//			
//			//Add the subInterface
//			SubInterfaceType[] subs = new SubInterfaceType[1];
//			subs [0] = subInterfaceType;
//			physicalInterfaceType.setSubInterfaces(subs);
//			
//			return physicalInterfaceType;		
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * Converts a LocationType (from the stubs) to a Location (router model)
//	 * 
//	 * @param locationType
//	 * @return
//	 */
//	public static Location locationType2Location(LocationType locationType) {
//		Location location = new Location();
//		location.setAddress(locationType.getAddress());
//		location.setCity(locationType.getCity());
//		location.setCountry(locationType.getCountry());
//		location.setTelephone(locationType.getTelephone());
//		location.setEmail(locationType.getEMail());
//		location.setLatitude(locationType.getLatitude());
//		location.setLongitude(locationType.getLongitude());
//		location.setTimeZone(locationType.getTimeZone());
//		return location;
//	}
//	
//	/**
//	 * Converts a AccessConfigurationType (from the stubs) to a AccessConfiguration (router model)
//	 * 
//	 * @param acct
//	 * @return
//	 */
//	public static AccessConfiguration accessConfigurationType2AccessConfiguration(AccessConfigurationType accessConfigurationType) {
//		AccessConfiguration accessConfiguration = new AccessConfiguration();
//		accessConfiguration.setIpAccessAddress(accessConfigurationType.getIpAccessAddress());
//		accessConfiguration.setPort(accessConfigurationType.getAccessPort());
//		accessConfiguration.setTransportProtocolName(accessConfigurationType.getTransportType());
//		accessConfiguration.setManagementProtocolName(accessConfigurationType.getProtocolType());
//		return accessConfiguration;
//	}
//	
//	/**
//	 * Converts a UserAccountType (from the stubs) to a UserAccount (router model)
//	 * 
//	 * @param usert
//	 * @return
//	 */
//	public static UserAccount userAccountType2UserAccount(UserAccountType userAccountType) {
//		UserAccount userAccount = new UserAccount();
//		userAccount.setUserID(userAccountType.getUserName());
//		userAccount.setPsw(userAccountType.getPassword());
//		userAccount.setUserIDPrivileged(userAccountType.getPrivilegedUser());
//		userAccount.setPrivilegedPsw(userAccountType.getPrivilegedPassword());
//		userAccount.setEmail(userAccountType.getEmailUser());
//		userAccount.setPswEmail(userAccountType.getEmailPassword());
//		userAccount.setSmtpServer(userAccountType.getSmtpServer());
//		if (userAccountType.getSmtpServerPort() != null)
//			userAccount.setSmtpPort(Integer.parseInt(userAccountType.getSmtpServerPort()));
//		return userAccount;
//	}
//	
//	/**
//	 * Converts a SubInterfaceType (from the stubs) to a SubInterface (router model)
//	 * 
//	 * @param sub
//	 * @return
//	 */
//	public static SubInterface subInterfaceType2SubInterface(SubInterfaceType subInterfaceType) {
//		SubInterface subInterface = new SubInterface();
//		subInterface.setIdentifier(subInterfaceType.getIdentifier());
//		subInterface.setMtu(subInterfaceType.getMtu());
//		subInterface.setDescription(subInterfaceType.getDescription());
//		subInterface.setSpeed(subInterfaceType.getSpeed());
//		subInterface.setEncapsulation(subInterfaceType.getEncapsulation());
//		subInterface.setPeerUnit(subInterfaceType.getPeerUnit());
//		subInterface.setVlanID(subInterfaceType.getVlanID());
//		if (subInterfaceType.getIPconfiguration() != null) {
//			subInterface.setIpConfiguration(ipConfigurationType2IPConfiguration(subInterfaceType.getIPconfiguration()));
//		}
//		return subInterface;
//	}
//	
//	/**
//	 * Converts a SubInterface (router model) to a SubInterfaceType (from the stubs) 
//	 * 
//	 * @param subInterface
//	 * @return
//	 */
//	public static SubInterfaceType subInterface2SubInterfaceType(SubInterface subInterface) {
//		SubInterfaceType subInterfaceType = new SubInterfaceType();
//		subInterfaceType.setIdentifier(subInterface.getIdentifier());
//		subInterfaceType.setMtu(subInterface.getMtu());
//		subInterfaceType.setDescription(subInterface.getDescription());
//		subInterfaceType.setSpeed(subInterface.getSpeed());
//		subInterfaceType.setEncapsulation(subInterface.getEncapsulation());
//		subInterfaceType.setPeerUnit(subInterface.getPeerUnit());
//		subInterfaceType.setVlanID(subInterface.getVlanID());
//		if (subInterface.getIpConfiguration() != null) {
//			subInterfaceType.setIPconfiguration(ipConfiguration2IPConfigurationType(subInterface.getIpConfiguration()));
//		}
//		return subInterfaceType;
//	}
//	
//	/**
//	 * Converts a IPConfigurationType (from the stubs) to a IPConfiguration (router model)
//	 * 
//	 * @param ipt
//	 * @return
//	 */
//	public static IPConfiguration ipConfigurationType2IPConfiguration(IPConfigurationType iPConfigurationType) {
//		IPConfiguration iPConfiguration = new IPConfiguration();
//		iPConfiguration.setIpAddress(iPConfigurationType.getIpAddress());
//		iPConfiguration.setNetmask(iPConfigurationType.getMask());
//		iPConfiguration.setIPSubNetwork(iPConfigurationType.getIpSubNetwork());
//		iPConfiguration.setBroadcast(iPConfigurationType.getBroadcast());
//		iPConfiguration.setFastSwitching(Boolean.parseBoolean(iPConfigurationType.getFastSwitching()));
//		
//		return iPConfiguration;
//	}
//	
//	/**
//	 * Converts a IPConfigurationType (from the stubs) to a IPConfiguration (router model)
//	 * 
//	 * @param ipt
//	 * @return
//	 */
//	public static IPConfigurationType ipConfiguration2IPConfigurationType(IPConfiguration iPConfiguration) {
//		IPConfigurationType iPConfigurationType = new IPConfigurationType();
//		iPConfigurationType.setIpAddress(iPConfiguration.getIpAddress());
//		iPConfigurationType.setMask(iPConfiguration.getNetmask());
//		iPConfigurationType.setIpSubNetwork(iPConfiguration.getIPSubNetwork());
//		iPConfigurationType.setBroadcast(iPConfiguration.getBroadcast());
//		iPConfigurationType.setFastSwitching(Boolean.toString(iPConfiguration.getFastSwitching()));
//		
//		return iPConfigurationType;
//	}
//	
//	/**
//	 * converts a RouterType (stubs) to a RouterInstance (model)
//	 * 
//	 * @param routerType
//	 * @return
//	 */
//	public static RouterInstance routerType2RouterInstance(RouterType routerType) {
//		RouterInstance router = new RouterInstance();
//		router.setRouterName(routerType.getRouterName());
//		router.setHostName(routerType.getHostName());
//		router.setModel(routerType.getRouterModel());
//		router.setVersionOS(routerType.getVersionOS());
//		router.setOperational(routerType.isIsOperation());
//		router.setIsPhysical(routerType.isIsPhysical());
//		router.setAllowsRouterInstancesCreation(routerType.isAllowsRouterInstanceCreation());
//		
//		if (routerType.getLocation() != null) {
//			router.setLocation(locationType2Location(routerType.getLocation()));
//		}
//		
//		if (routerType.getAccessConfiguration() != null) {
//			router.setAccessConfiguration(accessConfigurationType2AccessConfiguration(routerType.getAccessConfiguration()));
//		}
//		
//		if (routerType.getUserAccounts() != null) {
//			for (int i = 0; i < routerType.getUserAccounts().length; i++) {
//				router.addUserAccount(userAccountType2UserAccount(routerType.getUserAccounts(i)));
//			}
//		}
//		
//		if (routerType.getPhysicalInterfaces() != null) {
//			for (int i = 0; i < routerType.getPhysicalInterfaces().length; i++) {
//				PhysicalInterface pi = physicalInterfaceType2PhysicalInterface(routerType.getPhysicalInterfaces(i)); 
//				router.addInterface(pi);
//				pi.setRouterInstance(router);
//			}
//		}
//		
//		if (routerType.getChildren() != null) {
//			for (int i = 0; i < routerType.getChildren().length; i++) {
//				RouterInstance child = routerType2RouterInstance(routerType.getChildren(i));
//				router.addLogicalRouter(child);
//				child.setRouterParent(router);
//			}
//		}
//		
//		router.setEpr(routerType.getRouterEPR());
//		
//		return router;
//	}
//	/**
//	 * Converts a RouterInstance object to RouterType object
//	 * @param routerInstance
//	 * @return
//	 */
//	public static RouterType routerInstance2routerType(RouterInstance routerInstance) {
//		RouterType result = new RouterType();
//		
//		AccessConfigurationType accessConfigType = accessConfig2accessConfigType(routerInstance.getAccessConfiguration());
//		result.setAccessConfiguration(accessConfigType);
//		result.setAllowsRouterInstanceCreation(routerInstance.isAllowsRouterInstancesCreation());
//		result.setChildren(null);
//		result.setHostName(routerInstance.getHostName());
//		result.setIsOperation(routerInstance.isOperational());
//		result.setIsPhysical(routerInstance.isPhysical());
//		result.setLocation(location2locationType(routerInstance.getLocation()));
//		if(routerInstance.getRouterParent()!=null){
//			result.setParent(routerInstance.getRouterParent().getEpr());
//		}
//		result.setPhysicalInterfaces(InterfacesToInterfacesType(routerInstance.getInterfaces()));
//		result.setRouterEPR(routerInstance.getEpr());
//		result.setRouterModel(routerInstance.getModel());
//		result.setRouterName(routerInstance.getRouterName());
//		result.setUserAccounts(userAccountsListToUserAccountArray(routerInstance.getUserAccounts()));
//		result.setVersionOS(routerInstance.getVersionOS());
//		
//		return result;
//	}
//	/**
//	 * Convert AccessConfiguration object to AccessConfigurationType object
//	 * @param accessConfig
//	 * @return
//	 */
//	public static AccessConfigurationType accessConfig2accessConfigType(AccessConfiguration accessConfig){
//		AccessConfigurationType accessConfigType = new AccessConfigurationType();
//		
//		accessConfigType.setAccessPort(accessConfig.getPort());
//		accessConfigType.setIpAccessAddress(accessConfig.getIpAccessAddress());
//		accessConfigType.setProtocolType(accessConfig.getManagementProtocolName());
//		accessConfigType.setTransportType(accessConfig.getTransportProtocolName());
//		
//		return accessConfigType;
//	}
//	/**
//	 * Convert Location object 2 LocationType object
//	 * @param location
//	 * @return
//	 */
//	public static LocationType location2locationType(Location location){
//		LocationType locationType = new LocationType();
//		
//		locationType.setAddress(location.getAddress());
//		locationType.setCity(location.getCity());
//		locationType.setCountry(location.getCountry());
//		locationType.setEMail(location.getEmail());
//		locationType.setLatitude(location.getLatitude());
//		locationType.setLongitude(location.getLongitude());
//		locationType.setTelephone(location.getTelephone());
//		locationType.setTimeZone(location.getTimeZone());
//		
//		return locationType;
//	}
//	
//	/**
//	 * Converts a List of UserAccount to an Array of UserAccountType objects
//	 * @param accounts
//	 * @return accountsType
//	 */
//	public static UserAccountType[] userAccountsListToUserAccountArray(List<UserAccount> accounts){
//		UserAccountType[] accountsType = new UserAccountType[accounts.size()];
//		UserAccountType accountType;
//		for(int i=0; i<accounts.size(); i++){
//			accountType = userAccountToUserAccountType(accounts.get(i));
//			accountsType[i]=accountType;
//		}
//		return accountsType;
//	}
//	/**
//	 * Converts an UserAccount object to an UserAccountType object
//	 * @param account
//	 * @return
//	 */
//	public static UserAccountType userAccountToUserAccountType(UserAccount account){
//		UserAccountType accountType = new UserAccountType();
//		
//		if(account==null){
//			return accountType;
//		}
//		
//		accountType.setPassword(account.getPsw());
//		accountType.setPrivilegedPassword(account.getPrivilegedPsw());
//		accountType.setPrivilegedUser(account.getUserIDPrivileged());
//		accountType.setUserName(account.getUserID());
//		accountType.setEmailPassword(account.getPswEmail());
//		accountType.setSmtpServer(account.getSmtpServer());
//		accountType.setSmtpServerPort(Integer.toString(account.getSmtpPort()));
//		
//		return accountType;
//	}
//	
//	/**
//	 * Converts a list of PhysicalInterface object to a List of PhysicalInterfaceType objects
//	 * @param eInterfaces
//	 * @return
//	 */
//	public static PhysicalInterfaceType[] InterfacesToInterfacesType(List<PhysicalInterface> eInterfaces){
//		PhysicalInterfaceType[] interfacesType=new PhysicalInterfaceType[eInterfaces.size()];
//		PhysicalInterfaceType interfacet;
//		PhysicalInterface eInterface;
//		
//		for(int i=0; i<eInterfaces.size(); i++){
//			interfacet=new PhysicalInterfaceType();
//			eInterface=eInterfaces.get(i);
//			//convert the PhysicalInterface object to PhysicalInterfaceType object
//			interfacet=engineInterfaceToInterfaceType(eInterface);
//			interfacesType[i]=interfacet;
//		}
//		
//		return interfacesType;
//	}
//	/**
//	 * Converts a PhysicalInterface object to PhysicalInterfaceType object
//	 * @param eInterface
//	 * @return
//	 */
//	public static PhysicalInterfaceType engineInterfaceToInterfaceType(PhysicalInterface eInterface){
//		PhysicalInterfaceType interfacet=new PhysicalInterfaceType();
//		
//		if(eInterface==null){
//			return interfacet;
//		}
//		
//		interfacet.setLocation(eInterface.getLocation());
//		interfacet.setLinkMode(eInterface.getLinkMode());
//		if(!eInterface.getKeepalive().equals("")){
//			interfacet.setKeepalive(Integer.parseInt(eInterface.getKeepalive()));
//		}
//		interfacet.setLinkStatus(eInterface.getLinkStatus());
//		interfacet.setMacAddress(eInterface.getMacAddress());
//		interfacet.setStatus(eInterface.getStatus());
//		interfacet.setType(eInterface.getType());
//		//if exists some logical interface
//		if(eInterface.getSubInterfaces().size()>0){
//			SubInterfaceType[] subInterfaces = new SubInterfaceType[eInterface.getSubInterfaces().size()];
//			//for each logical Interface
//			for(int x=0; x<eInterface.getSubInterfaces().size(); x++){
//				//convert subInterface object to subIntrefaceType object
//				subInterfaces[x]=engineSubInterfaceToSubInterfaceType(eInterface.getSubInterfaces().get(x));
//			}
//			interfacet.setSubInterfaces(subInterfaces);
//		}else{
//			//it hasn't any logical interface
//			interfacet.setSubInterfaces(new SubInterfaceType[0]);
//		}
//		
//		return interfacet;
//		
//	}
//	/**
//	 * Converts a SubInterface object to aSubInterfaceType object
//	 * @param subInterface
//	 * @return
//	 */
//	private static SubInterfaceType engineSubInterfaceToSubInterfaceType(SubInterface subInterface) {
//		SubInterfaceType subInterfaceType = new SubInterfaceType();
//		
//		if(subInterface==null){
//			return subInterfaceType;
//		}
//		
//		subInterfaceType.setDescription(subInterface.getDescription());
//		subInterfaceType.setEncapsulation(subInterface.getEncapsulation());
//		subInterfaceType.setIdentifier(subInterface.getIdentifier());
//		subInterfaceType.setMtu(subInterface.getMtu());
//		subInterfaceType.setSpeed(subInterface.getSpeed());
//		subInterfaceType.setPeerUnit(subInterface.getPeerUnit());
//		subInterfaceType.setVlanID(subInterface.getVlanID());
//		subInterfaceType.setPhysicalInterfaceID(subInterface.getIdentifier());
//		
//		//set IPConfiguration
//		subInterfaceType.setIPconfiguration(engineIPConfigurationToIPConfigurationType(subInterface.getIpConfiguration()));
//		
//		return subInterfaceType;
//	}
//	/**
//	 * Converts an IPConfiguration object to an IPConfigurationType object
//	 * @param ipConfig
//	 * @return
//	 */
//	public static IPConfigurationType engineIPConfigurationToIPConfigurationType(IPConfiguration ipConfig){
//		IPConfigurationType result = new IPConfigurationType();
//		
//		if(ipConfig==null){
//			return result;
//		}
//		
//		result.setBroadcast(ipConfig.getBroadcast());
//		result.setIpSubNetwork(ipConfig.getIPSubNetwork());
//		result.setIpAddress(ipConfig.getIpAddress());
//		result.setIpVersion(ipConfig.getIpVersion());
//		result.setMask(ipConfig.getNetmask());
//		
//		return result;
//	}
	
	public static AdvancedBGPPolicyType[] getAdvancedBGPPolicies(BGPPolicyType[] bgpPolicies) {
		if(bgpPolicies == null) return null;
		List<AdvancedBGPPolicyType> advPolicies = new ArrayList<AdvancedBGPPolicyType>();
		for(BGPPolicyType policy : bgpPolicies){
			if(policy instanceof AdvancedBGPPolicyType){
				advPolicies.add((AdvancedBGPPolicyType) policy);
			}
		}
		
		AdvancedBGPPolicyType[] result = null;
		
		if(advPolicies.size()>0){
			result = new AdvancedBGPPolicyType[advPolicies.size()];
			for(int i=0; i<advPolicies.size(); i++){
				result[i] = advPolicies.get(i);
			}
		}
		return result;
	}

	public static SimpleBGPPolicyType[] getSimpleBGPPolicies(BGPPolicyType[] bgpPolicies) {
		if(bgpPolicies == null) return null;
		List<SimpleBGPPolicyType> sPolicies = new ArrayList<SimpleBGPPolicyType>();
		for(BGPPolicyType policy : bgpPolicies){
			if(policy instanceof SimpleBGPPolicyType){
				sPolicies.add((SimpleBGPPolicyType) policy);
			}
		}
		
		SimpleBGPPolicyType[] result = null;
		
		if(sPolicies.size()>0){
			result = new SimpleBGPPolicyType[sPolicies.size()];
			for(int i=0; i<sPolicies.size(); i++){
				result[i] = sPolicies.get(i);
			}
		}
		return result;
	}
}
