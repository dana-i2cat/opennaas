package org.opennaas.extensions.nexus.tests.helper;

import com.google.common.base.Joiner;

import java.io.File;

import org.ops4j.pax.exam.Option;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionBaseConfigurationOption;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import org.ops4j.pax.exam.options.extra.VMOption;

public abstract class OpennaasExamOptions
{
	public final static KarafDistributionBaseConfigurationOption
		opennaasDistributionConfiguration()
	{
		return
			karafDistributionConfiguration()
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
						 .artifactId("org.opennaas.extensions.nexus.tests.helper")
						 .versionAsInProject());
	}

	public final static Option includeTestMockProfile()
	{
		return
			mavenBundle()
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