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
 *  Copyright (C) 2013
 *  Sandia National Laboratories
 *
 *  File originated by:
 *    kholson on Dec 4, 2013
 *
 *
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.common.core.env;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * A static, singleton class that provides information about the operating
 * system on which the application is running.
 * 
 * @author kholson
 * 
 */
public class OS
{
  private static final Set<EOperatingSystem> _setUnix =
      new HashSet<EOperatingSystem>();

  private static String _operatingSystem;

  private static EOperatingSystem _eOS = EOperatingSystem.Unknown;

  private static EProcessorArchitecture _eArch = EProcessorArchitecture.Unknown;

  private static EBits _eProcBits = EBits.Unknown;
  
  private static EBits _eJVMBits = EBits.Unknown;

  @SuppressWarnings("unused")
  private static final OS _this = new OS();




  /**
   * Sets the underlying operating system based upon the
   * doPriviledge(GetPropertyAction("os.name") call, and also sets the enum to
   * the match.
   */
  private OS()
  {
    _setUnix.add(EOperatingSystem.AIX);
    _setUnix.add(EOperatingSystem.DigitalUnix);
    _setUnix.add(EOperatingSystem.FreeBSD);
    _setUnix.add(EOperatingSystem.HPUX);
    _setUnix.add(EOperatingSystem.Linux);
    _setUnix.add(EOperatingSystem.Solaris);

    _operatingSystem = getOSFromEnv();
    _eOS = getOS(_operatingSystem);

    _eArch = getArch(getArchFromEnv());
    
    _eProcBits = getBitsFromArch(_eArch);
    
    _eJVMBits = getJVMArch(getJVMArchFromEnv());
  }


  
  private static String getEnvProperty(final String key)
  {
    return System.getProperty(key);
  }
  


  /**
   * Using the osgi.arch setting, ascertain the processor architecture
   */
  private static String getArchFromEnv()
  {
    String arch = getEnvProperty("osgi.arch");

    return arch;
  }




  private static String getOSFromEnv()
  {
    String os = getEnvProperty("os.name");

    return os;
  }

  /**
   * 
   */
  private static String getJVMArchFromEnv()
  {
    String jvm_arch = getEnvProperty("os.arch");
    
    return jvm_arch;
  }



  private static EBits getJVMArch(final String jvmArch)
  {
    EBits eBits = EBits.Unknown;
    
    String lc_arch = (jvmArch != null ? jvmArch.toLowerCase() : "");
    
    if (StringUtils.isBlank(lc_arch)) {
      eBits = EBits.Unknown;
    }
    else if ("x86".equals(lc_arch)) {
      eBits = EBits.bit32;
    }
    else if ("x86_64".equals(lc_arch)) {
      eBits = EBits.bit64;
    }
    else if ("amd64".equals(lc_arch)) {
      eBits = EBits.bit64;
    }
    
    
    return eBits;
  }

  
  


  private static EProcessorArchitecture getArch(final String arch)
  {
    EProcessorArchitecture eArch = EProcessorArchitecture.Unknown;
    String lc_arch = (arch != null ? arch.toLowerCase() : "");

    if (StringUtils.isBlank(lc_arch)) {
      eArch = EProcessorArchitecture.Unknown;
    }
    else if ("x86".equals(lc_arch)) {
      eArch = EProcessorArchitecture.x86;
    }
    else if ("x86_64".equals(lc_arch)) {
      eArch = EProcessorArchitecture.x86_64;
    }
    else if ("sparc".equals(lc_arch)) {
      eArch = EProcessorArchitecture.sparc;
    }

    return eArch;
  }




  private EBits getBitsFromArch(final EProcessorArchitecture eArch)
  {
    EBits eBits = EBits.Unknown;

    switch (eArch) {
      case x86:
        eBits = EBits.bit32;
        break;

      case x86_64:
      case sparc:
        eBits = EBits.bit64;
        break;
        
        //
        // other cases; we leave at the default of unknown
        //
      case Other:
      case Unknown:
      case ia64:
        break;
    }

    return eBits;
  }





