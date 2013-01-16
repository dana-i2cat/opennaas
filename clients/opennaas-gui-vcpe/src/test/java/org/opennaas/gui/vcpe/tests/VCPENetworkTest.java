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
import org.opennaas.gui.vcpe.entities.BGP;
import org.opennaas.gui.vcpe.entities.Interface;
import org.opennaas.gui.vcpe.entities.VRRP;

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

	@Test
	public void interfaceIsNotValid() {
		log.debug("Testing interfaceIsNotValid...");

		Interface interface1 = new Interface();
		interface1.setName("");
		interface1.setPort("");
		interface1.setIpAddress("193.1.190.133");
		interface1.setVlan(5000);
		interface1.setType(null);
		interface1.setTemplateName(null);

		Set<ConstraintViolation<Interface>> constraintViolations =
				validator.validate(interface1);

		assertEquals(4, constraintViolations.size());
		for (ConstraintViolation<Interface> constraintViolation : constraintViolations) {
			log.debug("Invalid param = '" + constraintViolation.getPropertyPath()
					+ "', value = '" + constraintViolation.getInvalidValue()
					+ "', error = " + constraintViolation.getMessage());
		}
		log.debug("End testing interfaceIsNotValid.");
	}

	@Test
	public void interfaceIsValid() {
		log.debug("Testing interfaceIsValid...");

		Interface interface1 = new Interface();
		interface1.setName("ge-2/0/0");
		interface1.setPort("12");
		interface1.setIpAddress("193.1.190.133/30");
		interface1.setVlan(12);
		interface1.setType(null);
		interface1.setTemplateName(null);

		Set<ConstraintViolation<Interface>> constraintViolations =
				validator.validate(interface1);

		// no validation errors
		assertEquals(0, constraintViolations.size());
		log.debug("End testing interfaceIsValid.");
	}

	@Test
	public void vrrpIsNotValid() {
		log.debug("Testing vrrpIsNotValid...");

		VRRP vrrp = new VRRP();
		vrrp.setVirtualIPAddress("193.1.190.161/24");

		Set<ConstraintViolation<VRRP>> constraintViolations =
				validator.validate(vrrp);

		assertEquals(1, constraintViolations.size());
		for (ConstraintViolation<VRRP> constraintViolation : constraintViolations) {
			log.debug("Invalid param = '" + constraintViolation.getPropertyPath()
					+ "', value = '" + constraintViolation.getInvalidValue()
					+ "', error = " + constraintViolation.getMessage());
		}
		log.debug("End testing vrrpIsNotValid.");
	}

	@Test
	public void vrrpIsValid() {
		log.debug("Testing vrrpIsValid...");

		VRRP vrrp = new VRRP();
		vrrp.setVirtualIPAddress("193.1.190.161");
		vrrp.setGroup(100);
		vrrp.setPriorityMaster(200);
		vrrp.setPriorityBackup(100);

		Set<ConstraintViolation<VRRP>> constraintViolations =
				validator.validate(vrrp);

		// no validation errors
		assertEquals(0, constraintViolations.size());
		log.debug("End testing vrrpIsValid.");
	}

	@Test
	public void bgpIsNotValid() {
		log.debug("Testing bgpIsNotValid...");

		BGP bgp = new BGP();
		bgp.setClientASNumber("-1");
		bgp.setNocASNumber("4294967296");
		ArrayList<String> clientPrefixes = new ArrayList<String>();
		bgp.setClientPrefixes(clientPrefixes);

		Set<ConstraintViolation<BGP>> constraintViolations =
				validator.validate(bgp);

		assertEquals(3, constraintViolations.size());
		for (ConstraintViolation<BGP> constraintViolation : constraintViolations) {
			log.debug("Invalid param = '" + constraintViolation.getPropertyPath()
					+ "', value = '" + constraintViolation.getInvalidValue()
					+ "', error = " + constraintViolation.getMessage());
		}
		log.debug("End testing bgpIsNotValid.");
	}

	@Test
	public void bgpIsValid() {
		log.debug("Testing bgpIsValid...");

		BGP bgp = new BGP();
		bgp.setClientASNumber("65535");
		bgp.setNocASNumber("4294967295");
		ArrayList<String> clientPrefixes = new ArrayList<String>();
		clientPrefixes.add("193.1.190.128/26");
		bgp.setClientPrefixes(clientPrefixes);

		Set<ConstraintViolation<BGP>> constraintViolations =
				validator.validate(bgp);

		// no validation errors
		assertEquals(0, constraintViolations.size());
		log.debug("End testing bgpIsValid.");
	}
}
