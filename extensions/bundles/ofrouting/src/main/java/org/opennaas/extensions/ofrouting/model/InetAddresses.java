/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * This file has been copied from http://code.google.com/p/guava-libraries/ and
 * modified to remove unused methods as we cannot currently safely distribute
 * Guava with the Platform common modules.
 */
package org.opennaas.extensions.ofrouting.model;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Copied from http://code.google.com/p/guava-libraries/.
 *
 * Should be refactored away after
 * https://studio.atlassian.com/browse/REFAPP-164 has been resolved.
 */
final class InetAddresses {

    private static final int IPV4_PART_COUNT = 4;
    private static final int IPV6_PART_COUNT = 8;

    private InetAddresses() {
    }

    /**
     * Returns the {@link java.net.InetAddress} having the given string
     * representation.
     *
     * <p>This deliberately avoids all nameservice lookups (e.g. no DNS).
     *
     * @param ipString {@code String} containing an IPv4 or IPv6 string literal,
     * e.g. {@code "192.168.0.1"} or {@code "2001:db8::1"}
     * @return {@link java.net.InetAddress} representing the argument
     * @throws IllegalArgumentException if the argument is not a valid IP string
     * literal
     */
    public static InetAddress forString(String ipString) {
        byte[] addr = textToNumericFormatV4(ipString);
        if (addr == null) {
            // Scanning for IPv4 string literal failed; try IPv6.
            addr = textToNumericFormatV6(ipString);
        }

        // The argument was malformed, i.e. not an IP string literal.
        if (addr == null) {
            throw new IllegalArgumentException(
                    String.format("'%s' is not an IP string literal.", ipString));
        }

        try {
            return InetAddress.getByAddress(addr);
        } catch (UnknownHostException e) {

            /*
             * This really shouldn't happen in practice since all our byte
             * sequences should be valid IP addresses.
             *
             * However {@link InetAddress#getByAddress} is documented as
             * potentially throwing this "if IP address is of illegal length".
             *
             * This is mapped to IllegalArgumentException since, presumably,
             * the argument triggered some processing bug in either
             * {@link IPAddressUtil#textToNumericFormatV4} or
             * {@link IPAddressUtil#textToNumericFormatV6}.
             */
            throw new IllegalArgumentException(
                    String.format("'%s' is extremely broken.", ipString), e);
        }
    }

    /**
     * Returns {@code true} if the supplied string is a valid IP string literal,
     * {@code false} otherwise.
     *
     * @param ipString {@code String} to evaluated as an IP string literal
     * @return {@code true} if the argument is a valid IP string literal
     */
    public static boolean isInetAddress(String ipString) {
        try {
            forString(ipString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] textToNumericFormatV4(String ipString) {
        if (ipString.contains(":")) {
            // For the special mapped address cases (e.g. "::ffff:192.0.2.1") passing
            // InetAddress.getByAddress() the output of textToNumericFormatV6()
            // below will "do the right thing", i.e. construct an Inet4Address.
            return null;
        }

        String[] address = ipString.split("\\.");
        if (address.length != IPV4_PART_COUNT) {
            return null;
        }

        byte[] bytes = new byte[IPV4_PART_COUNT];
        try {
            for (int i = 0; i < bytes.length; i++) {
                int piece = Integer.parseInt(address[i]);
                if (piece < 0 || piece > 255) {
                    return null;
                }

                // No leading zeroes are allowed.  See
                // http://tools.ietf.org/html/draft-main-ipaddr-text-rep-00
                // section 2.1 for discussion.

                if (address[i].startsWith("0") && address[i].length() != 1) {
                    return null;
                }
                bytes[i] = (byte) piece;
            }
        } catch (NumberFormatException ex) {
            return null;
        }

        return bytes;
    }

    private static byte[] textToNumericFormatV6(String ipString) {
        if (!ipString.contains(":")) {
            return null;
        }
        if (ipString.contains(":::")) {
            return null;
        }

        if (ipString.contains(".")) {
            ipString = convertDottedQuadToHex(ipString);
            if (ipString == null) {
                return null;
            }
        }

        ByteBuffer rawBytes = ByteBuffer.allocate(2 * IPV6_PART_COUNT);
        // Keep a record of the number of parts specified above/before a "::"
        // (partsHi), and below/after any "::" (partsLo).
        int partsHi = 0;
        int partsLo = 0;

        String[] addressHalves = ipString.split("::", 2);  // At most 1 "::".
        // Parse parts above any "::", or the whole string if no "::" present.
        if (!addressHalves[0].equals("")) {
            String[] parts = addressHalves[0].split(":", IPV6_PART_COUNT);
            try {
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals("")) {
                        // No empty segments permitted.
                        return null;
                    }
                    int piece = Integer.parseInt(parts[i], 16);
                    rawBytes.putShort(2 * i, (short) piece);
                }
                partsHi = parts.length;
            } catch (NumberFormatException ex) {
                return null;
            }
        } else {
            // A leading "::".  At least one 16bit segment must be zero.
            partsHi = 1;
        }

        // Parse parts below "::" (if any), into the tail end of the byte array,
        // working backwards.
        if (addressHalves.length > 1) {
            if (!addressHalves[1].equals("")) {
                String[] parts = addressHalves[1].split(":", IPV6_PART_COUNT);
                try {
                    for (int i = 0; i < parts.length; i++) {
                        int partsIndex = parts.length - i - 1;
                        if (parts[partsIndex].equals("")) {
                            // No empty segments permitted.
                            return null;
                        }
                        int piece = Integer.parseInt(parts[partsIndex], 16);
                        int bytesIndex = 2 * (IPV6_PART_COUNT - i - 1);
                        rawBytes.putShort(bytesIndex, (short) piece);
                    }
                    partsLo = parts.length;
                } catch (NumberFormatException ex) {
                    return null;
                }
            } else {
                // A trailing "::".  At least one 16bit segment must be zero.
                partsLo = 1;
            }
        }

        // Some extra sanity checks.
        int totalParts = partsHi + partsLo;
        if (totalParts > IPV6_PART_COUNT) {
            return null;
        }
        if (addressHalves.length == 1 && totalParts != IPV6_PART_COUNT) {
            // If no "::" shortening is used then all bytes must have been specified.
            return null;
        }

        return rawBytes.array();
    }

