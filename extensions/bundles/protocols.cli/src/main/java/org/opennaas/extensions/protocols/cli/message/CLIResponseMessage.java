package org.opennaas.extensions.protocols.cli.message;

/*
 * #%L
 * OpenNaaS :: Protocol :: CLI
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a CLI message
 * 
 * @author edu
 * 
 */
public class CLIResponseMessage implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/** Logger */
	static private Logger		logger				= LoggerFactory.getLogger(CLIResponseMessage.class);

	/** The raw message before processing it **/
	private String				rawMessage			= null;

	/** The processed message (without prompts) **/
	private String				processedMessage	= null;

	/** Tells if the response is an error **/
	private boolean				hasErrors			= false;

	/**
	 * Make the constructor private, so that it cannot be instantiated
	 */
	private CLIResponseMessage() {
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public void setRawMessage(String rawMessage) {
		logger.debug("Received message: " + rawMessage);
		this.rawMessage = rawMessage;
	}

	/**
	 * Parses the respone message (removes the echos, prompts, look for errors)
	 * 
	 * @param rawMessage
	 * @param stringsToRemove
	 * @param errorPatterns
	 * @return
	 */
	public static CLIResponseMessage parse(String rawMessage, List<String> stringsToRemove,
			List<String> errorPatterns) {
		String processedMessage = rawMessage;
		CLIResponseMessage response = new CLIResponseMessage();

		// Remove prompts and echoes
		for (int i = 0; i < stringsToRemove.size(); i++) {
			if (processedMessage.indexOf(stringsToRemove.get(i)) != -1) {
				processedMessage = processedMessage.replaceAll(stringsToRemove.get(i), "");
			}
		}

		// Look for errors
		for (int i = 0; i < errorPatterns.size(); i++) {
			if (processedMessage.indexOf(errorPatterns.get(i)) != -1) {
				response.hasErrors = true;
			}
		}

		response.setRawMessage(rawMessage);
		response.processedMessage = processedMessage;

		return response;
	}

	public String getProcessedMessage() {
		return processedMessage;
	}

	public boolean hasErrors() {
		return hasErrors;
	}

	/**
	 * If the response has a table format, this operation returns a list of strings for each row of the table. Each list of strings contains the
	 * column string values.
	 * 
	 * @param columnSeparator
	 *            The string used to separate columns
	 * @return
	 */
	public List<List<String>> getTableResult(String columnSeparator) {
		List<List<String>> result = new ArrayList<List<String>>();
		List<String> parsedRow = null;
		String row = null;
		StringTokenizer columnTokenizer = null;

		StringTokenizer rowTokenizer = new StringTokenizer(processedMessage, "\n");
		while (rowTokenizer.hasMoreTokens()) {
			row = rowTokenizer.nextToken();
			parsedRow = new ArrayList<String>();
			columnTokenizer = new StringTokenizer(row, columnSeparator);
			while (columnTokenizer.hasMoreTokens()) {
				parsedRow.add(columnTokenizer.nextToken());
			}
			result.add(parsedRow);
		}

		return result;
	}
}