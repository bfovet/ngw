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

import gov.sandia.dart.common.core.env.OS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

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
public class StrategyExec extends AbsStrategy implements IHostnameStrategy
{
  public static final String KEY = "LOCALHOST.STRATEGY.EXEC";

  private static final String DESC = "By command";




  /**
   * 
   */
  public StrategyExec()
  {
    super(KEY, DESC);
  }




  @Override
  public boolean needsInput()
  {
    return true;
  }




  @Override
  public URI resolve(AbsHostnameInput input) throws IOException
  {
    final String command = getCmd(input);

    try {
      CommandLine cmdLine = CommandLine.parse(command);
      long timeout = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);
      ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
      DefaultExecutor exec = new DefaultExecutor();
      exec.setWatchdog(watchdog);

      ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      PumpStreamHandler steamHndlr = new PumpStreamHandler(stdout);
      exec.setStreamHandler(steamHndlr);

      exec.execute(cmdLine);

      String host = StringUtils.trim(stdout.toString());
      return makeUri(host);
    }
    catch (IllegalArgumentException iae) {
      logIt.log("Error parsing command: " + command, iae);
    }
    catch (IOException ioe) {
      logIt.log("Error executing command: " + command, ioe);
    }

    return null;
  }




  private String getCmd(AbsHostnameInput input) throws IOException
  {
    String command = input.getParameter().orElse("");

    if (StringUtils.isBlank(command)) {
      logIt.log("Null/empty command specified; "
          + "using default /bin/hostname", null);

      switch (OS.getOS()) {
        case Windows:
          command = "hostname";
          break;

        default:
          command = "/bin/hostname -s";
          break;
      }
    }

    return command;
  }
}
