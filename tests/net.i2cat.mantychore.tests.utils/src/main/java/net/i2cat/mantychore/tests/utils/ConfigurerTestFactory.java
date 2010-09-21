package net.i2cat.mantychore.tests.utils;

import static org.ops4j.pax.exam.CoreOptions.equinox;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.excludeDefaultRepositories;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.repositories;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
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
																				"http://repo1.maven.org/maven2"));

	public static final Option[]	HELPER_DEFAULT_OPTIONS		= Helper.getDefaultOptions(
																		systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level")
																				.value("WARN"));

	public static final Option		OPT_WORKING_DIRECTORY		= workingDirectory(IaaSIntegrationTestsHelper.WORKING_DIRECTORY);

	/* service mix features */

	public static final Option		OPT_SERVICE_MIX_FEATURES	= scanFeatures(IaaSIntegrationTestsHelper.SERVICE_MIX_FEATURES_REPO,
																		IaaSIntegrationTestsHelper.SERVICE_MIX_FEATURES);

	/* iaas features */

	public static final String		IAAS_FEATURES_REPO			= "mvn:iaas.framework/com-iaasframework/1.0.0/xml/features";

	public static final String[]	IAAS_FEATURES				= { "iaasframework-core", "iaasframework-capabilities", "iaasframework-extras" };

	public static final Option		OPT_IAAS_FEATURES			= scanFeatures(IAAS_FEATURES_REPO, IAAS_FEATURES);

	/* mantychore features */

	public static final String		MTCHORE_FEATURES_REPO		= "mvn:net.i2cat.mantychore/net-i2cat-mantychore/1.0.0-SNAPSHOT/xml/features";

	public static final String[]	MTCHORE_FEATURES			= { "i2cat-mantychore-models" };

	public static final Option		OPT_MTCHORE_FEATURES		= scanFeatures(MTCHORE_FEATURES_REPO, IaaSIntegrationTestsHelper.SERVICE_MIX_FEATURES);

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
		Option[] opts_features = options(OPT_SERVICE_MIX_FEATURES, OPT_WORKING_DIRECTORY,
				vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5006"),
				waitForFrameworkStartup(),
				equinox());

		Option[] opts_with_repos = combine(REPOSITORIES, opts_features);
		Option[] options = combine(HELPER_DEFAULT_OPTIONS, opts_with_repos);
		return options;

	}

	public static Option[] newModelTest() {

		return combine(
				newServiceMixTest(),
				// add the iaas bundles
				mavenBundle().groupId("com.iaasframework.extras").artifactId("com.iaasframework.extras.itesthelper"),
				mavenBundle().groupId("org.dynamicjava").artifactId("classloading-utils").version("1.0.0"),
				mavenBundle().groupId("org.dynamicjava").artifactId("service-binding-utils").version("1.0.0"),
				mavenBundle().groupId("org.apache.geronimo.specs").artifactId("geronimo-jpa_1.0_spec"),
				mavenBundle().groupId("org.hsqldb").artifactId("com.springsource.org.hsqldb"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.hibernate"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.persistence"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.resourcecore"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.model.soapendpoint"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.model"));

	}

	public static Option[] newTransportTest() {

		return combine(
				newServiceMixTest(),
				// add the iaas bundles
				mavenBundle().groupId("com.iaasframework.extras").artifactId("com.iaasframework.extras.itesthelper"),
				mavenBundle().groupId("org.dynamicjava").artifactId("classloading-utils").version("1.0.0"),
				mavenBundle().groupId("org.dynamicjava").artifactId("service-binding-utils").version("1.0.0"),
				mavenBundle().groupId("org.apache.geronimo.specs").artifactId("geronimo-jpa_1.0_spec"),
				mavenBundle().groupId("org.hsqldb").artifactId("com.springsource.org.hsqldb"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.hibernate"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.persistence"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.resourcecore"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.transports"),
				mavenBundle().groupId("com.iaasframework.transports").artifactId("com.iaasframework.transports.sockets"));

	}

	public static Option[] newActionTest() {
		return combine(
				newServiceMixTest(),
				// add the iaas bundles
				mavenBundle().groupId("com.iaasframework.extras").artifactId("com.iaasframework.extras.itesthelper"),
				mavenBundle().groupId("org.dynamicjava").artifactId("classloading-utils").version("1.0.0"),
				mavenBundle().groupId("org.dynamicjava").artifactId("service-binding-utils").version("1.0.0"),
				mavenBundle().groupId("org.apache.geronimo.specs").artifactId("geronimo-jpa_1.0_spec"),
				mavenBundle().groupId("org.hsqldb").artifactId("com.springsource.org.hsqldb"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.hibernate"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.persistence"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.resourcecore"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.transports"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.protocol"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.eventset"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.model"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.commandset"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.actionset"));
	}

	public static Option[] newCommandTest() {
		return combine(
				newServiceMixTest(),
				// add the iaas bundles
				mavenBundle().groupId("com.iaasframework.extras").artifactId("com.iaasframework.extras.itesthelper"),
				mavenBundle().groupId("org.dynamicjava").artifactId("classloading-utils").version("1.0.0"),
				mavenBundle().groupId("org.dynamicjava").artifactId("service-binding-utils").version("1.0.0"),
				mavenBundle().groupId("org.apache.geronimo.specs").artifactId("geronimo-jpa_1.0_spec"),
				mavenBundle().groupId("org.hsqldb").artifactId("com.springsource.org.hsqldb"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.hibernate"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.persistence"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.resourcecore"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.transports"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.protocol"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.eventset"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.model"),
				mavenBundle().groupId("com.iaasframework.capabilities").artifactId("com.iaasframework.capabilities.commandset"));
	}

	public static Option[] newResourceManagerTest() {
		return combine(
				newServiceMixTest(),
				// add the iaas bundles
				mavenBundle().groupId("com.iaasframework.extras").artifactId("com.iaasframework.extras.itesthelper"),
				mavenBundle().groupId("org.dynamicjava").artifactId("classloading-utils").version("1.0.0"),
				mavenBundle().groupId("org.dynamicjava").artifactId("service-binding-utils").version("1.0.0"),
				mavenBundle().groupId("org.apache.geronimo.specs").artifactId("geronimo-jpa_1.0_spec"),
				mavenBundle().groupId("org.hsqldb").artifactId("com.springsource.org.hsqldb"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.hibernate"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.persistence"),
				mavenBundle().groupId("com.iaasframework.core").artifactId("com.iaasframework.core.resourcecore"),
				/* mantichore dependencies */
				mavenBundle().groupId("net.i2cat.mantychore.tests").artifactId("net.i2cat.mantychore.tests.utils")

		);

	}
}