    private static String convertDottedQuadToHex(String ipString) {
        int lastColon = ipString.lastIndexOf(':');
        String initialPart = ipString.substring(0, lastColon + 1);
        String dottedQuad = ipString.substring(lastColon + 1);
        byte[] quad = textToNumericFormatV4(dottedQuad);
        if (quad == null) {
            return null;
        }
        String penultimate = Integer.toHexString(((quad[0] & 0xff) << 8) | (quad[1] & 0xff));
        String ultimate = Integer.toHexString(((quad[2] & 0xff) << 8) | (quad[3] & 0xff));
        return initialPart + penultimate + ":" + ultimate;
    }

    /**
     * Returns an InetAddress representing the literal IPv4 or IPv6 host portion
     * of a URL, encoded in the format specified by RFC 3986 section 3.2.2.
     *
     * <p>This function is similar to
     * {@link com.atlassian.ip.InetAddresses#forString(String)}, however, it
     * requires that IPv6 addresses are surrounded by square brackets.
     *
     * @param hostAddr A RFC 3986 section 3.2.2 encoded IPv4 or IPv6 address
     * @return an InetAddress representing the address in {@code hostAddr}
     * @throws IllegalArgumentException if {@code hostAddr} is not a valid IPv4
     * address, or IPv6 address surrounded by square brackets
     */
    public static InetAddress forUriString(String hostAddr) {
        InetAddress retval = null;

        // IPv4 address?
        try {
            retval = forString(hostAddr);
            if (retval instanceof Inet4Address) {
                return retval;
            }
        } catch (IllegalArgumentException e) {
            // Not a valid IP address, fall through.
        }

        // IPv6 address
        if (!(hostAddr.startsWith("[") && hostAddr.endsWith("]"))) {
            throw new IllegalArgumentException("Not a valid address: \"" + hostAddr + '"');
        }

        retval = forString(hostAddr.substring(1, hostAddr.length() - 1));
        if (retval instanceof Inet6Address) {
            return retval;
        }

        throw new IllegalArgumentException("Not a valid address: \"" + hostAddr + '"');
    }
/**
   * Returns the string representation of an {@link InetAddress} suitable
   * for inclusion in a URI.
   *
   * <p>For IPv4 addresses, this is identical to
   * {@link InetAddress#getHostAddress()}, but for IPv6 addresses it
   * compresses zeroes and surrounds the text with square brackets; for example
   * {@code "[2001:db8::1]"}.
   *
   * <p>Per section 3.2.2 of
   * <a target="_parent"
   *    href="http://tools.ietf.org/html/rfc3986#section-3.2.2"
   *  >http://tools.ietf.org/html/rfc3986</a>,
   * a URI containing an IPv6 string literal is of the form
   * {@code "http://[2001:db8::1]:8888/index.html"}.
   *
   * <p>Use of either {@link InetAddresses#toAddrString},
   * {@link InetAddress#getHostAddress()}, or this method is recommended over
   * {@link InetAddress#toString()} when an IP address string literal is
   * desired.  This is because {@link InetAddress#toString()} prints the
   * hostname and the IP address string joined by a "/".
   *
   * @param ip {@link InetAddress} to be converted to URI string literal
   * @return {@code String} containing URI-safe string literal
   */
    public static String toUriString(InetAddress ip) {
        if (ip instanceof Inet6Address) {
            return "[" + toAddrString(ip) + "]";
        }
        return toAddrString(ip);
    }

