package org.opennaas.core.security.acl;

import java.io.Serializable;

import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;

/**
 * Non caching AclCache
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class NullAclCache implements AclCache {

	@Override
	public void evictFromCache(Serializable pk) {
		// TODO Auto-generated method stub

	}

	@Override
	public void evictFromCache(ObjectIdentity objectIdentity) {
		// TODO Auto-generated method stub

	}

	@Override
	public MutableAcl getFromCache(ObjectIdentity objectIdentity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MutableAcl getFromCache(Serializable pk) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putInCache(MutableAcl acl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub

	}

}
