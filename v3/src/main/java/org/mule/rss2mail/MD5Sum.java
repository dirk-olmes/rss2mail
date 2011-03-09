/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.rss2mail;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Calculates an MD5 checksum over a given String.
 * This was inspired by http://www.javalobby.org/java/forums/t84420.html
 */
public class MD5Sum
{
    public static String digest(String input)
    {
        if (input == null)
        {
            return null;
        }

        byte[] bytes = input.getBytes();
        MessageDigest messageDigest = getMd5MessageDigest();
        messageDigest.update(bytes, 0, input.length());
        byte[] md5Buffer = messageDigest.digest();

        BigInteger bigInt = new BigInteger(1, md5Buffer);
        return bigInt.toString(16);
    }

    private static MessageDigest getMd5MessageDigest()
    {
        try
        {
            return MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }
}
