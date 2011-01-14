package net.i2cat.mantychore.queueManager;

import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;

import org.ops4j.pax.logging.OSGIPaxLoggingManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.iaasframework.resources.core.capability.AbstractJMSCapability;
import com.iaasframework.resources.core.capability.CapabilityException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.message.ICapabilityMessage;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.commandset.ICommand;
import com.iaasframework.capabilities.commandset.ICommandFactory;

public class QueueManagerCapability extends AbstractJMSCapability implements IQueueManagerService {
	
	private Vector<IAction> queue = new Vector<IAction>();
	
	private ICommandFactory queueCommandFactory;
	
	private ICommand prepareCommand;
	private ICommand confirmCommand;
	private ICommand rollbackCommand;
	
	public QueueManagerCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor, resourceId);
		
		ServiceReference[] queueCommandFactoryServiceReference;
		try {
			queueCommandFactoryServiceReference = Activator.getContext().getServiceReferences(ICommandFactory.class.getName(),"(name=queue)");
			
			queueCommandFactory = (ICommandFactory) Activator.getContext().getService(queueCommandFactoryServiceReference[0]);
			
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

	@Override
	protected void handleMessage(ICapabilityMessage message) throws JMSException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void activateCapability() throws CapabilityException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void deactivateCapability() throws CapabilityException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub
	}

	
	/*
	 * QUEUE MANAGER SERVICE
	 * 
	 * @see net.i2cat.mantychore.queueManager.IQueueManagerService#empty()
	 */
	@Override
	public void execute() {
		
	}
	
	@Override
	public void empty() {
		queue.clear();
	}

	@Override
	public void queueAction(IAction action) {
		queue.add(action);		
	}

	@Override
	public List<IAction> getActions() {
		return queue;
	}
}
