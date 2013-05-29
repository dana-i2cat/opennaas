package gim.energy;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 16:29:35
 */
public class Energy {

	private double		CO2PerKw;
	private String		energyName;
	private double		percentageGreen;

	private energyClass	energyClass;
	private energyType	energyType;

	@Deprecated
	public Energy(energyClass energyclass, energyType energytype, double co2) {

		energyClass = energyclass;
		CO2PerKw = co2;
		energyType = energytype;
		percentageGreen = getPercentageGreenFromEnergyClass(energyclass);
	}

	public Energy(energyClass energyclass, energyType energytype, double co2, double percentagegreen) {

		energyClass = energyclass;
		CO2PerKw = co2;
		energyType = energytype;
		percentageGreen = percentagegreen;
	}

	public double getCO2perKw() {
		return CO2PerKw;
	}

	public String getEnergyClass() {
		return energyClass.toString();
	}

	public String getEnergyType() {
		return energyType.toString();
	}

	public double getPercentageGreen() {
		return percentageGreen;
	}

	public void setPercentageGreen(double percentageGreen) {
		this.percentageGreen = percentageGreen;
	}

	public void finalize() throws Throwable {

	}

	@Deprecated
	private double getPercentageGreenFromEnergyClass(energyClass energyclass) {
		if (energyclass.equals(energyClass.Green))
			return 100;
		if (energyclass.equals(energyClass.Mixed))
			return 50;
		if (energyclass.equals(energyClass.Brown))
			return 0;

		return 0;
	}

}
