package org.opennaas.extensions.router.junos.actionssets.actions;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is not used. Save and load configuration in a local system provides corrupt configurations.
 * 
 * @author Carlos Baez Ruiz
 * 
 */
@Deprecated
public class TempFileManager {
	private static TempFileManager	tempFileManager	= null;
	private String					pathFile		= null;

	public void createFile(String name, String contain) throws FileNotFoundException {
		// Create temp file.
		try {
			File temp = new File(name);
			boolean noExists;
			noExists = temp.createNewFile();

			if (!noExists) {
				temp.delete();
				temp.createNewFile();
			}
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(temp), contain.length());
			out.write(contain);
			out.close();
			pathFile = temp.getAbsolutePath();
		} catch (IOException e) {
			throw new FileNotFoundException();
		}

	}

	public String loadFile() throws FileNotFoundException {
		String backupInfo = null;
		try {
			File file = new File(pathFile);
			// InputStream inputFile = getClass().getResourceAsStream(pathFile);
			StringBuffer fileData = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(pathFile));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
			backupInfo = fileData.toString();
		} catch (IOException e) {
			throw new FileNotFoundException();
		}

		return backupInfo;

	}

	public static TempFileManager getInstance() {
		if (tempFileManager == null) {
			tempFileManager = new TempFileManager();
		}
		return tempFileManager;
	}

}
