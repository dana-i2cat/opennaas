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


/**
 * Thrown exception when a parsing error occurs.
 * 
 * @author Mathieu Lemay
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1ParserException extends Exception {

	/**
     * 
     */
	private static final long	serialVersionUID	= 1L;

	/** Creates a new instance of TL1ParserException */
	public TL1ParserException() {
		super();
	}

	public TL1ParserException(String s)
	{
		super(s);
	}
}
