package org.opennaas.extensions.openflowswitch.capability.controllerinformation;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch
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

import org.opennaas.core.resources.action.IActionSetDefinition;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ControllerInformationActionSet implements IActionSetDefinition {

	public static final String	GET_MEMORY_USAGE	= "getMemoryUsage";
	public static final String	GET_HEALTH_STATE	= "getHealthState";

}
