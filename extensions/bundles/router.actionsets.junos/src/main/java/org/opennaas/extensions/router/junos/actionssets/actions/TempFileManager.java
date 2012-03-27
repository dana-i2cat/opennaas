package org.opennaas.extensions.router.junos.actionssets.actions;

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
