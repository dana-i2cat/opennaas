package org.opennaas.extensions.vrf.dijkstraroute.capability;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Dijkstra Route
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
        String source = String.valueOf(Utils.StringIPv4toInt("192.168.0.1"));
        String target = String.valueOf(Utils.StringIPv4toInt("192.168.0.2"));
        String DPID = "";
        int inPort = 0;
        DijkstraRoutingCapability instance = new DijkstraRoutingCapability();
        Response expResult = null;
        Response result = instance.getDynamicRoute(source, target, DPID, inPort);
        assertEquals(404, result);
        // TODO review the generated test code and remove the default call to fail.
               
        source = String.valueOf(Utils.StringIPv4toInt("10.0.1.1"));
        target = String.valueOf(Utils.StringIPv4toInt("10.0.2.2"));
        DPID = "00:00:00:00:00:00:00:01";
        inPort = 1;
        instance = new DijkstraRoutingCapability();
        result = instance.getDynamicRoute(source, target, DPID, inPort);
        assertEquals(200, result.getStatus());
    }    
}
