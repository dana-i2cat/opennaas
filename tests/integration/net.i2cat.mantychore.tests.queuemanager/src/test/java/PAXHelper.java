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

import org.apache.karaf.testing.Helper;
import org.ops4j.pax.exam.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAXHelper {
	
	public static final String		WORKING_DIRECTORY				= "target/paxrunner/features/";
	
	public static final Option		MANTYCHORE_REPOS				= repositories(
																				   "http://repository.inocybe.ca/content/groups/public/",
																				   "http://repository.inocybe.ca/content/groups/public-snapshots/",
																				   "http://repo.fusesource.com/maven2",
																				   "http://repo1.maven.org/maven2");
	public static final Option[]	REPOSITORIES					= options(excludeDefaultRepositories(), MANTYCHORE_REPOS);
	
	/* specify log level */
	public static final Option[]	HELPER_DEFAULT_OPTIONS			= Helper.getDefaultOptions(
																							   systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level")
																							   .value("INFO"));
	
	public static final Option		OPT_WORKING_DIRECTORY			= workingDirectory(WORKING_DIRECTORY);
	
	/* service mix features */
	
	public static final String		SERVICE_MIX_FEATURES_REPO	= "mvn:org.apache.servicemix/apache-servicemix/4.3.0-fuse-01-00/xml/features";
	public static final String		KARAF_FEATURES_REPO	= "mvn:org.apache.karaf/apache-karaf/2.0.0/xml/features";
	public static final String		ACTIVEMQ_FEATURES_REPO	= "mvn:org.apache.activemq/activemq-karaf/5.4.0-fuse-00-00/xml/features";
	
	public static final String[]	SERVICE_MIX_FEATURES		= { "servicemix-cxf-bc"};
	public static final String[]	KARAF_FEATURES		= { "spring-dm" };
	public static final String[]	ACTIVEMQ_FEATURES		= { "activemq-blueprint"};
	
	public static final Option		OPT_SERVICE_MIX_FEATURES		= scanFeatures(SERVICE_MIX_FEATURES_REPO, SERVICE_MIX_FEATURES);
	public static final Option		OPT_KARAF_FEATURES		= scanFeatures(KARAF_FEATURES_REPO, KARAF_FEATURES);
	public static final Option		OPT_ACTIVEMQ_FEATURES		= scanFeatures(ACTIVEMQ_FEATURES_REPO, ACTIVEMQ_FEATURES);

	
	/* mantychore features */
	
	private static final String		MTCHORE_FEATURES_REPO			= "mvn:net.i2cat.mantychore/mantychore/1.0.0-SNAPSHOT/xml/features";
	
	private static final String[]	MTCHORE_FEATURES				= { "i2cat-mantychore-core" };
	private static final Option		OPT_MANTYCHORE_FEATURES			= scanFeatures(MTCHORE_FEATURES_REPO, MTCHORE_FEATURES);
	
	static Logger					log								= LoggerFactory.getLogger(PAXHelper.class);
	
	/**
	 * Bundle loader
	 * 
	 * @param bundleDescriptor
	 * @return
	 */
	private static Option addBundle(String[] bundleDescriptor) {
		if (bundleDescriptor.length == 3) {
			return mavenBundle().groupId(bundleDescriptor[0]).artifactId(bundleDescriptor[1]).version(bundleDescriptor[2]);
		} else {
			return mavenBundle().groupId(bundleDescriptor[0]).artifactId(bundleDescriptor[1]);
		}
		
	}
	
	public static long	minInMillis	= 60;	// 60 secs * 1000 (1 milli)
	
	public static Option[] newSimpleTest() {
		long waitInMillis = minInMillis * 1000;
		Option[] optssimpleTest = combine(HELPER_DEFAULT_OPTIONS
										  , OPT_WORKING_DIRECTORY // directory where pax-runner saves OSGi
										  // bundles
										  // ,
										  // vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5006")
										  , waitForFrameworkStartup() // wait for a length of time
										  , equinox());
		Option[] opts_with_repos = combine(optssimpleTest, REPOSITORIES); // repositories
		return opts_with_repos;
		
	}
	
	public static Option[] newServiceMixTest() {
		Option[] optssimpleTest = newSimpleTest();
		
		Option[] optsKaraf = combine(optssimpleTest, OPT_KARAF_FEATURES);
		Option[] optsActiveMQ = combine(optsKaraf, OPT_SERVICE_MIX_FEATURES);
		Option[] optsServiceMix = combine(optsActiveMQ, OPT_SERVICE_MIX_FEATURES); // service
		
		return optsServiceMix;
		
	}
	
	public static Option[] newExampleTest() {
		return combine(HELPER_DEFAULT_OPTIONS, OPT_SERVICE_MIX_FEATURES);
	}
	
	public static Option[] newQueueManagerTest() {
		
		Option[] optsServiceMix = newServiceMixTest();
		Option[] opts_with_Mantychore = combine(optsServiceMix, OPT_MANTYCHORE_FEATURES); // service

		return opts_with_Mantychore;
	}

}
