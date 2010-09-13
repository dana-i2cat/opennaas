package net.i2cat.mantychore.models.router.tests;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.models.router.RouterModelFactory;
import net.i2cat.mantychore.models.router.RouterOSGIJpaRepository;

import org.junit.Test;
import org.springframework.test.jpa.AbstractJpaTests;

import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.CapabilityProperty;

public class RouterOSGIJpaTest extends AbstractJpaTests {

	private RouterOSGIJpaRepository	routerOSGIJpa;
	private RouterModelFactory		routerModelFactory;
	CapabilityDescriptor			descriptor	= new CapabilityDescriptor();
	List<CapabilityProperty>		properties	= new ArrayList<CapabilityProperty>();

	CapabilityProperty				property	= new CapabilityProperty();

	public void setRouterInventory(RouterOSGIJpaRepository routerOSGIJpa) {
		this.routerOSGIJpa = routerOSGIJpa;
	}

	protected String[] getConfigLocations() {
		return new String[] { "/META-INF/application-context.xml" };
	}

	@Test
	public void testAddRouter() {

		int oldRouterCount = jdbcTemplate
				.queryForInt("SELECT COUNT(0) FROM ROUTER");
		routerOSGIJpa.save(routerModelFactory
				.createResourceModelInstance(descriptor
						.setCapabilityProperties(properties.add(property
								.setId(1)))));
		sharedEntityManager.flush();

		int newRouterCount = jdbcTemplate
				.queryForInt("SELECT COUNT(0) FROM ROUTER");
		assertEquals("Se debe haber añadido una nueva row en ROUTER table",
				oldRouterCount + 1, newRouterCount);
	}

}