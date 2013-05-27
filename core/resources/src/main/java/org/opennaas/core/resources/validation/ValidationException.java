package org.opennaas.core.resources.validation;

import java.util.List;

import org.opennaas.core.resources.ResourceException;
import org.springframework.validation.Errors;


/**
 * @author Scott Campbell (CRC)
 *
 */
public class ValidationException extends ResourceException
{
	private static final long serialVersionUID = 2151457358047960610L;
	private List<Errors> errors;

	public ValidationException(List<Errors> errors) {
		super();
		this.errors = errors;
	}

	public List<Errors> getErrors() {
		return errors;
	}
}
