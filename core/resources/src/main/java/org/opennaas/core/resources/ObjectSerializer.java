package org.opennaas.core.resources;

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
	 * Unserialize the XML String into an IEngineMessage
	 * 
	 * @param xml
	 * @return
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
	 * Unserialize the XML String into a list of IEngineMessage
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> fromXML(String xml, Class<T> clazz) throws SerializationException {

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