package net.i2cat.nexus.resources.queue;

public class ModifyParams {
	private String		actionId;
	private Operations	QueueOper;
	private Object		params;

	public enum Operations {
		REMOVE, UP, DOWN
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public Operations getQueueOper() {
		return QueueOper;
	}

	public void setQueueOper(Operations queueOper) {
		QueueOper = queueOper;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(Object params) {
		this.params = params;
	}

	/**
	 * @return the params
	 */
	public Object getParams() {
		return params;
	}

}
