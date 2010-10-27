package cat.i2cat.manticore.test.util.login;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Download a file from a remote host
 */
public class FileDownload
{
	public static void download(String address, File localFile, Proxy proxy) throws IOException
	{
		OutputStream out = null;
		URLConnection conn = null;
		InputStream in = null;
		try {
			URL url = new URL(address);
			out = new BufferedOutputStream(new FileOutputStream(localFile));
			conn = url.openConnection(proxy);
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
			//System.out.println(localFile.getPath() + "\t" + numWritten);
			in.close();
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			//delete the local file if the download failed
			localFile.delete();
			throw new IOException(e.getMessage());
		}
	}
}
