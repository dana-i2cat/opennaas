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
			log.info("Start of activateOSPF call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.activateOSPF();
			log.info("End of activateOSPF call");
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
			log.info("Start of deactivateOSPF call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.deactivateOSPF();
			log.info("End of deactivateOSPF call");
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
			log.info("Start of deactivateOSPF call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.configureOSPF(ospfService);
			log.info("End of deactivateOSPF call");
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
			log.info("Start of clearOSPFconfiguration call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.clearOSPFconfiguration(ospfService);
			log.info("End of clearOSPFconfiguration call");
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
			log.info("Start of configureOSPFArea call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.configureOSPFArea(ospfAreaConfiguration);
			log.info("End of configureOSPFArea call");
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
			log.info("Start of removeOSPFArea call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.removeOSPFArea(ospfAreaConfiguration);
			log.info("End of removeOSPFArea call");
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
			log.info("Start of addInterfacesInOSPFArea call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.addInterfacesInOSPFArea(interfaces, ospfArea);
			log.info("End of addInterfacesInOSPFArea call");
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
			log.info("Start of removeInterfacesInOSPFArea call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.removeInterfacesInOSPFArea(interfaces, ospfArea);
			log.info("End of removeInterfacesInOSPFArea call");
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
			log.info("Start of enableOSPFInterfaces call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.enableOSPFInterfaces(interfaces);
			log.info("End of enableOSPFInterfaces call");
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
			log.info("Start of disableOSPFInterfaces call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.disableOSPFInterfaces(interfaces);
			log.info("End of disableOSPFInterfaces call");
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
			log.info("Start of disableOSPFInterfaces call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			iOSPFCapability.getOSPFConfiguration();
			log.info("End of disableOSPFInterfaces call");
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
			log.info("Start of showOSPFConfiguration call");
			IOSPFCapability iOSPFCapability = (IOSPFCapability) getCapability(resourceId, IOSPFCapability.class);
			OSPFService ospfService = iOSPFCapability.showOSPFConfiguration();
			log.info("End of showOSPFConfiguration call");
			return ospfService;
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
