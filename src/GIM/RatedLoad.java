package GIM;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:42:03
 */
public class RatedLoad extends ElectricalLoad {

	public PowerSupplyResource m_PowerSupplyResource;
	public NetworkResource m_NetworkResource;

	public RatedLoad(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}