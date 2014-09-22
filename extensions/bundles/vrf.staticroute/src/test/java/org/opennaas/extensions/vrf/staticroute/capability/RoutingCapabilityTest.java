package org.opennaas.extensions.vrf.staticroute.capability;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Static Routing
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.opennaas.extensions.vrf.staticroute.capability.routing.StaticRoutingCapability;
import org.opennaas.extensions.vrf.staticroute.capability.routemgt.StaticRouteMgtCapability;
import org.opennaas.extensions.vrf.utils.Utils;

/**
 *
 * @author josep
 */
public class RoutingCapabilityTest {

    private final static String PATH_FILE = "/routes/sampleJSONRoutes.json";
    private final static String FILE_NAME = "sampleJSONRoutes";
    private final static int version = 4;

    /**
     * Test of getRoute method, of class RoutingCapability.
     *
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testGetRoute() throws FileNotFoundException, IOException {
        System.out.println("getRoute");
        String ipSource = String.valueOf(Utils.StringIPv4toInt("10.0.1.1"));
        String ipDest = String.valueOf(Utils.StringIPv4toInt("10.0.2.2"));
        String switchDPID = "00:00:00:00:00:00:00:01";
        int inputPort = 1;
        boolean proactive = true;
        StaticRoutingCapability instance = new StaticRoutingCapability();
        StaticRouteMgtCapability instance2 = new StaticRouteMgtCapability();
        Response result = instance.getRoute(ipSource, ipDest, switchDPID, inputPort, proactive);
        assertEquals(404, result.getStatus());

        instance.getVRFModel();
        result = instance.getRoute(ipSource, ipDest, switchDPID, inputPort, proactive);
        assertEquals(404, result.getStatus());

        String filename = textFileToString(PATH_FILE);
        InputStream is = new ByteArrayInputStream(filename.getBytes("UTF-8"));
        instance2.insertRouteFile(FILE_NAME, is);
        proactive = false;
        result = instance.getRoute(ipSource, ipDest, switchDPID, inputPort, proactive);
        assertEquals(200, result.getStatus());
    }

    /**
     * Test of insertRoute method, of class RoutingCapability.
     */
    @Test
    public void testInsertRoute() {
        System.out.println("insertRoute");
        String ipSource = "192.168.1.2";
        String ipDest = "192.168.2.51";
        String switchDPID = "00:00:00:00:00:00:00:01";
        int inputPort = 1;
        int outputPort = 2;
        int lifeTime = 0;
        StaticRouteMgtCapability instance = new StaticRouteMgtCapability();
        Response result = instance.insertRoute(ipSource, ipDest, switchDPID, inputPort, outputPort, lifeTime);
        assertEquals(201, result.getStatus());
    }

    /**
     * Test of removeRoutes method, of class RoutingCapability.
     */
    @Test
    public void testRemoveRoutes() {
        System.out.println("removeRoutes");
        StaticRouteMgtCapability instance = new StaticRouteMgtCapability();
        Response result = instance.removeRoutes();
        assertEquals(200, result.getStatus());
    }

    /**
     * Test of getRoutes method, of class RoutingCapability.
     */
    @Test
    public void testGetRoutes_0args() {
        System.out.println("getRoutes");
        StaticRouteMgtCapability instance = new StaticRouteMgtCapability();
        Response result = instance.getRoutes();
        assertEquals(200, result.getStatus());
    }

    /**
     * Test of getRoutes method, of class RoutingCapability.
     */
    @Test
    public void testGetRoutes_int() {
        System.out.println("getRoutes");
        int ver = RoutingCapabilityTest.version;
        StaticRouteMgtCapability instance = new StaticRouteMgtCapability();
        Response result = instance.getRoutes(ver);
        Assert.assertEquals("List of routes", 200, result.getStatus());
    }

    /**
     * Test of insertRouteFile method, of class RoutingCapability.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testInsertRouteFile() throws IOException {
        System.out.println("insertRouteFile");
        String filename = textFileToString(PATH_FILE);
        InputStream is = new ByteArrayInputStream(filename.getBytes("UTF-8"));
        StaticRouteMgtCapability instance = new StaticRouteMgtCapability();
        Response result = instance.insertRouteFile(FILE_NAME, is);
        assertEquals(200, result.getStatus());
    }

    private String textFileToString(String fileLocation) throws IOException {
        String fileString = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileLocation)));
        String line;
        while ((line = br.readLine()) != null) {
            fileString += line += "\n";
        }
        br.close();
        return fileString;
    }
}