  private static EOperatingSystem getOS(final String os)
  {
    EOperatingSystem eOS;
    String lc_os = (os != null ? os.toLowerCase() : "");

    if (StringUtils.isBlank(os)) {
      eOS = EOperatingSystem.Unknown;
    }
    else if (lc_os.startsWith("linux")) {
      eOS = EOperatingSystem.Linux;
    }
    else if (lc_os.startsWith("mac os")) {
      eOS = EOperatingSystem.Mac;
    }
    else if (lc_os.startsWith("windows")) {
      eOS = EOperatingSystem.Windows;
    }
    else if (lc_os.startsWith("solaris") || lc_os.startsWith("sunos")) {
      eOS = EOperatingSystem.Solaris;
    }
    else if (lc_os.startsWith("freebsd")) {
      eOS = EOperatingSystem.FreeBSD;
    }
    else if (lc_os.startsWith("hp-ux")) {
      eOS = EOperatingSystem.HPUX;
    }
    else if (lc_os.startsWith("aix")) {
      eOS = EOperatingSystem.AIX;
    }
    else {
      eOS = EOperatingSystem.Other;
    }

    return eOS;
  }




  /**
   * Obtain the Operating System as determined by the os.name variable
   */
  public static EOperatingSystem getOS()
  {
    return _eOS;
  }




  /**
   * Obtain the processor architecture as determined by the osgi.arch variable
   */
  public static EProcessorArchitecture getProcessorArch()
  {
    return _eArch;
  }




  /**
   * Obtain the processor bit (32/64/etc). as derived from the osgi.arch
   * variable
   */
  public static EBits getProcessorBits()
  {
    return _eProcBits;
  }

  
  /**
   * Obtain the JVM bit size (32/64/etc) as derived from the os.arch
   * variable
   * 
   */
  public static EBits getJVMBits()
  {
    return _eJVMBits;
  }



  /**
   * Based upon the osgi.arch, is the processor 64 bit; note that this approach
   * does not tell us what JVM arch is being run
   */
  public static boolean isProcessor64bit()
  {
    return (_eProcBits == EBits.bit64);
  }




  /**
   * Based upon the osgi.arch, is the processor 32 bit; note that this approach
   * does not tell us what JVM arch is being run
   */
  public static boolean isProcessor32bit()
  {
    return (_eProcBits == EBits.bit32);
  }

  
  /**
   * Obtain whether the JVM is 64 bit based upon the os.arch
   */
  public static boolean isJVM64bit()
  {
    return (_eJVMBits == EBits.bit64);
  }
  
  /**
   * Obtain whether the JVM is 32 bit based upon the os.arch
   */
  public static boolean isJVM32bit()
  {
    return (_eJVMBits == EBits.bit32);
  }



  /**
   * @return true if the operating system is some variant of Windows
   */
  public static boolean isWindows()
  {
    return (_eOS == EOperatingSystem.Windows);
  }




  /**
   * @return true if the operating system is some variant of Linux
   */
  public static boolean isLinux()
  {
    return (_eOS == EOperatingSystem.Linux);
  }




  /**
   * @return true if the operating system is some variant of a Mac
   */
  public static boolean isMac()
  {
    return (_eOS == EOperatingSystem.Mac);
  }




  /**
   * @return true if the operating system is Solaris
   */
  public static boolean isSolaris()
  {
    return (_eOS == EOperatingSystem.Solaris);
  }




  /**
   * Returns true if the OS is some variant of *nix, including Solaris and Linux
   */
  public static boolean isUnix()
  {
    return (_setUnix.contains(_eOS));
  }




  /**
   * On the exceedingly rare chance one needs to know if the variant of Windows
   * happens to be Windows 95/98/ME, this method will return true
   */
  public static boolean is9xWindows()
  {
    boolean bRet;

    bRet =
        ("Windows 95".equals(_operatingSystem)
            || "Windows 98".equals(_operatingSystem) || "Windows ME".equals(_operatingSystem));

    return bRet;
  }





