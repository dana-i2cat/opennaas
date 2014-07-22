package org.opennaas.extensions.vrf.dijkstraroute.capability;

import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opennaas.extensions.vrf.utils.Utils;

/**
 *
 * @author josep
 */
public class RoutingCapabilityTest {
    
    public RoutingCapabilityTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDynamicRoute method, of class RoutingCapability.
     */
    @Test
    public void testGetDynamicRoute() {
        System.out.println("getDynamicRoute");
        String source = "";
        String target = "";
        String DPID = "";
        int inPort = 0;
        DijkstraRoutingCapability instance = new DijkstraRoutingCapability();
        Response expResult = null;
        Response result = instance.getDynamicRoute(source, target, DPID, inPort);
        assertEquals(404, result);
        // TODO review the generated test code and remove the default call to fail.
               
        source = String.valueOf(Utils.StringIPv4toInt("192.168.1.2"));
        target = String.valueOf(Utils.StringIPv4toInt("192.168.2.2"));
        DPID = "00:00:00:00:00:00:00:01";
        inPort = 1;
        instance = new DijkstraRoutingCapability();
        result = instance.getDynamicRoute(source, target, DPID, inPort);
        assertEquals(200, result.getStatus());
    }    
}
