package net.i2cat.mantychore.protocols.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializerHelper {

	public static String objectToString(Object object) {

		ByteArrayOutputStream byteOutputStream = null;
		ObjectOutputStream out = null;
		try {
			byteOutputStream = new ByteArrayOutputStream();
			out = new ObjectOutputStream(byteOutputStream);
			out.writeObject(object);
			out.close();
			return byteOutputStream.toString();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return "";
	}

	public static Object stringToObject(String str) {
		ByteArrayInputStream byteInputStream = null;
		ObjectInputStream in = null;
		Object object = null;
		try {
			byteInputStream = new ByteArrayInputStream(str.getBytes());
			in = new ObjectInputStream(byteInputStream);
			object = in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return object;
	}

}
