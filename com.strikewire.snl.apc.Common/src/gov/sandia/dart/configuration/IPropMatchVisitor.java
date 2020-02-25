/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/

package gov.sandia.dart.configuration;

/**
 * <p>An interface for a Vistor that is called while processing
 * a properties file that has the format of:</p>
 * <ul>
 * <li>Identifier (e.g., tool name)</li>
 * <li>LAN (the LAN, e.g., srn/scn)</li>
 * <li>ENV (the environment, e.g., dev/qual/prod)</li>
 * <li>SITE (the site, e.g., nm/ca)</li>
 * <li>OS (the operating system, matching gov.sandia.dart.env.os)</li>
 * </ul>
 * <p/>
 * <p>Furthermore, the entries may have an "*" in them, allowing a match
 * to 
 * @author kholson
 *
 */
public interface IPropMatchVisitor
{
  /**
   * <p>Return true if the specified entry should be accept.</p>
   * <p>NOTE: may use PropertyFileEntryProcessors.defaultMatcher to
   * assist with the matching.</p>
   */
  public boolean accept(IExecutionEnvironment execEnv, IPropMatch propEntry);
}
