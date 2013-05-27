package GIM;

import java.io.IOException;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:42:05
 */
public class RouterResource extends NetworkResource{

	int targetOutletIndex; 

	public RouterResource(PowerDeliveryResource pdu, int powerOutletNum) throws IOException{
		// statically set SNMPIP, sampleTime and sampleDuration
		m_PowerDeliveryResource = pdu;
		m_PowerMonitorLog = new PowerMonitorLog(10, 30);	
		targetOutletIndex = powerOutletNum;
	}

	public int getPowerMonitoringSampleDuration(){
		return m_PowerMonitorLog.getSampleDuration();
	}
	
	public int getPowerMonitoringSamplePeriod(){
		return m_PowerMonitorLog.getSamplePeriod();
	}
	
	public MeasuredLoad getCurrentRouterPower(){
		MeasuredLoad ml = null;
		
		ml = m_PowerDeliveryResource.getCurrentPowerMetrics(targetOutletIndex);
		
		return ml;
	}
	
	public boolean getPowerStatus() throws IOException{
		boolean status;

		status =  m_PowerDeliveryResource.getPortPowerStatus(targetOutletIndex);

		return status;
	}
	
	public String getPowerSupplyDeviceName() throws IOException{
		String name;

		name =  m_PowerDeliveryResource.getDeviceName();

		return name;
	}
	
	public String getPowerSupplyOutletName(int targetOutletIndex) throws IOException{
		String name;

		name =  m_PowerDeliveryResource.getOutletName(targetOutletIndex);

		return name;
	}
	
	
	public void readToPowerLog(){
		
		m_PowerMonitorLog.add(m_PowerDeliveryResource.getCurrentPowerMetrics(targetOutletIndex));

	}
	
	public void powerOn(){
		m_PowerDeliveryResource.powerOnPort(targetOutletIndex);
	}
	
	public void powerOff(){
		m_PowerDeliveryResource.powerOffPort(targetOutletIndex);
	}
	
	public String getEnergyType(){
		
	return m_PowerDeliveryResource.getEnergyType();

	}
	
	public String getEnergyClass(){
		
		return m_PowerDeliveryResource.getEnergyClass();

		}
	
	public double getCO2PerKw(){
		return m_PowerDeliveryResource.getCO2perKW();
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}