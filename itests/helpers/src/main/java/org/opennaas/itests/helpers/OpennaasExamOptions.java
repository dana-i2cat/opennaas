package org.opennaas.itests.helpers;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.io.File;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.KarafDistributionBaseConfigurationOption;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

import com.google.common.base.Joiner;

public abstract class OpennaasExamOptions
{

	public final static KarafDistributionBaseConfigurationOption opennaasDistributionConfiguration() {

		MavenArtifactUrlReference platformUrl = CoreOptions.maven()
				.groupId("org.opennaas")
				.artifactId("platform")
				.versionAsInProject()
				.type("zip");

		return KarafDistributionOption.karafDistributionConfiguration()
				.frameworkUrl(platformUrl)
				.unpackDirectory(new File("target/paxexam"))
				.name("opennaas")
				.karafVersion("2.3.4");

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
		return KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.features.cfg",
				"featuresBoot",
				Joiner.on(",").join(features));
	}

	public final static Option noConsole()
	{
		return KarafDistributionOption.configureConsole().ignoreLocalConsole().ignoreRemoteShell();
	}

	public final static Option keepLogConfiguration() {
		return KarafDistributionOption.doNotModifyLogConfiguration();
	}

	public final static Option openDebugSocket()
	{
		return KarafDistributionOption.debugConfiguration();
	}

	public final static Option keepRuntimeFolder() {
		return KarafDistributionOption.keepRuntimeFolder();
	}
}
