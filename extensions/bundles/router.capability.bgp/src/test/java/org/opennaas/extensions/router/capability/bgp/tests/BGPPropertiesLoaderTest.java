package org.opennaas.extensions.router.capability.bgp.tests;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.extensions.router.capability.bgp.BGPModelFactory;
import org.opennaas.extensions.router.model.ComputerSystem;

public class BGPPropertiesLoaderTest {

	private static final String	BGP_PROPERTIES_PATH	= "/absolute/path/to/bgp.properties";

	public static void main(String args[]) throws Exception {
		checkPropertiesAreCorrect(BGP_PROPERTIES_PATH);
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
