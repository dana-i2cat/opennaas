package GIM;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:42:01
 */
public class PowerSupplyResource extends ONS_Resource {

	private double pricePerUnit;
	public Energy m_Energy;

	public PowerSupplyResource(Energy energySource, double price){
		pricePerUnit = price;
		m_Energy = energySource;
	}
	
	public Energy getEnergy(){
		return m_Energy;
	}

	public void setEnergyType(Energy energyType){
		m_Energy = energyType;
	}


	
	public void finalize() throws Throwable {
		super.finalize();
	}

}