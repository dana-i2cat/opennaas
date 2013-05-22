/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.vnmapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ObjectCopier {

	public static Object deepCopy(Object original) throws IOException
	{
		ObjectInputStream ois;
		ObjectOutputStream oos;
		ByteArrayInputStream bais;
		ByteArrayOutputStream baos;
		byte[] data;
		Object copy = null;

		// write object to bytes
		baos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(baos);
		oos.writeObject(original);
		oos.close();

		// get the bytes
		data = baos.toByteArray();

		// construct an object from the bytes

		bais = new ByteArrayInputStream(data);
		ois = new ObjectInputStream(bais);
		try {
			copy = ois.readObject();
		} catch (Exception e) {
		}
		ois.close();

		return copy;
	}
}
