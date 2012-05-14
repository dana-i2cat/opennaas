/**
 *  This code is part of the Harmony System implemented in Work Package 1 
 *  of the Phosphorus project. This work is supported by the European 
 *  Comission under the Sixth Framework Programme with contract number 
 *  IST-034115.
 *
 *  Copyright (C) 2006-2009 Phosphorus WP1 partners. Phosphorus Consortium.
 *  http://ist-phosphorus.eu/
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.opennaas.core.resources.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Collection Class for small helping methods.
 *
 * @author gassen
 */
/**
 * @author stephan
 */
public final class Helpers {

	private static TimeZone defaultTimeZone = null;

	protected static Random random = null;

	/**
	 * Function to create a simple xml timestamp.
	 * 
	 * @return Current date
	 */
	public static XMLGregorianCalendar generateXMLCalendar() {
		return generateXMLCalendar(0, 0);
	}

	/**
	 * @param rollMin
	 *            minutes
	 * @param rollSeconds
	 *            seconds
	 * @return Current date + minutes + seconds
	 */
	public static XMLGregorianCalendar generateXMLCalendar(final int rollMin,
			final int rollSeconds) {
		GregorianCalendar calendar;
		calendar = new GregorianCalendar(getDefaultTimeZone());
		if (rollSeconds != 0) {
			calendar.add(Calendar.SECOND, rollSeconds);
		}
		if (rollMin != 0) {
			calendar.add(Calendar.MINUTE, rollMin);
		}

		try {
			DatatypeFactory factory = DatatypeFactory.newInstance();
			return factory.newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			return null;
		}
	}

	public static XMLGregorianCalendar rollXMLCalendar(
			XMLGregorianCalendar cal, final int rollMin, final int rollSeconds) {
		GregorianCalendar auxCal = cal.toGregorianCalendar();
		if (rollSeconds != 0) {
			auxCal.add(Calendar.SECOND, rollSeconds);
		}
		if (rollMin != 0) {
			auxCal.add(Calendar.MINUTE, rollMin);
		}

		try {
			DatatypeFactory factory = DatatypeFactory.newInstance();
			return factory.newXMLGregorianCalendar(auxCal);
		} catch (DatatypeConfigurationException e) {
			return null;
		}

	}

	public static int getRandomInt() {
		if (Helpers.random == null)
			Helpers.random = new Random();
		return Helpers.random.nextInt();
	}

	public static int getPositiveRandomInt() {
		if (Helpers.random == null)
			Helpers.random = new Random();
		return Math.abs(Helpers.random.nextInt());
	}

	public static long getRandomLong() {
		if (Helpers.random == null)
			Helpers.random = new Random();
		return Helpers.random.nextLong();
	}

	public static long getPositiveRandomLong() {
		if (Helpers.random == null)
			Helpers.random = new Random();
		return Math.abs(Helpers.random.nextLong());
	}

	/**
	 * Match a IPv4 Address against a Prefix.
	 * 
	 * @param ip
	 *            IPv4 Address
	 * @param prefix
	 *            IPv4 Prefix
	 * @return True if IP matches Prefix
	 */
	public static final boolean prefixMatch(final String ip, final String prefix) {
		final String[] prefixParts = prefix.split("\\/");
		final int length = Integer.parseInt(prefixParts[1]);

		final int mask = (Integer.MAX_VALUE | Integer.MIN_VALUE) << (32 - length);

		final String[] parts1 = ip.split("\\.");
		final String[] parts2 = prefixParts[0].split("\\.");

		int intIp1 = 0;
		int intIp2 = 0;

		for (int x = 0; x < parts1.length; x++) {
			intIp1 |= Integer.valueOf(parts1[x]) << (3 - x) * 8;
			intIp2 |= Integer.valueOf(parts2[x]) << (3 - x) * 8;
		}

		intIp1 &= mask;
		intIp2 &= mask;

		return intIp1 == intIp2;
	}

	/**
	 * Default Constructor.
	 */
	private Helpers() {
		// Utility class should not be instantiated
	}

	protected static TimeZone getDefaultTimeZone() {
		if (defaultTimeZone == null) {
			defaultTimeZone = TimeZone.getDefault();
		}
		return defaultTimeZone;
	}

	/**
	 * @return
	 */
	public static String getRandomString() {
		return "RandomString" + getRandomLong();
	}

	/**
	 * @param cal
	 */
	public static Date xmlCalendarToDate(XMLGregorianCalendar cal) {
		return xmlCalendarToCalendar(cal).getTime();
	}

	/**
	 * @param cal
	 */
	public static Calendar xmlCalendarToCalendar(XMLGregorianCalendar cal) {
		return cal.toGregorianCalendar();
	}

	/**
	 * @param date
	 */
	public static XMLGregorianCalendar DateToXmlCalendar(Date date) {
		GregorianCalendar calendar;
		calendar = new GregorianCalendar(getDefaultTimeZone());
		calendar.setTime(date);

		try {
			DatatypeFactory factory = DatatypeFactory.newInstance();
			return factory.newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			return null;
		}

	}

	public static long trimDateToSeconds(Date date) {
		return new Double(Math.floor(date.getTime() / 1000)).longValue();
	}
}
