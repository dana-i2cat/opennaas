package com.wonesys.emsModule.alarms;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class SnmpTrapListener implements TrapListener {

	Log					log							= LogFactory.getLog(SnmpTrapListener.class);

	// Oid's
	// General OIDs
	static final String		enterprise					= "1.3.6.1.4.1.9.";
	static final String		sysUpTime					= "1.3.6.1.2.1.1.3.0";
	static final String		mplsTunnelUp				= "1.3.6.1.4.1.15289.2.1.3.0.1";
	static final String		mplsTunnelDown				= "1.3.6.1.4.1.15289.2.1.3.0.2";

	// Trap OIDs
	static final String		VTPNotificationsVLANCreated	= enterprise
																+ "9.46.2.0.10";
	static final String		VTPNotificationsVLANDeleted	= enterprise
																+ "9.46.2.0.11";
	static final String		SNMPTrapsColdStart			= "1.3.6.1.6.3.1.1.5.1";
	static final String		SNMPTrapsWarmStart			= "1.3.6.1.6.3.1.1.5.2";
	static final String		SNMPTrapsLinkDown			= "1.3.6.1.6.3.1.1.5.3";
	static final String		SNMPTrapsLinkUp				= "1.3.6.1.6.3.1.1.5.4";
	static final String		SNMPTrapsAuthFailure		= "1.3.6.1.6.3.1.1.5.5";

	// Fi Oid's
	private int				port						= 162;
	private BufferedWriter	out;
	DefaultTrapContext		trapContext;
	// SnmpContextv2c context;

	AlarmsControler			alarmsControler;

	public SnmpTrapListener(int port, AlarmsControler alarmsControler) {
		this.port = port;
		this.alarmsControler = alarmsControler;
	}

	public void rcv() throws IOException {
		// context = new SnmpContextv2c("127.0.0.1", port,
		// SnmpContext.STANDARD_SOCKET);
		trapContext = DefaultTrapContext.getInstance(port,
				SnmpContextPool.STANDARD_SOCKET);
		trapContext.addTrapListener(this);
		log.debug("WoneView-AlarmListener On");
	}

	public String returnValue(String s) {
		return s.substring(s.toString().indexOf(":") + 2, s.toString().length());
	}

	public String returnOid(String s) {
		return s.substring(0, s.toString().indexOf(":"));
	}

	public void trapReceived(TrapEvent evt) {

		log.debug("trap received");

		int ver = evt.getVersion();
		String hostaddr = evt.getHostAddress();

		/*
		 * S'ha tret el context fora i s'han afegit destroy() fora, per evitar el IOException que no mola.
		 * 
		 * Idea: Obrir el context nomes un cop al iniciar i fer servir el mateix sempre.
		 */

		String log_entry = "hostaddr = " + hostaddr + ", ver = " + ver + ", deco = "
				+ evt.isDecoded();

		log.debug(log_entry);

		// EscriptorFitxers.writeLogTraps( log );
		Pdu trapPdu = null;
		String voodoo = ", received ";
		boolean decoded = false;
		String community = "publica";

		byte[] mess = null;
		try {

			if (ver == 0) // snmp v1
			{

				SnmpContext context = new SnmpContext(hostaddr, port,
						SnmpContext.STANDARD_SOCKET);
				context.setCommunity(community);
				trapPdu = context.processIncomingTrap(evt.getMessage());
				context.destroy();
				decoded = true;

				int genericTrap = ((uk.co.westhawk.snmp.stack.TrapPduv1) trapPdu)
						.getGenericTrap();

				procesTrap(hostaddr, trapPdu, evt.isDecoded(), genericTrap);
			} else {

				SnmpContextv2c context = new SnmpContextv2c(hostaddr, port,
						SnmpContext.STANDARD_SOCKET);
				context.setCommunity(community);
				trapPdu = context.processIncomingTrap(evt.getMessage());
				// context.destroy();
				decoded = true;

				procesTrap(hostaddr, trapPdu, evt.isDecoded());

			}

		} catch (DecodingException ex) {

			if (AlarmsControler.debug)
				log.debug("SnmpTrapListener: Decoding exception: "
						+ ex.getMessage(), ex);

			String dm = ex.getMessage();
			if (dm.startsWith("Wrong community: expected")) {

				if (AlarmsControler.debug)
					log.debug("SnmpTrapListener: Wrong community");

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
			if (AlarmsControler.debug)
				log.debug("", ex);
		}

		if (!decoded)
			try {

				if (ver == 0) // snmp v1
				{

					SnmpContext context = new SnmpContext(hostaddr, port,
							SnmpContext.STANDARD_SOCKET);
					context.setCommunity(community);
					trapPdu = context.processIncomingTrap(evt.getMessage());
					context.destroy();
					decoded = true;

					int genericTrap = ((uk.co.westhawk.snmp.stack.TrapPduv1) trapPdu)
							.getGenericTrap();

					procesTrap(hostaddr, trapPdu, evt.isDecoded(), genericTrap);

				} else {

					SnmpContextv2c context = new SnmpContextv2c(hostaddr, port,
							SnmpContext.STANDARD_SOCKET);
					context.setCommunity(community);
					trapPdu = context.processIncomingTrap(evt.getMessage());
					// context.destroy();
					decoded = true;

					procesTrap(hostaddr, trapPdu, evt.isDecoded());

				}

			} catch (DecodingException ex) {
				if (AlarmsControler.debug)
					log.debug("", ex);
			} catch (IOException ex) {
				if (AlarmsControler.debug)
					log.debug("", ex);
			}

	}

	public void procesTrap(String hostaddr, Pdu trapPdu, boolean decoded) {
		procesTrap(hostaddr, trapPdu, decoded, 0);
	}

	public void procesTrap(String hostaddr, Pdu trapPdu, boolean decoded,
			int genericTrap) {
		varbind[] b = null;
		String trap = trapPdu.toString();

		String str = "\t";
		StringTokenizer tok = new StringTokenizer(trap, "[,]");
		while (tok.hasMoreTokens())
			str = str + tok.nextToken() + " ";

		// System.out.println(tok.nextToken());
		try {
			b = trapPdu.getResponseVarbinds();
			// String snmpTrap = returnValue(b[1].toString());

			String values = "";
			String oids = "";

			// System.out.println(hostaddr);
			for (int i = 0; i < b.length; i++) {

				char[] chars = returnValue(b[i].toString()).toCharArray();

				if (chars.length >= 1) {
					if (chars[0] == 0x00)
						values += "0x00" + "#";
					else
						values += returnValue(b[i].toString()) + "#";

					oids += returnOid(b[i].toString()) + "#";
				}

				if (AlarmsControler.debug)
					log.debug("SnmpTrapListener: Value " + i + " is "
							+ returnValue(b[i].toString()));

			}

			// System.out.println(hostaddr + "  " /*+ b.toString()*/ + "  " +
			// str);

			String str1 = "";
			String str2 = "";
			if (0 < b.length)
				str1 = returnValue(b[0].toString());
			else
				str1 = String.valueOf(genericTrap);

			if (1 < b.length)
				str2 = returnValue(b[1].toString());
			else
				str2 = String.valueOf(genericTrap);

			// alarmafacaderemote.newAlarm(hostaddr,str1 , str2, values, oids);
			alarmsControler.registerAlarm(str2, hostaddr, values, oids);
		} catch (PduException pdue) {
			// EscriptorFitxers.writeLogExcepcions(pdue.toString());
			// System.out.println(pdue);

			if (AlarmsControler.debug)
				log.debug("SnmpTrapListener:PDU Exception: "
						+ pdue.getMessage());

		} catch (Exception e) {

			// e.printStackTrace();

			if (AlarmsControler.debug)
				log.debug("SnmpTrapListener: Unkown Exception:", e);
		}
	}

}
