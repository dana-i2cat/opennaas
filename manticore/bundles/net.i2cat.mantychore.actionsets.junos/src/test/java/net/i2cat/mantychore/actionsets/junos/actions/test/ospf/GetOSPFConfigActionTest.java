package net.i2cat.mantychore.actionsets.junos.actions.test.ospf;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import net.i2cat.mantychore.actionsets.junos.actions.ospf.GetOSPFConfigAction;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.OSPFProtocolEndpoint;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.System;
import net.i2cat.mantychore.model.utils.ModelHelper;
import net.i2cat.netconf.rpc.Reply;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;

import com.google.common.io.Files;

public class GetOSPFConfigActionTest {

	@Test
	public void testsParseOSPFConfigTwice() throws IOException, ActionException, URISyntaxException {

		String configFilePath = "/parsers/getConfigWithOSPF.xml";
		URL configFileURL = getClass().getResource(configFilePath);
		String netconfReply = Files.toString(new File(configFileURL.toURI()), Charset.forName("UTF-8"));

		Reply rpcReply = new Reply();
		rpcReply.setContain(netconfReply);

		System routerModel = new ComputerSystem();

		GetOSPFConfigAction action = new GetOSPFConfigAction();
		action.setModelToUpdate(routerModel);
		action.parseResponse(rpcReply, routerModel);

		GetOSPFConfigAction action2 = new GetOSPFConfigAction();
		action2.setModelToUpdate(routerModel);
		action2.parseResponse(rpcReply, routerModel);

		Assert.assertTrue("Router must have only one OPSFService", routerModel.getHostedService().size() == 1);

		// check interfaces has at most one OSPFProtocolEndpoint
		for (NetworkPort port : ModelHelper.getInterfaces(routerModel)) {
			int ospfPEPCount = 0;
			for (ProtocolEndpoint pep : port.getProtocolEndpoint()) {
				if (pep instanceof OSPFProtocolEndpoint)
					ospfPEPCount++;
			}
			Assert.assertTrue("A NetworkPort must have at most one OSPFProtocolEndpoint", ospfPEPCount < 2);
		}
	}

	@Test
	public void testsParseRemoveOSPFCorreclty() throws IOException, ActionException, URISyntaxException {

		String netconfReply1 = Files.toString(new File(getClass().getResource("/parsers/getConfigWithOSPF.xml").toURI()),
				Charset.forName("UTF-8"));
		String netconfReply2 = Files.toString(new File(getClass().getResource("/parsers/getConfigWithoutOSPF.xml").toURI()),
				Charset.forName("UTF-8"));

		Reply rpcReply1 = new Reply();
		rpcReply1.setContain(netconfReply1);

		Reply rpcReply2 = new Reply();
		rpcReply2.setContain(netconfReply2);

		System routerModel = new ComputerSystem();

		GetOSPFConfigAction action = new GetOSPFConfigAction();
		action.setModelToUpdate(routerModel);
		action.parseResponse(rpcReply1, routerModel);

		GetOSPFConfigAction action2 = new GetOSPFConfigAction();
		action2.setModelToUpdate(routerModel);
		action2.parseResponse(rpcReply2, routerModel);

		Assert.assertTrue("Router must have no OPSFService", routerModel.getHostedService().isEmpty());
		for (NetworkPort port : ModelHelper.getInterfaces(routerModel)) {
			for (ProtocolEndpoint pep : port.getProtocolEndpoint()) {
				if (pep instanceof OSPFProtocolEndpoint)
					Assert.fail("A NetworkPort must have no OSPFProtocolEndpoint");
			}
		}
	}

}
