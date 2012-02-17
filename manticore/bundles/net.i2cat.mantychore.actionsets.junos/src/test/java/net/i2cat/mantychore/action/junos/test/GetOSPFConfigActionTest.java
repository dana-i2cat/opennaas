package net.i2cat.mantychore.action.junos.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

public class GetOSPFConfigActionTest {

	@Test
	public void testsParseOSPFConfigTwice() throws IOException, ActionException {
		String netconfReply = readStringFromFile("/parsers/getConfigWithOSPF.xml");
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
	public void testsParseRemoveOSPFCorreclty() throws IOException, ActionException {
		String netconfReply1 = readStringFromFile("/parsers/getConfigWithOSPF.xml");
		String netconfReply2 = readStringFromFile("/parsers/getConfigWithoutOSPF.xml");

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

	/**
	 * Simple parser. It was used for proves with xml files
	 * 
	 * @param stream
	 * @return
	 */
	private String readStringFromFile(String pathFile) throws IOException {
		String answer = null;
		InputStream inputFile = getClass().getResourceAsStream(pathFile);
		InputStreamReader streamReader = new InputStreamReader(inputFile);
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(streamReader);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		answer = fileData.toString();

		return answer;
	}

}
