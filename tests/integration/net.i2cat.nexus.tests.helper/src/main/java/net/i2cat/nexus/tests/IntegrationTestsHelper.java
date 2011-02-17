package net.i2cat.nexus.tests;

import static org.ops4j.pax.exam.CoreOptions.equinox;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.excludeDefaultRepositories;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.repositories;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.workingDirectory;

import java.util.ArrayList;
import java.util.List;

import org.apache.karaf.testing.Helper;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTestsHelper {
	
	private static Logger log	= LoggerFactory.getLogger(IntegrationTestsHelper.class);
	
	public static Option[] getSimpleTestOptions() {
		String	WORKING_DIRECTORY = "target/paxrunner/features/";
		Option	REPOS = repositories("http://repo.fusesource.com/maven2",
												"http://repository.springsource.com/maven/bundles/external",
												"http://repository.springsource.com/maven/bundles/release",
												"http://repo1.maven.org/maven2", 
												"http://repository.ops4j.org/maven2",
												"http://repository.inocybe.ca/content/groups/public");
		
		/* specify log level */
		Option[] HELPER_DEFAULT_OPTIONS	= Helper.getDefaultOptions(systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level")
																						   .value("INFO"));
		Option	 OPT_WORKING_DIRECTORY	= workingDirectory(WORKING_DIRECTORY);
		
		Option[] optssimpleTest = combine(HELPER_DEFAULT_OPTIONS
										  , OPT_WORKING_DIRECTORY // directory where pax-runner saves OSGi
										  , waitForFrameworkStartup() // wait for a length of time
										  , equinox(), REPOS);
		return optssimpleTest;
	}
	
	public static Option[] getFuseTestOptions() {
		/* fuse features */
		String	SERVICE_MIX_FEATURES_REPO	= "mvn:org.apache.servicemix/apache-servicemix/4.3.0-fuse-01-00/xml/features";
		String	KARAF_FEATURES_REPO	= "mvn:org.apache.karaf/apache-karaf/2.0.0/xml/features";
		String	ACTIVEMQ_FEATURES_REPO	= "mvn:org.apache.activemq/activemq-karaf/5.4.0-fuse-00-00/xml/features";
		
		String[] SERVICE_MIX_FEATURES = { "servicemix-cxf-bc"};
		String[] KARAF_FEATURES	= { "spring-dm" };
		String[] ACTIVEMQ_FEATURES	= { "activemq-blueprint"};
		
		Option	OPT_SERVICE_MIX_FEATURES = scanFeatures(SERVICE_MIX_FEATURES_REPO, SERVICE_MIX_FEATURES);
		Option	OPT_KARAF_FEATURES = scanFeatures(KARAF_FEATURES_REPO, KARAF_FEATURES);
		Option	OPT_ACTIVEMQ_FEATURES = scanFeatures(ACTIVEMQ_FEATURES_REPO, ACTIVEMQ_FEATURES);
		
		Option[] optsKaraf = combine(getSimpleTestOptions(), OPT_KARAF_FEATURES);
		Option[] optsActiveMQ = combine(optsKaraf, OPT_ACTIVEMQ_FEATURES);
		return combine(optsActiveMQ, OPT_SERVICE_MIX_FEATURES);
	}
	
	public static Option[] getMantychoreTestOptions() {
		/* mantychore features */
		String	MTCHORE_FEATURES_REPO = "mvn:net.i2cat.mantychore/mantychore/1.0.0-SNAPSHOT/xml/features";
		String[] MTCHORE_FEATURES	= { "i2cat-mantychore-core" };
		Option	OPT_MANTYCHORE_FEATURES	= scanFeatures(MTCHORE_FEATURES_REPO, MTCHORE_FEATURES);
		return combine(getFuseTestOptions(), OPT_MANTYCHORE_FEATURES); // service
	}
	
	public static void waitForAllBundlesActive(BundleContext bundleContext){
		int MAX_RETRIES = 100;
		Bundle b = null;
		boolean active = true;
		List<Integer> fragments = new ArrayList<Integer>();
		
		for(int i=0; i<MAX_RETRIES; i++){
			active = true;
			for(int j=0; j<bundleContext.getBundles().length; j++){
				if (!fragments.contains(new Integer(j))){
					if (bundleContext.getBundles()[j].getState() == Bundle.RESOLVED){
						active = false;
						try{
							bundleContext.getBundles()[j].start();
						}catch(Exception ex){
							ex.printStackTrace();
							if (ex.getMessage().indexOf("fragment") != -1){
								fragments.add(new Integer(j));
							}
						}
					}
				}
			}
			
			if (active == true){
				break;
			}
			
			listBundles(bundleContext);
			log.info("Waiting for the activation of all the bundles, this is the "+i+" try. Sleeping for 1 second");
			try{
				Thread.sleep(1000);
			}catch(InterruptedException ex){
				ex.printStackTrace();
			}
		}
		
		listBundles(bundleContext);
	}
		
	
	public static void listBundles(BundleContext bundleContext) {
		Bundle b = null;
		String listBundles = "";
		for (int i = 0; i < bundleContext.getBundles().length; i++) {
			b = bundleContext.getBundles()[i];
			listBundles += b.toString() + " : " + getStateString(b.getState()) + '\n';
			if (getStateString(b.getState()).equals("INSTALLED")) {
				try {
					b.start();
				} catch (Exception e) {
					listBundles += "ERROR: " + e.getMessage() + '\n';
					e.printStackTrace();
				}
			}
		}
		log.info(listBundles);
	}
	
	private static String getStateString(int value) {
		if (value == Bundle.ACTIVE) {
			return "ACTIVE";
		} else if (value == Bundle.INSTALLED) {
			return "INSTALLED";
		} else if (value == Bundle.RESOLVED) {
			return "RESOLVED";
		} else if (value == Bundle.UNINSTALLED) {
			return "UNINSTALLED";
		}

		return "UNKNOWN";
	}

}
