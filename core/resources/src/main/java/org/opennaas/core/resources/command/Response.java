package org.opennaas.core.resources.command;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlType;

public class Response {

	@XmlType(name = "ResponseStatus")
	public enum Status {
		OK {
			@Override
			public String toString() {
				return "OK";
			}
		},
		ERROR {
			@Override
			public String toString() {
				return "ERROR";
			}
		},
		WAIT {
			@Override
			public String toString() {
				return "WAIT";
			}
		},
		QUEUED {
			@Override
			public String toString() {
				return "QUEUED";
			}
		},
	}

	private String			commandName;
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

	public static Response queuedResponse(String sentMessage) {
		Response response = new Response();
		response.setSentMessage(sentMessage);
		response.setStatus(Status.QUEUED);
		response.setErrors(new Vector<String>());
		return response;
	}

	public static Response queuedResponse(String sentMessage, String information) {
		Response response = new Response();
		response.setSentMessage(sentMessage);
		response.setStatus(Status.QUEUED);
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

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

}
