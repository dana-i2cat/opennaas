package net.i2cat.mantychore.capability.chassis;

import java.util.List;

import net.i2cat.mantychore.commons.ICapability;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChassisCapability implements ICapability {

	public final static  String CHASSIS = "chassis";
	
	Logger					log			= LoggerFactory
												.getLogger(ChassisCapability.class);

	private String			resourceId	= "";
	private List<String>	actionNames	= null;
	private QueueManagerWrapper queueManagerWrapper = new QueueManagerWrapper();
	private ActionSetWrapper actionSetWrapper = new ActionSetWrapper();
//	private IActionSetservice actionSet ;
	private IQueueManagerService queueManager ;
	

	private ProtocolSessionContext protocolSessionContext;
	

	public ChassisCapability(ProtocolSessionContext protocolSessionContext,
			String resourceId2) {
		this.protocolSessionContext = protocolSessionContext;
		this.resourceId = resourceId;
	}
	
	public void initialize () {
		IQueueManagerService queueManager = queueManagerWrapper.getQueueManager(resourceId);
//		actionSet = actionSetWrapper.getActionSet(CHASSIS+"+"+resourceId);
		
	}

	public void handleMessage(String message) {
		String[] paramsAction = message.split("|");
//		String idAction = paramsAction
		

		
	}
	
	



}
