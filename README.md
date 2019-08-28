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

 1. Download and install a Java 8 JDK. We recommend https://adoptopenjdk.net
 2. Download, install, and launch Eclipse Oxygen R3a for RCP/RAP developers using the Java 8 installation
    https://www.eclipse.org/downloads/packages/release/oxygen/3a/eclipse-rcp-and-rap-developers
    To use a particular JVM for Eclipse, see https://wiki.eclipse.org/Eclipse.ini
 2. In the Project Explorer, choose Import | Git | Projects from Git
 3. Clone the URL https://gitlab.com/iwf/ngw.git. Choose the "master" branch. Choose "Import Existing Eclipse Projects." You should end 
 up with a workspace containing 20 projects.
 4. Open Window | Preferences | Plugin Development | Target Platform. Choose "DART Platform". Choose "Apply and Close".
 5. Find	"NGW.product" in the "gov.sandia.dart.workflow.app" project. Select it, right click and	choose "Run As" | "Eclipse Application".
 6. Select an appropriate workspace directory or accept the default. 

-------------

**Instructions for creating and executing workflows will be available soon.**
 

