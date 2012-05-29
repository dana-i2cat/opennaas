package org.opennaas.web.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.ws.services.IChassisCapabilityService;
import org.opennaas.extensions.ws.services.IGRETunnelCapabilityService;
import org.opennaas.extensions.ws.services.IL2BoDCapabilityService;
import org.opennaas.extensions.ws.services.INetOSPFCapabilityService;
import org.opennaas.extensions.ws.services.INetQueueCapabilityService;
import org.opennaas.extensions.ws.services.IQueueManagerCapabilityService;
import org.opennaas.extensions.ws.services.IResourceManagerService;
import org.opennaas.web.ws.OpennaasClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class DestroyAction extends ActionSupport implements SessionAware {

	private static final Logger				LOGGER	= Logger.getLogger(DestroyAction.class);
	private Map<String, Object>				session;
	private IResourceManagerService			resourceManagerService;
	private INetQueueCapabilityService		queueService;
	private IChassisCapabilityService		chassisCapab;
	private IQueueManagerCapabilityService	queueCapab;
	private IGRETunnelCapabilityService		greCapab;
	private IL2BoDCapabilityService			l2BoDCapabilityService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * Destroy
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		deactivateOSPF();
		if (getText("autobahn.enabled").equals("true"))
			shutDownAutobahn();
		removeGRE();
		removeLR();
		removeResources();
		removeSession();
		return SUCCESS;
	}

	/**
	 * 
	 */
	private void removeSession() {
		session.clear();
	}

	/**
	 * @throws CapabilityException_Exception
	 */
	private void deactivateOSPF() {
		queueService = OpennaasClient.getNetQueueCapabilityService();
		try {
			if (session.get(getText("network.name")) != null) {
				String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();
				INetOSPFCapabilityService capabilityService = OpennaasClient.getNetOSPFCapabilityService();
				capabilityService.deactivateOSPF(networkId);

				queueService.execute(networkId);
			}
		} catch (Exception e) {
			LOGGER.error("Can't deactivate ospf.");
		}

	}

	/**
	 * @throws ResourceException_Exception
	 * @throws CapabilityException_Exception
	 * @throws ActionException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void removeLR() {
		try {
			chassisCapab = OpennaasClient.getChassisCapabilityService();
			resourceManagerService = OpennaasClient.getResourceManagerService();
			queueCapab = OpennaasClient.getQueueManagerCapabilityService();

			ResourceIdentifier lrUnic = (ResourceIdentifier) session.get(getText("unic.lrouter.name"));
			ResourceIdentifier lrGSN = (ResourceIdentifier) session.get(getText("myre.lrouter.name"));
			ResourceIdentifier lrMyre = (ResourceIdentifier) session.get(getText("gsn.lrouter.name"));

			try {
				if (lrUnic != null) {
					resourceManagerService.stopResource(lrUnic);
					resourceManagerService.removeResourceById(lrUnic.getId());

					if (session.get(getText("unic.router.name")) != null) {
						ComputerSystem router = new ComputerSystem();
						String routerUnicId = ((ResourceIdentifier) session.get(getText("unic.router.name"))).getId();
						router.setName(getText("unic.lrouter.name"));
						chassisCapab.deleteLogicalRouter(routerUnicId, router);
						queueCapab.execute(routerUnicId);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Can't delete " + getText("unic.lrouter.name") + " LR");
			}

			try {
				if (lrMyre != null) {
					resourceManagerService.stopResource(lrMyre);
					resourceManagerService.removeResourceById(lrMyre.getId());

					if (session.get(getText("myre.router.name")) != null) {
						ComputerSystem router = new ComputerSystem();
						String routerMyreId = ((ResourceIdentifier) session.get(getText("myre.router.name"))).getId();
						router.setName(getText("myre.lrouter.name"));
						chassisCapab.deleteLogicalRouter(routerMyreId, router);
						queueCapab.execute(routerMyreId);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Can't delete " + getText("myre.lrouter.name") + " LR");
			}

			try {
				if (lrMyre != null) {
					resourceManagerService.stopResource(lrGSN);
					resourceManagerService.removeResourceById(lrGSN.getId());

					if (session.get(getText("gsn.router.name")) != null) {
						ComputerSystem router = new ComputerSystem();
						String routerGSNId = ((ResourceIdentifier) session.get(getText("gsn.router.name"))).getId();
						router.setName(getText("gsn.lrouter.name"));
						chassisCapab.deleteLogicalRouter(routerGSNId, router);
						queueCapab.execute(routerGSNId);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Can't delete " + getText("gsn.router.name") + " LR");
			}

		} catch (Exception e) {
			LOGGER.error("Can't delete all logical routers.");
		}

	}

	/**
	 * @throws CapabilityException_Exception
	 * @throws ActionException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void removeGRE() {
		try {
			queueCapab = OpennaasClient.getQueueManagerCapabilityService();
			greCapab = OpennaasClient.getGreTunnelCapabilityService();
			if (session.get(getText("myre.lrouter.name")) != null) {
				GRETunnelService greTunnelService = new GRETunnelService();
				String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();

				greTunnelService.setName(getText("myre.iface.gre"));
				greCapab.deleteGRETunnel(lrMyreId, greTunnelService);

				queueCapab.execute(lrMyreId);
			}
		} catch (Exception e) {
			LOGGER.error("Can't delete GRE.");
		}
	}

	/**
	 * Shutdown autobahn connections
	 * 
	 * @throws CapabilityException_Exception
	 */
	private void shutDownAutobahn() {
		try {
			l2BoDCapabilityService = OpennaasClient.getL2BoDCapabilityService();
			if (session.get(getText("autobahn.bod.name")) != null) {
				String autbahnId = ((ResourceIdentifier) session.get(getText("autobahn.bod.name"))).getId();

				try {
					List<String> list1 = new ArrayList<String>();
					list1.add(getText("autobahn.connection1.interface1"));
					list1.add(getText("autobahn.connection1.interface2"));
					l2BoDCapabilityService.shutDownConnection(autbahnId, list1);
				} catch (Exception e) {
					LOGGER.error("Can't shut down autobahn connection 1 - "
							+ getText("autobahn.connection1.interface1") + " - "
							+ getText("autobahn.connection1.interface2"));
				}

				try {
					List<String> list2 = new ArrayList<String>();
					list2.add(getText("autobahn.connection2.interface1"));
					list2.add(getText("autobahn.connection2.interface2"));
					l2BoDCapabilityService.shutDownConnection(autbahnId, list2);
				} catch (Exception e) {
					LOGGER.error("Can't shut down autobahn connection 2 - "
							+ getText("autobahn.connection2.interface1") + " - "
							+ getText("autobahn.connection2.interface2"));
				}

				// try {
				// List<String> list3 = new ArrayList<String>();
				// list3.add(getText("autobahn.connection3.interface1"));
				// list3.add(getText("autobahn.connection3.interface2"));
				// l2BoDCapabilityService.shutDownConnection(autbahnId, list3);
				// } catch (Exception e) {
				// LOGGER.error("Can't shut down autobahn connection 3 - "
				// + getText("autobahn.connection3.interface1") + " - "
				// + getText("autobahn.connection3.interface2"));
				// }
			}
		} catch (Exception e) {
			LOGGER.error("Can't shut down autobahn.");
		}
	}

	/**
	 * @throws CapabilityException_Exception
	 * @throws ResourceException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void removeResources() {
		try {
			resourceManagerService = OpennaasClient.getResourceManagerService();

			try {
				if (session.get(getText("unic.router.name")) != null) {
					ResourceIdentifier routerUnic = (ResourceIdentifier) session.get(getText("unic.router.name"));
					resourceManagerService.stopResource(routerUnic);
					resourceManagerService.removeResourceById(routerUnic.getId());
				}
			} catch (Exception e) {
				LOGGER.error("Can't remove or stop " + getText("unic.router.name"));
			}

			try {
				if (session.get(getText("gsn.router.name")) != null) {
					ResourceIdentifier routerGSN = (ResourceIdentifier) session.get(getText("gsn.router.name"));
					resourceManagerService.stopResource(routerGSN);
					resourceManagerService.removeResourceById(routerGSN.getId());
				}
			} catch (Exception e) {
				LOGGER.error("Can't remove or stop " + getText("gsn.router.name"));
			}

			try {
				if (session.get(getText("myre.router.name")) != null) {
					ResourceIdentifier routerMyre = (ResourceIdentifier) session.get(getText("myre.router.name"));
					resourceManagerService.stopResource(routerMyre);
					resourceManagerService.removeResourceById(routerMyre.getId());
				}
			} catch (Exception e) {
				LOGGER.error("Can't remove or stop " + getText("myre.router.name"));
			}

			try {
				if (session.get(getText("network.name")) != null) {
					ResourceIdentifier networkDemo = (ResourceIdentifier) session.get(getText("network.name"));
					resourceManagerService.stopResource(networkDemo);
					resourceManagerService.removeResourceById(networkDemo.getId());
				}
			} catch (Exception e) {
				LOGGER.error("Can't remove or stop " + getText("network.name"));
			}

			try {
				if (session.get(getText("autobahn.bod.name")) != null) {
					ResourceIdentifier autobahnDemo = (ResourceIdentifier) session.get(getText("autobahn.bod.name"));
					resourceManagerService.stopResource(autobahnDemo);
					resourceManagerService.removeResourceById(autobahnDemo.getId());
				}
			} catch (Exception e) {
				LOGGER.error("Can't remove or stop " + getText("network.name"));
			}

		} catch (Exception e) {
			LOGGER.error("Can't remove resources.");
		}
	}
}
