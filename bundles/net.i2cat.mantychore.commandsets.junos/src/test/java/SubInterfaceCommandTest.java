import net.i2cat.mantychore.commandsets.junos.commands.CreateSubInterfaceCommand;
import net.i2cat.mantychore.commandsets.junos.commands.DeleteSubInterfaceCommand;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.resources.core.message.ICapabilityMessage;

public class SubInterfaceCommandTest {
	Logger	log	= LoggerFactory
						.getLogger(CreateSubInterfaceMock.class);

	public class CreateSubInterfaceMock extends CreateSubInterfaceCommand {

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
				response.setMessage(new DummyNetconfConnector("mock://foo:bar@foo:22/foo").sendAndReceive(command));
			} catch (Exception e) {
				log.error(e.getMessage());
			}

		}

	}

	public class DeleteSubInterfaceMock extends DeleteSubInterfaceCommand {

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
				response.setMessage(new DummyNetconfConnector("mock://foo:bar@foo:22/foo").sendAndReceive(command));
			} catch (Exception e) {
				log.error(e.getMessage());
			}

		}

	}

	@Test
	public void subInterfaceTest() {
		try {
			CreateSubInterfaceMock createSubInterface = new CreateSubInterfaceMock();
			createSubInterface.executeCommand();

			DeleteSubInterfaceCommand deleteSubInterface = new DeleteSubInterfaceCommand();
			deleteSubInterface.executeCommand();

		} catch (CommandException e) {
			log.error(e.getMessage());
		}

	}
}