    /**
     * Returns the string representation of an {@link InetAddress}.
     *
     * <p>For IPv4 addresses, this is identical to
     * {@link InetAddress#getHostAddress()}, but for IPv6 addresses, the output
     * follows <a href="http://tools.ietf.org/html/rfc5952">RFC 5952</a> section
     * 4. The main difference is that this method uses "::" for zero
     * compression, while Java's version uses the uncompressed form.
     *
     * <p>This method uses hexadecimal for all IPv6 addresses, including
     * IPv4-mapped IPv6 addresses such as "::c000:201". The output does not
     * include a Scope ID.
     *
     * @param ip {@link InetAddress} to be converted to an address string
     * @return {@code String} containing the text-formatted IP address
     * @since 10.0
     */
    public static String toAddrString(InetAddress ip) {
        Preconditions.checkNotNull(ip);
        if (ip instanceof Inet4Address) {
            // For IPv4, Java's formatting is good enough.
            return ip.getHostAddress();
        }
        Preconditions.checkArgument(ip instanceof Inet6Address);
        byte[] bytes = ip.getAddress();
        int[] hextets = new int[IPV6_PART_COUNT];
        for (int i = 0; i < hextets.length; i++) {
            hextets[i] = Ints.fromBytes(
                    (byte) 0, (byte) 0, bytes[2 * i], bytes[2 * i + 1]);
        }
        compressLongestRunOfZeroes(hextets);
        return hextetsToIPv6String(hextets);
    }

    /**
     * Identify and mark the longest run of zeroes in an IPv6 address.
     *
     * <p>Only runs of two or more hextets are considered. In case of a tie, the
     * leftmost run wins. If a qualifying run is found, its hextets are replaced
     * by the sentinel value -1.
     *
     * @param hextets {@code int[]} mutable array of eight 16-bit hextets
     */
    private static void compressLongestRunOfZeroes(int[] hextets) {
        int bestRunStart = -1;
        int bestRunLength = -1;
        int runStart = -1;
        for (int i = 0; i < hextets.length + 1; i++) {
            if (i < hextets.length && hextets[i] == 0) {
                if (runStart < 0) {
                    runStart = i;
                }
            } else if (runStart >= 0) {
                int runLength = i - runStart;
                if (runLength > bestRunLength) {
                    bestRunStart = runStart;
                    bestRunLength = runLength;
                }
                runStart = -1;
            }
        }
        if (bestRunLength >= 2) {
            Arrays.fill(hextets, bestRunStart, bestRunStart + bestRunLength, -1);
        }
    }

    /**
     * Convert a list of hextets into a human-readable IPv6 address.
     *
     * <p>In order for "::" compression to work, the input should contain
     * negative sentinel values in place of the elided zeroes.
     *
     * @param hextets {@code int[]} array of eight 16-bit hextets, or -1s
     */
    private static String hextetsToIPv6String(int[] hextets) {
        /*
         * While scanning the array, handle these state transitions:
         *   start->num => "num"     start->gap => "::"
         *   num->num   => ":num"    num->gap   => "::"
         *   gap->num   => "num"     gap->gap   => ""
         */
        StringBuilder buf = new StringBuilder(39);
        boolean lastWasNumber = false;
        for (int i = 0; i < hextets.length; i++) {
            boolean thisIsNumber = hextets[i] >= 0;
            if (thisIsNumber) {
                if (lastWasNumber) {
                    buf.append(':');
                }
                buf.append(Integer.toHexString(hextets[i]));
            } else {
                if (i == 0 || lastWasNumber) {
                    buf.append("::");
                }
            }
            lastWasNumber = thisIsNumber;
        }
        return buf.toString();
    }
}