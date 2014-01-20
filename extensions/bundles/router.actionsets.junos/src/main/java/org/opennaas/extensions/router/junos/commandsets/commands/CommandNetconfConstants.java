package org.opennaas.extensions.router.junos.commandsets.commands;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
