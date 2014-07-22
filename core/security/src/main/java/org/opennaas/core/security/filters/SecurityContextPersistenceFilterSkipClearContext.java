package org.opennaas.core.security.filters;

/*
 * #%L
 * OpenNaaS :: Core :: Security
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.GenericFilterBean;

/**
 * A modification of org.springframework.security.web.context.SecurityContextPersistenceFilter that allows for not clearing the SecurityContext once
 * the request has completed.
 * 
 * This filter uses same identifier than SecurityContextPersistenceFilter to ensure only one of them is applied once per request.
 * 
 * This code is based on org.springframework.security.web.context.SecurityContextPersistenceFilter in spring-security-web-3.0.8.RELEASE.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class SecurityContextPersistenceFilterSkipClearContext extends GenericFilterBean {

	static final String					FILTER_APPLIED				= "__spring_security_scpf_applied";

	private SecurityContextRepository	repo						= new HttpSessionSecurityContextRepository();

	private boolean						forceEagerSessionCreation	= false;

	private boolean						skipClearContext			= false;

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (request.getAttribute(FILTER_APPLIED) != null) {
			// ensure that filter is only applied once per request
			chain.doFilter(request, response);
			return;
		}

		final boolean debug = logger.isDebugEnabled();

		request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

		if (forceEagerSessionCreation) {
			HttpSession session = request.getSession();

			if (debug && session.isNew()) {
				logger.debug("Eagerly created session: " + session.getId());
			}
		}

		HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
		SecurityContext contextBeforeChainExecution = repo.loadContext(holder);

		try {
			SecurityContextHolder.setContext(contextBeforeChainExecution);

			chain.doFilter(holder.getRequest(), holder.getResponse());

		} finally {
			SecurityContext contextAfterChainExecution = SecurityContextHolder.getContext();
			if (!skipClearContext) {
				// Crucial removal of SecurityContextHolder contents - do this before anything else.
				SecurityContextHolder.clearContext();
			}
			repo.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
			request.removeAttribute(FILTER_APPLIED);

			if (debug) {
				logger.debug("SecurityContextHolder now cleared, as request processing completed");
			}
		}
	}

	public void setSecurityContextRepository(SecurityContextRepository repo) {
		this.repo = repo;
	}

	public void setForceEagerSessionCreation(boolean forceEagerSessionCreation) {
		this.forceEagerSessionCreation = forceEagerSessionCreation;
	}

	/**
	 * Tells whether clearing of SecurityContext after the request has completed should be skipped or not. Defaults to false.
	 * 
	 * @param skipClearContext
	 */
	public void setSkipClearContext(boolean skipClearContext) {
		this.skipClearContext = skipClearContext;
	}
}
