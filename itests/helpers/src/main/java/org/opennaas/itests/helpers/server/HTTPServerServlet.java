package org.opennaas.itests.helpers.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

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

		if (desiredResponse == null)
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		else {

			int status = desiredResponse.getStatus();

			if (HttpStatus.isServerError(status) || HttpStatus.isClientError(status))
				response.sendError(status);

			else {

				response.getWriter().println(desiredResponse.getBodyMessage());
				response.setContentType(desiredResponse.getContentType());
				response.setStatus(desiredResponse.getStatus());

			}
		}

	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HTTPResponse desiredResponse = getDesiredResponse(request);

		if (desiredResponse == null)
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);

		else {

			int status = desiredResponse.getStatus();

			if (HttpStatus.isServerError(status) || HttpStatus.isClientError(status))
				response.sendError(status);

			else {

				response.getWriter().println(desiredResponse.getBodyMessage());
				response.setContentType(desiredResponse.getContentType());
				response.setStatus(desiredResponse.getStatus());

			}
		}

	}

	private HTTPResponse getDesiredResponse(HttpServletRequest request) throws IOException {

		Iterator<HTTPServerBehaviour> iterator = desiredBehaviour.iterator();
		while (iterator.hasNext()) {
			HTTPServerBehaviour behaviour = iterator.next();
			HTTPRequest possibleReq = behaviour.getRequest();

			if (requestMatches(request, possibleReq))
				return behaviour.getResponse();

		}

		return null;
	}

	private boolean requestMatches(HttpServletRequest request, HTTPRequest possibleReq) throws IOException {

		if (!possibleReq.getMethod().equals(request.getMethod()))
			return false;

		if (!possibleReq.getRequestURL().equals(request.getRequestURI()))
			return false;

		String bodyMessage = getBodyMessage(request);

		if ((bodyMessage == null && possibleReq.getBodyMessage() != null) || (bodyMessage != null && possibleReq.getBodyMessage() == null))
			return false;

		if (bodyMessage != null && possibleReq.getBodyMessage() != null && !bodyMessage.equals(possibleReq.getBodyMessage()))
			return false;

		return true;

	}

	private String getBodyMessage(HttpServletRequest request) throws IOException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null)
			jb.append(line);

		return line;
	}
}
