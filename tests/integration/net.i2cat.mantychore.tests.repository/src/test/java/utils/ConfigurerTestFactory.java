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

import com.iaasframework.extras.itesthelper.IaaSIntegrationTestsHelper;

public class ConfigurerTestFactory {

	public static final Option[]	REPOSITORIES				= options(excludeDefaultRepositories(),
																		repositories(
																				"http://repository.inocybe.ca/content/groups/public/",
																				"http://repository.inocybe.ca/content/groups/public-snapshots/",
																				"http://repo.fusesource.com/maven2",
																				"http://repo1.maven.org/maven2")
																				);

	public static final Option[]	HELPER_DEFAULT_OPTIONS		= Helper.getDefaultOptions(
																		systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level")
																				.value("WARN"));

	public static final Option		OPT_WORKING_DIRECTORY		= workingDirectory(IaaSIntegrationTestsHelper.WORKING_DIRECTORY);

	/* service mix features */

	public static final Option		OPT_SERVICE_MIX_FEATURES	= scanFeatures(IaaSIntegrationTestsHelper.SERVICE_MIX_FEATURES_REPO,
																		IaaSIntegrationTestsHelper.SERVICE_MIX_FEATURES);

	/* iaas features */

	public static final String		IAAS_FEATURES_REPO			= "mvn:com.iaasframework/iaas-framework/1.0.0/xml/features";

	public static final String[]	IAAS_FEATURES				= { "iaas-thirdparty", "iaas-core", "iaas-capabilities", "iaas-extras" };

	public static final Option		OPT_IAAS_FEATURES			= scanFeatures(IAAS_FEATURES_REPO, IAAS_FEATURES);

	/* mantychore features */

	private static final String		MTCHORE_FEATURES_REPO		= "mvn:net.i2cat.mantychore/net-i2cat-mantychore/1.0.0-SNAPSHOT/xml/features";

	private static final String[]	MTCHORE_FEATURES			= { "i2cat-mantychore-core" };
	private static final Option		OPT_MANTYCHORE_FEATURES		= scanFeatures(MTCHORE_FEATURES_REPO, MTCHORE_FEATURES);

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

	public static Option[] newServiceMixTest() {
		/* prepare fuse container */
		Option[] opts_features = options(OPT_SERVICE_MIX_FEATURES, OPT_WORKING_DIRECTORY,
				// vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5006"),
				waitForFrameworkStartup(),
				equinox()); // FIXME It is necessary to use a felix osgi
							// container

		/* prepare IaaS dependencies */
		Option[] opts_features_iaas = { OPT_IAAS_FEATURES };

		/* specify repositories */
		Option[] opts_without_iaas = combine(REPOSITORIES, opts_features);
		Option[] opts_without_mantychore = combine(opts_features_iaas, opts_without_iaas);

		/* prepare mantychore dependencies */
		Option[] opts_features_mantychore = { OPT_MANTYCHORE_FEATURES };

		Option[] opts_with_features = combine(opts_features_mantychore, opts_without_mantychore);

		Option[] options = combine(HELPER_DEFAULT_OPTIONS, opts_with_features);
		return options;

	}

	public static Option[] newResourceManagerTest() {
		return combine(
				newServiceMixTest(),
				// add the iaas bundles
				mavenBundle().groupId("com.iaasframework.extras").artifactId("com.iaasframework.extras.itesthelper"), // for
				// testing
				mavenBundle().groupId("net.i2cat.mantychore.repository").artifactId("net.i2cat.mantychore.repository.junos")

		);

	}
}
