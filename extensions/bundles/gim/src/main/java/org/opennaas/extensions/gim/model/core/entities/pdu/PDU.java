package org.opennaas.extensions.gim.model.core.entities.pdu;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.load.DeliveryRatedLoad;

/**
 * An IPowerDelivery implementation formed by a PDU with its ports.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDU implements IPowerDelivery {

	private String				id;
	private List<PDUPort>		pduPorts;
	private List<IPowerSupply>	powerSupplies;
	private DeliveryRatedLoad	deliveryRatedLoad;
	private String				name;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
