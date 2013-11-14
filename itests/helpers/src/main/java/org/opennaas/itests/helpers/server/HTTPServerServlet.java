package org.opennaas.itests.helpers.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HTTPServerServlet extends HttpServlet {

	private static final long			serialVersionUID	= -7639723130739143382L;

	private List<HTTPServerBehaviour>	desiredBehaviour;

	public HTTPServerServlet(List<HTTPServerBehaviour> servletBehaviours) {
		this.desiredBehaviour = servletBehaviours;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		String uri = request.getRequestURI();

		if (uri.equals(desiredBehaviour.get(0).getRequest().getRequestURL())) {
			response.getWriter().println(desiredBehaviour.get(0).getResponse().getBodyMessage());
			response.setContentType(desiredBehaviour.get(0).getResponse().getContentType());
			response.setStatus(desiredBehaviour.get(0).getResponse().getStatus());
		}
		else
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);

		}

	}
}
