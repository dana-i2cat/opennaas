/*
 * HwdOp.java
 *
 * Created on 8 de abril de 2008, 15:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.hwd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * @author mbeltran
 */
public class HwdOp {
    
    public String sendOp(String operation, String ip) throws IOException {
        
         int timeout = 1000;
         int port = 27773;
         Socket sock = new Socket();
         String outS = "";
         byte[] bts = new BigInteger(operation, 16).toByteArray();

        // Bind to a local ephemeral port
        sock.bind(null);
        sock.connect(new InetSocketAddress(ip, port), timeout);

        // Your code goes here
        OutputStream out = sock.getOutputStream();
        InputStream in = sock.getInputStream();
            
        out.write(bts);
        out.flush();

        //start del read
          
        Timer timeoutControl = new Timer(timeout);
        timeoutControl.start();
          
        int i;
        byte[] buffer = new byte[256];
        
       // while ((i = in.read(buffer)) != 0){
            
        i = in.read(buffer);
            
            
        String salida = ""; //$NON-NLS-1$

        for(int j = 0; j < i; j++) {
            byte o = buffer[j];
            if ((o < 0x10) && (o >= 0))
                salida += "0"; //$NON-NLS-1$
            String bite = Integer.toHexString(o);
            if (bite.length() > 2)
                salida += bite.substring(bite.length() -2);
            else
                salida += bite;
        }

        outS += salida;
      //  }
        sock.close(); 
        return outS;
    }

    public String sendOpPort(String operation, String ip, int port) throws IOException {

         int timeout = 1000;
         Socket sock = new Socket();
         String outS = "";
         byte[] bts = new BigInteger(operation, 16).toByteArray();

        // Bind to a local ephemeral port
        sock.bind(null);
        sock.connect(new InetSocketAddress(ip, port), timeout);

        // Your code goes here
        OutputStream out = sock.getOutputStream();
        InputStream in = sock.getInputStream();

        out.write(bts);
        out.flush();

        //start del read

        Timer timeoutControl = new Timer(timeout);
        timeoutControl.start();

        int i;
        byte[] buffer = new byte[256];

       // while ((i = in.read(buffer)) != 0){

        i = in.read(buffer);


        String salida = ""; //$NON-NLS-1$

        for(int j = 0; j < i; j++) {
            byte o = buffer[j];
            if ((o < 0x10) && (o >= 0))
                salida += "0"; //$NON-NLS-1$
            String bite = Integer.toHexString(o);
            if (bite.length() > 2)
                salida += bite.substring(bite.length() -2);
            else
                salida += bite;
        }

        outS += salida;
      //  }
        sock.close();
        return outS;
    }
}


class Timer extends Thread
{
	/** Rate at which timer is checked */
	protected int m_rate = 100;
	
	/** Length of timeout */
	private int m_length;
	/** Rate at which timer is checked */

	/** Time elapsed */
	private int m_elapsed;

	/**
	  * Creates a timer of a specified length
	  * @param	length	Length of time before timeout occurs
	  */
	public Timer ( int length )
	{
		// Assign to member variable
		m_length = length;

		// Set time elapsed
		m_elapsed = 0;
	}

	
	/** Resets the timer back to zero */
	public synchronized void reset()
	{
		m_elapsed = 0;
	}

	/** Performs timer specific code */
	public void run() 
	{
		// Keep looping
		for (;;)
		{
			// Put the timer to sleep
			try
			{ 
				Thread.sleep(m_rate);
			}
			catch (InterruptedException ioe) 
			{
				continue;
			}

			// Use 'synchronized' to prevent conflicts
			synchronized ( this )
			{
				// Increment time remaining
				m_elapsed += m_rate;

				// Check to see if the time has been exceeded
				if (m_elapsed > m_length)
				{
					// Trigger a timeout
					timeout();
                                        break;
				}
			}

		}
	}

	public void timeout()
	{
		//System.exit(1);
	}
}

    
