package net.i2cat.mantychore.models.router.tests;

import net.i2cat.mantychore.models.router.RouterModel;
import net.i2cat.mantychore.models.router.RouterModelService;
import net.i2cat.mantychore.models.router.internal.RouterModelServiceImpl;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RouterModelServiceTest {
	private RouterModelService				client	= null;
	private static JaxWsServerFactoryBean	server;

	@BeforeClass
	public static void initJaxFactory() {
		// Loading a server for testing the web services
		server = new JaxWsServerFactoryBean();
		server.setServiceClass(RouterModelServiceImpl.class);
		RouterModelServiceImpl service = new RouterModelServiceImpl();
		server.setServiceBean(service);
		server.setAddress("http://localhost:8080/routerModelServiceImpl");
		server.create();
	}

	@Before
	public void setup() {
		// Create a web service client
		createWebServiceClient();
	}

	private void createWebServiceClient() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(RouterModelService.class);
		factory.setAddress("http://localhost:8080/routerModelServiceImpl");
		client = (RouterModelService) factory.create();
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
