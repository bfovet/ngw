Welcome
=======

This is the *Next Generation Workflow* project. The system includes an
Eclipse-based workflow editor and a portable Java runtime system. The
runtime is designed to be implementable on a variety of Java workflow
engines. The initial implementation is based on [Google Sarasvati](https://code.google.com/archive/p/sarasvati/).


### Installation
#### Required Software
- [Eclipse Oxygen SR3 for RCP/RAP Developer](http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/oxygen3a)
- Git
#### Procedure
*Note*: Be cognizant of any proxy requirements before using Eclipse. To set these launch Eclipse: Window -> Preferences -> General -> Network Connection 
 1. `$ git clone https://gitlab.com/iwf/ngw`
 2. Launch Eclipse -> Import existing project (cloned repository) 
 3. Via the Project Explorer open `Target-Platform/target-platform-definition.target` and changes the `${workspace_loc}` of the associated "Location" to reflect the Git clone location and save these changes. <br/>Example: `/home/user/git/ngw/Target-Platform/Target-Platform/eclipse` 
 4. Window -> Preferences -> ANT -> Runtime
 5. Select "Ant Home" and set the destination to the `apache-ant-1.9.6` folder in the repository and apply changes 
 6. Right click `Target-Platform/build.xml` -> Run As -> Ant Build <br/> *Note*: Depending on access rights to external sources, you may see an error associated with `cubitlib`, this can be ignored.
 7. Window -> Preferences -> Plug-in Development -> Target Platform
 8. Check the `Dart Platform` then "Reload" and finally "Apply and Close"
 9. File -> Restart <br/> Eclipse will restart and rebuild your workspace

### Running
When running these plugins as an Eclipse application, you'll need to
explicitly open the "Settings" view to configure workflow
components. A "Workflow Editing" perspective is in the works.