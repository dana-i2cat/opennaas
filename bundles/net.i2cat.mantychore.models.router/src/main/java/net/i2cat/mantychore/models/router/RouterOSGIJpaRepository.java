package net.i2cat.mantychore.models.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.core.persistence.GenericOSGiJpaRepository;

public class RouterOSGIJpaRepository extends
		GenericOSGiJpaRepository<RouterModel, Long> {
	private static Logger	logger	= LoggerFactory
											.getLogger(RouterOSGIJpaRepository.class);

	public RouterOSGIJpaRepository() {
		super();
	}

	@Override
	public RouterModel save(RouterModel routerModel) {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().merge(routerModel);
			getEntityManager().getTransaction().commit();
		} catch (Exception ex) {
			getEntityManager().getTransaction().rollback();
			logger.debug("Exception during saving RouterModel");
			ex.printStackTrace();
		}
		return routerModel;
	}

	@Override
	public void delete(RouterModel routerModel) {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().remove(routerModel);
			getEntityManager().getTransaction().commit();
		} catch (Exception ex) {
			getEntityManager().getTransaction().rollback();
			logger.debug("Exception while removing RouterModel");
			ex.printStackTrace();
		}
	}

}
