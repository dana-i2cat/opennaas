package net.i2cat.mantychore.core.resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
	
	/**
	 * Utility method to read a file located at the given InputStream into a String.
	 * @param stream
	 * @return
	 */
	public static String readStringFromFile(InputStream stream){
		String answer = null;
		
		try {
			InputStreamReader streamReader = new InputStreamReader(stream);
			StringBuffer fileData = new StringBuffer(10000);
			BufferedReader reader = new BufferedReader(streamReader);
			char[] buf = new char[1024];
			int numRead=0;
			while((numRead=reader.read(buf)) != -1){
			    String readData = String.valueOf(buf, 0, numRead);
			    fileData.append(readData);
			    buf = new char[1024];
			}
			reader.close();
			answer = fileData.toString();
			fileData = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return answer;
	}
}
