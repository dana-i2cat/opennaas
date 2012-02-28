package net.i2cat.mantychore.action.junos.test;

import net.i2cat.mantychore.actionsets.junos.actions.CreateTunnelAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class CreateTunnelActionTest {
	static CreateTunnelAction		action;
	Log								log			= LogFactory.getLog(CreateTunnelActionTest.class);
	static String					resourceId	= "RandomDevice";

	static ProtocolManager			protocolManager;
	static ProtocolSessionManager	protocolSessionManager;

	static ProtocolSessionContext	netconfContext;

}