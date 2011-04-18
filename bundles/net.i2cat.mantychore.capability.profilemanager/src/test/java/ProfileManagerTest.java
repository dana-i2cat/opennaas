import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.i2cat.mantychore.capability.profilemanager.ProfileManagerCapability;
import net.i2cat.mantychore.capability.profilemanager.ProfileManagerConstants;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.mantychore.queuemanager.QueueManagerConstants;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class ProfileManagerTest {
	Logger						log			= LoggerFactory
													.getLogger(ProfileManagerTest.class);

	public static String		template	= "<my><dummy></dummy></my>";

	ProfileManagerCapability	profileManager;

	@Before
	public void initProfileManager() {
		MockResource mockResource = new MockResource();

		/* instances */
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		Information capabilityInformation = new Information();

		/* initialize the resource with profile manager capability */
		capabilityInformation.setType(ProfileManagerCapability.PROFILEMANAGER);
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);
		mockResource.addCapabilityDescriptor(capabilityDescriptor);

		/* new instances */
		capabilityDescriptor = new CapabilityDescriptor();
		capabilityInformation = new Information();

		/* initialize queue */
		capabilityInformation.setType(QueueManager.QUEUE);
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);
		mockResource.addCapabilityDescriptor(capabilityDescriptor);

		List<String> actionIds = new ArrayList<String>();
		actionIds.add(QueueManagerConstants.EXECUTE);
		actionIds.add(QueueManagerConstants.GETQUEUE);
		MockQueueManager queueManager = new MockQueueManager(actionIds, mockResource);

		actionIds = new ArrayList<String>();
		actionIds.add(ProfileManagerConstants.ADDOPERATION);
		actionIds.add(ProfileManagerConstants.DELETEOPERATION);
		actionIds.add(ProfileManagerConstants.GETOPERATIONTEMPLATE);
		actionIds.add(ProfileManagerConstants.LISTOPERATIONS);
		actionIds.add(ProfileManagerConstants.QUEUEOPERATION);

		profileManager = new ProfileManagerCapability(actionIds, mockResource);
		profileManager.setQueueManager(queueManager);

	}

	@Test
	public void addOperation() {
		log.info("INFO: Add operation test ");

		/* add operation */
		Properties props = new Properties();
		props.put(ProfileManagerConstants.IDOPERATION, "myTemplate");
		props.put(ProfileManagerConstants.TEMPLATE, template);
		Response response = profileManager.sendMessage(ProfileManagerConstants.ADDOPERATION, props);
		Assert.isTrue(response.getStatus() == Response.Status.OK);

		/* get template for an operation */
		response = profileManager.sendMessage(ProfileManagerConstants.GETOPERATIONTEMPLATE, props);
		Assert.isTrue(response.getStatus() == Response.Status.OK);
		Assert.isTrue(response.getInformation().contains(template));
		log.info("INFO: Response: ");
		log.info(response.getInformation());
		log.info("----------------------");
		log.info("INFO: Add operation DONE!");

	}

	@Test
	public void addAndRemoveOperation() {
		log.info("INFO: Add and remove operation test ");

		/* add operation */
		Properties props = new Properties();
		props.put(ProfileManagerConstants.IDOPERATION, "myTemplate");
		props.put(ProfileManagerConstants.TEMPLATE, template);
		Response response = profileManager.sendMessage(ProfileManagerConstants.ADDOPERATION, props);
		Assert.isTrue(response.getStatus() == Response.Status.OK);

		/* add and remove operation */
		props.put(ProfileManagerConstants.IDOPERATION, "myTemplate");
		response = profileManager.sendMessage(ProfileManagerConstants.DELETEOPERATION, props);
		Assert.isTrue(response.getStatus() == Response.Status.OK);
		log.info("INFO: Add and remove operation DONE!");

	}

	@Test
	public void listOperations() {
		log.info("INFO: List Operations test ");
		/* add operation */
		Properties props = new Properties();
		props.put(ProfileManagerConstants.IDOPERATION, "myTemplate");
		props.put(ProfileManagerConstants.TEMPLATE, template);
		Response response = profileManager.sendMessage(ProfileManagerConstants.ADDOPERATION, props);
		Assert.isTrue(response.getStatus() == Response.Status.OK);

		/* list available opers */
		response = profileManager.sendMessage(ProfileManagerConstants.LISTOPERATIONS, props);
		Assert.isTrue(response.getStatus() == Response.Status.OK);
		log.info("INFO: Response: ");
		log.info(response.getInformation());
		log.info("----------------------");
		Assert.isTrue(response.getInformation().contains("myTemplate"));
		log.info("INFO: List Operations DONE!");

	}

	@Test
	public void queueOperations() {
		/* add operation */
		log.info("INFO: Queue Operation ");
		Properties props = new Properties();
		props.put(ProfileManagerConstants.IDOPERATION, "myTemplate");
		props.put(ProfileManagerConstants.TEMPLATE, template);
		Response response = profileManager.sendMessage(ProfileManagerConstants.ADDOPERATION, props);
		Assert.isTrue(response.getStatus() == Response.Status.OK);

		/* queue new oper */
		Object params = new Object();
		response = profileManager.sendMessage("myTemplate", params);
		Assert.isTrue(response.getStatus() == Response.Status.OK);
		log.info("INFO: Queue Operation DONE!");

	}

}
