/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.vnmapper;

/*
 * #%L
 * OpenNaaS :: VNMapper Resource
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
