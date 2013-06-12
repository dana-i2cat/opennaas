package org.opennaas.extensions.gim.model.energy;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 16:29:35
 */
public class Energy {

	private String		energyName;

	private EnergyClass	energyClass;
	private EnergyType	energyType;

	private double		CO2PerKw;
	private double		percentageGreen;

	@Deprecated
	public Energy(EnergyClass energyclass, EnergyType energytype, double co2) {

		energyClass = energyclass;
		CO2PerKw = co2;
		energyType = energytype;
		percentageGreen = getPercentageGreenFromEnergyClass(energyclass);
	}

	public Energy(EnergyClass energyclass, EnergyType energytype, double co2, double percentagegreen) {

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
	private double getPercentageGreenFromEnergyClass(EnergyClass energyclass) {
		if (energyclass.equals(EnergyClass.Green))
			return 100;
		if (energyclass.equals(EnergyClass.Mixed))
			return 50;
		if (energyclass.equals(EnergyClass.Brown))
			return 0;

		return 0;
	}

	@Override
	public String toString() {
		return "Energy [energyName=" + energyName + ", energyClass=" + energyClass + ", energyType=" + energyType + ", CO2PerKw=" + CO2PerKw + ", percentageGreen=" + percentageGreen + "]";
	}

}
