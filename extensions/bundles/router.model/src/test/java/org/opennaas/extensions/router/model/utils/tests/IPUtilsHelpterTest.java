package org.opennaas.extensions.router.model.utils.tests;

/*
 * #%L
 * OpenNaaS :: CIM Model
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

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

public class IPUtilsHelpterTest {

	@Test
	public void IPv6ValidatorTest() {

		Assert.assertTrue(IPUtilsHelper.validateIPv6Address("ABCD:09BD:A:23:DF3:12DA:B5:01"));
		Assert.assertTrue(IPUtilsHelper.validateIPv6Address("::1")); // compressed, beginning with ::
		Assert.assertTrue(IPUtilsHelper.validateIPv6Address("::1:B4:A2")); // compressed, beginning with ::
		Assert.assertTrue(IPUtilsHelper.validateIPv6Address("1:B4:A2::")); // compressed, ends with ::
		Assert.assertTrue(IPUtilsHelper.validateIPv6Address("::")); // compressed,
		Assert.assertTrue(IPUtilsHelper.validateIPv6Address("A::43A:B41")); // compressed,

		Assert.assertFalse(IPUtilsHelper.validateIPv6Address("ZBCD:09B:A:23:DF3:12DA:B5:01")); // Invalid character "Z"
		Assert.assertFalse(IPUtilsHelper.validateIPv6Address("ABCDE:09B:A:23:DF3:12DA:B5:01")); // group of 5 hexas
		Assert.assertFalse(IPUtilsHelper.validateIPv6Address("ABCD:09B:A:23:DF3:12DA:B5")); // 112 bits
		Assert.assertFalse(IPUtilsHelper.validateIPv6Address("ABCD:09B:A:23:DF3:12DA:B5:B5:B5")); // 144 bits
		Assert.assertFalse(IPUtilsHelper.validateIPv6Address("::A:")); // compressed, ending with :

	}
}
