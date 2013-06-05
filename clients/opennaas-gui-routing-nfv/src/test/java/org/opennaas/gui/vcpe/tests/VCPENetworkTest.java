package org.opennaas.gui.vcpe.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class VCPENetworkTest {
	private Logger				log	= Logger.getLogger(VCPENetworkTest.class);

	private static Validator	validator;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	
}
