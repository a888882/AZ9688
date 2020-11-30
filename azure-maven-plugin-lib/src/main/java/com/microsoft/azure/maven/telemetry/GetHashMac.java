/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

/**
 * Disclaimer:
 *      This class is copied from https://github.com/Microsoft/azure-tools-for-java/ with minor modification (fixing
 *      static analysis error).
 *      Location in the repo: /Utils/azuretools-core/src/com/microsoft/azuretools/azurecommons/util/GetHashMac.java
 */

package com.microsoft.azure.maven.telemetry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetHashMac {
    public static final String MAC_REGEX = "([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}";
    public static final String MAC_REGEX_ZERO = "([0]{2}[:-]){5}[0]{2}";
    public static final String HASHED_MAC_REGEX = "[0-9a-f]{64}";

    public static boolean isValidHashMacFormat(String hashMac) {
        if (hashMac == null || hashMac.isEmpty()) {
            return false;
        }

        final Pattern hashedMacPattern = Pattern.compile(HASHED_MAC_REGEX);
        final Matcher matcher = hashedMacPattern.matcher(hashMac);
        return matcher.matches();
    }

    public static String getHashMac() {
        final String rawMac = getRawMac();
        if (rawMac == null || rawMac.isEmpty()) {
            return null;
        }

        final Pattern pattern = Pattern.compile(MAC_REGEX);
        final Pattern patternZero = Pattern.compile(MAC_REGEX_ZERO);
        final Matcher matcher = pattern.matcher(rawMac);
        String mac = "";
        while (matcher.find()) {
            mac = matcher.group(0);
            if (!patternZero.matcher(mac).matches()) {
                break;
            }
        }

        return hash(mac);
    }

    private static String getRawMac() {
        String ret = null;
        try {
            final String os = System.getProperty("os.name").toLowerCase();
            String[] command = {"ifconfig", "-a"};
            if (os != null && !os.isEmpty() && os.startsWith("win")) {
                command = new String[]{"getmac"};
            }

            final ProcessBuilder builder = new ProcessBuilder(command);
            final Process process = builder.start();
            final InputStream inputStream = process.getInputStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader br = new BufferedReader(inputStreamReader);
            String tmp;
            while ((tmp = br.readLine()) != null) {
                ret += tmp;
            }

            if (inputStream != null) {
                inputStream.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (IOException ex) {
            return null;
        }

        return ret;
    }

    private static String hash(String mac) {
        if (mac == null || mac.isEmpty()) {
            return null;
        }

        String ret;
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] bytes = mac.getBytes("UTF-8");
            md.update(bytes);
            final byte[] bytesAfterDigest = md.digest();
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytesAfterDigest.length; i++) {
                sb.append(Integer.toString((bytesAfterDigest[i] & 0xff) + 0x100, 16).substring(1));
            }

            ret = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (UnsupportedEncodingException ex) {
            return null;
        }

        return ret;
    }
}
