package net.i2cat.nexus.resources.command;

import java.util.List;
import java.util.Vector;

public class Response {

	public enum Status {
		OK, ERROR, WAIT
	}

	private String			sentMessage;
	private Status			status;
	private String			information;
	private List<String>	errors	= new Vector<String>();

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public String getSentMessage() {
		return sentMessage;
	}

	public void setSentMessage(String sentMessage) {
		this.sentMessage = sentMessage;
	}

	public static Response okResponse(String sentMessage) {
		Response response = new Response();
		response.setSentMessage(sentMessage);
		response.setStatus(Status.OK);
		response.setErrors(new Vector<String>());
		return response;

	}

	public static Response okResponse(String sentMessage, String information) {
		Response response = new Response();
		response.setSentMessage(sentMessage);
		response.setStatus(Status.OK);
		response.setInformation(information);
		response.setErrors(new Vector<String>());
		return response;

	}

	public static Response errorResponse(String sentMessage, Vector<String> errors) {
		Response response = new Response();
		response.setSentMessage(sentMessage);
		response.setStatus(Status.ERROR);
		response.setErrors(errors);
		return response;
	}

	public static Response waitResponse(String sentMessage) {
		Response response = new Response();
		response.setSentMessage(sentMessage);
		response.setStatus(Status.WAIT);
		return response;
	}

}
