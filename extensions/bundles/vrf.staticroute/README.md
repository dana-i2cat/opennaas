Virtualized Routing Function - Static Routing
========

This bundle offers the Static Routing Function.
Is deployed as application on top of OpenNaaS and is accessible through WebServices.
Contains two REST interfaces.

One is used in order to manage the routing tables (insert, delete... routes)
	- The address is /opennaas/vrf/routemgt/
	- Functions: 
		- insert Route
		- insertRoutesFromFile
		- remove Route
		- remove Routes
		- get Routes

The other REST interface is used by OpenFlow controllers in order to request a route.
	- The address is /opennaas/vrf/staticrouting/
	- Functions: 
		- get Route
		- set Generic Network Resource Name
