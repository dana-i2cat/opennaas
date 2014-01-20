package org.opennaas.extensions.gim.controller.capabilities;

/*
 * #%L
 * GIM :: GIModel and APC PDU driver
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

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerManagementController {

	/**
	 * 
	 * @return true if power is on, false otherwise.
	 * @throws Exception
	 */
	public boolean getPowerStatus() throws Exception;

	/**
	 * Turn on power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOn() throws Exception;

	/**
	 * Turn off power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOff() throws Exception;

}
