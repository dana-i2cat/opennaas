package org.opennaas.extensions.protocols.tl1.message;

/*
 * #%L
 * OpenNaaS :: Protocol :: TL-1
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

/**
 * This is an object representation of a TL1 Response Message.
 * 
 * @author Mathieu Lemay
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */

public class TL1ResponseMsg extends TL1OutputMsg implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7533895049035806763L;

	/** Command Tag */
	private String				ctag;

	/** Command CODE */
	private String				cmdCode;

	/** Creates a new instance of TL1ResponseMsg */
	public TL1ResponseMsg() {
	}

	/**
	 * Returns the Completion Code
	 * 
	 * @return Completion code
	 */
	public String getCompletionCode() {
		return cmdCode;
	}

	/**
	 * Returns the associated CTAG
	 * 
	 * @return Command TAG
	 */
	public String getCTAG() {
		return ctag;
	}

	/**
	 * Sets the CTAG (used for parsing)
	 * 
	 * @param localctag
	 *            Command TAG
	 */
	public void setCTAG(String localctag) {
		ctag = localctag;
	}

	/**
	 * Sets the Command Code (used for parsing)
	 * 
	 * @param localCmdCode
	 *            Command TAG
	 */
	public void setCmdCode(String localCmdCode) {
		cmdCode = localCmdCode;
	}

	/**
	 * Appends the message payload of the given Tl1ResponseMsg to the message payload of this TL1ResponseMsg. The term code of the existing
	 * TL1ResponseMsg will be removed and the termcode of the msg being appended will be kept at the end of the payload.
	 * 
	 * @param msg
	 *            TL1ResponseMsg to append to the back of the payload of this message.
	 */
	public void append(TL1ResponseMsg msg) {
		TL1Line[] oldlines = getPayload();
		TL1Line[] newlines = msg.getPayload();
		// allways subtract one from the length of oldlines to remove the term
		// code.
		TL1Line[] appending = new TL1Line[oldlines.length - 1 + newlines.length];

		for (int j = 0; j < oldlines.length - 1; j++) {
			appending[j] = oldlines[j];
		}
		for (int j = 0; j < newlines.length; j++) {
			appending[oldlines.length - 1 + j] = newlines[j];
		}
		setPayload(appending);
	}
}