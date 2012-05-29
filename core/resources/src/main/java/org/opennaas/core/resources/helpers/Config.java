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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This class is used to externalize configuration informations in the property
 * files. You can modify all relevant constants within this project by editing
 * the corresponding files.
 * 
 * @author Alexander Willner (mail@alexanderwillner.de)
 * @version $Id: Config.java 128 2006-10-16 20:14:45Z awillner $
 */
public final class Config {

	/** Properties directory. */
	private static final String PROPERTIES_DIR = "";

	/** Local suffix. */
	private static final String LOCAL_SUFFIX = "_local";

	/** Classloader. */
	private static final ClassLoader CLASSLOADER = Config.class
			.getClassLoader();

	/** Cache to speedup the lookup */
	private static final HashMap<String, String> cache = new HashMap<String, String>();

	/**
	 * Gets the int value for a given key.
	 * 
	 * @param propertyFile
	 *            name of the property file
	 * @param key
	 *            name of the string
	 * @return the int value for the key
	 */
	public static Integer getInt(final String propertyFile, final String key) {

		final String result = Config.getString(propertyFile, key);

		return new Integer(result);
	}

	/**
	 * Gets the long value for a given key.
	 * 
	 * @param propertyFile
	 *            name of the property file
	 * @param key
	 *            name of the string
	 * @return the long value for the key
	 */
	public static Long getLong(final String propertyFile, final String key) {

		final String result = Config.getString(propertyFile, key);

		return new Long(result);
	}

	/**
	 * Get the Properties for a given property file.
	 * 
	 * @param propertyFile
	 *            Name of the property file.
	 * @return The Properties object.
	 * @throws IOException
	 *             If the file could not be found or read.
	 */
	public static Properties getProperties(final String propertyFile)
			throws IOException {
		final Properties properties = new Properties();
		final URL resource = Config.CLASSLOADER.getResource(propertyFile
				+ ".properties");

		if (null == resource) {
			throw new FileNotFoundException(propertyFile
					+ " could not be found");
		}

		final InputStream propertyStream = resource.openStream();
		properties.load(propertyStream);
		return properties;
	}

	/**
	 * @param propertyFile
	 * @param key
	 * @return
	 */
	private static String getStringFromBundle(final String propertyFile,
			final String key) {

		final ResourceBundle bundle = ResourceBundle
				.getBundle(Config.PROPERTIES_DIR + propertyFile);

		try {
			return bundle.getString(key).trim();
		} catch (MissingResourceException exception) {
			throw new MissingResourceException("Could not find '" + key
					+ "' in '" + propertyFile + "'. Details: "
					+ exception.getMessage(), exception.getClassName(),
					exception.getKey());
		}
	}

	/**
	 * get the string for a given key, the property-file will be searched in
	 * both: the ressource-path of the main project and the ressource-path of
	 * the lib that called the methode. (Notice: property-file in main project
	 * has to be named: abc_local.properties)
	 * 
	 * @param instance
	 *            class of the caller
	 * @param propertyFile
	 *            name of the property file
	 * @param key
	 *            name of the key
	 * @return the string for the key
	 */
	public static String getString(final String propertyFile, final String key) {
		final String localFileName = propertyFile + Config.LOCAL_SUFFIX;
		String result;

		if (cache.containsKey(propertyFile + key)) {
			return cache.get(propertyFile + key);
		}

		try {
			// We try first the local file
			result = Config.getStringFromBundle(localFileName, key);
		} catch (MissingResourceException e) {
			// Use common file else
			result = Config.getStringFromBundle(propertyFile, key);
		}

		cache.put(propertyFile + key, result);

		return result;
	}

	/**
	 * Get string from propertyfile specified by id.
	 * 
	 * @param id
	 *            file.key
	 * @return
	 */
	public static String getString(final String id) {
		final int seperator = id.indexOf(".");

		final String file = id.substring(0, seperator);
		final String key = id.substring(seperator + 1);

		return Config.getString(file, key);
	}

	/**
	 * Returns a generic URL to a given key which is specified in the according
	 * property file. If you want to open a file with a relative path you should
	 * use this function to avoid problems with different user directories.
	 * 
	 * @param propertyFile
	 *            name of the property file
	 * @param key
	 *            name of the string
	 * @return url to the given key
	 * @throws FileNotFoundException
	 *             If the property file cannot be found.
	 */
	public static URL getURL(final String propertyFile, final String key)
			throws FileNotFoundException {

		final String fileName = Config.getString(propertyFile, key);

		final URL url = Config.CLASSLOADER.getResource(fileName);

		if (null == url) {
			throw new FileNotFoundException("File " + fileName
					+ " can not be found. Please check properties file");
		}

		return url;
	}

	/**
	 * Gets the boolean value for a given id.
	 * 
	 * @param id
	 *            file.key
	 * @return
	 */
	public static boolean isTrue(final String id) {
		final int seperator = id.indexOf(".");

		final String file = id.substring(0, seperator);
		final String key = id.substring(seperator + 1);

		return Config.isTrue(file, key);
	}

	/**
	 * Gets the boolean value for a given key.
	 * 
	 * @param propertyFile
	 *            name of the property file
	 * @param key
	 *            name of the string
	 * @return the boolean value for the key
	 */
	public static boolean isTrue(final String propertyFile, final String key) {

		final String result = Config.getString(propertyFile, key);

		return "true".equalsIgnoreCase(result);
	}

	/**
	 * Utility class should not get instantiated.
	 * 
	 * @throws InstantiationException
	 *             If the class was somehow instantiated.
	 */
	private Config() throws InstantiationException {
		throw new InstantiationException("Do not instantiate a utility class");
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isCached(String key) {
		return cache.containsKey(key);
	}

	/**
     * 
     */
	public static void resetCache() {
		cache.clear();
	}
}
