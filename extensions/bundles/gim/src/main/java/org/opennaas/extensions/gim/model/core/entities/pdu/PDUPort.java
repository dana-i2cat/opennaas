package org.opennaas.extensions.gim.model.core.entities.pdu;

import java.util.List;

import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerMonitorLogging;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.load.DeliveryRatedLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUPort implements IPowerDelivery, IPowerMonitorLogging {

	private String					id;
	private String					name;
	private List<IPowerConsumer>	powerConsumers;
	private DeliveryRatedLoad		deliveryRatedLoad;
	private PowerMonitorLog			powerMonitorLog;

	private PDU						pdu;

	public PDU getPdu() {
		return pdu;
	}

	public void setPdu(PDU pdu) {
		this.pdu = pdu;
	}

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

	/**
	 * Return @code{IPowerSupply}s associated to the PDU
	 */
	public List<IPowerSupply> getPowerSupplies() {
		return pdu.getPowerSupplies();
	}

	public List<IPowerConsumer> getPowerConsumers() {
		return powerConsumers;
	}

	/**
	 * @param powerConsumers
	 *            the powerConsumers to set
	 */
	public void setPowerConsumers(List<IPowerConsumer> powerConsumers) {
		this.powerConsumers = powerConsumers;
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
	 * @return the powerMonitorLog
	 */
	public PowerMonitorLog getPowerMonitorLog() {
		return powerMonitorLog;
	}

	/**
	 * @param powerMonitorLog
	 *            the powerMonitorLog to set
	 */
	public void setPowerMonitorLog(PowerMonitorLog powerMonitorLog) {
		this.powerMonitorLog = powerMonitorLog;
	}

}
