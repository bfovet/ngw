/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government
 * retains certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License v1.0.
 * For more information see the files copyright.txt and license.txt
 * included with the software. Information also available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Jun 18, 2018
 ******************************************************************************/
package gov.sandia.dart.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

/**
 * @author kholson
 *
 */
public class BundleFileLister
{
  /**
   * _log -- A Logger instance for BundleFileLister
   */
  private static final Logger _log =
      LogManager.getLogger(BundleFileLister.class);

  final Bundle bndl;




  /**
   * @param bndl
   *          A Bundle that has a directory to be found; generally such
   *          bundles/plugins have the <code>Eclipse-BundleShape: dir</code>
   *          setting to ensure the exploded distribution.
   */
  public BundleFileLister(Bundle bndl)
  {
    Objects.requireNonNull(bndl, "Null bnld specified");


    this.bndl = bndl;
  }




  /**
   * For the bundle of this lister class, returns the URL to the directory.
   * 
   * @param directoryName
   *          The directory name in the bundle to find (e.g., "resources",
   *          "machines", etc.).
   */
  public URL getDirectory(final String directoryName) throws IOException
  {
    if (StringUtils.isBlank(directoryName)) {
      throw new IOException("Null/empty directory name");
    }


    IPath path = new Path(directoryName);

    URL machineBndlDir = FileLocator.find(bndl, path, null);

    if (machineBndlDir == null) {
      String msg = String.format("Unable to find directory {} in bundle {}",
          directoryName,
          bndl.getSymbolicName());
      _log.error(msg);
      throw new IOException(msg);
    }

    URL machineUrlDir = FileLocator.toFileURL(machineBndlDir);

    _log.debug("Bundle Directory: {}", machineUrlDir.toString());
    return machineUrlDir;
  }




  /**
   * Returns all of the files in the specified directory that should exist
   * within the Bundle
   * 
   * @param directoryName
   *          The directory name in the bundle to find (e.g., "resources",
   *          "machines", etc.).
   */
  public List<URL> getFiles(String directoryName) throws IOException
  {
    return getFiles(getDirectory(directoryName), new AllFilesFilter());
  }




  /**
   * Returns all of the files for the specified URL that represents a directory
   * in the bundle (usually from <code>getDirectory(...)</code>)
   * 
   * @param baseDirInBundle
   *          The non-null URL to a directory in the bundle
   * @see BundleFileLister#getDirectory(String)
   */
  public List<URL> getFiles(URL baseDirInBundle) throws IOException
  {
    return getFiles(baseDirInBundle, new AllFilesFilter());
  }




  /**
   * Returns files matching a specific extension (e.g., <i>.xml</i>) in the
   * specified directory (that is in the bundle).
   * 
   * @param directoryName
   *          The directory name in the bundle to find (e.g., "resources",
   *          "machines", etc.).
   * @param extension
   *          A non null/empty String, usually starting with a "." character
   *          that is used to check the end of a given filename for inclusion in
   *          the return.
   */
  public List<URL> getFiles(String directoryName, String extension)
    throws IOException
  {
    return getFiles(getDirectory(directoryName),
        new FileExtensionFilter(extension));
  }




  /**
   * Returns the files at the URL within the bundle that match the specified
   * filter.
   * 
   * @param baseDirInBundle
   *          The non-null URL to a directory in the bundle
   * @param inFilter
   *          A filter to apply to files found in the directory; if null then
   *          will will use an "all files" filter.
   */
  public List<URL> getFiles(URL baseDirInBundle, FileFilter inFilter)
    throws IOException
  {
    Objects.requireNonNull(baseDirInBundle, "Null directory");

    FileFilter filter = inFilter;
    if (filter == null) {
      filter = new AllFilesFilter();
    }

    List<URL> files = new ArrayList<>();

    //
    // read the directory
    //
    File filesDir;
    try {
      // If the original URL path has '%20' instead of spaces, this will
      // convert them to spaces.
      filesDir = new File(baseDirInBundle.toURI());
    }
    catch (URISyntaxException e) {
      // If the original URL path has spaces instead of '%20' the conversion
      // will fail...
      // However that means we can use the URL path directly!
      filesDir = new File(baseDirInBundle.getPath());
    }

    File[] foundFiles = filesDir.listFiles(filter);

    if (foundFiles != null) {
      for (File file : foundFiles) {
        files.add(file.toURI().toURL());
      }
    }

    return files;
  }

  
  
  
  
  
  /*
   * =========================================================================
   * Private classes
   * =========================================================================
   */

  /**
   * A class that filters based upon with an "endsWith" applied against
   * the file found in a given directory; only allows files and those
   * ending with the specified extension.
   */
  private static class FileExtensionFilter implements FileFilter
  {
    final String ext;




    public FileExtensionFilter(String ext)
    {
      this.ext = ext;
    }




    /**
     * @see java.io.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File pathname)
    {
      return (pathname.isFile() && pathname.getName().endsWith(ext));
    }
  }

  
  

  /**
   * A "filter" that accepts all files
   */
  private static class AllFilesFilter implements FileFilter
  {
    /**
     * @see java.io.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File pathname)
    {
      return pathname.isFile();
    }
  }
}
