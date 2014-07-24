package org.opennaas.extensions.vrf.staticroute.utils;

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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.vrf.staticroute.capability.routing.StaticRoutingCapability;

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
