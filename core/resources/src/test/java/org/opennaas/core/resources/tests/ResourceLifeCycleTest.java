package org.opennaas.core.resources.tests;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.CorruptStateException;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;

public class ResourceLifeCycleTest {
	Resource	resource;

	// methods
	// resource.initialize();
	// resource.activate();
	// resource.shutdown();
	// resource.deactivate();
	@Test
	public void expectedLifeCycleTest() {
		resource = new Resource();// STATE INSTANTIATED
		try {
			resource.initialize();
			Assert.assertEquals(resource.getState(), State.INITIALIZED);
			resource.activate();
			Assert.assertEquals(resource.getState(), State.ACTIVE);
			resource.deactivate();
			Assert.assertEquals(resource.getState(), State.INITIALIZED);
			resource.shutdown();
			Assert.assertEquals(resource.getState(), State.SHUTDOWN);
		} catch (IncorrectLifecycleStateException e) {
			Assert.fail(e.getMessage());
		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void initializedStateTest() {
		resource = new Resource();// STATE INSTANTIATED
		resource.setState(State.INITIALIZED);
		boolean a = false, b = false;
		try {

			try {
				resource.initialize();
			} catch (IncorrectLifecycleStateException e1) {
				a = true;
			}
			try {
				resource.deactivate();
			} catch (IncorrectLifecycleStateException e) {
				b = true;
			}
			Assert.assertEquals(resource.getState(), State.INITIALIZED);
			try {
				resource.activate();
			} catch (IncorrectLifecycleStateException e) {
				Assert.fail(e.getMessage());
			}
			Assert.assertEquals(resource.getState(), State.ACTIVE);
			resource.setState(State.INITIALIZED);
			try {
				resource.shutdown();
			} catch (IncorrectLifecycleStateException e1) {
				Assert.fail(e1.getMessage());
			}
			Assert.assertEquals(resource.getState(), State.SHUTDOWN);
		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getMessage());
		}

		Assert.assertTrue(a);
		Assert.assertTrue(b);

	}

	@Test
	public void activeStateTest() {
		resource = new Resource();// STATE INSTANTIATED
		resource.setState(State.ACTIVE);
		boolean a = false, b = false, c = false;
		try {

			try {
				resource.initialize();
			} catch (IncorrectLifecycleStateException e1) {
				a = true;
			}
			try {
				resource.activate();
			} catch (IncorrectLifecycleStateException e) {
				b = true;
			}
			try {
				resource.shutdown();
			} catch (IncorrectLifecycleStateException e1) {
				c = true;
			}

			Assert.assertEquals(resource.getState(), State.ACTIVE);
			try {
				resource.deactivate();
			} catch (IncorrectLifecycleStateException e) {
				Assert.fail(e.getMessage());
			}
			Assert.assertEquals(resource.getState(), State.INITIALIZED);

		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getMessage());
		}

		Assert.assertTrue(a);
		Assert.assertTrue(b);
		Assert.assertTrue(c);
	}

	@Test
	public void instantiatedStateTest() {
		resource = new Resource();// STATE INSTANTIATED

		boolean a = false, b = false, c = false;
		try {

			try {
				resource.deactivate();
			} catch (IncorrectLifecycleStateException e1) {
				a = true;
			}
			try {
				resource.activate();
			} catch (IncorrectLifecycleStateException e) {
				b = true;
			}
			try {
				resource.shutdown();
			} catch (IncorrectLifecycleStateException e1) {
				c = true;
			}

			Assert.assertEquals(resource.getState(), State.INSTANTIATED);
			try {
				resource.initialize();
			} catch (IncorrectLifecycleStateException e) {
				Assert.fail(e.getMessage());
			}
			Assert.assertEquals(resource.getState(), State.INITIALIZED);

		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getMessage());
		}

