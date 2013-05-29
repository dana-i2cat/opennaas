package gim.core.entities.pdu;

import gim.core.IPowerConsumer;
import gim.core.IPowerDelivery;
import gim.core.IPowerSupply;
import gim.load.DeliveryRatedLoad;

import java.util.ArrayList;
import java.util.List;

/**
 * An IPowerDelivery implementation formed by a PDU with its ports.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDU implements IPowerDelivery {

	private List<PDUPort>		pduPorts;
	private List<IPowerSupply>	powerSupplies;
	private DeliveryRatedLoad	deliveryRatedLoad;

	/**
	 * @return the pduPorts
	 */
	public List<PDUPort> getPduPorts() {
		return pduPorts;
	}

	/**
	 * @param pduPorts
	 *            the pduPorts to set
	 */
	public void setPduPorts(List<PDUPort> pduPorts) {
		this.pduPorts = pduPorts;
	}

	public List<IPowerSupply> getPowerSupplies() {
		return powerSupplies;
	}

	/**
	 * @param powerSupplies
	 *            the powerSupplies to set
	 */
	public void setPowerSupplies(List<IPowerSupply> powerSupplies) {
		this.powerSupplies = powerSupplies;
	}

	/**
	 * Return consumers associated to the PDU ports.
	 */
	public List<IPowerConsumer> getPowerConsumers() {
		List<IPowerConsumer> allConsumers = new ArrayList<IPowerConsumer>();
		for (PDUPort port : pduPorts) {
			allConsumers.addAll(port.getPowerConsumers());
		}
		return allConsumers;
	}

	public DeliveryRatedLoad getDeliveryRatedLoad() {
		return deliveryRatedLoad;
	}

	/**
	 * @param deliveryRatedLoad
	 *            the deliveryRatedLoad to set
	 */
	public void setDeliveryRatedLoad(DeliveryRatedLoad deliveryRatedLoad) {
		this.deliveryRatedLoad = deliveryRatedLoad;
	}

}
