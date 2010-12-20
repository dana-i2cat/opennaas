import java.net.URI;

import net.i2cat.mantychore.commandsets.junos.commands.RefreshCommand;
import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.resources.core.message.CapabilityMessage;
import com.iaasframework.resources.core.message.ICapabilityMessage;

public class SubInterfaceCommandTest {
	Logger	log	= LoggerFactory
						.getLogger(SubInterfaceMockCommand.class);

	public class SubInterfaceMockCommand extends RefreshCommand {
		SessionContext	sessionContext;
		NetconfSession	session;

		SubInterfaceMockCommand() {
			try {
				sessionContext = new SessionContext();
			} catch (ConfigurationException e) {
				log.error(e.getMessage());

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
				log.error(e.getMessage());
			}

		}

	}

	@Test
	public void subInterfaceTest() {

	}
}
