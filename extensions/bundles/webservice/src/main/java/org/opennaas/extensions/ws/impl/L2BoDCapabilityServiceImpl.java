package org.opennaas.extensions.ws.impl;

import java.util.List;
import java.util.NoSuchElementException;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.ws.services.IL2BoDCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService(portName = "L2BoDCapabilityPort", serviceName = "L2BoDCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class L2BoDCapabilityServiceImpl extends GenericCapabilityService implements IL2BoDCapabilityService {

	Log	log	= LogFactory.getLog(L2BoDCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IL2BoDCapabilityService#requestConnection(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void requestConnection(String resourceId, String interfaceName1, String interfaceName2, String vlanid, String capacity,
			String endTime) throws ResourceException {
		try {
			IL2BoDCapability iL2BoDCapability = (IL2BoDCapability) getCapability(resourceId, IL2BoDCapability.class);
			iL2BoDCapability.requestConnection(getParameters(resourceId, interfaceName1, interfaceName2, vlanid, capacity, endTime));
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		} catch (ResourceException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IL2BoDCapabilityService#shutDownConnection(java.lang.String, java.util.List)
	 */
	@Override
	public void shutDownConnection(String resourceId, List<Interface> listInterfaces) throws CapabilityException {
		try {
			IL2BoDCapability iL2BoDCapability = (IL2BoDCapability) getCapability(resourceId, IL2BoDCapability.class);
			iL2BoDCapability.shutDownConnection(listInterfaces);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * @param endTime
	 * @param capacity
	 * @param vlanid
	 * @param interfaceName2
	 * @param interfaceName1
	 * @return
	 * @throws ResourceException
	 */
	private RequestConnectionParameters getParameters(String resourceId, String interfaceName1, String interfaceName2, String vlanid,
			String capacity, String endTime) throws ResourceException {
		RequestConnectionParameters params = new RequestConnectionParameters();
		params.interface1 = getInterfaceByName(resourceId, interfaceName1);
		params.interface2 = getInterfaceByName(resourceId, interfaceName2);
		params.vlanid = Integer.valueOf(vlanid);
		params.capacity = Long.valueOf(capacity);
		params.endTime = parseISO8601Date(endTime);
		return params;
	}

	/**
	 * @param interfaceName1
	 * @param interfaceName1
	 * @return
	 * @throws ResourceException
	 */
	private Interface getInterfaceByName(String resourceId, String ifaceName) throws ResourceException {
		IResource resource = getResource(resourceId);
		NetworkModel networkModel = (NetworkModel) resource.getModel();
		List<NetworkElement> elements = networkModel.getNetworkElements();
		Interface i =
				NetworkModelHelper.getInterfaceByName(elements, ifaceName);
		if (i == null) {
			throw new NoSuchElementException("No such interface: " + ifaceName);
		}
		return i;
	}

	/**
	 * @param s
	 * @return
	 */
	private DateTime parseISO8601Date(String s) {
		DateTime time = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(s);
		return ((s == null) ? new DateTime() : time);
	}
}
