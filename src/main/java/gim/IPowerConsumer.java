package gim;

import java.util.List;

/**
 * An interface to announce implementors have an IPowerDelivery.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerConsumer {

	/**
	 * 
	 * @return a list of IPowerDelivery systems associated to this
	 */
	List<IPowerDelivery> getPowerDeliveries();

}
