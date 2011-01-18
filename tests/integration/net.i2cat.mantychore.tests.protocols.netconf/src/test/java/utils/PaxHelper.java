package utils;

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

import org.apache.felix.karaf.testing.Helper;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.iaasframework.extras.itesthelper.IaaSIntegrationTestsHelper;

public class PaxHelper {

	public static final Option		MANTYCHORE_REPOS				= repositories(
																			"http://repository.inocybe.ca/content/groups/public/",
																			"http://repository.inocybe.ca/content/groups/public-snapshots/",
																			"http://repo.fusesource.com/maven2",
																			"http://repo1.maven.org/maven2");
	public static final Option[]	REPOSITORIES					= options(excludeDefaultRepositories(), MANTYCHORE_REPOS);

	public static final Option[]	HELPER_DEFAULT_OPTIONS			= Helper.getDefaultOptions(
																			systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level")
																					.value("WARN"));

	public static final Option		OPT_WORKING_DIRECTORY			= workingDirectory(IaaSIntegrationTestsHelper.WORKING_DIRECTORY);

	/* service mix features */

	public static final String		SERVICE_MIX_FEATURES_REPO_V4_3	= "mvn:org.apache.servicemix/apache-servicemix/4.3.0-fuse-01-00/xml/features";

	public static final String[]	SERVICE_MIX_FEATURES_V4_3		= { "servicemix-cxf-bc", "camel", "camel-cxf", "camel-jms", "camel-jaxb" };

	// public static final Option OPT_SERVICE_MIX_FEATURES =
	// scanFeatures(IaaSIntegrationTestsHelper.SERVICE_MIX_FEATURES_REPO,IaaSIntegrationTestsHelper.SERVICE_MIX_FEATURES);
	public static final Option		OPT_SERVICE_MIX_FEATURES		= scanFeatures(SERVICE_MIX_FEATURES_REPO_V4_3, SERVICE_MIX_FEATURES_V4_3);
	/* iaas features */

	public static final String		IAAS_FEATURES_REPO				= "mvn:com.iaasframework/iaas-framework/1.0.0/xml/features";

	public static final String[]	IAAS_FEATURES					= { "iaas-thirdparty", "iaas-core", "iaas-capabilities", "iaas-extras" };

	public static final Option		OPT_IAAS_FEATURES				= scanFeatures(IAAS_FEATURES_REPO, IAAS_FEATURES);

	/* mantychore features */

	private static final String		MTCHORE_FEATURES_REPO			= "mvn:net.i2cat.mantychore/mantychore/1.0.0-SNAPSHOT/xml/features";

	private static final String[]	MTCHORE_FEATURES				= { "i2cat-mantychore-core" };
	public static final Option		OPT_MANTYCHORE_FEATURES			= scanFeatures(MTCHORE_FEATURES_REPO, MTCHORE_FEATURES);

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

	public static long	minInMillis	= 120;	// 120 secs * 1000 (1 milli)

	public static Option[] newSimpleTest() {
		long waitInMillis = minInMillis * 1000;
		return combine(HELPER_DEFAULT_OPTIONS
				, OPT_WORKING_DIRECTORY // directory where pax-runner saves OSGi
				// bundles
				// ,
				// vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5006")
				, waitForFrameworkStartup() // wait for a length of time
				, equinox());

	}

	public static Option[] newServiceMixTest() {
		Option[] optssimpleTest = newSimpleTest();
		Option[] opts_with_repos = combine(optssimpleTest, REPOSITORIES); // repositories
		Option[] optsServiceMix = combine(opts_with_repos, OPT_SERVICE_MIX_FEATURES); // service
		// mix
		return optsServiceMix;

	}

	public static Option[] newExampleTest() {
		return combine(HELPER_DEFAULT_OPTIONS, OPT_SERVICE_MIX_FEATURES);
	}

	public static void listBundles(BundleContext bundleContext) {
		Bundle b = null;
		for (int i = 0; i < bundleContext.getBundles().length; i++) {
			b = bundleContext.getBundles()[i];
			System.out.println(b.toString() + " : " + getStateString(b.getState()));
			if (getStateString(b.getState()).equals("INSTALLED")) {
				try {
					b.start();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
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
