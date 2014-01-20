package org.opennaas.extensions.macbridge.ios.resource.commandsets.commands;

/*
 * #%L
 * OpenNaaS :: MAC Bridge :: IOS Resource
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

import org.opennaas.core.resources.command.CommandException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;

public class TerminalLengthCommand extends IOSCommand {

	int	length	= 0;

	public TerminalLengthCommand() {
	}

	public TerminalLengthCommand(int length) {
		this.length = length;
	}

	@Override
	public void initialize() throws CommandException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCommand() {
		return "terminal length " + length;
	}

	@Override
	public void updateModel(CLIResponseMessage responseMessage, MACBridge model) {
	}
}
