package GIM;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 16:29:35
 */
public class Energy {

	private double CO2PerKw;
	private String energyName;

	energyClass energyClass;
	energyType energyType;
	
	public Energy(energyClass energyclass, energyType energytype, double co2){

		energyClass = energyclass; 
		CO2PerKw = co2;
		energyType = energytype;
		
	}

	public double getCO2perKw(){
		return CO2PerKw;
	}
	
	public String getEnergyClass(){
		return energyClass.toString();
	}
	
	public String getEnergyType(){
		return energyType.toString();
	}
	
	public void finalize() throws Throwable {

	}

	
}
