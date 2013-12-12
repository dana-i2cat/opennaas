package org.opennaas.extensions.ofertie.ncl.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class ProviderOutInterceptor extends AbstractPhaseInterceptor<Message> {

	private final static Log	log	= LogFactory.getLog(ProviderOutInterceptor.class);

	public ProviderOutInterceptor() {
		super(Phase.POST_PROTOCOL);
	}

	@Override
	public void handleMessage(Message arg0) throws Fault {
		log.info("Intercepting message.");
	}
}
