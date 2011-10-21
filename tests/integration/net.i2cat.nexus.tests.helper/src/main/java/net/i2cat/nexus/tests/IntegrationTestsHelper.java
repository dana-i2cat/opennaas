package net.i2cat.nexus.tests;

import static org.ops4j.pax.exam.CoreOptions.equinox;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.repositories;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.workingDirectory;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.Helper;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class IntegrationTestsHelper {

	private static Log	log	= LogFactory.getLog(IntegrationTestsHelper.class);

	public static Option[] getSimpleTestOptions() {
		String WORKING_DIRECTORY = "target/paxrunner/features/";
		// Option REPOS = repositories("http://repo.fusesource.com/maven2",
		// "http://repository.springsource.com/maven/bundles/external",
		// "http://repository.springsource.com/maven/bundles/release",
		// "http://repo1.maven.org/maven2",
		// "http://repository.ops4j.org/maven2",
		// "http://repository.inocybe.ca/content/groups/public");

		// TODO DELETE INOCYBE REPOSITORY
		Option REPOS = repositories("http://maven.i2cat.net:8081/artifactory/repo", "http://repo1.maven.org/maven2");
		// ,"http://repository.inocybe.ca/content/groups/public");

		/* specify log level */

		Option[] HELPER_DEFAULT_OPTIONS = Helper.getDefaultOptions(systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level")
				// .value("INFO"));
				.value("DEBUG"));
		Option OPT_WORKING_DIRECTORY = workingDirectory(WORKING_DIRECTORY);
		Option OPT_NOVERIFY = vmOption("-noverify");

		Option[] optssimpleTest = combine(HELPER_DEFAULT_OPTIONS
											, OPT_WORKING_DIRECTORY // directory where pax-runner saves OSGi
				, waitForFrameworkStartup() // wait for a length of time
				, equinox(), REPOS, OPT_NOVERIFY);
		return optssimpleTest;
	}

	// public static Option[] getFuseOptions() {
	// /* fuse features */
	// String SERVICE_MIX_FEATURES_REPO = "mvn:org.apache.servicemix/apache-servicemix/4.4.0-fuse-00-43/xml/features";
	// String[] SERVICE_MIX_FEATURES = new String[] { "activemq", "karaf-framework", "config", "cxf", "activemq" };
	// Option OPT_SERVICE_MIX_FEATURES = scanFeatures(SERVICE_MIX_FEATURES_REPO, SERVICE_MIX_FEATURES);
	// return combine(getSimpleTestOptions(), OPT_SERVICE_MIX_FEATURES);
	// }

	public static Option[] getKarafFrameworkOptions() {
		/* fuse features */
		String SERVICE_MIX_FEATURES_REPO = "mvn:org.apache.karaf.assemblies.features/standard/2.2.0-fuse-00-43/xml/features";
		String[] SERVICE_MIX_FEATURES = new String[] { "karaf-framework" };
		Option OPT_SERVICE_MIX_FEATURES = scanFeatures(SERVICE_MIX_FEATURES_REPO, SERVICE_MIX_FEATURES);
		return combine(getSimpleTestOptions(), OPT_SERVICE_MIX_FEATURES);
	}

	public static Option[] getConfigOptions() {
		/* fuse features */
		String SERVICE_MIX_FEATURES_REPO = "mvn:org.apache.karaf.assemblies.features/standard/2.2.0-fuse-00-43/xml/features";
		String[] SERVICE_MIX_FEATURES = new String[] { "config" };
		Option OPT_SERVICE_MIX_FEATURES = scanFeatures(SERVICE_MIX_FEATURES_REPO, SERVICE_MIX_FEATURES);
		return combine(getKarafFrameworkOptions(), OPT_SERVICE_MIX_FEATURES);
	}

	public static Option[] getCXFOptions() {
		/* Opennaas features */
		String OPENNAAS_FEATURE_REPO = "mvn:org.apache.servicemix/apache-servicemix/4.4.0-fuse-00-43/xml/features";
		String[] OPENNAAS_FEATURES = { "cxf" };
		// String[] FUSE_FEATURES = { "opennaas-core-deps" };
		Option OPT_OPENNAAS_FEATURES = scanFeatures(OPENNAAS_FEATURE_REPO, OPENNAAS_FEATURES);
		return combine(getConfigOptions(), OPT_OPENNAAS_FEATURES);
	}

	public static Option[] getActiveMQOptions() {
		/* Opennaas features */
		String OPENNAAS_FEATURE_REPO = "mvn:org.apache.activemq/activemq-karaf/5.5.0-fuse-00-43/xml/features";
		String[] OPENNAAS_FEATURES = { "activemq" };
		// String[] FUSE_FEATURES = { "opennaas-core-deps" };
		Option OPT_OPENNAAS_FEATURES = scanFeatures(OPENNAAS_FEATURE_REPO, OPENNAAS_FEATURES);
		return combine(getCXFOptions(), OPT_OPENNAAS_FEATURES);
	}

	// public static Option[] getFuseOptions() {
	// /* fuse features */
	// String SERVICE_MIX_FEATURES_REPO = "mvn:org.apache.servicemix/apache-servicemix/4.4.0-fuse-00-43/xml/features";
	// String[] SERVICE_MIX_FEATURES = new String[] { "activemq", "karaf-framework", "config", "cxf", "activemq" };
	// Option OPT_SERVICE_MIX_FEATURES = scanFeatures(SERVICE_MIX_FEATURES_REPO, SERVICE_MIX_FEATURES);
	// return combine(getActiveMQOptions(), OPT_SERVICE_MIX_FEATURES);
	// }

	public static Option[] getOpennaasOptions() {
		/* Opennaas features */
		String OPENNAAS_FEATURE_REPO = "mvn:org.opennaas/opennaas-core-features/1.0.0-SNAPSHOT/xml/features";
		String[] OPENNAAS_FEATURES = { "opennaas-core", "opennaas-core-deps" };
		// String[] FUSE_FEATURES = { "opennaas-core-deps" };
		Option OPT_OPENNAAS_FEATURES = scanFeatures(OPENNAAS_FEATURE_REPO, OPENNAAS_FEATURES);
		return combine(getActiveMQOptions(), OPT_OPENNAAS_FEATURES);
	}

	public static Option[] getMantychoreTestOptions() {
		/* mantychore features */
		String MTCHORE_FEATURES_REPO = "mvn:net.i2cat.mantychore/mantychore/1.0.0-SNAPSHOT/xml/features";
		String[] MTCHORE_FEATURES = { "i2cat-mantychore-core" };
		Option OPT_MANTYCHORE_FEATURES = scanFeatures(MTCHORE_FEATURES_REPO, MTCHORE_FEATURES);
		return combine(getOpennaasOptions(), OPT_MANTYCHORE_FEATURES);
	}

	public static Option[] getLuminisTestOptions() {
		/* luminis features */
		String MTCHORE_FEATURES_REPO = "mvn:net.i2cat.mantychore/mantychore/1.0.0-SNAPSHOT/xml/features";
		String[] MTCHORE_FEATURES = { "i2cat-luminis-core" };
		Option OPT_MANTYCHORE_FEATURES = scanFeatures(MTCHORE_FEATURES_REPO, MTCHORE_FEATURES);
		return combine(getOpennaasOptions(), OPT_MANTYCHORE_FEATURES); // service
	}

	public static Option[] getMantychoreLuminisTestOptions() {

		String MTCHORE_FEATURES_REPO = "mvn:net.i2cat.mantychore/mantychore/1.0.0-SNAPSHOT/xml/features";
		String[] MTCHORE_FEATURES = { "i2cat-mantychore-core", "i2cat-luminis-core" };
		Option OPT_MANTYCHORE_FEATURES = scanFeatures(MTCHORE_FEATURES_REPO, MTCHORE_FEATURES);
		return combine(getOpennaasOptions(), OPT_MANTYCHORE_FEATURES); // service
	}

	public static Option[] getNexusTestOptions() {
		/* luminis features */
		String MTCHORE_FEATURES_REPO = "mvn:net.i2cat.mantychore/mantychore/1.0.0-SNAPSHOT/xml/features";
		String[] MTCHORE_FEATURES = { "i2cat-commons" };
		Option OPT_MANTYCHORE_FEATURES = scanFeatures(MTCHORE_FEATURES_REPO, MTCHORE_FEATURES);
		return combine(getOpennaasOptions(), OPT_MANTYCHORE_FEATURES); // service
	}

	/**
	 * Wait for all bundles to be active, tries to start non active bundles.
	 */
	public static void waitForAllBundlesActive(BundleContext bundleContext) {

		log.info("Waiting for activation of all bundles");

		int MAX_RETRIES = 20;
		Bundle b = null;
		boolean active = true;
		List<Integer> fragments = new ArrayList<Integer>();
		String strBundles;

		for (int i = 0; i < MAX_RETRIES; i++) {
			active = true;
			for (int j = 0; j < bundleContext.getBundles().length; j++) {
				if (!fragments.contains(new Integer(j))) {
					if (bundleContext.getBundles()[j].getState() == Bundle.RESOLVED) {
						active = false;
						try {
							bundleContext.getBundles()[j].start();
						} catch (Exception ex) {
							ex.printStackTrace();
							if (ex.getMessage().indexOf("fragment") != -1) {
								fragments.add(new Integer(j));

								Dictionary headers_dic = bundleContext.getBundles()[j].getHeaders();
								Enumeration headers = headers_dic.keys();
								log.info("Fragment headers:");
								while (headers.hasMoreElements()) {
									log.info(headers.nextElement());
								}
							}
						}
					}
				}
			}

			if (active) {
				break;
			}

			strBundles = listBundles(bundleContext);
			log.info("Waiting for activation of all bundles, this is the " + i + " try. Sleeping for 1 second");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				break;
			}
		}

		strBundles = listBundles(bundleContext);
		String fragmentsNums = "";
		for (Integer num : fragments) {
			fragmentsNums += num.toString() + ", ";
		}
		log.info("Detected " + fragments.size() + " fragments: " + fragmentsNums);

		if (active)
			log.info("All the bundles activated. Waiting for 15 seconds more to allow Blueprint to publish all the services into the OSGi registry");
		else
			log.warn("MAX RETRIES REACHED!!! Waiting for 15 seconds more to allow Blueprint to publish all the services into the OSGi registry");

		try {
			Thread.sleep(15000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	// /**
	// * Only waits, it doesn't try to start bundles (container will do)
	// */
	// public static void waitForAllBundlesActive(BundleContext bundleContext) {
	// int MAX_RETRIES = 100;
	// Bundle b = null;
	// boolean active = true;
	// List<Integer> fragments = new ArrayList<Integer>();
	// String strBundles;
	//
	// for (int i = 0; i < MAX_RETRIES; i++) {
	// active = true;
	// for (int j = 0; j < bundleContext.getBundles().length; j++) {
	// if (!isFragment(bundleContext.getBundles()[j]) &&
	// bundleContext.getBundles()[j].getState() != Bundle.ACTIVE) {
	// active = false;
	// } else if (isFragment(bundleContext.getBundles()[j])) {
	// if (!fragments.contains(j))
	// fragments.add(j);
	// if (bundleContext.getBundles()[j].getState() != Bundle.RESOLVED)
	// active = false;
	// }
	// }
	//
	// if (active == true) {
	// break;
	// }
	//
	// strBundles = listBundles(bundleContext);
	// log.info("Waiting for the activation of all the bundles, this is the " + i + " try. Sleeping for 1 second");
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException ex) {
	// ex.printStackTrace();
	// break;
	// }
	// }
	//
	// strBundles = listBundles(bundleContext);
	//
	// String fragmentsNums = "";
	// for (Integer num : fragments) {
	// fragmentsNums += num.toString() + ", ";
	// }
	// log.info("Detected " + fragments.size() + " fragments: " + fragmentsNums);
	//
	// if (active)
	// log.info("All the bundles activated. Waiting for 15 seconds more to allow Blueprint to publish all the services into the OSGi registry");
	// else
	// log.warn("MAX RETRIES REACHED!!! Waiting for 15 seconds more to allow Blueprint to publish all the services into the OSGi registry");
	//
	// try {
	// Thread.sleep(15000);
	// } catch (InterruptedException ex) {
	// ex.printStackTrace();
	// }
	// }

	public static String listBundles(BundleContext bundleContext) {
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
		return listBundles;

	}

	private static boolean isFragment(Bundle bundle) {
		Dictionary headers = bundle.getHeaders();
		Enumeration headerNames = headers.keys();
		boolean isFragment = false;
		while (headerNames.hasMoreElements() && !isFragment) {
			if (headerNames.nextElement().equals("Fragment-Host"))
				isFragment = true;
		}
		return isFragment;
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
