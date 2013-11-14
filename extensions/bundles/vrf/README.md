SampleResource readme file.

SampleResource is a resource intended to illustrate how to support new resources in OpenNaaS.
It offers a skeleton that can be used as a base to develop new resource types.

Resources are created by providing a resource descriptor to OpenNaaS ResourceManager.
ResourceManager delegates descriptor interpretation to a ResourceRepository based on the resource type specified in the descriptor.

ResourceRepository is one of pieces to provide for OpenNaaS being able to support a new resource type. 
It's its responsability:
* to create resources given a correct descriptor, 
* to persist the descriptor and
* to manage resource life-cycle through life-cycle calls (createResource, startResource, stopResource, removeResource)


Once a resource has been created it may be started for it to be active.
During the start process ResourceBootstrapper comes into play.
It is the responsible of initializing the resource model, and is also required as part of the new resource type support.

An active resource has an initialized model and some capabilities to operate with it.

Capabilities are per resource-type functional packages that users may interact to get resource state and alter it.
Capabilities often delegate to drivers some of its operations, that's because while capabilities are fabric unaware, drivers are fabric specific.

ExampleCapability require no drivers, because it can provide all functionality by its own.
However, an ExampleActionSet class is provided as a guide.

A capability ActionSet defines which actions should be provided by a driver in order to be suitable for the capability.

When a resource is created in OpenNaaS, which capabilities should be loaded for it
and what drivers should they use is specified in the resource descriptor.
A descriptor for sample resource using example capability can be found in 
utils/examples/descriptors/sampleresource.descriptor
 

In order for all pieces to communicate with each other, some of them should export or consume OSGi services.
It is the case of capabilities and drivers, but also resource repository and bootstrapper.
src/main/resources/OSGI-INF/blueprint/core.xml file is used to achieve this goal. 

More information about service OSGi publication can be find here:
http://fusesource.com/docs/esb/4.2/deploy_osgi/index.html 