  /**
   * Simple reference for operating system; so much for write once myth of Java
   * 
   * @author kholson
   * 
   */
  public enum EOperatingSystem implements Predicate<EOperatingSystem>
  {
    /**
     * Unknown - The Operating system is unknown; can happen potentially if the
     * ability to retrieve the name from the operating system fails
     */
    Unknown,

    /**
     * Linux - A Linux operating system
     */
    Linux,

    /**
     * Solaris - A Solaris operating system
     */
    Solaris,

    /**
     * Mac - The Macintosh O/S
     */
    Mac,

    /**
     * Windows - A windows operating system
     */
    Windows,

    /**
     * HPUX - HP-UX
     */
    HPUX,

    /**
     * FreeBSD - FreeBSD
     */
    FreeBSD,

    DigitalUnix,

    AIX,

    /**
     * Other - Some other operating system
     */
    Other,
    
    /**
     * Any - Any operating system;  
     */
    Any,
    

    ;
    
    
    /**
     * Returns whether the specified os matches the current os; differs
     * from a straight comparison in that if the specified os or "this"
     * os is any, then matches is true. 
     */
    @Override
    public boolean test(final EOperatingSystem os)
    {
      boolean bRet = false;
      if (os != null) {
        if (os == EOperatingSystem.Any ||
            (this == EOperatingSystem.Any)) {
          bRet = true;
        }
        else {
          bRet = (this == os);
        }
      }
      
      return bRet;
    } //matches
    
    /**
     * Returns the operating system matching the specified input
     * after it has been convered to Title case. If the specified
     * input is an "*" (wildcard), the return will be
     * EOperatingSystem.Any. If the input is null/empty, 
     * EOperatingSystem.Unknown will be returned. If the specified
     * input cannot otherwise be matched, an IllegalArgumentException
     * will be thrown. The primary difference between an direct attempt
     * for .valueOf and this method is that this method will return
     * Unknown on a null/empty input, and will process the wildcard.
     * @param input The string to convert to an eOperatingSystem
     * @return The found Operating System, subject to "*" being Any,
     * and a null/empty input being "Unknown"
     * @throws IllegalArgumentException If not null/empty and not "*",
     * and the input cannot be converted via .valueOf 
     */
    public static EOperatingSystem toOS(final String input)
      throws IllegalArgumentException
    {
      EOperatingSystem retOS = EOperatingSystem.Unknown;
      
      if (StringUtils.isNotBlank(input)) {
        if ("*".equals(input)) {
          retOS = EOperatingSystem.Any;
        }
        else {
          final String capInput = WordUtils.capitalize(input);
          retOS = EOperatingSystem.valueOf(capInput);
        }
      }
      
      return retOS;
    }
  };


  /**
   * The architecture of the processor, generally 32/64 bit, but allows for
   * sparc and potentially others.
   */
  public enum EProcessorArchitecture
  {
    Unknown,

    x86,

    x86_64,

    ia64,

    sparc,

    Other,

    ;
  };

  /**
   * An attempt to indicate if the operating system is 32/64/??? bits
   */
  public enum EBits
  {
    Unknown,

    bit32,

    bit64,

    bit128,

    ;
  }

  
  public static void main(String[] args)
  {
    EOperatingSystem eos = EOperatingSystem.Any;
    EOperatingSystem any = EOperatingSystem.Any;
    EOperatingSystem win = EOperatingSystem.Windows;
    EOperatingSystem linux = EOperatingSystem.Linux;
    
    System.out.println("IsTrue: " + eos.test(linux));
    System.out.println("IsTrue: " + any.test(win));
    System.out.println("IsTrue: " + linux.test(any));
    System.out.println("IsTrue: " + win.test(any));
    System.out.println("IsFalse: " + win.test(linux));
    
    
    eos = EOperatingSystem.toOS("*");
    System.out.println("IsTrue: " + (eos == EOperatingSystem.Any));
    
    eos = EOperatingSystem.toOS("linux");
    System.out.println("IsTrue: " + (eos == EOperatingSystem.Linux));
    
    try {
      eos = EOperatingSystem.toOS("junk");
      System.err.println("Should not be converted");
    }
    catch (IllegalArgumentException e) {
      System.out.println("Caught exception on junk");
    }
  }
}
