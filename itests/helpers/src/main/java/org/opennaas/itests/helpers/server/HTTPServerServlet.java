package org.opennaas.itests.helpers.server;

import java.io.IOException;
import java.util.Iterator;
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

		HTTPResponse desiredResponse = getDesiredResponse(request);

		if (desiredResponse != null) {

			response.getWriter().println(desiredResponse.getBodyMessage());
			response.setContentType(desiredResponse.getContentType());
			response.setStatus(desiredResponse.getStatus());

		}
		else
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);

		}

	}

	private HTTPResponse getDesiredResponse(HttpServletRequest request) {

		Iterator<HTTPServerBehaviour> iterator = desiredBehaviour.iterator();
		while (iterator.hasNext()) {
			HTTPServerBehaviour behaviour = iterator.next();
			HTTPRequest possibleReq = behaviour.getRequest();

			if (possibleReq.getMethod().equals(request.getMethod()) && possibleReq.getRequestURL().equals(request.getRequestURI()))
				return behaviour.getResponse();
		}

		return null;
	}
}
