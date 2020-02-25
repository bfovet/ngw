/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*---------------------------------------------------------------------------*/
/*
 *  Copyright (C) 2012
 *  Sandia National Laboratories
 *
 *  File originated by:
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.common.core.formatting;

import java.math.BigDecimal;

/**
 * A class designed to help format bytes into a more human readable form.
 * Formats the filesize, in long bytes, into a human readable form based upon
 * the unit. For example:
 * 
 * <pre>
 *                                                    SI     BINARY
 *                                        0:          0 B          0 B
 *                                       27:         27 B         27 B
 *                                      999:        999 B        999 B
 *                                    1,000:       1.0 kB       1000 B
 *                                    1,023:       1.0 kB       1023 B
 *                                    1,024:       1.0 kB      1.0 KiB
 *                                    1,728:       1.7 kB      1.7 KiB
 *                                  110,592:     110.6 kB    108.0 KiB
 *                                7,077,888:       7.1 MB      6.8 MiB
 *                              452,984,832:     453.0 MB    432.0 MiB
 *                           28,991,029,248:      29.0 GB     27.0 GiB
 *                        1,855,425,871,872:       1.9 TB      1.7 TiB
 *                      276,576,287,364,243:     276.6 TB    251.5 TiB
 *                   27,657,628,736,424,395:      27.7 PB     24.6 PiB
 *                  276,576,287,364,243,956:     276.6 PB    245.6 PiB
 *                9,223,372,036,854,775,807:       9.2 EB      8.0 EiB (Long.MAX_VALUE)
 *                
 * (using the BigDecimal variant)               
 *                9,223,372,036,854,776,000:       9.2 EB      8.0 EiB
 *            9,444,732,965,739,290,000,000:       9.4 ZB      8.0 ZiB
 *          544,630,895,404,237,660,000,000:     544.6 ZB    461.3 ZiB
 *        9,671,406,556,917,033,000,000,000:       9.7 YB      8.0 YiB
 *       90,071,992,547,409,920,000,000,000:      90.1 YB     74.5 YiB
 *      557,702,036,893,939,360,000,000,000:     557.7 YB    461.3 YiB
 *    2,605,374,312,730,190,000,000,000,000:     1000+ YB    1000+ YiB
 * </pre>
 * 
 * Will return "1000+ YB" if the input size is too large; currently SI does not
 * define a standard prefix beyond "yotta" (1000^8 or 10^24)
 * 
 * @see http://en.wikipedia.org/wiki/SI_prefix
 * @author kholson
 */
public class FilesizeFormatter implements IFilesizeFormatter
{
  FilesizeFormatter()
  {
  }




  /**
   * Formats the filesize, in long bytes, into a human readable form based upon
   * the unit, with bytes always a "B".
   * 
   * <pre>
   *                                                    SI     BINARY
   *                                        0:          0 B          0 B
   *                                       27:         27 B         27 B
   *                                      999:        999 B        999 B
   *                                    1,000:       1.0 kB       1000 B
   *                                    1,023:       1.0 kB       1023 B
   *                                    1,024:       1.0 kB      1.0 KiB
   *                                    1,728:       1.7 kB      1.7 KiB
   *                                  110,592:     110.6 kB    108.0 KiB
   *                                7,077,888:       7.1 MB      6.8 MiB
   *                              452,984,832:     453.0 MB    432.0 MiB
   *                           28,991,029,248:      29.0 GB     27.0 GiB
   *                        1,855,425,871,872:       1.9 TB      1.7 TiB
   *                      276,576,287,364,243:     276.6 TB    251.5 TiB
   *                   27,657,628,736,424,395:      27.7 PB     24.6 PiB
   *                  276,576,287,364,243,956:     276.6 PB    245.6 PiB
   *                9,223,372,036,854,775,807:       9.2 EB      8.0 EiB (Long.MAX_VALUE)
   * </pre>
   * 
   * @param bytes
   *          The bytes to format
   * @param unit
   *          International System of Units (SI) or Binary "27 Bytes"; if false
   *          will return "B" Based upon the algorithm at
   *          http://stackoverflow.com
   *          /questions/3758606/how-to-convert-byte-size
   *          -into-human-readable-format-in-java
   */
  @Override
  public String bytesToHumanReadable(final long bytes, final EUnits unit)
  {
    IFilesizeFmtOpts fmtOpts = new FilesizeFmtOpts();
    return bytesToHumanReadable(BigDecimal.valueOf(bytes), unit, fmtOpts);
  }


