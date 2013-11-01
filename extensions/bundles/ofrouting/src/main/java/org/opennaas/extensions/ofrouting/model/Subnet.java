package org.opennaas.extensions.ofrouting.model;

import java.net.InetAddress;

/**
 * IP subnet consisting of a network address and a network mask. Individual ip
 * addresses are represented as all ones subnets. Zero subnets are also
 * allowed.
 */
public class Subnet {

    private final InetAddress ipAddress;
    private final int mask;

    public Subnet(InetAddress ipAddress, int mask) {
        this.ipAddress = ipAddress;
        this.mask = mask;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public String getIpAddressString() {
        return ipAddress.getHostAddress();
    }

    /**
     * Creates a subnet of the given pattern.
     *
     * Patterns can be IPv4/IPv6 addresses or subnets in both asterisk and CIDR
     * notation. Examples of valid patterns:
     * <pre>
     * 192.168.1.10
     * ::10
     * 192.168.1.*
     * 192.168.5.128/26
     * 0:0:0:7b::/64
     * </pre>
     *
     * @param pattern pattern to create a Subnet of
     * @return Subnet based on the given pattern
     */
    public static Subnet forPattern(String pattern) {
        final InetAddress ipAddress;
        final int mask;
        if (pattern.matches("(\\d+\\.)*\\*(\\.\\*)*")) // Asterisk subnet notation
        {
            String[] parts = pattern.split("\\.");
            int asteriskCount = 0;
            for (int i = parts.length - 1; i >= 0 && parts[i].equals("*"); --i) {
                ++asteriskCount;
            }
            ipAddress = InetAddresses.forString(pattern.replace("*", "0"));
            mask = 32 - 8 * asteriskCount;
        } else if (pattern.contains("/")) // CIDR subnet notation
        {
            final String[] addressComponents = pattern.split("/", 2);
            ipAddress = InetAddresses.forString(addressComponents[0]);
            mask = Integer.parseInt(addressComponents[1]);
        } else // Plain IP address (Treated as all ones subnet for matching purposes)
        {
            ipAddress = InetAddresses.forString(pattern);
            mask = 8 * ipAddress.getAddress().length;
        }
        return new Subnet(ipAddress, mask);
    }

    /**
     * Creates an all ones subnet of the given ip address.
     *
     * @param ipAddress ipAddress to create a subnet of
     * @return all ones subnet based on the given ip address
     */
    public static Subnet forAddress(InetAddress ipAddress) {
        return new Subnet(ipAddress, ipAddress.getAddress().length * 8);
    }

    /**
     * Returns true if the given pattern is a valid subnet pattern.
     *
     * @param pattern pattern to validate
     * @return true if the given pattern is a valid subnet pattern
     */
    public static boolean isValidPattern(String pattern) {
        try {
            forPattern(pattern);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns the network address of this subnet.
     *
     * @return network address of this subnet
     */
    public byte[] getAddress() {
        return ipAddress.getAddress();
    }

    /**
     * Returns the network mask of this subnet.
     *
     * @return network mask of this subnet
     */
    public int getMask() {
        return mask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Subnet subnet = (Subnet) o;

        if (mask != subnet.mask) {
            return false;
        }
        if (!ipAddress.equals(subnet.ipAddress)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = ipAddress.hashCode();
        result = 31 * result + mask;
        return result;
    }
    
    /*
 * Get network mask for the IP address and network prefix specified...
 * The network mask will be returned has an IP, thus you can
 * print it out with .getHostAddress()...
 */
public static InetAddress getIPv4LocalNetMask(InetAddress ip, int netPrefix) {

    try {
        // Since this is for IPv4, it's 32 bits, so set the sign value of
        // the int to "negative"...
        int shiftby = (1<<31);
        // For the number of bits of the prefix -1 (we already set the sign bit)
        for (int i=netPrefix-1; i>0; i--) {
            // Shift the sign right... Java makes the sign bit sticky on a shift...
            // So no need to "set it back up"...
            shiftby = (shiftby >> 1);
        }
        // Transform the resulting value in xxx.xxx.xxx.xxx format, like if
        /// it was a standard address...
        String maskString = Integer.toString((shiftby >> 24) & 255) + "." + Integer.toString((shiftby >> 16) & 255) + "." + Integer.toString((shiftby >> 8) & 255) + "." + Integer.toString(shiftby & 255);
        // Return the address thus created...
        return InetAddress.getByName(maskString);
    }
        catch(Exception e){e.printStackTrace();
    }
    // Something went wrong here...
    return null;
}
}