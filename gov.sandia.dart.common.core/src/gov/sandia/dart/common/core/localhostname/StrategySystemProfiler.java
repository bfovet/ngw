/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.core.localhostname;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Resolves a hostname via an Exec.
 * </p>
 * 
 * @author kholson
 *
 */
public class StrategySystemProfiler extends AbsStrategy implements IHostnameStrategy
{
  public static final String KEY = "LOCALHOST.STRATEGY.SYSTEMPROFILER.MACOS";

  private static final String DESC = "Machine Serial Number";

  private static final Pattern PAT_SERIAL = Pattern.compile("^[\\s]*Serial[^:]+:(.*)$");




  /**
   * 
   */
  public StrategySystemProfiler()
  {
    super(KEY, DESC);
  }




  @Override
  public boolean needsInput()
  {
    return false;
  }




  @Override
  public URI resolve(AbsHostnameInput input) throws IOException
  {
    final String command = "system_profiler";
    
    try {
      CommandLine cmdLine = new CommandLine(command);
      cmdLine.addArgument("SPHardwareDataType");

      long timeout = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);
      ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
      DefaultExecutor exec = new DefaultExecutor();
      exec.setWatchdog(watchdog);

      ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      PumpStreamHandler steamHndlr = new PumpStreamHandler(stdout);
      exec.setStreamHandler(steamHndlr);

      exec.execute(cmdLine);
      
      final String result = StringUtils.trim(stdout.toString());
      if (StringUtils.isBlank(result)) {
        IOException e = new IOException("No results from " + command);
        logIt.log("Empty result from " + command, e);
        throw e;
      }

      String serialNumber = "";
      
      final String[] lines = result.split("\n");
      for (String line : lines) {
        Matcher m = PAT_SERIAL.matcher(line);
        if (m.matches() && m.groupCount() >= 1) {
          serialNumber = StringUtils.trim(m.group(1));
          break;
        }
      }
      
      if (StringUtils.isBlank(serialNumber)) {
        IOException e = new IOException("Unable to find serial number");
        logIt.log("No serial number found", e);
        throw e;
      }
      
      return makeUri(serialNumber);
    }
    catch (IllegalArgumentException iae) {
      logIt.log("Error parsing command: " + command, iae);
    }
    catch (IOException ioe) {
      logIt.log("Error executing command: " + command, ioe);
    }

    return null;
  }



}
