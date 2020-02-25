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



/**
 * A factory for generating a variety for formatters used in the 
 * DART Workbench.
 */
public class FormatterFactory
{
  private FormatterFactory()
  {
  }
  
  /**
   * A formatter for file size, converting bytes to a human
   * readable form.
   */
  public static IFilesizeFormatter filesizeFormatter()
  {
    return new FilesizeFormatter();
  }
  
  
//  /**
//   * A formatter for dates with reference to the
//   * formats stored in the preference store.
//   */
//  public static IAPCDateFormatter dateFormatter()
//  {
//    return new APCDateFormatter();
//  }
//  
//  
//  /**
//   * Returns the Eclipse 4 Style engine that may be used to apply
//   * styles to widges. May return null if cannot obtain the service
//   * @return The style engine, or null if cannot be obtained
//   * @see http://www.vogella.com/tutorials/Eclipse4CSS/article.html
//   * @see https://wiki.eclipse.org/Eclipse4/RCP/CSS#Differences_from_HTML_and_SVG_CSS
//   */
//  public static IStylingEngine styleEngine()
//  {
//    IStylingEngine styleEngine = null;
//    
//    IEclipseContext ctx = EventUtils.getContext();
//    
//    if (ctx != null) {
//      styleEngine = ctx.get(IStylingEngine.class);
//    }
//
//    return styleEngine;
//  }
  
} //class FormatterFactory
