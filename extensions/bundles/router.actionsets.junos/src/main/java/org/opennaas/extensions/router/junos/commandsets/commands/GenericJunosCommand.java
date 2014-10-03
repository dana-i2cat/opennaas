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

import net.i2cat.netconf.rpc.Operation;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.RPCElement;

/**
 * <p>
 * This class builds {@link GenericJunosQuery} to be sent to the Netconf server. The reason to create this {@link JunosCommand} is not to include new
 * Junos capabilitis in the netconf4j library.
 * </p>
 * <p>
 * This class directly embeds the <code>netconfXML</code> inside the "<rpc>" element.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GenericJunosCommand extends JunosCommand {

	class GenericJunosQuery extends Query {

		/**
		 * 
		 */
		private static final long	serialVersionUID	= 3490737598269574892L;

		@Override
		public String toXML() {

			StringBuilder sb = new StringBuilder();
			sb.append("<rpc message-id=\"").append(getMessageId()).append("\">").append(netconfXML).append("</rpc>");

			return sb.toString();
		}

		@Override
		public Operation getOperation() {
			// This is not the real operation, since we override toXML() method.
			return Operation.GET;
		}

		@Override
		public RPCElement getRpcElement() {
			return this;
		}

	}

	public GenericJunosCommand(String commandID, String netconfXML) {
		super(commandID, netconfXML);
	}

	@Override
	public Query message() {

		Query query = new GenericJunosQuery();
		setQuery(query);
		return query;
	}

}
