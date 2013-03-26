package GIM;

import java.util.ArrayList;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:41:58
 */
public class PowerMonitorLog {

	private int samplePeriod; // in mins
	private int sampleDuration; // in mins
	public PowerSupplyResource m_PowerSupplyResource;

	public ArrayList<MeasuredLoad> measuredLoads;

	public PowerMonitorLog(int sampTime, int sampDur){
		samplePeriod = sampTime;
		sampleDuration = sampDur;
		
		measuredLoads = new ArrayList<MeasuredLoad>();
	}

	public void add(MeasuredLoad ml){
		measuredLoads.add(ml);
	}

	public int getSampleDuration(){
		return sampleDuration;
	}
	
	public int getSamplePeriod(){
		return samplePeriod;
	}
	
	public void finalize() throws Throwable {

	}

}