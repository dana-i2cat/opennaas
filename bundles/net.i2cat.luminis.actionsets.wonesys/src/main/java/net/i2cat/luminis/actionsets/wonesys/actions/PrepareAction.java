package net.i2cat.luminis.actionsets.wonesys.actions;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.command.Response.Status;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.queue.QueueConstants;

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
