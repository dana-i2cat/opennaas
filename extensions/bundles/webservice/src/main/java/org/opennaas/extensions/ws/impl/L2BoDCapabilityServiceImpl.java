package org.opennaas.extensions.ws.impl;

import java.util.ArrayList;
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
			log.info("Start of requestConnection call");
			IL2BoDCapability iL2BoDCapability = (IL2BoDCapability) getCapability(resourceId, IL2BoDCapability.class);
			iL2BoDCapability.requestConnection(getParameters(resourceId, interfaceName1, interfaceName2, vlanid, capacity, endTime));
			log.info("End of requestConnection call");
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
	public void shutDownConnection(String resourceId, String interfaceName1, String interfaceName2, String vlanid, String capacity,
			String endTime) throws ResourceException {
		try {
			log.info("Start of shutDownConnection call");
			IL2BoDCapability iL2BoDCapability = (IL2BoDCapability) getCapability(resourceId, IL2BoDCapability.class);
			iL2BoDCapability.shutDownConnection(getParameters(resourceId, interfaceName1, interfaceName2, vlanid, capacity, endTime));
			log.info("End of shutDownConnection call");
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		} catch (ResourceException e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * @param resourceId
	 * @param listInterfaces
	 * @return list of Interfaces
	 * @throws ResourceException
	 */
	private List<Interface> getListInterfaces(String resourceId, List<String> listInterfaces) throws ResourceException {
		List<Interface> list = new ArrayList<Interface>();
		for (String name : listInterfaces) {
			list.add(getInterfaceByName(resourceId, name));
		}
		return list;
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
		return new RequestConnectionParameters(
				getInterfaceByName(resourceId, interfaceName1),
				getInterfaceByName(resourceId, interfaceName2),
				Long.valueOf(capacity),
				Integer.valueOf(vlanid),
				parseISO8601Date(null),
				parseISO8601Date(endTime));
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
		return (s == null) ? new DateTime() : ISODateTimeFormat.dateTimeNoMillis().parseDateTime(s);
	}
}
