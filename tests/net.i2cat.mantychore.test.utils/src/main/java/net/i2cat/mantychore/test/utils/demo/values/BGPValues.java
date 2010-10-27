package cat.i2cat.manticore.test.demo.values;

import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.stubs.ipnetwork.BGPConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.EBGPConfigurationType;
import cat.i2cat.manticore.stubs.ipnetwork.IBGPConfigurationType;
/**
 * 
 * @author Xavi Barrera
 *
 */
public class BGPValues {
	private String routerASNumber;
	private String routerID;
	private String type;
	private int holdTimer = 180;
	
	private boolean routerReflector;
	private String localAddress;
	private String[] neighbors;
	
	private String remoteAS;
	private String neighborAddress;
	private boolean removePrivateAS;
	
	private String[] exportPolicies;
	private String[] importPolicies;

	public BGPConfigurationType getDesiredBGPConfiguration() {
		BGPConfigurationType bgpConfiguration = null;
		if(type.compareTo(EngineConstants.EXTERNAL_BGP_TYPE)==0){
			bgpConfiguration = new EBGPConfigurationType();
			((EBGPConfigurationType)bgpConfiguration).setAsName(routerASNumber);
			((EBGPConfigurationType)bgpConfiguration).setExportPoliciesNames(exportPolicies);
			((EBGPConfigurationType)bgpConfiguration).setHoldTimer(holdTimer);
			((EBGPConfigurationType)bgpConfiguration).setImportPoliciesNames(importPolicies);
			((EBGPConfigurationType)bgpConfiguration).setNeighborAddress(neighborAddress);
			((EBGPConfigurationType)bgpConfiguration).setRemoteAS(remoteAS);
			((EBGPConfigurationType)bgpConfiguration).setRouterID(routerID);
			((EBGPConfigurationType)bgpConfiguration).setRemovePrivateAS(removePrivateAS);
		}else if(type.compareTo(EngineConstants.INTERNAL_BGP_TYPE)==0){
			bgpConfiguration = new IBGPConfigurationType();
			((IBGPConfigurationType)bgpConfiguration).setAsName(routerASNumber);
			((IBGPConfigurationType)bgpConfiguration).setExportPoliciesNames(exportPolicies);
			((IBGPConfigurationType)bgpConfiguration).setHoldTimer(holdTimer);
			((IBGPConfigurationType)bgpConfiguration).setImportPoliciesNames(importPolicies);
			((IBGPConfigurationType)bgpConfiguration).setLocalAddress(localAddress);
			((IBGPConfigurationType)bgpConfiguration).setNeighbors(neighbors);
			((IBGPConfigurationType)bgpConfiguration).setRouterID(routerID);
			((IBGPConfigurationType)bgpConfiguration).setRouterReflector(routerReflector);
		}
		return bgpConfiguration;
	}

	public String getRouterASNumber() {
		return routerASNumber;
	}

	public void setRouterASNumber(String routerASNumber) {
		this.routerASNumber = routerASNumber;
	}

	public String getRouterID() {
		return routerID;
	}

	public void setRouterID(String routerID) {
		this.routerID = routerID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getHoldTimer() {
		return holdTimer;
	}

	public void setHoldTimer(int holdTimer) {
		this.holdTimer = holdTimer;
	}

	public boolean isRouterReflector() {
		return routerReflector;
	}

	public void setRouterReflector(boolean routerReflector) {
		this.routerReflector = routerReflector;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public String[] getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(String[] neighbors) {
		this.neighbors = neighbors;
	}

	public String getRemoteAS() {
		return remoteAS;
	}

	public void setRemoteAS(String remoteAS) {
		this.remoteAS = remoteAS;
	}

	public String getNeighborAddress() {
		return neighborAddress;
	}

	public void setNeighborAddress(String neighborAddress) {
		this.neighborAddress = neighborAddress;
	}

	public boolean isRemovePrivateAS() {
		return removePrivateAS;
	}

	public void setRemovePrivateAS(boolean removePrivateAS) {
		this.removePrivateAS = removePrivateAS;
	}

	public String[] getExportPolicies() {
		return exportPolicies;
	}

	public void setExportPolicies(String[] exportPolicies) {
		this.exportPolicies = exportPolicies;
	}

	public String[] getImportPolicies() {
		return importPolicies;
	}

	public void setImportPolicies(String[] importPolicies) {
		this.importPolicies = importPolicies;
	}


}
