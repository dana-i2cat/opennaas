/*
 * NewMain.java
 *
 * Created on 8 de abril de 2008, 15:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.tests;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.co.westhawk.snmp.pdu.OneTrapPduv2;
import uk.co.westhawk.snmp.stack.AsnInteger;
import uk.co.westhawk.snmp.stack.AsnObject;
import uk.co.westhawk.snmp.stack.AsnObjectId;
import uk.co.westhawk.snmp.stack.AsnOctets;
import uk.co.westhawk.snmp.stack.AsnUnsInteger;
import uk.co.westhawk.snmp.stack.PduException;
import uk.co.westhawk.snmp.stack.SnmpContext;
import uk.co.westhawk.snmp.stack.SnmpContextv2c;
import uk.co.westhawk.snmp.stack.TrapPduv2;
import uk.co.westhawk.snmp.stack.varbind;

import com.wonesys.emsModule.alarms.Alarm;
import com.wonesys.emsModule.alarms.AlarmsControler;
import com.wonesys.emsModule.hwd.HwdOp;
import com.wonesys.emsModule.hwd.IMessageArrivalListener;

/**
 * 
 * @author mbeltran
 * @author Isart Canyameres @ Fundació i2cat
 */
public class EMSmoduleTest implements IMessageArrivalListener {

	Log						log			= LogFactory.getLog(EMSmoduleTest.class);

	private static final int	ALARM_PORT	= 32162;

	AlarmsControler				alamsControler;
	SnmpContext					snmpContext;
	String						message		= "";
	Object						mutex		= new Object();

	boolean						connected	= false;
	boolean						closed		= false;
	Exception					error		= null;

	/** Creates a new instance of this class */
	public EMSmoduleTest() {

		alamsControler = new AlarmsControler();
	}

	@Test
	public void libTest() throws Exception {

		try {

			executa("hwd");
			if (error != null) {
				Assert.fail(error.getLocalizedMessage());
			}

			executa("s");
			checkAlarmPortIsOpened();

			executa("a");
			checkAlarmHasBeenReceived();

			executa("l");
			snmpContext.destroy();
		} catch (IOException e) {
			// just to display error when test fails
			log.error("IOException", e);
			throw e;
		} catch (Exception e) {
			// just to display error when test fails
			log.error("IOException", e);
			throw e;
		}

	}

	private void checkAlarmPortIsOpened() {
		Assert.assertFalse(availablePort(ALARM_PORT));
	}

