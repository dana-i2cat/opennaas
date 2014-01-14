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

import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants.TargetConfiguration;

public class GetNetconfCommand extends JunosCommand {

	private static final TargetConfiguration	DEFAULT_SOURCE	= TargetConfiguration.RUNNING;
	private TargetConfiguration					source			= DEFAULT_SOURCE;
	private String								attrFilter		= null;

	public GetNetconfCommand(String netconfXML) {
		super(CommandNetconfConstants.GET, netconfXML);
	}

	public GetNetconfCommand(String netconfXML, TargetConfiguration source) {
		super(CommandNetconfConstants.GET, netconfXML);
		this.source = source;
	}

	@Override
	public Query message() {
		// setQuery(QueryFactory.newGetInterfaceInformation());
		setQuery(QueryFactory.newGetConfig(source.toString(), netconfXML, attrFilter));
		return query;
	}

}
