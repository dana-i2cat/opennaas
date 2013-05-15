package org.opennaas.core.security.acl;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.security.Activator;
import org.opennaas.core.security.persistence.SecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class ACLManager implements IACLManager {

	private static Log			log	= LogFactory.getLog(ACLManager.class);

	// OpenNaaS-Security SQL scripts to initialize Spring Security ACLs SQL schema
	private static String		SQL_ACLS;

	private SecurityRepository	securityRepository;

	@Autowired
	private MutableAclService	aclService;
	@Autowired
	private PermissionEvaluator	permissionEvaluator;

	public void init() {
		initializeSpringSecurityAclDB();
		initializeUsers();
		initializeClasses();
	}

	private void initializeSpringSecurityAclDB() {
		log.debug("Reading OpenNaaS-Security SQL init scripts contents...");
		SQL_ACLS = Activator.getBundleTextFileContents("/security_db_scripts/acls.sql");
		log.debug("OpenNaaS-Security SQL init scripts contents read.");

		log.debug("Executing OpenNaaS-Security SQL init scripts...");
		executeSqlQuery(SQL_ACLS);
		log.debug("OpenNaaS-Security SQL init scripts executed.");
	}

	private void initializeUsers() {
		insertAclSid(1, true, new PrincipalSid("admin"));
		insertAclSid(2, true, new PrincipalSid("noc"));
		insertAclSid(3, true, new PrincipalSid("noc2"));
		insertAclSid(4, true, new PrincipalSid("noc3"));
		insertAclSid(5, true, new PrincipalSid("client"));
		insertAclSid(6, true, new PrincipalSid("client2"));
	}

	private void initializeClasses() {
		insertAclClass(0, Resource.class);
	}

	private void insertAclSid(long id, boolean isPrincipal, PrincipalSid sid) {
		String query = "insert into acl_sid (id, principal, sid) values ( " + id + ", " + (isPrincipal ? 1 : 0) + ", '" + sid
				.getPrincipal() + "');";
		executeSqlQuery(query);
	}

	private void insertAclClass(long id, Class<?> clazz) {
		String query = "insert into acl_class (id, class) values ( " + id + ", '" + clazz.getName() + "');";
		executeSqlQuery(query);
	}

	private void insertAcl(Object object, long id, PrincipalSid principalSid, Permission permission) {
		ObjectIdentity oi = new ObjectIdentityImpl(object.getClass(), id);

		// Create or update the relevant ACL
		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = aclService.createAcl(oi);
		}

		// Now grant some permissions via an access control entry (ACE)
		acl.insertAce(acl.getEntries().size(), permission, principalSid, true);
		aclService.updateAcl(acl);
	}

	@Override
	public void secureResource(Resource resource, String user) {
		insertAcl(resource, ResourceIdToSecureId(resource.getResourceIdentifier().getId()), new PrincipalSid(user), BasePermission.READ);
	}

	@Override
	public boolean isResourceAccessible(String resourceId) {
		return permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), ResourceIdToSecureId(resourceId),
				Resource.class.getName(), BasePermission.READ);
	}

	private static long ResourceIdToSecureId(String resourceId) {
		// generate a long secure Id (64 bits) from Resource ID (128 bits)to use in ACL DB
		return UUID.fromString(resourceId).getMostSignificantBits();
	}

	private void executeSqlQuery(String sqlQuery) {
		log.debug("Executing SQL query: [ " + sqlQuery + " ]");
		EntityManager em = securityRepository.getEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		try {
			em.createNativeQuery(sqlQuery).executeUpdate();
			em.flush();
			et.commit();
			log.debug("SQL query executed.");
		} catch (Exception e) {
			log.error("Error executing SQL query, rollbacking", e);
			et.rollback();
		}
	}

	public void setSecurityRepository(SecurityRepository securityRepository) {
		this.securityRepository = securityRepository;
	}

	public void setAclService(MutableAclService aclService) {
		this.aclService = aclService;
	}

	public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
		this.permissionEvaluator = permissionEvaluator;
	}

}