  /**
   * Formats the filesize, in long bytes, into a human readable form based upon
   * the unit. For example:
   * 
   * <pre>
   *                                                    SI     BINARY
   *                                        0:          0 B          0 B
   *                                       27:         27 B         27 B
   *                                      999:        999 B        999 B
   *                                    1,000:       1.0 kB       1000 B
   *                                    1,023:       1.0 kB       1023 B
   *                                    1,024:       1.0 kB      1.0 KiB
   *                                    1,728:       1.7 kB      1.7 KiB
   *                                  110,592:     110.6 kB    108.0 KiB
   *                                7,077,888:       7.1 MB      6.8 MiB
   *                              452,984,832:     453.0 MB    432.0 MiB
   *                           28,991,029,248:      29.0 GB     27.0 GiB
   *                        1,855,425,871,872:       1.9 TB      1.7 TiB
   *                      276,576,287,364,243:     276.6 TB    251.5 TiB
   *                   27,657,628,736,424,395:      27.7 PB     24.6 PiB
   *                  276,576,287,364,243,956:     276.6 PB    245.6 PiB
   *                9,223,372,036,854,775,807:       9.2 EB      8.0 EiB (Long.MAX_VALUE)
   * </pre>
   * 
   * @param bytes
   *          The bytes to format
   * @param unit
   *          International System of Units (SI) or Binary
   * @param bytesAsWord
   *          If true, then will return "0 Bytes" or "27 Bytes"; if false will
   *          return "B" Based upon the algorithm at
   *          http://stackoverflow.com/questions
   *          /3758606/how-to-convert-byte-size
   *          -into-human-readable-format-in-java
   */
  @Override
  public String bytesToHumanReadable(final long bytes,
                                     final EUnits unit,
                                     final IFilesizeFmtOpts fmtOpts)
  {
    return bytesToHumanReadable(BigDecimal.valueOf(bytes), unit, fmtOpts);
  }




  /**
   * Converts the specified bytes into a human readable form based upon the
   * units (which will be 1000 or 1024 as the basis). In the event the size >
   * 999 yotta bytes (seriously, it doesn't exist), will return "1000+ YB" or
   * "1000+ YiB"
   * 
   * @param bytes
   *          The bytes to format
   * @param unit
   *          International System of Units (SI) or Binary
   * @param bytesAsWord
   *          If true, then will return "0 Bytes" or "27 Bytes"; if false will
   *          return "B"
   */
  @Override
  public String bytesToHumanReadable(final BigDecimal bytes,
                                     final EUnits unit,
                                     final IFilesizeFmtOpts fmtOpts)
  {
    BigDecimal base = BigDecimal.valueOf(unit.getBase());

    // if we are smaller than our base, we're just bytes
    if (bytes.compareTo(base) < 0) { // bytes < base) {
      String s = bytes.intValue() + " B";
      if (fmtOpts.isByteAWord()) {
        if (bytes.compareTo(BigDecimal.valueOf(1)) == 0) {
          s += "yte";
        }
        else {
          s += "ytes";
        }
      }

      return s;
    }

    // get an exponent which is the base^exp; this exp provides
    // an offset into the prefix string
    int exp =
        (int) (Math.log(bytes.doubleValue()) / Math.log(base.doubleValue()));

    // the prefix is the k/M/G, plus the potential "i" in the binary
    String prefix;
    String retValue;

    try {
      prefix =
          unit.getPrefixes().charAt(exp - 1)
              + (unit == EUnits.BINARY ? "i" : "");
      
      double divisor = Math.pow(base.doubleValue(), exp);
      double val = bytes.doubleValue() / divisor;
      
      retValue = String.format("%5.1f %sB", val, prefix);
    }
    catch (StringIndexOutOfBoundsException e) {
      int len = unit.getPrefixes().length();
      prefix =
          unit.getPrefixes().charAt(len - 1)
              + (unit == EUnits.BINARY ? "i" : "");
      retValue = "1000+ " + prefix + "B";
    }


    if (fmtOpts.isReturnTrimmed()) {
      retValue = retValue.trim();
    }
    
    return retValue;
  }




  /**
   * Returns a number such as 12345 formatted to 12,345 using the Locale for the
   * thousands separator
   */
  @Override
  public String numberWithSeparators(final long num)
  {
    return String.format("%,d", num);
  }





  public static void main(String[] args)
  {
    long[] alTests = {
        // bytes
        0L, 1L, 27L, 999L, 1000L, 1023L,
        // kb
        1024L, 1728L, 110592L,
        // mb
        7077888L, 452984832L,
        // gb
        28991029248L,
        28999769248L,
        29000769842L,
        29200769842L,
        29991029248L,
        // tb
        1855425871872L, 276576287364243L,
        // pb
        27657628736424395L, 276576287364243956L,

        // eb

        Long.MAX_VALUE };


    FilesizeFormatter fsf = new FilesizeFormatter();
    IFilesizeFmtOpts fmtOpts = new FilesizeFmtOpts();

    for (long l : alTests) {
      String si = fsf.bytesToHumanReadable(l, EUnits.SI);
      String bin = fsf.bytesToHumanReadable(l, EUnits.BINARY, fmtOpts);
      System.out.println(String.format("%,40d: %12s %12s", l, si, bin));
    }


    for (int i = 1; i < 8; ++i) {
      BigDecimal bd = new BigDecimal((double) Long.MAX_VALUE * Math.pow(i, 10));
      String si = fsf.bytesToHumanReadable(bd, EUnits.SI, fmtOpts);
      String bin = fsf.bytesToHumanReadable(bd, EUnits.BINARY, fmtOpts);
      System.out.println(String.format("%,40.0f: %12s %12s",
          bd.doubleValue(),
          si,
          bin));
    }
    
    double[] aD = { 27.4D, 1011.4756D, 24847847577.4D };
    
    for (double d : aD) {
      BigDecimal bd = new BigDecimal(d);
      String si = fsf.bytesToHumanReadable(bd, EUnits.SI, fmtOpts);
      String bin = fsf.bytesToHumanReadable(bd, EUnits.BINARY, fmtOpts);
      System.out.println(String.format("%,40.0f: %12s %12s",
          bd.doubleValue(),
          si,
          bin));
    }

  }
}
