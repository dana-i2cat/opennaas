package org.opennaas.itests.helpers.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.xml.sax.SAXException;

public class HTTPServerServlet extends HttpServlet {

	private static final long			serialVersionUID	= -7639723130739143382L;

	private List<HTTPServerBehaviour>	desiredBehaviour;

	public HTTPServerServlet(List<HTTPServerBehaviour> servletBehaviours) {
		this.desiredBehaviour = servletBehaviours;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		createResponse(request, response);

	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		createResponse(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		createResponse(request, response);
	}

	private void createResponse(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HTTPServerBehaviour behavior = getDesiredBehavior(request);

		if (behavior == null)
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);

		else {

			HTTPResponse desiredResponse = behavior.getResponse();

			if (behavior.isConsumible())
				desiredBehaviour.remove(behavior);

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

	private HTTPServerBehaviour getDesiredBehavior(HttpServletRequest request) throws IOException, ServletException
	{
		try {
			Iterator<HTTPServerBehaviour> iterator = desiredBehaviour.iterator();
			while (iterator.hasNext()) {
				HTTPServerBehaviour behaviour = iterator.next();
				HTTPRequest possibleReq = behaviour.getRequest();

				if (requestMatches(request, possibleReq)) {

					return behaviour;

				}
			}

			return null;

		} catch (SAXException e) {
			throw new ServletException(e);
		} catch (TransformerException e) {
			throw new ServletException(e);
		} catch (ParserConfigurationException e) {
			throw new ServletException(e);
		}
	}

	private boolean requestMatches(HttpServletRequest request, HTTPRequest possibleReq) throws IOException, SAXException, TransformerException,
			ParserConfigurationException {

		if (!possibleReq.getMethod().equals(request.getMethod()))
			return false;

		if (!possibleReq.getRequestURL().equals(request.getRequestURI()))
			return false;

		String bodyMessage = getBodyMessage(request);

		if (!bodyMatches(bodyMessage, request.getContentType(), possibleReq.getBodyMessage(), possibleReq.getContentType()))
			return false;

		return true;

	}

	private boolean bodyMatches(String reqBody, String reqContentType, String possibleReqBody, String possibleReqContentType)
			throws JsonProcessingException, IOException, SAXException, TransformerException, ParserConfigurationException {

		if (!StringUtils.equals(reqContentType, possibleReqContentType))
			return false;

		if (StringUtils.isBlank(reqBody) && StringUtils.isBlank(possibleReqBody))
			return true;

		if ((StringUtils.isBlank(reqBody) && StringUtils.isNotBlank(possibleReqBody)) || ((StringUtils.isNotBlank(reqBody) && StringUtils
				.isBlank(possibleReqBody))))
			return false;

		if (reqContentType.contains(MediaType.APPLICATION_XML) || reqContentType.contains(MediaType.TEXT_XML))
			return compareXml(reqBody, possibleReqBody);

		if (reqContentType.contains(MediaType.APPLICATION_JSON))
			return compareJson(reqBody, possibleReqBody);

		return true;

	}

	private boolean compareJson(String reqBody, String possibleReqBody) throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(reqBody);
		JsonNode tree2 = mapper.readTree(possibleReqBody);

		return tree1.equals(tree2);

	}

	private boolean compareXml(String reqBody, String possibleReqBody) throws SAXException, IOException, TransformerException,
			ParserConfigurationException {

		String formattedBody = XmlHelper.formatXML(reqBody);
		String formattedBody2 = XmlHelper.formatXML(possibleReqBody);

		return (formattedBody.equals(formattedBody2));
	}

	private String getBodyMessage(HttpServletRequest request) throws IOException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null)
			jb.append(line);

		return jb.toString();
	}
}
