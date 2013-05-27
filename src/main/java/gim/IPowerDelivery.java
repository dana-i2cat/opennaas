package gim;

import gim.load.MeasuredLoad;

import java.util.List;

public interface IPowerDelivery {

	public MeasuredLoad getCurrentPowerMetrics() throws Exception;

	public boolean getPowerStatus() throws Exception;

	public boolean powerOn() throws Exception;

	public boolean powerOff() throws Exception;

	public List<IPowerSupply> getPowerSupplies();

}
