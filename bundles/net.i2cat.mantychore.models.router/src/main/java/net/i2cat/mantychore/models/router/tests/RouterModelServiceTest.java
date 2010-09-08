package net.i2cat.mantychore.models.router.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.i2cat.mantychore.models.router.RouterModel;
import net.i2cat.mantychore.models.router.RouterModelService;

public class RouterModelServiceTest {
	private RouterModelService	client	= null;

	@Before
	public void setup() {
	}

	@Test
	public void testGetPlatformData() {
		try {
			RouterModel model = client.getModel();
			System.out.println(model.toString());
			String routername = model.getRouterName();
			Assert.assertNotNull(routername);

		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.assertTrue(false);
		}
	}

}
