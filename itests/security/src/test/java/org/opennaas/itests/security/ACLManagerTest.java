package org.opennaas.itests.security;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.security.acl.IACLManager;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ACLManagerTest {

	private final static Log	log	= LogFactory.getLog(ACLManagerTest.class);

	@Inject
	private IACLManager			aclManager;

	@Inject
	protected IResourceManager	resourceManager;

	@Configuration
	public static Option[] configuration() {
		return CoreOptions.options(OpennaasExamOptions.opennaasDistributionConfiguration(),

				OpennaasExamOptions.includeFeatures("opennaas-router", "opennaas-junos", "opennaas-vcpe", "itests-helpers"),
				OpennaasExamOptions.noConsole(),
				// OpennaasExamOptions.openDebugSocket(),
				KarafDistributionOption.keepRuntimeFolder());
	}

	@Before
	public void initBundles() throws ResourceException, ProtocolException {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Initialized!");
	}

	@After
	public void stopBundle() throws Exception {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Stopped!");
	}

	@Test
	public void testAclManager() {
		// create Resource with generated ResourceIdentifier
		Resource resource = new Resource();
		ResourceIdentifier resourceIdentifier = new ResourceIdentifier();
		resource.setResourceIdentifier(resourceIdentifier);

		// create users
		String adminUser = "admin";
		String basicUser = "user";

		// create Authentication objects
		Collection<GrantedAuthority> adminAuthorities = new ArrayList<GrantedAuthority>();
		adminAuthorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
		Authentication adminAuthentication = new UsernamePasswordAuthenticationToken(adminUser, adminUser, adminAuthorities);

		Collection<GrantedAuthority> basicAuthorities = new ArrayList<GrantedAuthority>();
		basicAuthorities.add(new GrantedAuthorityImpl("ROLE_USER"));
		Authentication basicAuthentication = new UsernamePasswordAuthenticationToken(basicUser, basicUser, basicAuthorities);

		// secure Resource using ACLManager (admin credentials are necessary to create ACLs, set it)
		SecurityContextHolder.getContext().setAuthentication(adminAuthentication);
		aclManager.secureResource(resource.getResourceIdentifier().getId(), adminUser);

		/* check accessibility using ACLManager */

		// expect access granted
		SecurityContextHolder.getContext().setAuthentication(adminAuthentication);
		boolean accessible = aclManager.isResourceAccessible(resource.getResourceIdentifier().getId());
		Assert.assertEquals("Permission must be granted for admin user", true, accessible);

		// expect access NOT granted
		SecurityContextHolder.getContext().setAuthentication(basicAuthentication);
		accessible = aclManager.isResourceAccessible(resource.getResourceIdentifier().getId());
		Assert.assertEquals("Permission must be NOT granted for basic user", false, accessible);
	}
}