	/**
	 * Checks to see if a specific port is available.
	 * 
	 * @param port
	 *            the port to check for availability
	 */
	private static boolean availablePort(int port) {
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}
		return false;
	}

	private void checkAlarmHasBeenReceived() {
		try {
			log.info("Sleeping: Giving time for snmptrap to be received");
			Thread.sleep(10000);
			log.info("Finished sleeping");
		} catch (InterruptedException e) {
			log.info("Interrupted!", e);
		}
		Collection<Alarm> list = alamsControler.getAlarmsList();
		log.info("Check Alarm Has Been Received in AlarmControler " + alamsControler);
		log.info("Alarms size: " + list.size());
		Assert.assertFalse(list.isEmpty());
	}

	/**
	 * Simulates an alarm has been received.
	 * 
	 * @throws IOException
	 */
	private void sendSNMPTrap() throws IOException {
		snmpContext = new SnmpContextv2c("127.0.0.1", ALARM_PORT); // should send from a different port
		snmpContext.setCommunity("publica");
		TrapPduv2 pdu = new OneTrapPduv2(snmpContext);

		AsnObjectId varbindID;
		AsnObject varbindValue;

		// pdu.setIpAddress(new byte[] { 127, 0, 0, 1 });
		// pdu.setEnterprise("1.3.6.1.4.1.9");
		// pdu.setGenericTrap(SnmpConstants.SNMP_TRAP_ENTERPRISESPECIFIC);
		// pdu.setSpecificTrap(0);
		// pdu.setTimeTicks(20646400);

		varbindID = new AsnObjectId("1.3.6.1.2.1.1.3.0");
		varbindValue = new AsnUnsInteger(20646400);
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.6.3.1.1.4.1.0");
		varbindValue = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.2");
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.3");
		varbindValue = new AsnInteger(0);
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.4");
		varbindValue = new AsnInteger(4);
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.5");
		varbindValue = new AsnOctets("0B");
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.6");
		varbindValue = new AsnOctets("586376724c617365724f6666");
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.7");
		varbindValue = new AsnOctets("5472616E73636569766572204C61736572206F6666");
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.8");
		varbindValue = new AsnInteger(0);
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.9");
		varbindValue = new AsnOctets("0B0001");
		pdu.addOid(new varbind(varbindID, varbindValue));

		try {
			log.info("Sending Pdu");
			pdu.send();
		} catch (PduException e) {
			log.error("PDUException", e);
		}
	}

	private void executa(String entrada) throws Exception {

		if (entrada.equals("hwd")) {

			HwdOp hwd = new HwdOp();

			String ip = "10.10.80.11";
			int port = 27773;
			hwd.connect(ip, port, this);
			connected = true;

			String command = createGetCommand();

			log.info("Sending Operation to HWD @ " + ip + ":" + port);
			log.info(command);
			hwd.sendOp(command);
			String response = waitForMessageArrival();
			Assert.assertFalse(response.equals(""));
			log.info(response);

			log.info("Sending Operation to HWD @ " + ip + ":" + port);
			log.info(command);
			hwd.sendOp(command);
			response = waitForMessageArrival();
			Assert.assertFalse(response.equals(""));
			log.info(response);

			hwd.disconnect();
			connected = false;
			closed = true;
		}

		else if (entrada.equals("s")) {

			int alarmsPort = ALARM_PORT;

			log.info("Creating alarm listener at port " + alarmsPort);
			alamsControler.createAlarmListener(alarmsPort);
			log.info("Alarm listener created at port " + alarmsPort);

		} else if (entrada.equals("a")) {

			log.info("Generating alarm");
			sendSNMPTrap();

		} else if (entrada.equals("l")) {

			Collection<Alarm> list = alamsControler.getAlarmsList();

			log.info("Alarm collection size is " + list.size());

			for (Alarm elem : list) {
				log.info(elem.toString());
			}

			alamsControler.flush();

		} else
			log.error("No implementado");
	}

	private String createGetCommand() {
		// getInventory command
		return "5910ffffffffff01ffffffff0000b700";
	}

	// private String getXOR(String cmd) {
	//
	// int xor = Integer.parseInt(cmd.substring(0, 2), 16) ^ Integer.parseInt(cmd.substring(2, 4), 16);
	// for (int i = 4; i <= cmd.length() - 2; i++) {
	// xor = xor ^ Integer.parseInt(cmd.substring(i, i + 2), 16);
	// i++;
	// }
	// String hxor = Integer.toHexString(xor);
	// if (hxor.length() < 2) {
	// hxor = "0" + hxor;
	// }
	// return hxor;
	// }

	/**
	 * @param args
	 *            the command line arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		EMSmoduleTest main = new EMSmoduleTest();

		int c;

		String entrada = "";

		System.out.println("Menu autónomo de EMS Module para pruebas");
		System.out.println("Opciones:");
		System.out.println("\ts Arranca el servicio de recepcion de alarmas");
		System.out.println("\tl Mostrar alarmas registradas");
		System.out.println();
		System.out.println("\tq Finaliza el programa");
		System.out.println();
		System.out.println("Para test: snmptrap -v 2c -c publica 192.168.0.22:1162 1 1.3.6.1.4.1.18223.9.1.2.2 1.1 s 0 1.1 x 0x03");

		while ((c = System.in.read()) != 'q') {
			if (c == '\n') {
				main.executa(entrada);
				entrada = "";

			} else {
				// feedback
				// System.out.print( (char)c );
				entrada += String.valueOf((char) c);
			}

		}

		System.out.println();
		System.out.println("Gracias por usar EMS Module");

		System.exit(0);
	}

	public String waitForMessageArrival() throws Exception {
		synchronized (mutex) {
			message = "";
			mutex.wait(30000);
			if (message.equals(""))
				throw new Exception("Timeout waiting for message arrival");
			return message.toString();
		}
	}

	@Override
	public void messageReceived(String message) {
		synchronized (mutex) {
			this.message = message;
			mutex.notify();
		}
	}

	@Override
	public void errorHappened(Exception e) {
		if (connected) {
			log.error("Error happened on async reader", e);
			error = e;
		} else if (!closed) {
			log.warn("Error happened on async reader", e);
		}
	}

}
