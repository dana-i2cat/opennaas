package GIM;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:41:52
 */
public class ONS_Resource {

	private int resourceID;
	private int resourceType;
	private int powerState;
	public Facility m_Facility;
	public PowerMonitorLog m_PowerMonitorLog;
	public ONS_Resource m_ONS_Resource;

	public ONS_Resource(){

	}

	public void finalize() throws Throwable {

	}

}