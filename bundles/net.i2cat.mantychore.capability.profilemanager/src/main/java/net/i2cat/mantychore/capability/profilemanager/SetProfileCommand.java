package net.i2cat.mantychore.capability.profilemanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.i2cat.mantychore.commandsets.junos.commands.JunosCommand;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

public class SetProfileCommand extends JunosCommand {

	private final String	target				= "candidate";
	private final String	defaultOperation	= null;
	private final String	testOption			= null;
	private final String	errorOption			= null;
	private String			xmlResponse			= "";

	public SetProfileCommand(String nameCommand, String pathTemplate) {
		super(nameCommand, pathTemplate);
	}

	@Override
	public Object sendQuery() {
		return QueryFactory.newEditConfig(target, defaultOperation, testOption,
				errorOption, netconfXML);
	}

	@Override
	public void parseResponse(Object response, Object model) throws CommandException {

		// It is not parsed nothing...
		Reply rpcReply = (Reply) response;
		xmlResponse = rpcReply.toXML();
	}

	public static SetProfileCommand createProfileCommandFromXML(String nameCommand, String strTemplate) throws IOException {
		String pathTemplate = createTemplate(nameCommand, strTemplate);
		SetProfileCommand profileCommand = new SetProfileCommand(nameCommand, pathTemplate);
		return profileCommand;
	}

	private static String createTemplate(String nameCommand, String strTemplate) throws IOException {

		// Create temp file.
		File temp = File.createTempFile(nameCommand, ".vm");

		// Delete temp file when program exits.
		temp.deleteOnExit();

		// Write to temp file
		BufferedWriter out = new BufferedWriter(new FileWriter(temp), strTemplate.length());
		out.write(strTemplate);
		out.close();

		return temp.getAbsolutePath();

	}

	public String getXmlResponse() {
		return xmlResponse;
	}

	public void setXmlResponse(String xmlResponse) {
		this.xmlResponse = xmlResponse;
	}

}
