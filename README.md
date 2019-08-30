Welcome
-------

This is the *Next Generation Workflow* project. The system includes an
Eclipse-based workflow editor and a portable Java runtime system. The
runtime is designed to be implementable on a variety of Java workflow
engines. The initial implementation is based on Google Sarasvati.

These plugins are intended to be used with Eclipse Oxygen R3a. Use the
Target-Platform to generate your target platform for PDE development. 

Running	the NGW	Editor Application from Source
-----------

 1. Download and install a Java 8 JDK. We recommend https://adoptopenjdk.net .
 2. Download, install, and launch Eclipse Oxygen R3a for RCP/RAP developers from
    https://www.eclipse.org/downloads/packages/release/oxygen/3a/eclipse-rcp-and-rap-developers using
    the Java 8 JDK you downloaded. To use a particular JVM for Eclipse, see https://wiki.eclipse.org/Eclipse.ini
 2. In the Project Explorer, choose Import | Git | Projects from Git .
 3. Clone the URL https://gitlab.com/iwf/ngw.git . Choose the "master" branch. Choose "Import Existing Eclipse Projects." You should end
    up with a workspace containing 20 projects.
 4. Open Window | Preferences | Plugin Development | Target Platform. Choose "DART Platform". Press "Apply and Close".
 5. Find "NGW.product" in the "gov.sandia.dart.workflow.app" project. Select it, right click and choose "Run As" | "Eclipse Application".
 6. Select an appropriate workspace directory or accept the default. 
 7. Continue with the tutorials in the file "IntroNGW.pdf" .

Building the Standalone Workflow Engine
--------------

 1. Right-click the file "ant-build.xml" in the project "gov.sandia.dart.workflow.phase3" and choose "Run As | Ant Build"
 2. The standalone workflow engine is installed in a folder "wflib" in your (OS-dependent) home directory.
 3. To test the install, open a terminal window, navigate to this folder, and type "run.sh hello.iwf" (on Mac or Linux) or "run.bat hello.iwf" (on Windows.)
 4. You can rename the "wflib" folder or move it to any other computer. 

