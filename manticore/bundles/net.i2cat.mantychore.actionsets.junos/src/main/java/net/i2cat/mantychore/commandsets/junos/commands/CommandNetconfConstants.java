package net.i2cat.mantychore.commandsets.junos.commands;

public class CommandNetconfConstants {
	public static final String	COMMIT				= "commit";
	public static final String	KEEPALIVE			= "keepAlive";
	public static final String	GET					= "getConfiguration";
	public static final String	EDIT				= "setConfiguration";
	public static final String	DISCARD				= "discard";
	public static final String	RESTORE				= "restore";
	public static final String	LOCK				= "lock";
	public static final String	UNLOCK				= "unlock";

	public static final String	NONE_OPERATION		= "none";
	public static final String	REPLACE_OPERATION	= "replace";
	public static final String	MERGE_OPERATION		= "merge";
	public static final String	DELETE_OPERATION	= "delete";

	public static enum TargetConfiguration {
		RUNNING {
			@Override
			public String toString() {
				return "running";
			}
		},
		CANDIDATE {
			@Override
			public String toString() {
				return "candidate";
			}
		}
	}

}
