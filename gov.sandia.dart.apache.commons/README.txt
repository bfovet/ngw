So, you want to add a file to this repository.

*** DON'T ADD THE FILE DIRECTLY ***

You must have the following projects:
 * build
 * BinaryPluginHelper
 * gov.sandia.dart.apache.commons (duh)

You must do the following:

1. In the build project:
  * Modify the ivy.xml to specify the file
  * Run the "ant update" to obtain the file into the ivy-repo/libs
    * Alternatively to the update, you can modify the build.xml "qi" to
      pull it into the repo (you must still update the ivy.xml)
      
2. In the gov.sandia.dart.apache.commons project:
  * Modify the ivy.xml to specify the file
  * Run "ant -f ant-build.xml update" to pull the new file into the
    directory, as well as to update the .classpath
    

We use ivy to maintain our dependencies because:
  * It provides a common way to ensure that a few various projects are in
    sync with versions at a given point in time
  * It gives us ease of updating once the file is specified in the ivy.xml
    files
  * The updating from the build project talks to external sources, the
    other projects talk to the build project, so by having tags, we always
    know what was present when and from where
  * Since we know from whence files came and what versions, we can also
    track license compliance without stuffing licenses everywhere
    
If you have a file that (a)must go into this apache.commons project and
(b)is not in one of the central repositories:
  * Are you sure it should go into this project?
  * If so, it can be directly added to the build project by using
    a static import. Look in the build project for how to get it added.
  * Then update the ivy.xml file in the apache commons
  * Was it mentioned NOT to directly add the file to the apache commons --
    DON'T DO IT! It can be added to the build project, and then updated
    into the apache commons.
    
    
*** Note on dependencies ***
It would appear the stax.jar file should not be included in this
plugin. Eclipse itself has equivalent classes. Therefore, be careful with
adding entries to the ivy.xml; it may be necessary to ensure the
transitive=false setting is present.    