package net.i2cat.mantychore.commandsets.junos.commands;

import java.util.Vector;

import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.Reply;
import net.i2cat.nexus.resources.command.Command;
import net.i2cat.nexus.resources.command.CommandException;
import net.i2cat.nexus.resources.command.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class JunosCommand extends Command {

	/** logger **/
	Log				log	= LogFactory.getLog(JunosCommand.class);
	protected Query	query;

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	protected String	netconfXML;
	protected String	xmlResponse	= "";

	protected JunosCommand(String commandID, String netconfXML) {
		this.setCommandId(commandID);
		this.netconfXML = netconfXML;
	}

	@Override
	public void initialize() throws CommandException {
	}

	public Response checkResponse(Object resp) {
		// Check if is it a wellformed reply message
		if (!(resp instanceof Reply)) {
			Vector<String> errors = new Vector<String>();
			errors.add("The response message is badformed. It is not a reply message");
			return Response.errorResponse(getQuery().toXML(), errors);
		}
		Reply reply = (Reply) resp;
		// extra control, it checks if is not null the error list
		if (reply.isOk() || reply.getErrors() == null
				|| reply.getErrors().size() == 0) {
			// BUILD OK RESPONSE
			Response response = Response.okResponse(getQuery().toXML());
			response.setCommandName(this.getClass().toString());
			response.setInformation(reply.getContain());
			return response;
		} else {
			// BUILD ERROR MESSAGE
			Vector<String> errors = new Vector<String>();
			for (Error error : reply.getErrors())
				errors.add(error.getMessage() + " : " + error.getInfo());
			return Response.errorResponse(getQuery().toXML(), errors);
		}
	}

	public String getXmlResponse() {
		return xmlResponse;
	}

	public void setXmlResponse(String xmlResponse) {
		this.xmlResponse = xmlResponse;
	}

	public abstract Query message();

}
