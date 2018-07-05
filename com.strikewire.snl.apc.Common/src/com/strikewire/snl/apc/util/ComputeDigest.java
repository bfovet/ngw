/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;

public class ComputeDigest 
{
	public static final String MD5 = "MD5";
	
    /**
     * Converts an array of bytes to a hex string.
     * 
     * @param data
     *            an array of bytes to convert.
     * @return a <B>String</B> with the bytes converted to hex digits.
     */
    private static java.lang.String bytesToHexString ( byte[] data )
    {
        // [10,21,3F,..] -> "10213F..."
        if ( data == null || data.length == 0 )
            return new String( "" );
        StringBuffer sb = new StringBuffer( 2 * data.length );
        for ( int i = 0; i < data.length; i++ )
        {
            sb.append( Character.forDigit( ( ( data[i] >> 4 ) & 0x0F ), 16 ) );
            sb.append( Character.forDigit( ( data[i] & 0x0F ), 16 ) );
        }
        return sb.toString();
    } // end bytesToHexString()
    
    public static String digestOf(String str, String algorithm)
    {
        // Create our MessageDigest object with the specified algorithm.
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance( algorithm );
        }
        catch ( java.security.NoSuchAlgorithmException exc )
        {
            return "Error: cannot get MessageDigest for algorithm '"
                    + algorithm + "'";
        }

        // Now compute the digest, turn them into hex digits, and return it.
        byte[] digestBytes = md.digest(str.getBytes());
        String digestResult = bytesToHexString( digestBytes );
        return digestResult;
    }

    /**
     * Computes the MessageDigest signature of a file.
     * 
     * @param file
     *            the <B>File</B> to compute the MD5 digest on.
     * @param algorithm
     *            a <B>String</B> containing the name of the algorithm
     *            requested. See Appendix A in the Java Cryptography
     *            Architecture API Specification & Reference for information
     *            about standard algorithm names.
     * @return a <B>String</B> containing the MD5 Digest of the file or an
     *         error message which starts with "Error: ".
     */
    public static java.lang.String digestOf ( java.io.File file,
            String algorithm )
    {
        // Create our MessageDigest object with the specified algorithm.
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance( algorithm );
        }
        catch ( java.security.NoSuchAlgorithmException exc )
        {
            return "Error: cannot get MessageDigest for algorithm '"
                    + algorithm + "'";
        }

        // Open up the file input stream for reading.
        FileInputStream in;
        try
        {
            in = new FileInputStream( file );
        }
        catch ( FileNotFoundException exc )
        {
            return "Error: trouble opening file '" + file + "'";
        }

        byte[] buf = new byte[4096];
        int count = 0;
        int len;
        try
        {
            while ( true )
            {
                try
                {
                    len = in.read( buf );
                }
                catch ( IOException exc )
                {
                    return "Error: trouble reading file '" + file + "' after "
                            + count + " bytes\n" + exc.getMessage();
                }
                if ( len == -1 )
                    break; // we are at end of file
                md.update( buf, 0, len );
                count += len;
            }
        }
        finally
        {
            try
            {
                in.close();
            }
            catch ( IOException exc )
            {
                return "Error: trouble closing file '" + file + "'";
            }
        }

        // Now compute the digest, turn them into hex digits, and return it.
        byte[] digestBytes = md.digest();
        String digestResult = bytesToHexString( digestBytes );
        return digestResult;
    } // end digestOf()

    /**
     * Right pad the initial string with a padding string until final string is
     * the desired length. Multiple-character padding is used repeatedly from
     * end of initial string to the result's end, even if this means a complete
     * repetition of the pad string is not at the end.
     * 
     * @param initialString
     *            a <B>String</B> containing the original text.
     * @param paddingString
     *            a <B>String</B> containing the padding text.
     * @param requiredLength
     *            an <B>int</B> containing the desired length of the result
     *            string.
     * @return a <B>String</B> containing the inital text padded to the right
     *         with the padding text (possibly repeated).
     */
    public static String rightPad ( String initialString, String paddingString,
            int requiredLength )
    {
        if ( paddingString.length() <= 0
                || initialString.length() >= requiredLength )
            return initialString;

        // Use the padding string the requiredLength number of
        // times - if a single character paddingString, we'll
        // completely fill requiredLength number of characters;
        // if paddingString is longer, we'll more than fill it
        StringBuffer result = new StringBuffer( initialString );
        while ( requiredLength > result.length() )
        {
            result.append( paddingString );
        }
        return result.toString().substring( 0, requiredLength );
    } // end rightPad()

    /**
     * This program computes Message Digest(s) of each of file on the
     * command line. It's output is similar to the "md5sum" executable.
     * 
     * @param args
     *            the filenames to compute Message Digest(s) on
     */
    public static void main ( String[] args )
    {
        // The list of algorithms to use.
        // Note: not sure why, but "MD2" algorithm not available.
        // Note: "SHA" and "SHA-1" refer to the same algorithm.
        String[] algorithms = { "MD5", "SHA", "SHA-1", "SHA-256",
        /*
         * "SHA-384", "SHA-512",
         */
        };

        // Get the length of the longest algorithm name.
        int maxName = 0;
        for ( int j = 0; j < algorithms.length; j++ )
        {
            if ( algorithms[j].length() > maxName )
                maxName = algorithms[j].length();
        }

        for ( int i = 0; i < args.length; i++ )
        {
            File myFile = new File( args[i] );
            if ( myFile.exists() )
            {
                for ( int j = 0; j < algorithms.length; j++ )
                {
                    String result = digestOf( myFile, algorithms[j] );
                    if ( result.startsWith( "Error: " ) )
                        System.out.println( "*** " + result );
                    else
                        System.out.println(
                                ( algorithms.length > 1
                                    ? rightPad( algorithms[j], " ", maxName + 1 )
                                    : "" )
                                + result + " *" + args[i] );
                }
            }
            else
                System.out.println( "*** Error: file '" + args[i] + "' does not exist" );
            if ( algorithms.length > 1 )
                System.out.println();
        }
    } // end main()

}
