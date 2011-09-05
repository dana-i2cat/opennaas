package net.i2cat.luminis.actionsets.wonesys.actions;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.queue.QueueConstants;

public class PrepareAction extends Action {

	static Log	log	= LogFactory.getLog(PrepareAction.class);

	public PrepareAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(QueueConstants.PREPARE);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		// lock node
		LockNodeAction lockAction = new LockNodeAction();
		ActionResponse lockResponse = lockAction.execute(protocolSessionManager);

		// TODO start timer to refresh lock periodically

		// TODO SAVE SOME KIND OF CONFIG TO ALLOW ROLLBACK!!!!

		ActionResponse actionResponse = lockResponse;

		return actionResponse;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		log.warn("Given params are ignored");
		return true;
	}

}
