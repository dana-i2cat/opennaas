package gim;

import gim.energy.Energy;

public interface IPowerSupply {

	public Energy getEnergy();

	public double getPricePerUnit();
}
