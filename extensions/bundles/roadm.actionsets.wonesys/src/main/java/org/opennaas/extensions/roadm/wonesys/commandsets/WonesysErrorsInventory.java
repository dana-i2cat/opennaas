package org.opennaas.extensions.roadm.wonesys.commandsets;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Actionset
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

import java.util.HashMap;
import java.util.Map;

public class WonesysErrorsInventory {

	/**
	 * Error storage commandID, errorDescription
	 */
	private static final Map<String, String>	WONESYS_ERRORS;

	static {
		Map<String, String> tmp_errors = new HashMap<String, String>();

		tmp_errors.put("0c00", "Undefined");

		tmp_errors.put("0c11", "Received Frame is incomplete");
		tmp_errors.put("0c12", "Checksum ");
		tmp_errors.put("0c13", "Header ");
		tmp_errors.put("0c14", "Wrong module ID");

		tmp_errors.put("0c21", "Some parameter is bad");
		tmp_errors.put("0c22", "operation unavailable");
		tmp_errors.put("0c23", "the module doesn't match with the slot and chasis defined");
		tmp_errors.put("0c24", "unknown chassis slot");

		tmp_errors.put("0c31", "Communication with hardware");
		tmp_errors.put("0c32", "hardware doesn't accepts the value");
		tmp_errors.put("0c33", "Node is locked");
		tmp_errors.put("0c34", "Node is not locked");

		WONESYS_ERRORS = tmp_errors;
	}

	public static boolean isError(String commandId) {
		return WONESYS_ERRORS.get(commandId) != null;
	}

	public static String getErrorDescription(String commandId) {
		return WONESYS_ERRORS.get(commandId);
	}

}
