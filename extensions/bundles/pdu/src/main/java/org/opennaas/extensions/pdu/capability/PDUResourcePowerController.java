package org.opennaas.extensions.pdu.capability;

import java.util.Date;
import java.util.List;

import org.opennaas.core.resources.IResource;
import org.opennaas.extensions.gim.controller.AbstractPDUPowerController;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;
import org.opennaas.extensions.pdu.model.PDUModel;

public class PDUResourcePowerController extends AbstractPDUPowerController {
	
	private IResource pduResource;

	public IResource getPduResource() {
		return pduResource;
	}

	public void setPduResource(IResource pduResource) {
		this.pduResource = pduResource;
	}

	@Override
	public Energy getAggregatedEnergy() throws Exception {
		return ((IPDUPowerSupplyCapability) pduResource.getCapabilityByInterface(IPDUPowerSupplyCapability.class))
				.getAggregatedEnergy();
	}

	@Override
	public double getAggregatedPricePerEnergyUnit() throws Exception {
		return ((IPDUPowerSupplyCapability) pduResource.getCapabilityByInterface(IPDUPowerSupplyCapability.class))
				.getAggregatedPricePerEnergyUnit();
	}

	@Override
	public boolean getPowerStatus(String portId) throws Exception {
		return ((IPDUPowerManagementIDsCapability) pduResource.getCapabilityByInterface(IPDUPowerManagementIDsCapability.class))
				.getPowerStatus(portId);
	}

	@Override
	public boolean powerOn(String portId) throws Exception {
		return ((IPDUPowerManagementIDsCapability) pduResource.getCapabilityByInterface(IPDUPowerManagementIDsCapability.class))
				.powerOn(portId);
	}

	@Override
	public boolean powerOff(String portId) throws Exception {
		return ((IPDUPowerManagementIDsCapability) pduResource.getCapabilityByInterface(IPDUPowerManagementIDsCapability.class))
				.powerOff(portId);
	}

	@Override
	public MeasuredLoad getCurrentPowerMetrics(String portId) throws Exception {
		return ((IPDUPowerMonitoringIDsCapability) pduResource.getCapabilityByInterface(IPDUPowerMonitoringIDsCapability.class))
				.getCurrentPowerMetrics(portId);
	}

	@Override
	public PowerMonitorLog getPowerMetricsByTimeRange(String portId, Date from,
			Date to) throws Exception {
		return ((IPDUPowerMonitoringIDsCapability) pduResource.getCapabilityByInterface(IPDUPowerMonitoringIDsCapability.class))
				.getPowerMetricsByTimeRange(portId, from, to);
	}

	@Override
	public List<PDUPort> listPorts() throws Exception {
		List<PowerSource> ports = ((PDUModel) pduResource.getModel()).getPdu().getPowerSources();
		//TODO 
		return null;
	}

}
