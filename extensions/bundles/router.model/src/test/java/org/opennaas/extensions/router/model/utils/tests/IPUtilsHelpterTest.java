package org.opennaas.extensions.router.model.utils.tests;

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
