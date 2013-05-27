import java.io.IOException;
import java.util.Date;



import GIM.MeasuredLoad;
import GIM.PowerDeliveryResource;
import GIM.PowerMonitorLog;
import GIM.RouterResource;


public class Test {
	public static void main(String[] args) throws IOException {
		String PDUIP = "udp:193.1.29.121/161";
		int routerOutletNumber = 1;
		
		PowerDeliveryResource PDU = new PowerDeliveryResource(PDUIP);
		RouterResource router1 = new RouterResource(PDU, routerOutletNumber);
		
		System.out.println("Router Power Supply : "+router1.getPowerSupplyDeviceName());
		System.out.println("Router Outlet ID: "+router1.getPowerSupplyOutletName(routerOutletNumber));

		
		boolean status = router1.getPowerStatus();
		System.out.println("Router Status: "+status);
		

		//router1.powerOff();
		
		status = router1.getPowerStatus();
		System.out.println("Router Status: "+status);
		
		//router1.powerOn();
		
		status = router1.getPowerStatus();
		System.out.println("Router Status: "+status);
		
		MeasuredLoad ml = router1.getCurrentRouterPower();
		
		System.out.println("Router Energy Class (PowerSupply) "+router1.getEnergyClass());
		System.out.println("Router Energy Type (PowerSupply) "+router1.getEnergyType());
		System.out.println("Router Energy CO2perKW: "+router1.getCO2PerKw());
		
		System.out.println("Router Voltage @ "+ml.getReadingTime().toLocaleString()+": "+ml.getVoltage()+" Volts");
		System.out.println("Router Current @ "+ml.getReadingTime().toLocaleString()+": "+ml.getCurrent()+" Amps");
		System.out.println("Router Power @ "+ml.getReadingTime().toLocaleString()+": "+ml.getPower()+"KW");
		System.out.println("Router Energy @ "+ml.getReadingTime().toLocaleString()+": "+ml.getEnergy()+"KWh");
			
		int time = 0, monintoringDuration = router1.getPowerMonitoringSampleDuration(), 
		sampleTime = router1.getPowerMonitoringSamplePeriod();
	
		while(time < monintoringDuration){
			
			if(time%sampleTime ==0)
				router1.readToPowerLog();
			
			time++;
		}
		
		
		
		}
}
