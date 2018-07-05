/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.core.formatting;

import java.math.BigDecimal;


/**
 * Defines methods for formatting a size in bytes into a human readable form,
 * generally along the lines of:
 * 
 * <pre>
 * 
 *                                        0:          0 B
 *                                       27:         27 B
 *                                      999:        999 B
 *                                    1,000:       1.0 kB
 *                                    1,024:       1.0 kB
 *                                    1,728:       1.7 kB      1.7 KiB
 *                                  110,592:     110.6 kB    108.0 KiB
 *                                7,077,888:       7.1 MB      6.8 MiB
 *                              452,984,832:     453.0 MB    432.0 MiB
 *                           28,991,029,248:      29.0 GB     27.0 GiB
 *                        1,855,425,871,872:       1.9 TB      1.7 TiB
 * </pre>
 * 
 */
public interface IFilesizeFormatter
{

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
   *          will return "B"
   */
  public String bytesToHumanReadable(final long bytes, final EUnits unit);




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
   * <p>Based upon the algorithm at
   *          http://stackoverflow.com/questions
   *          /3758606/how-to-convert-byte-size
   *          -into-human-readable-format-in-java
   * </p> 
   * @param bytes
   *          The bytes to format
   * @param unit
   *          International System of Units (SI) or Binary
   * @param fmtOpts Formatting options, including whether to trim the
   * result and whether to display bytes as "Bytes" or "B".
   *          If true, then will return "0 Bytes" or "27 Bytes"; if false will
   *          return "B".
   */
  public String bytesToHumanReadable(final long bytes,
                                     final EUnits unit,
                                     IFilesizeFmtOpts fmtOpts);


  



  /**
   * <p>Converts the specified bytes into a human readable form based upon the
   * units (which will be 1000 or 1024 as the basis). In the event the size >
   * 999 yotta bytes will return "1000+ YB" or
   * "1000+ YiB".</p>
   * 
   * <p>In the unstandardized world, there is also Brontobyte and
   * Geopbyte, but they are not officially sanctioned.
   *  Just so you know,
   * It would take approximately 11 trillion years to download a Yottabyte 
   * file from the Internet using high-power broadband. In short, there
   * is nothing larger than a yottabyte at this time.</p> 
   * 
   * @param bytes
   *          The bytes to format
   * @param unit
   *          International System of Units (SI) or Binary
   *          If true, then will return "0 Bytes" or "27 Bytes"; if false will
   *          return "B"
   * @see http://www.whatsabtye.com          
   */
  public String bytesToHumanReadable(final BigDecimal bytes,
                                     final EUnits unit,
                                     IFilesizeFmtOpts fmtOpts);


  /**
   * Returns a number 
   * (such as 12345) formatted with the thousands separator
   * (e.g., to 12,345) with the separator appropriate for the
   * current Locale.
   */
  public String numberWithSeparators(final long num);  
  
  
  
  
  /**
   * <p>
   * Units for the basis of size formatting.
   * </p>
   * <ul>
   * <li>SI : International System of Units (base 10)</li>
   * <li>Binary : base 2</li>
   * </ul>
   * 
   * @author kholson
   * 
   */
  public enum EUnits
  {
    /**
     * SI - International System of Units (SI), based upon powers of 10 (e.g.,
     * 1000 = 1.0 kB)
     */
    SI(1000, "kMGTPEZY"),

    /**
     * BINARY - Binary, based upon powers of 2 (e.g., 1024 = 1.0 KiB)
     */
    BINARY(1024, "KMGTPEZY"),

    ;

    final int _base;
    final String _prefix;




    private EUnits(int base, String prefix)
    {
      _base = base;
      _prefix = prefix;
    }




    /**
     * Returns the base in which this operates
     */
    public int getBase()
    {
      return _base;
    }




    /**
     * Returns the String with the various prefixes
     */
    public String getPrefixes()
    {
      return _prefix;
    }
  } // enum EUnits
} // interface IFilesizeFormatter
