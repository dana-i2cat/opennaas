package controller;

import gim.core.entities.pdu.PDU;
import gim.core.entities.pdu.PDUPort;
import gim.energy.Energy;
import gim.load.MeasuredLoad;
import gim.log.PowerMonitorLog;

import java.util.Date;

/**
 * A PDUPort controller delegating to PDUPowerController.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUPortPowerController extends AbstractPowerController {

	private PDUPort				port;
	private PDUPowerController	pduController;

	public MeasuredLoad getCurrentPowerMetrics() throws Exception {
		return getPDUController(port.getPdu()).getCurrentPowerMetrics(port);
	}

	public PowerMonitorLog getPowerMetricsByTimeRange(Date from, Date to) throws Exception {
		return getPDUController(port.getPdu()).getPowerMetricsByTimeRange(port, from, to);
	}

	public boolean getPowerStatus() throws Exception {
		return getPDUController(port.getPdu()).getPowerStatus(port);
	}

	public boolean powerOn() throws Exception {
		return getPDUController(port.getPdu()).powerOn(port);
	}

	public boolean powerOff() throws Exception {
		return getPDUController(port.getPdu()).powerOff(port);
	}

	public Energy getAggregatedEnergy() {
		return getPDUController(port.getPdu()).getAggregatedEnergy();
	}

	public double getAggregatedPricePerEnergyUnit() {
		return getPDUController(port.getPdu()).getAggregatedPricePerEnergyUnit();
	}

	private PDUPowerController getPDUController(PDU pdu) {
		return getPduController();
	}

	public PDUPort getPort() {
		return port;
	}

	public void setPort(PDUPort port) {
		this.port = port;
	}

	/**
	 * @return the pduController
	 */
	public PDUPowerController getPduController() {
		return pduController;
	}

	/**
	 * @param pduController
	 *            the pduController to set
	 */
	public void setPduController(PDUPowerController pduController) {
		this.pduController = pduController;
	}

}
