package org.opennaas.core.resources.queue;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ModifyParams {
	private int			posAction;

	private Operations	QueueOper;
	private Object		params;

	public enum Operations {
		REMOVE, UP, DOWN
	}

	public int getPosAction() {
		return posAction;
	}

	public void setPosAction(int posAction) {
		this.posAction = posAction;
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

	public static ModifyParams newRemoveOperation(int posAction) {
		ModifyParams removeParams = new ModifyParams();
		removeParams.setPosAction(posAction);
		removeParams.setQueueOper(Operations.REMOVE);
		return removeParams;
	}

}
