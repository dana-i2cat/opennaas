package gim.log;

import gim.load.MeasuredLoad;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:41:58
 */
public class PowerMonitorLog {

	private int					samplePeriod;	// in mins
	private int					sampleDuration; // in mins

	private List<MeasuredLoad>	measuredLoads;

	public PowerMonitorLog(int sampTime, int sampDur) {
		samplePeriod = sampTime;
		sampleDuration = sampDur;

		measuredLoads = new ArrayList<MeasuredLoad>();
	}

	public List<MeasuredLoad> getMeasuredLoads() {
		return measuredLoads;
	}

	public void setMeasuredLoads(List<MeasuredLoad> measuredLoads) {
		this.measuredLoads = measuredLoads;
	}

	public void add(MeasuredLoad ml) {
		measuredLoads.add(ml);
	}

	public int getSampleDuration() {
		return sampleDuration;
	}

	public int getSamplePeriod() {
		return samplePeriod;
	}

	public void setSamplePeriod(int samplePeriod) {
		this.samplePeriod = samplePeriod;
	}

	public void setSampleDuration(int sampleDuration) {
		this.sampleDuration = sampleDuration;
	}

	public void finalize() throws Throwable {

	}

}