package org.opennaas.core.resources;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;

/**
 * A utility class that marshall IEngineMessages to and from XML using JAXB
 * 
 * @author Scott Campbell (CRC)
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class ObjectSerializer {

	public static String toXml(Object obj) throws SerializationException {
		StringWriter sw = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller m = context.createMarshaller();

			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(obj, sw);
			return sw.toString();
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Unserialize the XML String into an IEngineMessage
	 * 
	 * @param xml
	 * @return
	 */
	public static Object fromXml(String xml, String packageName) throws SerializationException {

		StringReader in = new StringReader(xml);
		try {
			JAXBContext context = JAXBContext.newInstance(packageName);
			Object obj = context
					.createUnmarshaller().unmarshal(in);
			return obj;
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Deserialize the XML String into an instance of provided class
	 * 
	 * @param xml
	 * @param objectClass
	 * @return
	 * @throws SerializationException
	 */
	@SuppressWarnings("rawtypes")
	public static Object fromXml(String xml, Class objectClass) throws SerializationException {

		StringReader in = new StringReader(xml);
		try {
			JAXBContext context = JAXBContext.newInstance(objectClass);
			Object obj = context
					.createUnmarshaller().unmarshal(in);
			return obj;
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Deserialize the XML InputStream into an instance of provided class
	 * 
	 * @param xml
	 * @param objectClass
	 * @return
	 * @throws SerializationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(InputStream xml, Class<T> objectClass) throws SerializationException {
		try {
			JAXBContext context = JAXBContext.newInstance(objectClass);
			T obj = (T) context
					.createUnmarshaller().unmarshal(xml);
			return obj;
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Deserialize the XML String into a List of instances of provided class
	 * 
	 * @param xml
	 * @param clazz
	 * @return
	 * @throws SerializationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> listFromXml(String xml, Class<T> clazz) throws SerializationException {

		StringReader in = new StringReader(xml);
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(GenericListWrapper.class, clazz);
			GenericListWrapper<T> wrapper = (GenericListWrapper<T>) context.createUnmarshaller().unmarshal(new StreamSource(in),
					GenericListWrapper.class).getValue();

			return wrapper.getItems();

		} catch (JAXBException e) {
			throw new SerializationException(e);

		}

	}

}