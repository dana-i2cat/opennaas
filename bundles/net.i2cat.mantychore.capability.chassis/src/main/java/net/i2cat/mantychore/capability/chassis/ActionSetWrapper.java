package net.i2cat.mantychore.capability.chassis;


import net.i2cat.nexus.protocols.sessionmanager.IProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.resources.RegistryUtil;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionSetWrapper {

	private Logger			logger	= LoggerFactory.getLogger(QueueManagerWrapper.class);

	IProtocolSessionManager	protocolSessionManager;

//	public IActionSetService getActionSet(String resourceId) {
//
//		IActionSetService actionSet = getOSGiActionSet();
//
//		return actionSet;
//
//	}
//
//
//	//TODO We have to create a new ActionSet 
//	private IActionSetService getOSGiActionSet(String deviceID) {
//		BundleContext bundleContext = Activator.getContext();
//
//		logger.info("getting service: " + IProtocolManager.class.getName());
//		IActionSetService actionSet = null;
//		try {
//			Filter properties = buildActionProperties(deviceID);
//			//TODO DON'T CALL QUEUEMANAGER
//			RegistryUtil.getServiceFromRegistry(bundleContext,properties);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		return actionSet;
//	}
//
//
//	private Filter buildActionProperties(String deviceID) {
//		
//		//TODO  TO FILL
//		return null;
//	}
	

}
