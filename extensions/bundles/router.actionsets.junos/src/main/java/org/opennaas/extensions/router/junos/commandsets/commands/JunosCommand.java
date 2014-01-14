package org.opennaas.extensions.router.junos.commandsets.commands;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Vector;

import net.i2cat.netconf.rpc.Error;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.command.Command;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.helpers.XmlHelper;

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

			String sentMessage = getQuery().toXML();
			try {
				sentMessage = XmlHelper.formatXML(sentMessage);
			} catch (Exception e) {
				// ignored
			}
			return Response.errorResponse(sentMessage, errors);
		}
		Reply reply = (Reply) resp;
		// extra control, it checks if is not null the error list
		if (reply.isOk() || reply.getErrors() == null
				|| reply.getErrors().size() == 0) {

			// BUILD OK RESPONSE
			String sentMessage = getQuery().toXML();
			try {
				sentMessage = XmlHelper.formatXML(sentMessage);
			} catch (Exception e) {
				// ignored
			}
			Response response = Response.okResponse(sentMessage);
			response.setCommandName(this.getClass().getSimpleName());

			response.setInformation(reply.getContain());
			return response;
		} else {
			// BUILD ERROR MESSAGE
			Vector<String> errors = new Vector<String>();
			for (Error error : reply.getErrors())
				errors.add(error.getMessage() + " : " + error.getInfo());

			String sentMessage = getQuery().toXML();
			try {
				sentMessage = XmlHelper.formatXML(sentMessage);
			} catch (Exception e) {
				// ignored
			}
			return Response.errorResponse(sentMessage, errors);
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
