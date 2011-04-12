package net.i2cat.mantychore.capability.profilemanager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import net.i2cat.mantychore.commons.AbstractMantychoreCapability;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ErrorConstants;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.CapabilityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileManagerCapability extends AbstractMantychoreCapability {

	public final static String		PROFILEMANAGER		= "profilemanager";

	Logger							log					= LoggerFactory
																.getLogger(ProfileManagerCapability.class);

	private Map<String, String>		profileOperations	= new HashMap<String, String>();

	private IQueueManagerService	queueManager;

	public ProfileManagerCapability(List<String> actionIds, IResource resource) {
		super(actionIds, resource, resource.getResourceDescriptor()
				.getCapabilityDescriptor(PROFILEMANAGER));
	}

	@Override
	protected void initializeCapability() throws CapabilityException {

	}

	@Override
	public Response sendMessage(String idOperation, Object params) {
		if (!actionIds.contains(idOperation)) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs.add(ErrorConstants.ERROR_CAPABILITY);
			return Response.errorResponse(idOperation, errorMsgs);
		}
		Response okResponse = Response.okResponse(idOperation);
		try {
			Properties props = (Properties) params;
			if (idOperation.equals(ProfileManagerConstants.ADDOPERATION)) {
				addOperation(props);
				return Response.okResponse(idOperation);
			} else if (idOperation.equals(ProfileManagerConstants.DELETEOPERATION)) {
				deleteOperation(props);
			} else if (idOperation.equals(ProfileManagerConstants.LISTOPERATIONS)) {
				String information = listOperations();
				okResponse.setInformation(information);
			} else {
				queueOperation(idOperation, params);
			}
		} catch (IOException e) {
			Vector<String> errors = new Vector<String>();
			errors.add(e.getMessage() + " : " + e.getLocalizedMessage());
			return Response.errorResponse(idOperation, errors);
		}

		return okResponse;
	}

	public IQueueManagerService getQueueManager() {
		return queueManager;
	}

	public void setQueueManager(IQueueManagerService queueManager) {
		this.queueManager = queueManager;
	}

	private void addOperation(Properties params) throws IOException {
		String id = params.getProperty(ProfileManagerConstants.IDOPERATION);
		String template = params.getProperty(ProfileManagerConstants.TEMPLATE);
		profileOperations.put(id, template);
		actionIds.add(id);
	}

	private void deleteOperation(Properties params) throws IOException {
		String id = params.getProperty(ProfileManagerConstants.IDOPERATION);
		profileOperations.remove(id);
	}

	private String listOperations() {
		Set<String> keys = profileOperations.keySet();
		String listOpers = "";
		for (String key : keys) {
			listOpers = key + '\n';
		}

		return listOpers;
	}

	private void queueOperation(String id, Object params) {

		String template = profileOperations.get(id);
		SetProfileCommand profileCommand = new SetProfileCommand(id, template);
		profileCommand.setParams(params);

		Action action = new Action();
		action.getCommands().add(profileCommand);

		queueManager.queueAction(action);

	}

	@Override
	protected void activateCapability() throws CapabilityException {

	}

	@Override
	protected void deactivateCapability() throws CapabilityException {

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {

	}

}
