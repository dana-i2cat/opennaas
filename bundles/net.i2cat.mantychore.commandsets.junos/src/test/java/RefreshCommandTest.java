import java.net.URI;
import java.util.List;

import net.i2cat.mantychore.commandsets.junos.commands.RefreshCommand;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.ManagedSystemElement;
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
				sessionContext.setURI(new URI("mock://foo:bar@foo:22/foo"));

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
				log.info(ethernet.getOtherPortType());
				for (ProtocolEndpoint protocolEndpoint : ethernet.getProtocolEndpoints()) {
					if (protocolEndpoint instanceof IPProtocolEndpoint) {
						IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) protocolEndpoint;
						log.info("ipv4: " + ipProtocolEndpoint.getIPv4Address());
						log.info("ipv6: " + ipProtocolEndpoint.getIPv6Address());

					}

				}
				List<VLANEndpoint> listVLANs = ethernet.getVLANEndpoints();
				for (VLANEndpoint vlanEndpoint : listVLANs) {
					log.info("vlanID: " + vlanEndpoint.getVlanID());

				}

			}

		} catch (CommandException e) {
			Assert.fail(e.getMessage());
		}
		log.info("Finished!!!");

	}

}
