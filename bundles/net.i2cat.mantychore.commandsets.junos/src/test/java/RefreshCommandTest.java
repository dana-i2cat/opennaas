import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import net.i2cat.mantychore.commandsets.junos.commands.RefreshCommand;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.ManagedSystemElement;
import net.i2cat.mantychore.model.NextHopIPRoute;
import net.i2cat.mantychore.model.NextHopRoute;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.resources.core.message.CapabilityMessage;
import com.iaasframework.resources.core.message.ICapabilityMessage;

public class RefreshCommandTest {
	Logger	log	= LoggerFactory
						.getLogger(RefreshCommandTest.class);

	private void printCLASSPATH() {
		// Get the System Classloader
		ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

		// Get the URLs
		URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();

		for (int i = 0; i < urls.length; i++) {
			log.info(urls[i].getFile());
		}

		final String surefireClassPathPropertyName = "surefire.test.class.path";
		final String javaClassPathPropertyName = "java.class.path";

		final String surefireClassPath = System
						.getProperty(surefireClassPathPropertyName);
		final String javaClassPath = System
						.getProperty(javaClassPathPropertyName);

		log.info(surefireClassPathPropertyName + "="
						+ surefireClassPath);
		log.info(javaClassPathPropertyName + "=" + javaClassPath);

	}

	public class RefreshMockCommand extends RefreshCommand {
		SessionContext	sessionContext;
		NetconfSession	session;

		RefreshMockCommand() {
			try {
				sessionContext = new SessionContext();
			} catch (ConfigurationException e) {
				Assert.fail(e.getMessage());

			}
		}

		@Override
		public void responseReceived(ICapabilityMessage responseMessage) throws CommandException {
			// TODO Auto-generated method stub
			super.responseReceived(responseMessage);
		}

		/**
		 * Library netconf
		 */
		public void sendCommandToProtocol(Object command) {
			try {

				// printCLASSPATH();

				sessionContext.setURI(new URI("mock://foo:boo@testing.default.net:22"));
				// sessionContext.setURI(new URI("ssh://i2cat:mant6WWe@lola.hea.net:22/netconf"));

				session = new NetconfSession(sessionContext);
				session.connect();
				Reply reply = session.sendSyncQuery((Query) command);

				response = new CapabilityMessage();
				response.setMessage(reply.getContain());

				session.disconnect();

			} catch (Exception e) {
				Assert.fail(e.getMessage());
			}

		}

	}

	public RefreshCommandTest() {

	}

	@Test
	public void refreshTest() {
		log.info("Preparing refresh test....");

		try {
			RefreshMockCommand refreshCommand = new RefreshMockCommand();
			refreshCommand.executeCommand();
			ComputerSystem routerModel = new ComputerSystem();

			refreshCommand.parseResponse(routerModel);

			List<ManagedSystemElement> listManagedSystemElems = routerModel.getManagedSystemElements();
			for (ManagedSystemElement elem : listManagedSystemElems) {
				EthernetPort ethernet = (EthernetPort) elem;

				log.info("Eth interface " + ethernet.getOtherPortType());

				for (ProtocolEndpoint protocolEndpoint : ethernet.getProtocolEndpoint()) {
					if (protocolEndpoint instanceof IPProtocolEndpoint) {
						IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint)
								protocolEndpoint;
						log.info("		ipv4: " + ipProtocolEndpoint.getIPv4Address());
						log.info("		mask: " + ipProtocolEndpoint.getSubnetMask());
						log.info("		ipv6: " + ipProtocolEndpoint.getIPv6Address());

					} else if (protocolEndpoint instanceof VLANEndpoint) {
						VLANEndpoint vlanEndpoint = (VLANEndpoint) protocolEndpoint;
						log.info("			vlanID: " + vlanEndpoint.getVlanID());
					}

				}
			}

			List<NextHopRoute> nexthopList = routerModel.getNextHopRoute();
			for (NextHopRoute elem : nexthopList) {
				NextHopIPRoute nexthop = (NextHopIPRoute) elem;
				log.info("---------------->Routing options");
				log.info("Destination address   " + nexthop.getDestinationAddress());
				log.info("Prefix length   " + nexthop.getPrefixLength());

				log.info("Destination Mask   " + nexthop.getDestinationMask());
				log.info("Is static   " + nexthop.isIsStatic());

				ProtocolEndpoint prot = nexthop.getProtocolEndpoint();
				Assert.assertNotNull(prot);
				if (prot instanceof IPProtocolEndpoint) {
					IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) prot;
					log.info("					Next hop   " + ipProtocolEndpoint.getIPv4Address());
				} else {
					log.info("Error in  nexthop.getProtocolEndpoint(): no IPProtocolEndpoint");
				}

			}
		} catch (CommandException e) {
			Assert.fail(e.getMessage());
		}
		log.info("Finished!!!");

	}
}
