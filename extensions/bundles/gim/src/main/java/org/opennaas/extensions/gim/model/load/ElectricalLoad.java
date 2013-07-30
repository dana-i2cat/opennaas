package org.opennaas.extensions.gim.model.load;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:41:39
 */
public class ElectricalLoad extends Load {

	private double inputVoltage;
	private double inputCurrent;
	private double inputPower;
	private double inputEnergy;

	public ElectricalLoad(){

	}

	public void setVoltage(double voltage){
		inputVoltage = voltage;
	}
	
	public void setCurrent(double current){
		inputCurrent = current;
	}
	
	public void setPower(double power){
		inputPower = power;
	}
	
	public void setEnergy(double energy){
		inputEnergy = energy;
	}
	
	public double getVoltage(){
		return inputVoltage;
	}
	
	public double getCurrent(){
		return inputCurrent;
	}
	
	public double getPower(){
		return inputPower;
	}
	
	public double getEnergy(){
		return inputEnergy;
	}
	
	public void finalize() throws Throwable {
		super.finalize();
	}

}