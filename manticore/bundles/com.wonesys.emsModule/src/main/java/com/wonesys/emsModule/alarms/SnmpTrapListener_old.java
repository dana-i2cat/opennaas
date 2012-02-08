/*
 * SnmpTrapListener.java
 *
 * Created on 8 de abril de 2008, 15:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.alarms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import uk.co.westhawk.snmp.event.TrapEvent;
import uk.co.westhawk.snmp.event.TrapListener;
import uk.co.westhawk.snmp.stack.DecodingException;
import uk.co.westhawk.snmp.stack.DefaultTrapContext;
import uk.co.westhawk.snmp.stack.Pdu;
import uk.co.westhawk.snmp.stack.PduException;
import uk.co.westhawk.snmp.stack.SnmpContext;
import uk.co.westhawk.snmp.stack.SnmpContextPool;
import uk.co.westhawk.snmp.stack.SnmpContextv2c;
import uk.co.westhawk.snmp.stack.varbind;

public class SnmpTrapListener_old implements TrapListener {
	// Oid's
	// General OIDs
	static final String		enterprise					= "1.3.6.1.4.1.9.";
	static final String		sysUpTime					= "1.3.6.1.2.1.1.3.0";
	static final String		mplsTunnelUp				= "1.3.6.1.4.1.15289.2.1.3.0.1";
	static final String		mplsTunnelDown				= "1.3.6.1.4.1.15289.2.1.3.0.2";

	// Trap OIDs
	static final String		VTPNotificationsVLANCreated	= enterprise + "9.46.2.0.10";
	static final String		VTPNotificationsVLANDeleted	= enterprise + "9.46.2.0.11";
	static final String		SNMPTrapsColdStart			= "1.3.6.1.6.3.1.1.5.1";
	static final String		SNMPTrapsWarmStart			= "1.3.6.1.6.3.1.1.5.2";
	static final String		SNMPTrapsLinkDown			= "1.3.6.1.6.3.1.1.5.3";
	static final String		SNMPTrapsLinkUp				= "1.3.6.1.6.3.1.1.5.4";
	static final String		SNMPTrapsAuthFailure		= "1.3.6.1.6.3.1.1.5.5";

	// Fi Oid's
	private int				port						= 162;
	private BufferedWriter	out;
	DefaultTrapContext		trapContext;
	SnmpContextv2c			context;

	AlarmsControler			alarmsControler;

	public SnmpTrapListener_old(int port, AlarmsControler alarmsControler) {
		this.port = port;
		this.alarmsControler = alarmsControler;
	}

	public void rcv() {
		try {
			context = new SnmpContextv2c("127.0.0.1", port, SnmpContext.STANDARD_SOCKET);
			trapContext = DefaultTrapContext.getInstance(port, SnmpContextPool.STANDARD_SOCKET);
			trapContext.addTrapListener(this);
			System.out.println("WoneView-AlarmListener On");
		} catch (IOException ex) {
			System.out.println("WoneView-AlarmListener error: " + ex);

		}

	}

	public String returnValue(String s) {
		return s.substring(s.toString().indexOf(":") + 2, s.toString().length());
	}

	public void trapReceived(TrapEvent evt) {

		if (AlarmsControler.debug)
			System.out.println("SnmpTrapListener: Trap received");

		int ver = evt.getVersion();
		String hostaddr = evt.getHostAddress();

		if (AlarmsControler.debug)
			System.out.println("SnmpTrapListener: Version " + ver + " , Host " + hostaddr);

		/*
		 * S'ha tret el context fora i s'han afegit destroy() fora, per evitar el IOException que no mola.
		 *
		 * Idea: Obrir el context nomes un cop al iniciar i fer servir el mateix sempre.
		 */

		String log = "hostaddr = " + hostaddr + ", ver = " + ver + ", deco = " + evt.isDecoded();

		// System.out.println(log);

		// EscriptorFitxers.writeLogTraps( log );
		Pdu trapPdu = null;
		String voodoo = ", received ";
		boolean decoded = false;
		String community = "publica";

		byte[] mess = null;
		try {

			if (ver == 0) // snmp v1
			{
				/*
				 * SnmpContext context = new SnmpContext (hostaddr, port, SnmpContext.STANDARD_SOCKET); context.setCommunity (community); trapPdu =
				 * context.processIncomingTrap(evt.getMessage()); context.destroy(); decoded = true;
				 */
			} else {

				// context = new SnmpContextv2c (hostaddr, port, SnmpContext.STANDARD_SOCKET);
				context.setCommunity(community);
				trapPdu = context.processIncomingTrap(evt.getMessage());
				// context.destroy();
				decoded = true;

				if (AlarmsControler.debug)
					System.out.println("SnmpTrapListener: PDU decoded");

			}

		} catch (DecodingException ex) {
			// context.destroy();

			if (AlarmsControler.debug)
				System.out.println("SnmpTrapListener: Decoding exception: " + ex.getMessage());

			String dm = ex.getMessage();
			if (dm.startsWith("Wrong community: expected")) {

				if (AlarmsControler.debug)
					System.out.println("SnmpTrapListener: Wrong community");

				int sloc = dm.lastIndexOf(voodoo);
				if (sloc > 0) {
					sloc += voodoo.length();
					community = dm.substring(sloc);
					decoded = false;

				}
			}

		} catch (IOException ex) {
			// context.destroy();
			// System.out.println(ex.toString());
			ex.printStackTrace();
		}

		if (!decoded)
			try {

				if (ver == 0) // snmp v1
				{
					/*
					 * SnmpContext context = new SnmpContext (hostaddr, port, SnmpContext.STANDARD_SOCKET); context.setCommunity (community); trapPdu
					 * = context.processIncomingTrap(evt.getMessage()); context.destroy(); decoded = true;
					 */
				} else {

					// context = new SnmpContextv2c (hostaddr, port, SnmpContext.STANDARD_SOCKET);
					context.setCommunity(community);
					trapPdu = context.processIncomingTrap(evt.getMessage());
					// context.destroy();
					decoded = true;

				}

			} catch (DecodingException ex) {

			} catch (IOException ex) {
				ex.printStackTrace();
			}

		procesTrap(hostaddr, trapPdu, evt.isDecoded());

	}

	public void procesTrap(String hostaddr, Pdu trapPdu, boolean decoded) {
		varbind[] b = null;
		String trap = trapPdu.toString();

		if (AlarmsControler.debug)
			System.out.println("SnmpTrapListener: Process Trap");

		String str = "\t";
		StringTokenizer tok = new StringTokenizer(trap, "[,]");
		while (tok.hasMoreTokens())
			str = str + tok.nextToken() + " ";
		try {
			b = trapPdu.getResponseVarbinds();
			String snmpTrap = returnValue(b[1].toString());

			String values = "";

			for (int i = 1; i < b.length; i++) {
				char[] chars = returnValue(b[i].toString()).toCharArray();
				if (chars.length >= 1) {
					if (chars[0] == 0x00) {
						values += "0x00" + "#";
						if (AlarmsControler.debug)
							System.out.println("SnmpTrapListener: Value " + i + " is 0x00");
					} else {
						values += returnValue(b[i].toString()) + "#";
						if (AlarmsControler.debug)
							System.out.println("SnmpTrapListener: Value " + i + " is " + returnValue(b[i].toString()));
					}
				}
			}

			// alarmsControler.registerAlarm(returnValue(b[1].toString()),hostaddr,values);

		} catch (PduException pdue) {
			// EscriptorFitxers.writeLogExcepcions(pdue.toString());
			// System.out.println(pdue);

			if (AlarmsControler.debug)
				System.out.println("SnmpTrapListener:PDU Exception: " + pdue.getMessage());

		} catch (Exception e) {

			e.printStackTrace();

			if (AlarmsControler.debug)
				System.out.println("SnmpTrapListener: Unkown Exception: " + e.getMessage());

		}
	}

}
