package org.opennaas.extensions.vrf.staticroute.capability.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.vrf.staticroute.capability.StaticRoutingCapability;

/**
 *
 * @author Josep Batallé
 */
public class Utils {
    
    static Log log = LogFactory.getLog(Utils.class);
    
    public static void deleteFloodlightFlowHttpRequest(String uri, String dpid) {
        try {
            OutputStreamWriter wr = null;
            URL url = new URL(uri + "/wm/staticflowentrypusher/clear/" + dpid + "/json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.getInputStream();
            //response = connection.getResponseMessage();
        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void insertFloodlightFlowHttpRequest(String Url, String json) {
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(json);
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();
        } catch (UnknownHostException e) {
            log.error("Url is null. Maybe the controllers are not registred.");
        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
