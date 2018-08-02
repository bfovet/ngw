Welcome
=======

This is the *Next Generation Workflow* project. The system includes an
Eclipse-based workflow editor and a portable Java runtime system. The
runtime is designed to be implementable on a variety of Java workflow
engines. The initial implementation is based on [Google Sarasvati](https://code.google.com/archive/p/sarasvati/).


### Installation - Eclipse
#### Required Software
- [Eclipse Oxygen SR3 for RCP/RAP Developer](http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/oxygen3a)
- Git

#### Procedure
*Note*: Be cognizant of any proxy requirements before using Eclipse. To set these launch Eclipse: Window -> Preferences -> General -> Network Connection 
 1. `$ git clone https://gitlab.com/iwf/ngw`
 2. Launch Eclipse -> Import existing project (cloned repository) 
 3. Via the Project Explorer open `Target-Platform/target-platform-definition.target` and changes the `${workspace_loc}` of the associated "Location" to reflect the Git clone location and save these changes. <br/>Example: `/home/user/git/ngw/Target-Platform/Target-Platform/eclipse` 
 4. Window -> Preferences -> ANT -> Runtime
 5. Select "Ant Home", set the destination to the `apache-ant-1.9.6` folder in the repository, and apply changes 
 6. Right click `Target-Platform/build.xml` -> Run As -> Ant Build <br/> *Note*: Depending on access rights to external sources, you may see an error associated with `cubitlib`, this can be ignored.
 7. Window -> Preferences -> Plug-in Development -> Target Platform
 8. Check the `Dart Platform` then "Reload" and finally "Apply and Close"
 9. File -> Restart <br/> Eclipse will restart and rebuild your workspace

#### Docker / Charliecloud
Required [Docker](https://docs.docker.com/install/) and [Charliecloud](https://github.com/hpc/charliecloud/); however, you can run the container with Charilecloud but you will need to write the appropraite `docker run` statement.
1. `$ docker pull registry.gitlab.com/iwf/ngw/eclipse_x11`
2. `$ docker tag registry.gitlab.com/iwf/ngw/eclipse_x11 eclipse_x11`
3. `$ ch-docker2tar eclise_x11 /var/tmp`
4. `$ ch-tar2dir /var/tmp/eclipse_x11.tar.gz /var/tmp`
5. `$ ch-run /var/tmp/eclipse_x11 -- /opt/eclipse/./eclipse`
6. Use your host systems home directory as the workspace and import the project using the same steps listed above. By doing so you can ensure that development work is not tired directly to a container.

*TODO* Upload Dockerfile and document in README

#### Running
When running these plugins as an Eclipse application, you'll need to
explicitly open the "Settings" view to configure workflow
components. A "Workflow Editing" perspective is in the works.

1. File -> New -> Project (General/Project)
2. Create "New Project" providing required details <br/>There are no referenced projects
3. Right click on newly created project -> Run As -> Run Configurations...
4. Create a new `Eclipse Application`
5. In the new application via the Plug-ins tabe un-check `Validate Plug-ins automatically prior to launching` and Apply
6. Run
7. Ignore all errors & warnings that appear in the Console during this initial process
8. *First time setup* in newly opened window
9. Window -> Perspective -> Open Perspective -> Java
10. Window -> Show View -> Other -> "Settings" -> Open
11. File -> New -> Java Project <br/> Enter Project Name, all other settings default
12. Right click new project -> New -> Other -> "Workflow" -> Next
13. Name your workflow file and Finish
14. The newly created workflile (fileName.iwf) will be oepn in the middle of the screen.<br/>Below you should see the `Settings` and `Palette` windows.

From this point you can begin creating the workflow, simply select an object from the Palette and place it in the workflow window. After this module has been deployed in the workflow further configuration can be accomplised via the Settings window.

### Installation - Workflow Engine (only)
I have only tested the workflow engine running on a local host (virtual machine) and not on an HPC. 
There are likely adidtional requirements that need added to the container to offer the full range of support.
#### Require Software
- Java JDK
- Git
- [Docker](https://docs.docker.com/install/) 
- [Charliecloud](https://github.com/hpc/charliecloud/)

#### Docker / Charliecloud
1. `$ docker pull registry.gitlab.com/iwf/ngw/engine`
2. `$ docker tag registry.gitlab.com/iwf/ngw/engine engine`
3. `$ ch-docker2tar engine /var/tmp`
3. `$ ch-tar2dir /var/tmp/engine.tar.gz /var/tmp`
4. `$ ch-run -w -b {directory storing .iwf} /var/tmp/engine -- java -jar wf_engine.jar /mnt/0/{workflowName}.iwf`

*TODO* Upload Dockerfile and document in README