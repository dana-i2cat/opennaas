package gim.core.entities.pdu;

import gim.core.IPowerConsumer;
import gim.core.IPowerDelivery;
import gim.core.IPowerMonitorLogging;
import gim.core.IPowerSupply;
import gim.load.DeliveryRatedLoad;
import gim.log.PowerMonitorLog;

import java.util.List;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUPort implements IPowerDelivery, IPowerMonitorLogging {

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
