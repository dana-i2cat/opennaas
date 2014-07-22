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
import java.util.StringTokenizer;

/**
 * This objet stores the Time Information contained in the TL1 Autonomous and Response Messages' header.
 * 
 * @author Mathieu Lemay
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1Time implements Serializable {

	private static final long	serialVersionUID	= 5225689273012367541L;
	/** Time information for TL1 Command */
	private int					hour;
	/** Time information for TL1 Command */
	private int					minutes;
	/** Time information for TL1 Command */
	private int					seconds;

	/**
	 * Creates a new instance of TL1Time
	 * 
	 * @param date
	 *            Time in raw string format
	 */
	public TL1Time(String date) {
		StringTokenizer parser = new StringTokenizer(date, ":");
		hour = (Integer.parseInt(parser.nextToken()));
		minutes = (Integer.parseInt(parser.nextToken()));
		seconds = (Integer.parseInt(parser.nextToken()));
	}

	/**
	 * Returns the hour in the message header
	 * 
	 * @return Hour
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Returns the minutes in the message header
	 * 
	 * @return Minutes
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * Returns the seconds in the message header
	 * 
	 * @return Seconds
	 */
	public int getSeconds() {
		return seconds;
	}
}
