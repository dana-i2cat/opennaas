package org.opennaas.itests.security;

/*
 * #%L
 * OpenNaaS :: iTests :: Security
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.security.acl.IACLManager;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ACLManagerTest {

	private final static Log	log	= LogFactory.getLog(ACLManagerTest.class);

	@Inject
	private IACLManager			aclManager;

	@Inject
	protected IResourceManager	resourceManager;

	/**
	 * Make sure blueprint for org.opennaas.core.security bundle has finished its initialization
	 */
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.core.security)", timeout = 20000)
	private BlueprintContainer	securityBlueprintContainer;

	@Configuration
	public static Option[] configuration() {
		return CoreOptions.options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("itests-helpers"),
				OpennaasExamOptions.noConsole(),
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.keepRuntimeFolder());
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
		adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		Authentication adminAuthentication = new UsernamePasswordAuthenticationToken(adminUser, adminUser, adminAuthorities);

		Collection<GrantedAuthority> basicAuthorities = new ArrayList<GrantedAuthority>();
		basicAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
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
