package org.opennaas.extensions.vrf.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class Utils {

    /**
     * Accepts an IPv4 address and returns of string of the form xxx.xxx.xxx.xxx
     * ie 192.168.0.1
     *
     * @param ipAddress
     * @return
     */
    public static String fromIPv4Address(int ipAddress) {
        StringBuilder sb = new StringBuilder();
        int result;
        for (int i = 0; i < 4; ++i) {
            result = (ipAddress >> ((3 - i) * 8)) & 0xff;
            sb.append(Integer.valueOf(result).toString());
            if (i != 3) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * Accepts an IPv4 address of the form xxx.xxx.xxx.xxx, ie 192.168.0.1 and
     * returns the corresponding byte array.
     *
     * @param ipAddress The IP address in the form xx.xxx.xxx.xxx.
     * @return The IP address separated into bytes
     */
    public static byte[] toIPv4AddressBytes(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            throw new IllegalArgumentException("Specified IPv4 address must"
                    + "contain 4 sets of numerical digits separated by periods");
        }

        byte[] result = new byte[4];
        for (int i = 0; i < 4; ++i) {
            result[i] = Integer.valueOf(octets[i]).byteValue();
        }
        return result;
    }

    /**
     * Accepts an IPv4 address of the form xxx.xxx.xxx.xxx, ie 192.168.0.1 and
     * returns the corresponding 32 bit integer.
     *
     * @param ipAddress
     * @return
     */
    public static int toIPv4Address(String ipAddress) {
        if (ipAddress == null) {
            throw new IllegalArgumentException("Specified IPv4 address must"
                    + "contain 4 sets of numerical digits separated by periods");
        }
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            throw new IllegalArgumentException("Specified IPv4 address must"
                    + "contain 4 sets of numerical digits separated by periods");
        }

        int result = 0;
        for (int i = 0; i < 4; ++i) {
            int oct = Integer.valueOf(octets[i]);
            if (oct > 255 || oct < 0) {
                throw new IllegalArgumentException("Octet values in specified"
                        + " IPv4 address must be 0 <= value <= 255");
            }
            result |= oct << ((3 - i) * 8);
        }
        return result;
    }

    /**
     * Is an IPv4 address received from the controller. In integer format
     * @param ipAddress
     * @return 
     */
    public static boolean isIPv4Address(String ipAddress) {
        if (ipAddress.contains(":")) {
            return false;
        }
        return true;
    }

    public static String tryToCompressIPv6(String ipv6) {
        if (ipv6.contains("::")) {
            return ipv6;
        } else {
            return ipv6.replaceFirst(":0:", "::");
        }
    }

    public static int isIpAddress(String ipAddress) {
        Pattern VALID_IPV4_PATTERN = null;
        Pattern VALID_IPV6_PATTERN = null;
        String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
        final String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
        final String IPV6_HEX4DECCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?) ::((?:[0-9A-Fa-f]{1,4}:)*)(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
        final String IPV6_6HEX4DEC_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}:){6,6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
        final String IPV6_HEXCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)\\z";
        final String IPV6_REGEX = "\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z";
        try {
            VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
            VALID_IPV6_PATTERN = Pattern.compile(IPV6_HEXCOMPRESSED_REGEX, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            //logger.severe("Unable to compile pattern", e);
        }
        Matcher m1 = VALID_IPV4_PATTERN.matcher(ipAddress);
        if (m1.matches()) {
            return 4;
        }
        Matcher m2 = VALID_IPV6_PATTERN.matcher(ipAddress);
        if(m2.matches()){//Compressed
            return 6;
        }
        //NO match. Try to find with other patterns
        VALID_IPV6_PATTERN = Pattern.compile(IPV6_REGEX, Pattern.CASE_INSENSITIVE);
        m2 = VALID_IPV6_PATTERN.matcher(ipAddress);
        if(m2.matches()){
            return 6;
        }
        VALID_IPV6_PATTERN = Pattern.compile(ipv6Pattern, Pattern.CASE_INSENSITIVE);
        m2 = VALID_IPV6_PATTERN.matcher(ipAddress);
        if(m2.matches()){
            return 6;
        }
        VALID_IPV6_PATTERN = Pattern.compile(IPV6_HEX4DECCOMPRESSED_REGEX, Pattern.CASE_INSENSITIVE);
        m2 = VALID_IPV6_PATTERN.matcher(ipAddress);
        if(m2.matches()){
            return 6;
        }
        VALID_IPV6_PATTERN = Pattern.compile(IPV6_6HEX4DEC_REGEX, Pattern.CASE_INSENSITIVE);
        m2 = VALID_IPV6_PATTERN.matcher(ipAddress);
        if(m2.matches()){
            return 6;
        }        
        
        return 0;
    }
    /**
     * addr is subnet address and addr1 is ip address. Function will return true, if addr1 is within addr(subnet)
     * @param addr
     * @param addr1
     * @return 
     */
    public static boolean netMatch(String addr, String addr1){ 
        
        String[] parts = addr.split("/");
        String ip = parts[0];
        int prefix;

        if (parts.length < 2) {
            prefix = 0;
        } else {
            prefix = Integer.parseInt(parts[1]);
        }

        String[] parts1 = addr1.split("/");

        if (parts1.length > 1) {
            if(addr.equals(addr1)){
                return true;
            }
            return false;
        }
        
        Inet4Address a =null;
        Inet4Address a1 =null;
        try {
            a = (Inet4Address) InetAddress.getByName(ip);
            a1 = (Inet4Address) InetAddress.getByName(addr1);
        } catch (UnknownHostException e){}

        byte[] b = a.getAddress();
        int ipInt = ((b[0] & 0xFF) << 24) |
                         ((b[1] & 0xFF) << 16) |
                         ((b[2] & 0xFF) << 8)  |
                         ((b[3] & 0xFF) << 0);

        byte[] b1 = a1.getAddress();
        int ipInt1 = ((b1[0] & 0xFF) << 24) |
                         ((b1[1] & 0xFF) << 16) |
                         ((b1[2] & 0xFF) << 8)  |
                         ((b1[3] & 0xFF) << 0);

        int mask = ~((1 << (32 - prefix)) - 1);

        if ((ipInt & mask) == (ipInt1 & mask)) {
            return true;
        }
        else {
            return false;
        }
    }
    /**
     * The string is a subnet address, contains the mask
     * @param addr
     * @return The mask of the given ip subnet
     */
    public static int isSubnetAddress(String addr){
        String[] parts = addr.split("/");
        
        if (parts.length < 2) {
            return 0;
        } else {
            return Integer.parseInt(parts[1]);
        }
    }
    /**
     * Prepare the json that should be send to the controller (Floodlight)
     * @param name
     * @param dpid
     * @param destIp
     * @param ethertype
     * @param output
     * @return The json formed
     */
    public static String createJson(String name, String dpid, String destIp, String ethertype, int output){
        return "{\"name\":\"" + name + "\", "
                + "\"switch\": \"" + dpid + "\", "
                + "\"priority\":\"32767\", "
                + "\"dst-ip\":\"" + destIp + ", "
                + "\"ether-type\":\"" + ethertype + "\", "
                + "\"active\":\"true\", "
                + "\"actions\":\"output=" + output + "\"}";
    }
    /**
     * Prepare the json that should be send to the controller (Floodlight). With the ingress port
     * @param name
     * @param dpid
     * @param destIp
     * @param ethertype
     * @param ingress
     * @param output
     * @return The json formed
     */
    public static String createJson(String name, String dpid, String destIp, String ethertype, int ingress, int output){
        return "{\"name\":\"" + name + "\", "
                + "\"switch\": \"" + dpid + "\", "
                + "\"priority\":\"32767\", "
                + "\"ingress-port\":\"" + ingress + ", "
                + "\"dst-ip\":\"" + destIp + ", "
                + "\"ether-type\":\"" + ethertype + "\", "
                + "\"active\":\"true\", "
                + "\"actions\":\"output=" + output + "\"}";
    }
    
    /**
     * HttpRequest in order to insert a new flow. The flow in json format.
     * @param Url
     * @param json 
     */
    public static void putFlowHttpRequest(String Url, String json) {
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
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, "Url is null. Maybe the controllers are not registred.", e);
        } catch (Exception ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * HttpRequest to the controller in order to remove a specific flow
     * @param uri
     * @param flow
     * @return 
     */
    public static String removeHttpRequest(String uri, String flow) {
        String response = "";
        try {
            URL url = new URL(uri + "/wm/staticflowentrypusher/json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // override HTTP method allowing sending body
            connection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
            connection.setDoOutput(true);

            // prepare body
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(flow);
            wr.flush();
            wr.close();
            // get HTTP Response
            response = IOUtils.toString(connection.getInputStream(), "UTF-8");
            if (!response.equals("{\"status\" : \"Entry " + flow + " deleted\"}")) {
                try {
                    throw new Exception("Invalid response: " + response);
                } catch (Exception ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException e) {
            
        }
        return response;
    }
    
    /**
     * Remove all flow entries of the specified switch (dpid)
     * @param uri
     * @param dpid
     * @return status
     */
    public static String removeAllHttpRequest(String uri, String dpid) {
        String response = "";
        try {
            URL url = new URL(uri + "/wm/staticflowentrypusher/clear/"+dpid+"/json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.flush();
            wr.close();
            response = IOUtils.toString(connection.getInputStream(), "UTF-8");

        } catch (IOException e) {
            
        }
        return response;
    }
}
