package org.opennaas.extensions.itests.helpers;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.configureConsole;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.io.File;

import org.openengsb.labs.paxexam.karaf.options.KarafDistributionBaseConfigurationOption;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.extra.VMOption;

import com.google.common.base.Joiner;

public abstract class OpennaasExamOptions
{
	public final static KarafDistributionBaseConfigurationOption
			opennaasDistributionConfiguration()
	{
		return karafDistributionConfiguration()
				.frameworkUrl(maven()
						.groupId("org.opennaas")
						.artifactId("platform")
						.type("zip")
						.versionAsInProject())
				.karafVersion("2.2.2")
				.name("opennaas")
				.unpackDirectory(new File("target/paxexam"));
	}

	public final static Option includeTestHelper()
	{
		return composite(mavenBundle()
				.groupId("org.opennaas")
				// .artifactId("org.opennaas.extensions.nexus.tests.helper")
				.artifactId("org.opennaas.extensions.itests.helpers")
				.versionAsInProject());
	}

	public final static Option includeTestMockProfile()
	{
		return mavenBundle()
				.groupId("org.opennaas")
				.artifactId("org.opennaas.core.tests-mockprofile")
				.versionAsInProject();
	}

	public final static Option includeSwissboxFramework()
	{
		return composite(mavenBundle()
				.groupId("org.ops4j.base")
				.artifactId("ops4j-base-spi")
				.versionAsInProject(),
				mavenBundle()
						.groupId("org.ops4j.pax.swissbox")
						.artifactId("pax-swissbox-framework")
						.versionAsInProject());
	}

	public final static Option includeFeatures(String... features)
	{
		return editConfigurationFilePut("etc/org.apache.karaf.features.cfg",
				"featuresBoot",
				Joiner.on(",").join(features));
	}

	public final static Option noConsole()
	{
		return configureConsole().ignoreLocalConsole().ignoreRemoteShell();
	}

	public final static Option openDebugSocket()
	{
		return new VMOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005");
	}
}
