package org.opennaas.itests.ofertie.ncl;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class NCLProvisionerTest {

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private INCLController		nclController;

	@Inject
	private INCLProvisioner		provisioner;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.ofertie.ncl)")
	private BlueprintContainer	ofertieNCLBundleContainer;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch)")
	private BlueprintContainer	ofSwitchBundleContainer;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch.driver.floodlight)")
	private BlueprintContainer	floodlightDriverBundleContainer;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-ofertie-ncl"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void startResources() {

	}

	@Test
	public void test() {

	}

}
