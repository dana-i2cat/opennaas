package org.opennaas.extensions.vcpe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.security.acl.IACLManager;
import org.opennaas.extensions.vcpe.manager.IVCPENetworkManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractActivator implements BundleActivator {

	private static BundleContext	context;

	static Log						log	= LogFactory.getLog(Activator.class);

	public static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

	public static IResourceManager getResourceManagerService() throws ActivatorException {
		log.trace("Calling ResourceManagerService");
		return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	}

	public static IProtocolManager getProtocolManagerService() throws ActivatorException {
		log.trace("Calling ProtocolSessionManagerService");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}

	public static IVCPENetworkManager getVCPEManagerService() throws ActivatorException {
		log.trace("Calling VCPEManagerService");
		return (IVCPENetworkManager) getServiceFromRegistry(context, IVCPENetworkManager.class.getName());
	}

	public static IACLManager getACLManagerService() throws ActivatorException {
		log.trace("Calling ACLManagerService");
		return (IACLManager) getServiceFromRegistry(context, IACLManager.class.getName());
	}

}
