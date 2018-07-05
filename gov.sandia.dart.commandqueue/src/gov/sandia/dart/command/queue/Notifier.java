/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.command.queue;

import java.io.PrintWriter;

/**
 * Notification sink
 * <P>
 * (C) 2000 Sandia National Laboratories
 *
 * @version $Id: Notifier.java,v 1.20 2008/11/03 20:37:58 ejfried Exp $
 */


public interface Notifier {

    public static final int HIGH = 0;
    public static final int NORMAL = 1;
    public static final int LOW = 2;

    public PrintWriter getPrintWriter();

    public ProgressReporter getProgressReporter(Runnable cancellationCallback, String title);

    public void reportError(String msg);

    public void reportError(Throwable e);

    public void reportWarning(String msg);

    public void reportStatus(int priority, String msg);

    public void reportInfo(String title, String msg);

    public void setBusyIndicator(boolean on);

    public void clearStatusDisplay();

    public boolean askYesNo(String msg);

    public void flush();
    
    public boolean canShowVt100codes();
    
    
}

