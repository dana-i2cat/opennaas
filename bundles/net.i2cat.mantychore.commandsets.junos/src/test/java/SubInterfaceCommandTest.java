import java.util.ArrayList;

import net.i2cat.mantychore.commandsets.junos.commands.CreateSubInterfaceCommand;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.NextHopIPRoute;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.resources.core.message.CapabilityMessage;
import com.iaasframework.resources.core.message.ICapabilityMessage;

public class SubInterfaceCommandTest {
	Logger	log	= LoggerFactory
						.getLogger(CreateSubInterfaceMock.class);

	public class CreateSubInterfaceMock extends CreateSubInterfaceCommand {

		@Override
		public void responseReceived(ICapabilityMessage responseMessage) throws
				CommandException {
			// TODO Auto-generated method stub
			super.responseReceived(responseMessage);
		}

		/**
		 * Library netconf
		 **/
		public void sendCommandToProtocol(Object command) {
			try {

				DummyNetconfConnector dummy = new DummyNetconfConnector("ssh://i2cat:mant6WWe@lola.hea.net:22/netconf");
				// DummyNetconfConnector dummy = new DummyNetconfConnector("mock://foo:bar@foo:22/netconf");

				String resp = dummy.sendAndReceive(command);
				log.error("Response " + resp);

				response = new CapabilityMessage();
				response.setMessage(resp);

				Assert.assertNotNull("resp null " + resp);
				Assert.assertNotNull("response null " + response);

			} catch (Exception e) {
				Assert.fail("Error revicing message " + e.getMessage());
				log.error("Error reciving message " + e.getMessage());
			}

		}

	}

	// public class DeleteSubInterfaceMock extends DeleteSubInterfaceCommand {
	//
	// @Override
	// public void responseReceived(ICapabilityMessage responseMessage) throws
	// CommandException {
	// // TODO Auto-generated method stub
	// super.responseReceived(responseMessage);
	// }
	//
	// /**
	// * Library netconf
	// */
	// public void sendCommandToProtocol(Object command) {
	// try {
	// response.setMessage(new
	// DummyNetconfConnector("mock://foo:bar@foo:22/foo").sendAndReceive(command));
	// } catch (Exception e) {
	// log.error(e.getMessage());
	// }
	//
	// }
	//
	// }
	//
	@Test
	public void subInterfaceTest() {
		try {
			CreateSubInterfaceMock createSubInterface = new CreateSubInterfaceMock();
			// create interface
			EthernetPort eth = new EthernetPort();
			eth.setElementName("ge-0/1/0");
			eth.setPortNumber(30);
			IPProtocolEndpoint ip = new IPProtocolEndpoint();
			ip.setIPv4Address("193.1.24.88");
			ip.setSubnetMask("255.255.255.0");
			eth.addProtocolEndpoint(ip);

			// add static route
			NextHopIPRoute nhipr = new NextHopIPRoute();
			String ipv4 = "192.168.2.3/24";
			String shortMask = ipv4.split("/")[1];
			String ipv = ipv4.split("/")[0];
			// adding to the model
			nhipr.setDestinationAddress(ipv);
			nhipr.setPrefixLength(Byte.parseByte(shortMask));
			nhipr.setIsStatic(true);
			IPProtocolEndpoint p = new IPProtocolEndpoint();
			p.setIPv4Address("192.168.55.3");
			nhipr.setProtocolEndpoint(p);

			ArrayList params = new ArrayList();
			params.add(eth);
			// params.add(nhipr);

			createSubInterface.setParams(params);
			createSubInterface.initializeCommand("candidate", null, null, null);
			createSubInterface.executeCommand();

			// DeleteSubInterfaceCommand deleteSubInterface = new
			// DeleteSubInterfaceCommand();
			// deleteSubInterface.executeCommand();

		} catch (CommandException e) {
			log.error(e.getMessage());
		}
		log.info("Finished!!!");
	}
}
