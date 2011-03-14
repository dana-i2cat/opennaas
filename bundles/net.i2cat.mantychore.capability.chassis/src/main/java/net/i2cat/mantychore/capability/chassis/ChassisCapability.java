package net.i2cat.mantychore.capability.chassis;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.i2cat.mantychore.commons.IActionSetFactory;
import net.i2cat.mantychore.actionsets.junos.BasicActionSetFactory;
import net.i2cat.mantychore.actionsets.junos.ChassisActionSetFactory;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ICapability;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChassisCapability implements ICapability {

	public final static  String CHASSIS = "chassis";
	
	Logger					log			= LoggerFactory
												.getLogger(ChassisCapability.class);

	private String			resourceId	= "";
	private QueueManagerWrapper queueManagerWrapper = new QueueManagerWrapper();
    private List<String> actionIds = new ArrayList<String>();
    private List<String> basicActionIds = new ArrayList<String>();
    
	private IQueueManagerService queueManager ;


	private Object model;
	

	private ProtocolSessionContext protocolSessionContext;
	

	public ChassisCapability(List<String> actionIds, ProtocolSessionContext protocolSessionContext,
			String resourceId) {
		this.protocolSessionContext = protocolSessionContext;
		this.resourceId = resourceId;
		this.actionIds = actionIds;
	}
	
	public void initialize () {
		//FIXME GIX THIS NULL IF WE GET THE QUEUEMANAGER WITH OTHER OPERATION
		if (queueManager==null)
			queueManager = queueManagerWrapper.getQueueManager(resourceId);		
		//FIXME use actionWrapper for basic actions and chassis actions
	}
	
	

	public Response sendMessage(String idOperation, Object params) {
		//Check if it is an available operation 
		if (!actionIds.contains(idOperation)) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs.add(ICapability.ERROR_CAPABILITY);
			Response.errorResponse(idOperation,errorMsgs);
		}
		
		//FIXME IT HAS TO BE CALLED THROUGH OSGI SERVICE
		IActionSetFactory actionFactory =  new ChassisActionSetFactory();
		Action action = actionFactory.createAction(idOperation);
		//IT IS HARDCODED!!
		if (action==null) {
			IActionSetFactory basicActionFactory =  new BasicActionSetFactory();
			action = basicActionFactory.createAction(idOperation);
		}
		action.setModelToUpdate(model);

		queueManager.queueAction(action,protocolSessionContext,params);
		return Response.okResponse(idOperation);
	}

	public void setResource(Object model) {
		this.model = model;
	}
	
	public Object getResource() {
		return model;
	}
	
	public List<String> getIdMessages () {
		return actionIds;
		
	}
	
	public void setQueueManager(IQueueManagerService queueManager) {
		this.queueManager = queueManager;
	}
	
	



}
