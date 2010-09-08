package net.i2cat.mantychore.tests;

import org.junit.runner.RunWith;

/**
 * This class contains the integration TCP tests of the sockets bundle. It
 * checks that the bundle is activated, and that the services are published in
 * the registry as expected.
 * 
 * @author eduardgrasa
 * 
 */
@RunWith(JUnit4TestRunner.class)
public class NetconfProtocolTest extends AbstractIntegrationTest {

	// @Inject
	// BundleContext bundleContext = null;
	//	
	// private ITransportFactory tcpTransportFactory = null;
	// private ITransportFactory sslTransportFactory = null;
	//	
	// @Configuration
	// public static Option[] configure()
	// {
	// return combine(
	// // Default karaf environment
	// Helper.getDefaultOptions(
	// // this is how you set the default log level when using pax logging
	// (logProfile)
	// systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("DEBUG")),
	// // add the required servicemix features
	// scanFeatures(IaaSIntegrationTestsHelper.SERVICE_MIX_FEATURES_REPO,
	// IaaSIntegrationTestsHelper.SERVICE_MIX_FEATURES),
	// workingDirectory(IaaSIntegrationTestsHelper.WORKING_DIRECTORY),
	// //add the iaas bundles
	// mavenBundle().groupId("com.iaasframework.extras").artifactId("com.iaasframework.extras.itesthelper"),
	// mavenBundle().groupId( "org.dynamicjava" ).artifactId(
	// "classloading-utils" ).version("1.0.0"),
	// mavenBundle().groupId( "org.dynamicjava" ).artifactId(
	// "service-binding-utils" ).version("1.0.0"),
	// mavenBundle().groupId("org.apache.geronimo.specs").artifactId("geronimo-jpa_1.0_spec"),
	// mavenBundle().groupId("org.hsqldb").artifactId("com.springsource.org.hsqldb"),
	// mavenBundle().groupId( "com.iaasframework.core" ).artifactId(
	// "com.iaasframework.core.hibernate" ),
	// mavenBundle().groupId( "com.iaasframework.core" ).artifactId(
	// "com.iaasframework.core.persistence" ),
	// mavenBundle().groupId( "com.iaasframework.core" ).artifactId(
	// "com.iaasframework.core.resourcecore" ),
	// mavenBundle().groupId( "com.iaasframework.core" ).artifactId(
	// "com.iaasframework.core.transports" ),
	// mavenBundle().groupId( "com.iaasframework.transports" ).artifactId(
	// "com.iaasframework.transports.sockets" ),
	// waitForFrameworkStartup(),
	// equinox()
	// );
	// }
	//    
	// @Before
	// public void setup(){
	// tcpTransportFactory = getOsgiService(ITransportFactory.class,
	// "transport=TCP", 20000);
	// sslTransportFactory = getOsgiService(ITransportFactory.class,
	// "transport=SSL", 20000);
	// }
	//
	// public void bundleContextShouldNotBeNull(){
	// assertNotNull(bundleContext);
	// }
	//    
	// @Test
	// public void testAll(){
	// IaaSIntegrationTestsHelper.listBundles(bundleContext);
	// bundleContextShouldNotBeNull();
	// testTCPTransportFactoryRegistered();
	// testSSLTransportFactoryRegistered();
	// }
	//    
	// private void testTCPTransportFactoryRegistered(){
	// assertNotNull(tcpTransportFactory);
	// }
	//    
	// private void testSSLTransportFactoryRegistered(){
	// assertNotNull(sslTransportFactory);
	// }
}