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
import org.codehaus.jackson.node.ObjectNode;
import org.eclipse.jetty.http.HttpStatus;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.xml.sax.SAXException;

/**
 * Servlet for the HTTP Server.
 * 
 * The HTTP servlet is the one containing the list of {@link HTTPServerBehaviour}, which define the answers of the server to specific requests.
 * Request sent by the user that are not defined in the list of behaviors are answered with a {@link HttpServletResponse}
 * 
 * In the other hand, if the user's request matches a desired behavior, the associated request is sent to the user, which could be either a sucessfull
 * or error answer. If the behavior was consumable, it's removed from the list whenever a request matches it.
 * 
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class HTTPServerServlet extends HttpServlet {

	private static final long			serialVersionUID	= -7639723130739143382L;

	private List<HTTPServerBehaviour>	desiredBehaviour;

	private String						reqContentType;
	private String						reqBody;
	private String						reqURL;
	private String						reqMethod;										;

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

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		createResponse(request, response);
	}

	private void createResponse(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		this.reqContentType = request.getContentType();
		this.reqURL = request.getRequestURI();
		this.reqMethod = request.getMethod();
		this.reqBody = getBodyMessage(request);

		HTTPServerBehaviour behavior = getDesiredBehavior();

		if (behavior == null)
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);

		else {

			HTTPResponse desiredResponse = behavior.getResponse();

			if (behavior.isConsumable())
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

	private HTTPServerBehaviour getDesiredBehavior() throws IOException, ServletException
	{
		try {
			Iterator<HTTPServerBehaviour> iterator = desiredBehaviour.iterator();
			while (iterator.hasNext()) {
				HTTPServerBehaviour behaviour = iterator.next();
				HTTPRequest possibleReq = behaviour.getRequest();

				if (requestMatches(possibleReq)) {

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

	private boolean requestMatches(HTTPRequest possibleReq) throws IOException, SAXException, TransformerException,
			ParserConfigurationException {

		if (!possibleReq.getMethod().equals(this.reqMethod))
			return false;

		if (!possibleReq.getRequestURL().equals(this.reqURL))
			return false;

		if (!bodyMatches(possibleReq.getBodyMessage(), possibleReq.getContentType(),
				possibleReq.getOmittedFields()))
			return false;

		return true;

	}

	private boolean bodyMatches(String possibleReqBody, String possibleReqContentType,
			List<String> ommitedFields)
			throws JsonProcessingException, IOException, SAXException, TransformerException, ParserConfigurationException {

		if (!StringUtils.equals(this.reqContentType, possibleReqContentType))
			return false;

		if (StringUtils.isBlank(this.reqBody) && StringUtils.isBlank(possibleReqBody))
			return true;

		if ((StringUtils.isBlank(this.reqBody) && StringUtils.isNotBlank(possibleReqBody)) || ((StringUtils.isNotBlank(this.reqBody) && StringUtils
				.isBlank(possibleReqBody))))
			return false;

		if (this.reqContentType.contains(MediaType.APPLICATION_XML) || this.reqContentType.contains(MediaType.TEXT_XML))
			return compareXml(this.reqBody, possibleReqBody);

		if (this.reqContentType.contains(MediaType.APPLICATION_JSON))
			return compareJson(this.reqBody, possibleReqBody, ommitedFields);

		return true;

	}

	private boolean compareJson(String reqBody, String possibleReqBody, List<String> ommitedFields) throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(reqBody);
		JsonNode tree2 = mapper.readTree(possibleReqBody);

		for (String field : ommitedFields) {

			((ObjectNode) tree1).remove(field);
			((ObjectNode) tree2).remove(field);

		}

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