		Assert.assertTrue(a);
		Assert.assertTrue(b);
		Assert.assertTrue(c);
	}

	@Test
	public void activateResourceTest() {

		try {

			Resource resource = new Resource();
			resource.initialize();

			ICapabilityLifecycle mockCapability = createMock(ICapabilityLifecycle.class);

			mockCapability.initialize(); // expect capability to be initialized and activated
			mockCapability.activate();
			replay(mockCapability);

			List<ICapability> capabilities = new ArrayList<ICapability>();
			capabilities.add(mockCapability);
			resource.setCapabilities(capabilities);

			IResourceBootstrapper mockBootstrapper = createMock(IResourceBootstrapper.class);
			mockBootstrapper.bootstrap(resource); // expect a call to bootstrap
			replay(mockBootstrapper);

			resource.setBootstrapper(mockBootstrapper);

			resource.setState(State.INITIALIZED);
			resource.activate();

			verify(mockBootstrapper); // verify bootstrap() has been called
			verify(mockCapability); // verify capability initialize() and activate() have been called

			Assert.assertTrue(resource.getState().equals(State.ACTIVE));

		} catch (ResourceException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (IncorrectLifecycleStateException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void deactivateResourceTest() {

		try {

			Resource resource = new Resource();
			resource.initialize();

			ICapabilityLifecycle mockCapability = createMock(ICapabilityLifecycle.class);

			mockCapability.deactivate(); // expected
			mockCapability.shutdown();
			replay(mockCapability);

			List<ICapability> capabilities = new ArrayList<ICapability>();
			capabilities.add(mockCapability);
			resource.setCapabilities(capabilities);

			IResourceBootstrapper mockBootstrapper = createMock(IResourceBootstrapper.class);
			mockBootstrapper.revertBootstrap(resource); // expected
			replay(mockBootstrapper);

			resource.setBootstrapper(mockBootstrapper);

			resource.setState(State.ACTIVE);
			resource.deactivate();

			verify(mockBootstrapper); // verify mockBootsrapper expectations
			verify(mockCapability); // verify mockCapability expectations

			Assert.assertTrue(resource.getState().equals(State.INITIALIZED));

		} catch (ResourceException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (IncorrectLifecycleStateException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void activateResourceRollbackTest1() {

		try {

			Resource resource = new Resource();
			resource.initialize();

			ICapabilityLifecycle mockCapability = createMock(ICapabilityLifecycle.class);
			mockCapability.getState();
			expectLastCall().andReturn(ICapabilityLifecycle.State.INITIALIZED).anyTimes();

			mockCapability.initialize();
			expectLastCall().atLeastOnce();

			mockCapability.activate();
			expectLastCall().andThrow(new ResourceException());

			mockCapability.deactivate(); // @ rollback
			expectLastCall().anyTimes();
			mockCapability.shutdown();
			expectLastCall().anyTimes();

			replay(mockCapability);

			List<ICapability> capabilities = new ArrayList<ICapability>();
			capabilities.add(mockCapability);
			resource.setCapabilities(capabilities);

			IResourceBootstrapper mockBootstrapper = createMock(IResourceBootstrapper.class);
			mockBootstrapper.bootstrap(resource); // expect a call to bootstrap
			expectLastCall().anyTimes(); // it may not happen
			replay(mockBootstrapper);

			resource.setBootstrapper(mockBootstrapper);

			resource.setState(State.INITIALIZED);
			boolean hasThrown = false;
			try {
				resource.activate();
			} catch (ResourceException e) {
				hasThrown = true;
			}
			Assert.assertTrue(hasThrown);

			verify(mockBootstrapper); // verify bootstrap() has been called
			verify(mockCapability); // verify capability initialize() and activate() have been called

			Assert.assertTrue(resource.getState().equals(State.INITIALIZED));

		} catch (ResourceException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (IncorrectLifecycleStateException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void activateResourceRollbackTest2() {

		try {

			Resource resource = new Resource();
			resource.initialize();

			ICapabilityLifecycle mockCapability = createMock(ICapabilityLifecycle.class);
			mockCapability.getState();
			expectLastCall().andReturn(ICapabilityLifecycle.State.INITIALIZED).anyTimes();

			mockCapability.initialize(); // it may not happen if bootstrap fails
			expectLastCall().anyTimes();
			mockCapability.activate();
			expectLastCall().anyTimes();

			mockCapability.deactivate(); // @ rollback
			expectLastCall().anyTimes();
			mockCapability.shutdown();
			expectLastCall().anyTimes();

			replay(mockCapability);

			List<ICapability> capabilities = new ArrayList<ICapability>();
			capabilities.add(mockCapability);
			resource.setCapabilities(capabilities);

			IResourceBootstrapper mockBootstrapper = createMock(IResourceBootstrapper.class);
			mockBootstrapper.bootstrap(resource);
			expectLastCall().andThrow(new ResourceException());
			replay(mockBootstrapper);

			resource.setBootstrapper(mockBootstrapper);

			resource.setState(State.INITIALIZED);
			boolean hasThrown = false;
			try {
				resource.activate();
			} catch (ResourceException e) {
				hasThrown = true;
			}
			Assert.assertTrue(hasThrown);

			verify(mockBootstrapper);
			verify(mockCapability);

			Assert.assertTrue(resource.getState().equals(State.INITIALIZED));

		} catch (ResourceException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (IncorrectLifecycleStateException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void deactivateResourceRollbackTest1() {

		try {

			Resource resource = new Resource();
			resource.initialize();

			ICapabilityLifecycle mockCapability = createMock(ICapabilityLifecycle.class);
			mockCapability.getState();
			expectLastCall().andReturn(ICapabilityLifecycle.State.INITIALIZED).anyTimes();

			mockCapability.deactivate();
			expectLastCall().andThrow(new ResourceException());
			mockCapability.shutdown();
			expectLastCall().anyTimes();

			mockCapability.initialize(); // @ rollback
			expectLastCall().anyTimes();
			mockCapability.activate();
			expectLastCall().anyTimes();

			replay(mockCapability);

			List<ICapability> capabilities = new ArrayList<ICapability>();
			capabilities.add(mockCapability);
			resource.setCapabilities(capabilities);

			IResourceBootstrapper mockBootstrapper = createMock(IResourceBootstrapper.class);
			mockBootstrapper.revertBootstrap(resource); // expect a call to bootstrap
			expectLastCall().anyTimes(); // it may not happen if deactivate fails first
			replay(mockBootstrapper);

			resource.setBootstrapper(mockBootstrapper);

			resource.setState(State.ACTIVE);
			boolean hasThrown = false;
			try {
				resource.deactivate();
			} catch (CorruptStateException e) { // CORRUPT! capability.deactivate() fails after revertBootstrap() is executed -> corrupt state
				hasThrown = true;
			}
			Assert.assertTrue(hasThrown);

			verify(mockBootstrapper);
			verify(mockCapability);

			Assert.assertTrue(resource.getState().equals(State.ERROR));

		} catch (ResourceException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (IncorrectLifecycleStateException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void deactivateResourceRollbackTest2() {

		try {

			Resource resource = new Resource();
			resource.initialize();

			ICapabilityLifecycle mockCapability = createMock(ICapabilityLifecycle.class);
			mockCapability.getState();
			expectLastCall().andReturn(ICapabilityLifecycle.State.INITIALIZED).anyTimes();

			mockCapability.deactivate(); // they may not happen if revertBootstrap fails first
			expectLastCall().anyTimes();
			mockCapability.shutdown();
			expectLastCall().anyTimes();

			mockCapability.initialize(); // @ rollback
			expectLastCall().anyTimes();
			mockCapability.activate();
			expectLastCall().anyTimes();

			replay(mockCapability);

			List<ICapability> capabilities = new ArrayList<ICapability>();
			capabilities.add(mockCapability);
			resource.setCapabilities(capabilities);

			IResourceBootstrapper mockBootstrapper = createMock(IResourceBootstrapper.class);
			mockBootstrapper.revertBootstrap(resource);
			expectLastCall().andThrow(new ResourceException());
			replay(mockBootstrapper);

			resource.setBootstrapper(mockBootstrapper);

			resource.setState(State.ACTIVE);
			boolean hasThrown = false;
			try {
				resource.deactivate();
			} catch (ResourceException e) {
				hasThrown = true;
			}
			Assert.assertTrue(hasThrown);

			verify(mockBootstrapper); // verify bootstrap() has been called
			verify(mockCapability); // verify capability initialize() and activate() have been called

			Assert.assertTrue(resource.getState().equals(State.ACTIVE));

		} catch (ResourceException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (IncorrectLifecycleStateException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	// TODO test activate && deactivate corrupt

	@Test
	public void activateResourceRollbackCorruptTest() {

		try {

			Resource resource = new Resource();
			resource.initialize();

			// fails to deactivate
			ICapabilityLifecycle mockCapability2 = createMock(ICapabilityLifecycle.class);
			mockCapability2.getState();
			expectLastCall().andReturn(ICapabilityLifecycle.State.INITIALIZED).anyTimes();

			mockCapability2.deactivate(); // @ rollback
			expectLastCall().andThrow(new ResourceException());
			mockCapability2.shutdown();
			expectLastCall().anyTimes();

			mockCapability2.initialize();
			expectLastCall().atLeastOnce();
			mockCapability2.activate();
			expectLastCall().atLeastOnce();

			replay(mockCapability2);

			// fails to activate
			ICapabilityLifecycle mockCapability1 = createMock(ICapabilityLifecycle.class);
			mockCapability1.getState();
			expectLastCall().andReturn(ICapabilityLifecycle.State.INITIALIZED).anyTimes();

			mockCapability1.deactivate(); // @ rollback
			expectLastCall().anyTimes();
			mockCapability1.shutdown();
			expectLastCall().anyTimes();

			mockCapability1.initialize();
			expectLastCall().atLeastOnce();
			mockCapability1.activate();
			expectLastCall().andThrow(new ResourceException());

			replay(mockCapability1);

			List<ICapability> capabilities = new ArrayList<ICapability>();
			capabilities.add(mockCapability2);
			capabilities.add(mockCapability1);
			resource.setCapabilities(capabilities);

			IResourceBootstrapper mockBootstrapper = createMock(IResourceBootstrapper.class);
			mockBootstrapper.bootstrap(resource); // expect a call to bootstrap
			expectLastCall().anyTimes(); // it may not happen if deactivate fails first
			replay(mockBootstrapper);

			resource.setBootstrapper(mockBootstrapper);

			resource.setState(State.INITIALIZED);
			boolean hasThrown = false;
			try {
				resource.activate();
			} catch (CorruptStateException e) { // CORRUPT!
				hasThrown = true;
			}
			Assert.assertTrue(hasThrown);

			verify(mockBootstrapper);
			verify(mockCapability1);
			verify(mockCapability2);

			Assert.assertTrue(resource.getState().equals(State.ERROR));

		} catch (ResourceException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (IncorrectLifecycleStateException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void deactivateResourceRollbackCorruptTest() {

		try {

			Resource resource = new Resource();
			resource.initialize();

			// fails to activate
			ICapabilityLifecycle mockCapability1 = createMock(ICapabilityLifecycle.class);
			mockCapability1.getState();
			expectLastCall().andReturn(ICapabilityLifecycle.State.INITIALIZED).anyTimes();

			mockCapability1.deactivate();
			expectLastCall().atLeastOnce();
			mockCapability1.shutdown();
			expectLastCall().atLeastOnce();

			mockCapability1.initialize(); // @ rollback
			expectLastCall().anyTimes();
			mockCapability1.activate();
			expectLastCall().andThrow(new ResourceException());

			replay(mockCapability1);

			// fails to deactivate
			ICapabilityLifecycle mockCapability2 = createMock(ICapabilityLifecycle.class);
			mockCapability2.getState();
			expectLastCall().andReturn(ICapabilityLifecycle.State.INITIALIZED).anyTimes();

			mockCapability2.deactivate();
			expectLastCall().andThrow(new ResourceException());
			mockCapability2.shutdown();
			expectLastCall().anyTimes();

			mockCapability2.initialize(); // @ rollback
			expectLastCall().anyTimes();
			mockCapability2.activate();
			expectLastCall().anyTimes();

			replay(mockCapability2);

			List<ICapability> capabilities = new ArrayList<ICapability>();
			capabilities.add(mockCapability1);
			capabilities.add(mockCapability2);
			resource.setCapabilities(capabilities);

			IResourceBootstrapper mockBootstrapper = createMock(IResourceBootstrapper.class);
			mockBootstrapper.revertBootstrap(resource); // expect a call to bootstrap
			expectLastCall().anyTimes(); // it may not happen if deactivate fails first
			replay(mockBootstrapper);

			resource.setBootstrapper(mockBootstrapper);

			resource.setState(State.ACTIVE);
			boolean hasThrown = false;
			try {
				resource.deactivate();
			} catch (CorruptStateException e) { // CORRUPT!
				hasThrown = true;
			}
			Assert.assertTrue(hasThrown);

			verify(mockBootstrapper);
			verify(mockCapability1);
			verify(mockCapability2);

			Assert.assertTrue(resource.getState().equals(State.ERROR));

		} catch (ResourceException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (IncorrectLifecycleStateException e) {
			Assert.fail(e.getLocalizedMessage());
		} catch (CorruptStateException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void forceDeactivateResourceTest() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException {

		Resource resource = new Resource();
		resource.initialize();

		IResourceBootstrapper mockBootstrapper = createMock(IResourceBootstrapper.class);
		mockBootstrapper.revertBootstrap(resource);
		expectLastCall().andThrow(new ResourceException());
		replay(mockBootstrapper);

		resource.setBootstrapper(mockBootstrapper);

		boolean isThrown = false;
		try {
			resource.forceDeactivate();
		} catch (ResourceException e) {
			isThrown = true;
		}
		Assert.assertTrue(isThrown);

		/* check fail capability */

		ICapabilityLifecycle mockCapability = createMock(ICapabilityLifecycle.class);
		/* force to fail the deactivate method */
		mockCapability.deactivate();
		expectLastCall().andThrow(new ResourceException()).anyTimes();

		replay(mockCapability);

		List<ICapability> capabilities = new ArrayList<ICapability>();
		capabilities.add(mockCapability);

		resource.setCapabilities(capabilities);
		resource.setBootstrapper(createMock(IResourceBootstrapper.class));

		isThrown = false;
		try {
			resource.forceDeactivate();
		} catch (ResourceException e) {
			isThrown = true;
		}
		Assert.assertTrue(isThrown);

	}

}
