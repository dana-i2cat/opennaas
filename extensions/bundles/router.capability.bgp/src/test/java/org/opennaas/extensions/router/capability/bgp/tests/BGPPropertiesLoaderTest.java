package org.opennaas.extensions.router.capability.bgp.tests;

/*
 * #%L
 * OpenNaaS :: Router :: BGP Capability
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

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.extensions.router.capability.bgp.BGPModelFactory;
import org.opennaas.extensions.router.model.ComputerSystem;

public class BGPPropertiesLoaderTest {

	private static final String	BGP_PROPERTIES_PATH	= "/absolute/path/to/bgp.properties";

	public static void main(String args[]) throws Exception {
		checkPropertiesAreCorrect(args[0]);
		System.out.println("Properties loaded without error");
	}

	private static void checkPropertiesAreCorrect(String path) throws IOException {
		BGPModelFactory factory = new BGPModelFactory(path);
		ComputerSystem model = factory.createRouterWithBGP();
	}

	// TODO Remove @Ignore to check file in BGP_PROPERTIES_PATH can be loaded without error.
	@Ignore
	@Test
	public void checkTest() throws IOException {
		checkPropertiesAreCorrect(BGP_PROPERTIES_PATH);
	}

}
