package org.opennaas.core.resources.queue;

import org.opennaas.core.resources.action.IActionSetDefinition;

public class QueueConstants implements IActionSetDefinition {
	public static final String	CONFIRM			= "confirm";
	public static final String	ISALIVE			= "isAlive";
	public static final String	PREPARE			= "prepare";
	public static final String	RESTORE			= "restore";
	public static final String	REFRESH			= "refresh";
	public static final String	EXECUTE			= "execute";
	public static final String	GETQUEUE		= "getQueue";
	public static final String	MODIFY			= "modify";
	public static final String	DUMMYEXECUTE	= "dummyExecute";
	public static final String	CLEAR			= "clear";
}
