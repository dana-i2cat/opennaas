package org.opennaas.core.security.acl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
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

	private static Log			log							= LogFactory.getLog(ACLManager.class);

	// OpenNaaS-Security SQL scripts to initialize Spring Security ACLs SQL schema
	private static String		SQL_ACLS;

	// Properties containing users to add to DB
	private String				usersPropertiesFile;
	private static final String	usersPropertiesUsersPrefix	= "users.";
	private static final String	usersPropertiesUsersSize	= "size";

	private SecurityRepository	securityRepository;

	@Autowired
	private MutableAclService	aclService;
	@Autowired
	private PermissionEvaluator	permissionEvaluator;

	public void init() throws IOException {
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

	private void initializeUsers() throws IOException, NumberFormatException {
		Properties usersProperties = getProperties(usersPropertiesFile);

		// getting users.size
		int usersSize = Integer.parseInt(usersProperties.getProperty(usersPropertiesUsersPrefix + usersPropertiesUsersSize));

		for (int i = 0; i < usersSize; i++) {
			// adding users.{i}
			String user = usersProperties.getProperty(usersPropertiesUsersPrefix + i);
			if (user != null) {
				insertAclSid(i, true, new PrincipalSid(user));
			}
		}
	}

	public Properties getProperties(final String propertyFile)
			throws IOException {
		final Properties properties = new Properties();
		final URL resource = Activator.class.getClassLoader().getResource(propertyFile
				+ ".properties");

		if (null == resource) {
			throw new FileNotFoundException(propertyFile
					+ " could not be found");
		}

		final InputStream propertyStream = resource.openStream();
		properties.load(propertyStream);
		return properties;
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

	private void insertAcl(long id, PrincipalSid principalSid, Permission permission) {
		ObjectIdentity oi = new ObjectIdentityImpl(Resource.class, id);

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
	public void secureResource(String resourceId, String user) {
		insertAcl(ResourceIdToSecureId(resourceId), new PrincipalSid(user), BasePermission.READ);
	}

	@Override
	public Boolean isResourceAccessible(String resourceId) {
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

	public void setUsersPropertiesFile(String usersPropertiesFile) {
		this.usersPropertiesFile = usersPropertiesFile;
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
