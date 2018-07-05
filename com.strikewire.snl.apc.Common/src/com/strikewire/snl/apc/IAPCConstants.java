/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*****************************************************************************/


/*****************************************************************************/
/*
 *
 *  $Author: snmuell $
 *  $Date: 2008/04/18 17:54:57 $
 *  
 *  $Name:  $ 
 *
 * FILE: 
 *  $Source: /cvs_root/snl/current/apc/plugins/com.strikewire.snl.apc.Common/src/com/strikewire/snl/apc/IAPCConstants.java,v $
 *
 *
 * Description ($Revision: 1.27 $):
 *
 */
/*****************************************************************************/

package com.strikewire.snl.apc;

/**
 * @author kholson
 *
 */
public interface IAPCConstants {
  public static final String APCCONTEXT_INIT = "APC_Context_init";
  
  public static final String DLG_USERNAME = "username";
  public static final String DLG_PASSWORD = "password";
  public static final String DLG_SERVER = "server";
  public static final String DLG_SERVER_NAME = "server.name";
  public static final String DLG_HOST = "host";
  public static final String DLG_SERVER_ENV = "server.env";

  
  public static final String DLG_FOLDERTYPE = "folderType";
  public static final String DLG_FOLDERNAME = "folderName";
  public static final String DLG_FOLDERDESC = "folderDesc";
  
  public static final String DLG_PROJECTID   = "projectId";
  public static final String DLG_PROJECTTYPE = "projectType";
  public static final String DLG_PROJECTNAME = "projectName";
  public static final String DLG_PROJECTDESC = "projectDesc";
  public static final String DLG_PROJECTPATH = "projectPath";
  
  public static final String DLG_ARTTYPE = "artifactType";
  public static final String DLG_ARTNAME = "artifactName";
  public static final String DLG_ARTTITLE = "artifactTitle";
  public static final String DLG_AUTONAME = "artifactAutoname";
  public static final String DLG_ARTDESC = "artifactDescription";
  public static final String DLG_ARTCLASS = "artifactClassification";
  
  public static final String DLG_NODES = "vizNumNodes";
  public static final String DLG_MINUTES = "vizNumMinutes";
  public static final String DLG_PROJECT = "vizProject";
//  public static final String DLG_TASK = "vizTask";
  
  public static final String DLG_FILES = "files";
  
  public static final String DLG_ENGNOTE_SEARCH = "engNoteSearch";
  
  public static final String DLG_REMOTE_DIR = "remoteDirPath";
  
  public static final String PROJ_SCOPE = "ProjectScope";
  
  public static final String MCP_MODEL_ADDED = "dataModelAdded";
  
  public static final String SCRIPT = "script";
  public static final String HOST = "host";
  public static final String PATH = "path";
  public static final String NODES = "nodes";
  public static final String TIME = "minutes";
  public static final String PROJECT = "project";
  public static final String TASK = "task";
  
  public static final String WORKING_FILE = "workingFile";
  public static final String WORKING_DIR = "workingDir";
  public static final String TOOL_PATH = "toolPath";
  
  public static final String PARAM_DISPLAY = "-d";
  public static final String PARAM_FILE = "-f";  
  public static final String PARAM_DB = "-db";  
  public static final String PARAM_HOST = "-h";
  public static final String PARAM_METADATA = "-m";
  public static final String PARAM_NODES = "-n";
  public static final String PARAM_PROJECT = "-p";  
  public static final String PARAM_TIME = "-t";
  public static final String PARAM_WORKINGDIR = "-w";
  public static final String PARAM_SCRIPT = "-s";
  public static final String PARAM_COMPAREVIEW = "-c";
  public static final String PARAM_OUTPUTFILE = "-o";

  

  public static final String PROJECTS_AVAILABLE = 
    CommonMessages.getString("APCConstants.projectsAvailable");
}
