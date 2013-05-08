import groovy.xml.MarkupBuilder

import javax.xml.bind.JAXBContext

import org.opennaas.core.resources.descriptor.ResourceDescriptor
import org.opennaas.core.resources.IResource

println "DUMMY, this script should be launched only by integration test CoreTest.runDummyScript"

println 'The resource manager is available as "resources" a globally bind variable'
println resources

def writer = new StringWriter()
def descriptorXml = new MarkupBuilder(writer)

descriptorXml.resourceDescriptor() {
	
	information() {
		type("sampleresource") 
		name("resource1")
	}

	capabilityDescriptors() {
		information() {
			type("example")
		}
	}

	properties()	
}

println "Loaded XML for resource descriptor looks like:\n" + writer.toString()

def descriptor = (ResourceDescriptor) JAXBContext.newInstance(ResourceDescriptor.class).createUnmarshaller().unmarshal(new StringReader(writer.toString()))

println "Descriptor created looks like:\n" + descriptor

IResource resource = resources.createResource descriptor

resources.listResources().each { println it }

print "Starting resource... "
resources.startResource resource.getResourceIdentifier()
println "started."

sleep 2000 // dOSGi needs some time..

print "Stopping resource... "
resources.stopResource resource.getResourceIdentifier()
println "stopped."

sleep 2000 // dOSGi needs some time..

print "Removing resource... "
resources.removeResource resources.getIdentifierFromResourceName("sampleresource", "resource1")
println "removed."
