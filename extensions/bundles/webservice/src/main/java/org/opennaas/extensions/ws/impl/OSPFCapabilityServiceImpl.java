package org.opennaas.extensions.ws.impl;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.capability.ospf.IOSPFCapability;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.ws.services.IOSPFCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService(portName = "OSPFCapabilityPort", serviceName = "OSPFCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class OSPFCapabilityServiceImpl extends GenericCapabilityService implements IOSPFCapabilityService {

	Log	log	= LogFactory.getLog(OSPFCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#activateOSPF(java.lang.String)
	 */
	@Override
	public void activateOSPF(String resourceId) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.activateOSPF();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#deactivateOSPF(java.lang.String)
	 */
	@Override
	public void deactivateOSPF(String resourceId) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.deactivateOSPF();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#configureOSPF(java.lang.String,
	 * org.opennaas.extensions.router.model.OSPFService)
	 */
	@Override
	public void configureOSPF(String resourceId, OSPFService ospfService) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.configureOSPF(ospfService);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#clearOSPFconfiguration(java.lang.String,
	 * org.opennaas.extensions.router.model.OSPFService)
	 */
	@Override
	public void clearOSPFconfiguration(String resourceId, OSPFService ospfService) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.clearOSPFconfiguration(ospfService);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#configureOSPFArea(java.lang.String,
	 * org.opennaas.extensions.router.model.OSPFAreaConfiguration)
	 */
	@Override
	public void configureOSPFArea(String resourceId, OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.configureOSPFArea(ospfAreaConfiguration);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#removeOSPFArea(java.lang.String,
	 * org.opennaas.extensions.router.model.OSPFAreaConfiguration)
	 */
	@Override
	public void removeOSPFArea(String resourceId, OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.removeOSPFArea(ospfAreaConfiguration);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#addInterfacesInOSPFArea(java.lang.String, java.util.List,
	 * org.opennaas.extensions.router.model.OSPFArea)
	 */
	@Override
	public void addInterfacesInOSPFArea(String resourceId, List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.addInterfacesInOSPFArea(interfaces, ospfArea);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#removeInterfacesInOSPFArea(java.lang.String, java.util.List,
	 * org.opennaas.extensions.router.model.OSPFArea)
	 */
	@Override
	public void removeInterfacesInOSPFArea(String resourceId, List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.removeInterfacesInOSPFArea(interfaces, ospfArea);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#enableOSPFInterfaces(java.lang.String, java.util.List)
	 */
	@Override
	public void enableOSPFInterfaces(String resourceId, List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.enableOSPFInterfaces(interfaces);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#disableOSPFInterfaces(java.lang.String, java.util.List)
	 */
	@Override
	public void disableOSPFInterfaces(String resourceId, List<OSPFProtocolEndpoint> interfaces) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.disableOSPFInterfaces(interfaces);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#getOSPFConfiguration(java.lang.String)
	 */
	@Override
	public void getOSPFConfiguration(String resourceId) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.getOSPFConfiguration();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IOSPFCapabilityService#showOSPFConfiguration(java.lang.String)
	 */
	@Override
	public OSPFService showOSPFConfiguration(String resourceId) throws CapabilityException {
		try {
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			return iOSPFCapability.showOSPFConfiguration();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
