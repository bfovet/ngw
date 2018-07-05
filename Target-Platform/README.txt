This project holds all the features contributed to to the DART Workbench 
product not included in the base eclipse install.

Add any contributions by adding a zip file to the Contribs folder.  The zip
file should expand to the default eclipse folder structure:
 -eclipse
  -features
   -blah
  -plugins
   -blah
where blah is your plugins and features being contributed.

Please keep the Contribs/sources.txt file updated with any changes/additions
to the zips in the Contribs directory.  This will make it easier to 
update them later.

The project is configured with an auto-builder which runs the build.xml file
which will extract all the zips in the Contribs directory into a 
Target-Platform directory.

The target-platform-definition.target file provides a predefined target
definition which can be selected under
Preferences -> Plug-in Development -> Target Platform.

The build_Target-Platform.launch file provides a predefined launch
configuration for manually running the build to generate
the local Target-Platform.

Cruise control has been configured to use this project for its target
platform for the applications it builds.

After receiving any updates in this project, it is most likely necessary to
go to Window->Preferences->Plug-in Development, and select the running
platform (active), and then select the "Reload" button. There may be
build errors until this step is completed.