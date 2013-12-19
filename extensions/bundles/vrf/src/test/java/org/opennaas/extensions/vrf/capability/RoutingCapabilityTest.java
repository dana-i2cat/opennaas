package org.opennaas.extensions.vrf.capability;

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
        String ipSource = String.valueOf(Utils.StringIPv4toInt("192.168.1.2"));
        String ipDest = String.valueOf(Utils.StringIPv4toInt("192.168.2.51"));
        String switchDPID = "00:00:00:00:00:00:00:01";
        int inputPort = 1;
        boolean proactive = true;
        RoutingCapability instance = new RoutingCapability();
        Response result = instance.getRoute(ipSource, ipDest, switchDPID, inputPort, proactive);
        assertEquals(404, result.getStatus());

        instance.getVRFModel();
        result = instance.getRoute(ipSource, ipDest, switchDPID, inputPort, proactive);
        assertEquals(404, result.getStatus());

        String filename = textFileToString(PATH_FILE);
        InputStream is = new ByteArrayInputStream(filename.getBytes("UTF-8"));
        instance.insertRouteFile(FILE_NAME, is);
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
        RoutingCapability instance = new RoutingCapability();
        Response result = instance.insertRoute(ipSource, ipDest, switchDPID, inputPort, outputPort, lifeTime);
        assertEquals(201, result.getStatus());
    }

    /**
     * Test of removeRoutes method, of class RoutingCapability.
     */
    @Test
    public void testRemoveRoutes() {
        System.out.println("removeRoutes");
        RoutingCapability instance = new RoutingCapability();
        Response result = instance.removeRoutes();
        assertEquals(200, result.getStatus());
    }

    /**
     * Test of getRoutes method, of class RoutingCapability.
     */
    @Test
    public void testGetRoutes_0args() {
        System.out.println("getRoutes");
        RoutingCapability instance = new RoutingCapability();
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
        RoutingCapability instance = new RoutingCapability();
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
        RoutingCapability instance = new RoutingCapability();
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
