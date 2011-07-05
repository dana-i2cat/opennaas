import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.command.Response;

public class MockActionFactory {

	public static IAction newMockActionAnError(String actionID) {
		MockAction action = new MockAction();
		action.setActionID(actionID);
		/* build responses */
		ActionResponse actionResponse = new ActionResponse();

		List<Response> responses = new ArrayList<Response>();
		Vector<String> errors = new Vector<String>();

		errors.add("response 1 - mock description error 1");

		responses.add(Response.errorResponse("response 1: " + actionID, errors));

		actionResponse.setResponses(responses);
		action.setActionResponse(actionResponse);

		return action;

	}

	public static IAction newMockActionVariousError(String actionID) {
		MockAction action = new MockAction();
		action.setActionID(actionID);
		/* build responses */
		ActionResponse actionResponse = new ActionResponse();

		List<Response> responses = new ArrayList<Response>();
		responses.add(Response.okResponse("response 1: " + actionID));
		Vector<String> errors1 = new Vector<String>();
		errors1.add("response 2 - mock description error 1");
		errors1.add("response 2 - mock description error 1");

		responses.add(Response.errorResponse("response 2: " + actionID, errors1));
		responses.add(Response.okResponse("response 3: " + actionID));
		Vector<String> errors2 = new Vector<String>();

		errors2.add("response 4 - mock description error 1");

		responses.add(Response.errorResponse("response 4: " + actionID, errors2));

		actionResponse.setResponses(responses);
		action.setActionResponse(actionResponse);

		return action;
	}

	public static IAction newMockActionOK(String actionID) {
		MockAction action = new MockAction();
		action.setActionID(actionID);

		/* build responses */
		ActionResponse actionResponse = new ActionResponse();

		List<Response> responses = new ArrayList<Response>();
		responses.add(Response.okResponse("response 1: " + actionID));

		actionResponse.setResponses(responses);
		action.setActionResponse(actionResponse);

		return action;
	}

	public static IAction newMockActionDiffsCommandOks(String actionID) {
		MockAction action = new MockAction();
		action.setActionID(actionID);

		/* build responses */
		ActionResponse actionResponse = new ActionResponse();

		List<Response> responses = new ArrayList<Response>();
		responses.add(Response.okResponse("response 1: " + actionID));
		responses.add(Response.okResponse("response 2: " + actionID));
		responses.add(Response.okResponse("response 3: " + actionID));
		responses.add(Response.okResponse("response 4: " + actionID));

		actionResponse.setResponses(responses);
		action.setActionResponse(actionResponse);

		return action;
	}

}
