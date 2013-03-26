package GIM;

import java.util.Date;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:41:47
 */
public class MeasuredLoad extends ElectricalLoad {

	private Date reading_time;
	//private double inputVoltage;
	//private double inputCurrent;
	//private double inputPower;

	public MeasuredLoad(){

	}

	public void setReadingTime(Date dt){
		reading_time = dt;
	}
	
	public Date getReadingTime(){
		return reading_time;
	}
	
	
	
	
	
	public void finalize() throws Throwable {
		super.finalize();
	}

}